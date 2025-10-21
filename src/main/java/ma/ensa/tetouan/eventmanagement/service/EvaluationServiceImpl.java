package ma.ensa.tetouan.eventmanagement.service;

import ma.ensa.tetouan.eventmanagement.dao.*;
import ma.ensa.tetouan.eventmanagement.exception.BusinessException;
import ma.ensa.tetouan.eventmanagement.exception.InvalidEventStateException;
import ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException;
import ma.ensa.tetouan.eventmanagement.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implémentation du service de gestion des évaluations avec validation de participation.
 *
 * @author ENSA Tétouan
 */
public class EvaluationServiceImpl implements EvaluationService {

    private static final Logger logger = LoggerFactory.getLogger(EvaluationServiceImpl.class);

    private final EvaluationDAO evaluationDAO;
    private final ParticipantDAO participantDAO;
    private final EvenementDAO evenementDAO;
    private final InscriptionDAO inscriptionDAO;

    /**
     * Constructeur avec injection des DAOs.
     */
    public EvaluationServiceImpl() {
        this.evaluationDAO = new EvaluationDAOImpl();
        this.participantDAO = new ParticipantDAOImpl();
        this.evenementDAO = new EvenementDAOImpl();
        this.inscriptionDAO = new InscriptionDAOImpl();
    }

    /**
     * Constructeur pour les tests (injection de dépendance).
     */
    public EvaluationServiceImpl(EvaluationDAO evaluationDAO, ParticipantDAO participantDAO,
                                  EvenementDAO evenementDAO, InscriptionDAO inscriptionDAO) {
        this.evaluationDAO = evaluationDAO;
        this.participantDAO = participantDAO;
        this.evenementDAO = evenementDAO;
        this.inscriptionDAO = inscriptionDAO;
    }

    @Override
    public Evaluation addEvaluation(Long participantId, Long evenementId, int note, String texte) {
        logger.info("Tentative d'ajout d'une évaluation par le participant {} pour l'événement {}",
                   participantId, evenementId);

        // 1. Valider l'existence du participant
        Optional<Participant> participantOpt = participantDAO.findById(participantId);
        if (!participantOpt.isPresent()) {
            logger.warn("Participant non trouvé: ID={}", participantId);
            throw new ResourceNotFoundException("Participant", participantId);
        }
        Participant participant = participantOpt.get();

        // 2. Valider l'existence de l'événement
        Optional<Evenement> evenementOpt = evenementDAO.findById(evenementId);
        if (!evenementOpt.isPresent()) {
            logger.warn("Événement non trouvé: ID={}", evenementId);
            throw new ResourceNotFoundException("Événement", evenementId);
        }
        Evenement evenement = evenementOpt.get();

        // 3. Valider la note
        validateNote(note);

        // 4. Valider le texte (optionnel)
        if (texte != null) {
            validateTexte(texte);
        }

        // 5. Vérifier que l'événement est TERMINE
        if (evenement.getStatut() != StatutEvenement.TERMINE) {
            logger.warn("Tentative d'évaluation d'un événement non terminé: statut={}", evenement.getStatut());
            throw new InvalidEventStateException(
                "Impossible d'évaluer un événement qui n'est pas terminé. " +
                "Statut actuel: " + evenement.getStatut().getLibelle()
            );
        }

        // 6. Vérifier que le participant a assisté à l'événement (inscription ACCEPTEE)
        if (!hasAttendedEvent(participantId, evenementId)) {
            logger.warn("Participant {} n'a pas assisté à l'événement {}", participantId, evenementId);
            throw new BusinessException("Vous devez avoir assisté à l'événement pour pouvoir l'évaluer");
        }

        // 7. Vérifier que le participant n'a pas déjà évalué cet événement
        Optional<Evaluation> existingEvaluation =
            evaluationDAO.findByParticipantAndEvenement(participantId, evenementId);
        if (existingEvaluation.isPresent()) {
            logger.warn("Participant {} a déjà évalué l'événement {}", participantId, evenementId);
            throw new BusinessException("Vous avez déjà évalué cet événement. Utilisez la fonction de modification.");
        }

        // 8. Créer l'évaluation
        Evaluation evaluation = new Evaluation();
        evaluation.setParticipant(participant);
        evaluation.setEvenement(evenement);
        evaluation.setNote(note);
        evaluation.setTexte(texte);
        evaluation.setHorodatage(LocalDateTime.now());
        evaluation.setVisible(true);

        try {
            Evaluation savedEvaluation = evaluationDAO.save(evaluation);

            // 9. Mettre à jour la note moyenne de l'événement
            updateEventAverageRating(evenementId);

            logger.info("Évaluation créée avec succès: ID={}, Note={}", savedEvaluation.getId(), note);
            return savedEvaluation;
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'évaluation", e);
            throw new BusinessException("Erreur lors de l'ajout de l'évaluation", e);
        }
    }

    @Override
    public Evaluation updateEvaluation(Long evaluationId, Long participantId, int note, String texte) {
        logger.info("Tentative de mise à jour de l'évaluation {} par le participant {}",
                   evaluationId, participantId);

        // 1. Récupérer l'évaluation
        Evaluation evaluation = getEvaluationById(evaluationId);

        // 2. Vérifier que c'est bien le participant propriétaire
        if (!evaluation.getParticipant().getId().equals(participantId)) {
            logger.warn("Participant non autorisé: ID={}", participantId);
            throw new BusinessException("Vous n'êtes pas autorisé à modifier cette évaluation");
        }

        // 3. Valider la nouvelle note
        validateNote(note);

        // 4. Valider le nouveau texte (optionnel)
        if (texte != null) {
            validateTexte(texte);
        }

        // 5. Mettre à jour l'évaluation
        evaluation.setNote(note);
        evaluation.setTexte(texte);

        try {
            Evaluation updated = evaluationDAO.update(evaluation);

            // 6. Recalculer la note moyenne de l'événement
            updateEventAverageRating(evaluation.getEvenement().getId());

            logger.info("Évaluation mise à jour avec succès: ID={}, Nouvelle note={}", evaluationId, note);
            return updated;
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de l'évaluation", e);
            throw new BusinessException("Erreur lors de la mise à jour de l'évaluation", e);
        }
    }

    @Override
    public void deleteEvaluation(Long evaluationId, Long participantId) {
        logger.info("Tentative de suppression de l'évaluation {} par le participant {}",
                   evaluationId, participantId);

        // 1. Récupérer l'évaluation
        Evaluation evaluation = getEvaluationById(evaluationId);

        // 2. Vérifier que c'est bien le participant propriétaire
        if (!evaluation.getParticipant().getId().equals(participantId)) {
            logger.warn("Participant non autorisé: ID={}", participantId);
            throw new BusinessException("Vous n'êtes pas autorisé à supprimer cette évaluation");
        }

        // 3. Sauvegarder l'ID de l'événement pour recalculer la moyenne après suppression
        Long evenementId = evaluation.getEvenement().getId();

        // 4. Supprimer l'évaluation
        try {
            evaluationDAO.delete(evaluation);

            // 5. Recalculer la note moyenne de l'événement
            updateEventAverageRating(evenementId);

            logger.info("Évaluation supprimée avec succès: ID={}", evaluationId);
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de l'évaluation", e);
            throw new BusinessException("Erreur lors de la suppression de l'évaluation", e);
        }
    }

    @Override
    public List<Evaluation> getEvaluationsByEvenement(Long evenementId) {
        logger.debug("Récupération des évaluations visibles de l'événement {}", evenementId);

        if (evenementId == null) {
            throw new IllegalArgumentException("L'ID de l'événement ne peut pas être null");
        }

        try {
            List<Evaluation> allEvaluations = evaluationDAO.findByEvenement(evenementId);

            // Filtrer pour ne retourner que les évaluations visibles
            List<Evaluation> visibleEvaluations = allEvaluations.stream()
                .filter(e -> e.getVisible())
                .sorted((e1, e2) -> e2.getHorodatage().compareTo(e1.getHorodatage()))
                .collect(Collectors.toList());

            logger.debug("{} évaluations visibles récupérées sur {} total",
                        visibleEvaluations.size(), allEvaluations.size());
            return visibleEvaluations;
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des évaluations de l'événement", e);
            throw new BusinessException("Erreur lors de la récupération des évaluations", e);
        }
    }

    @Override
    public List<Evaluation> getEvaluationsByParticipant(Long participantId) {
        logger.debug("Récupération des évaluations du participant {}", participantId);

        if (participantId == null) {
            throw new IllegalArgumentException("L'ID du participant ne peut pas être null");
        }

        try {
            return evaluationDAO.findByParticipant(participantId);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des évaluations du participant", e);
            throw new BusinessException("Erreur lors de la récupération des évaluations", e);
        }
    }

    @Override
    public Double getAverageRating(Long evenementId) {
        logger.debug("Calcul de la note moyenne pour l'événement {}", evenementId);

        if (evenementId == null) {
            throw new IllegalArgumentException("L'ID de l'événement ne peut pas être null");
        }

        try {
            return evaluationDAO.calculateAverageRating(evenementId);
        } catch (Exception e) {
            logger.error("Erreur lors du calcul de la note moyenne", e);
            throw new BusinessException("Erreur lors du calcul de la note moyenne", e);
        }
    }

    @Override
    public boolean canEvaluate(Long participantId, Long evenementId) {
        logger.debug("Vérification si le participant {} peut évaluer l'événement {}",
                    participantId, evenementId);

        if (participantId == null || evenementId == null) {
            return false;
        }

        try {
            // 1. Vérifier que l'événement existe
            Optional<Evenement> evenementOpt = evenementDAO.findById(evenementId);
            if (!evenementOpt.isPresent()) {
                return false;
            }

            Evenement evenement = evenementOpt.get();

            // 2. Vérifier que l'événement est TERMINE
            if (evenement.getStatut() != StatutEvenement.TERMINE) {
                logger.debug("Événement pas terminé: statut={}", evenement.getStatut());
                return false;
            }

            // 3. Vérifier que le participant a assisté à l'événement
            if (!hasAttendedEvent(participantId, evenementId)) {
                logger.debug("Participant n'a pas assisté à l'événement");
                return false;
            }

            // 4. Vérifier que le participant n'a pas déjà évalué
            Optional<Evaluation> existingEvaluation =
                evaluationDAO.findByParticipantAndEvenement(participantId, evenementId);
            if (existingEvaluation.isPresent()) {
                logger.debug("Participant a déjà évalué l'événement");
                return false;
            }

            // Toutes les conditions sont remplies
            return true;

        } catch (Exception e) {
            logger.error("Erreur lors de la vérification de la possibilité d'évaluation", e);
            return false;
        }
    }

    @Override
    public Optional<Evaluation> getEvaluationByParticipantAndEvent(Long participantId, Long evenementId) {
        logger.debug("Récupération de l'évaluation du participant {} pour l'événement {}",
                    participantId, evenementId);

        if (participantId == null || evenementId == null) {
            return Optional.empty();
        }

        try {
            return evaluationDAO.findByParticipantAndEvenement(participantId, evenementId);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de l'évaluation", e);
            throw new BusinessException("Erreur lors de la récupération de l'évaluation", e);
        }
    }

    @Override
    public Evaluation getEvaluationById(Long evaluationId) {
        logger.debug("Récupération de l'évaluation {}", evaluationId);

        if (evaluationId == null) {
            throw new IllegalArgumentException("L'ID de l'évaluation ne peut pas être null");
        }

        Optional<Evaluation> evaluationOpt = evaluationDAO.findById(evaluationId);
        if (!evaluationOpt.isPresent()) {
            logger.warn("Évaluation non trouvée: ID={}", evaluationId);
            throw new ResourceNotFoundException("Evaluation", evaluationId);
        }

        return evaluationOpt.get();
    }

    /**
     * Met à jour la note moyenne d'un événement.
     *
     * @param evenementId L'ID de l'événement
     */
    private void updateEventAverageRating(Long evenementId) {
        try {
            Double averageRating = evaluationDAO.calculateAverageRating(evenementId);

            Optional<Evenement> evenementOpt = evenementDAO.findById(evenementId);
            if (evenementOpt.isPresent()) {
                Evenement evenement = evenementOpt.get();
                evenement.setNoteMoyenne(averageRating);
                evenementDAO.update(evenement);

                logger.debug("Note moyenne mise à jour pour l'événement {}: {}",
                           evenementId, averageRating != null ? averageRating : "null");
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de la note moyenne", e);
            // Ne pas propager l'exception pour ne pas bloquer l'opération principale
        }
    }

    /**
     * Vérifie si un participant a assisté à un événement (inscription ACCEPTEE).
     *
     * @param participantId L'ID du participant
     * @param evenementId L'ID de l'événement
     * @return true si le participant a assisté, false sinon
     */
    private boolean hasAttendedEvent(Long participantId, Long evenementId) {
        try {
            List<Inscription> inscriptions = inscriptionDAO.findByParticipant(participantId);

            return inscriptions.stream()
                .anyMatch(i -> i.getEvenement().getId().equals(evenementId) &&
                              i.getStatut() == StatutInscription.ACCEPTEE);
        } catch (Exception e) {
            logger.error("Erreur lors de la vérification de la participation", e);
            return false;
        }
    }

    /**
     * Valide la note d'une évaluation.
     *
     * @param note La note à valider
     * @throws BusinessException Si la validation échoue
     */
    private void validateNote(int note) {
        if (note < 1 || note > 5) {
            throw new BusinessException("La note doit être comprise entre 1 et 5");
        }
    }

    /**
     * Valide le texte d'une évaluation.
     *
     * @param texte Le texte à valider
     * @throws BusinessException Si la validation échoue
     */
    private void validateTexte(String texte) {
        if (texte != null && !texte.trim().isEmpty()) {
            if (texte.length() > 1000) {
                throw new BusinessException("Le texte de l'évaluation ne doit pas dépasser 1000 caractères");
            }
        }
    }
}
