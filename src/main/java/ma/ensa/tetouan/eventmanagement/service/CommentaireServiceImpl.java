package ma.ensa.tetouan.eventmanagement.service;

import ma.ensa.tetouan.eventmanagement.dao.*;
import ma.ensa.tetouan.eventmanagement.exception.BusinessException;
import ma.ensa.tetouan.eventmanagement.exception.InvalidEventStateException;
import ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException;
import ma.ensa.tetouan.eventmanagement.model.Commentaire;
import ma.ensa.tetouan.eventmanagement.model.Evenement;
import ma.ensa.tetouan.eventmanagement.model.Participant;
import ma.ensa.tetouan.eventmanagement.model.StatutEvenement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implémentation du service de gestion des commentaires avec modération.
 *
 * @author ENSA Tétouan
 */
public class CommentaireServiceImpl implements CommentaireService {

    private static final Logger logger = LoggerFactory.getLogger(CommentaireServiceImpl.class);
    private static final int AUTO_HIDE_THRESHOLD = 3; // Auto-hide après 3 signalements

    private final CommentaireDAO commentaireDAO;
    private final ParticipantDAO participantDAO;
    private final EvenementDAO evenementDAO;

    /**
     * Constructeur avec injection des DAOs.
     */
    public CommentaireServiceImpl() {
        this.commentaireDAO = new CommentaireDAOImpl();
        this.participantDAO = new ParticipantDAOImpl();
        this.evenementDAO = new EvenementDAOImpl();
    }

    /**
     * Constructeur pour les tests (injection de dépendance).
     */
    public CommentaireServiceImpl(CommentaireDAO commentaireDAO, ParticipantDAO participantDAO,
                                   EvenementDAO evenementDAO) {
        this.commentaireDAO = commentaireDAO;
        this.participantDAO = participantDAO;
        this.evenementDAO = evenementDAO;
    }

    @Override
    public Commentaire addCommentaire(Long participantId, Long evenementId, String texte) {
        logger.info("Tentative d'ajout d'un commentaire par le participant {} sur l'événement {}",
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

        // 3. Vérifier que l'événement est PUBLIE ou TERMINE
        if (evenement.getStatut() != StatutEvenement.PUBLIE &&
            evenement.getStatut() != StatutEvenement.TERMINE) {
            logger.warn("Tentative de commentaire sur un événement non publié/terminé: statut={}",
                       evenement.getStatut());
            throw new InvalidEventStateException(
                "Impossible de commenter un événement qui n'est pas publié ou terminé. " +
                "Statut actuel: " + evenement.getStatut().getLibelle()
            );
        }

        // 4. Valider le texte
        validateTexte(texte);

        // 5. Créer le commentaire
        Commentaire commentaire = new Commentaire();
        commentaire.setParticipant(participant);
        commentaire.setEvenement(evenement);
        commentaire.setTexte(texte);
        commentaire.setDateCreation(LocalDateTime.now());
        commentaire.setVisible(true);
        commentaire.setModere(false);
        commentaire.setNombreSignalements(0);

        try {
            Commentaire savedCommentaire = commentaireDAO.save(commentaire);
            logger.info("Commentaire créé avec succès: ID={}", savedCommentaire.getId());
            return savedCommentaire;
        } catch (Exception e) {
            logger.error("Erreur lors de la création du commentaire", e);
            throw new BusinessException("Erreur lors de l'ajout du commentaire", e);
        }
    }

    @Override
    public Commentaire updateCommentaire(Long commentaireId, Long participantId, String texte) {
        logger.info("Tentative de mise à jour du commentaire {} par le participant {}",
                   commentaireId, participantId);

        // 1. Récupérer le commentaire
        Commentaire commentaire = getCommentaireById(commentaireId);

        // 2. Vérifier que c'est bien le participant propriétaire
        if (!commentaire.getParticipant().getId().equals(participantId)) {
            logger.warn("Participant non autorisé: ID={}", participantId);
            throw new BusinessException("Vous n'êtes pas autorisé à modifier ce commentaire");
        }

        // 3. Vérifier que le commentaire n'a pas été rejeté par un modérateur
        if (commentaire.getModere() && !commentaire.getVisible()) {
            logger.warn("Tentative de modification d'un commentaire rejeté par modération");
            throw new BusinessException(
                "Impossible de modifier un commentaire qui a été rejeté par un modérateur"
            );
        }

        // 4. Valider le nouveau texte
        validateTexte(texte);

        // 5. Mettre à jour le commentaire
        commentaire.setTexte(texte);
        commentaire.setModere(false); // Réinitialiser le statut de modération

        try {
            Commentaire updated = commentaireDAO.update(commentaire);
            logger.info("Commentaire mis à jour avec succès: ID={}", commentaireId);
            return updated;
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du commentaire", e);
            throw new BusinessException("Erreur lors de la mise à jour du commentaire", e);
        }
    }

    @Override
    public void deleteCommentaire(Long commentaireId, Long participantId) {
        logger.info("Tentative de suppression du commentaire {} par le participant {}",
                   commentaireId, participantId);

        // 1. Récupérer le commentaire
        Commentaire commentaire = getCommentaireById(commentaireId);

        // 2. Vérifier que c'est bien le participant propriétaire
        if (!commentaire.getParticipant().getId().equals(participantId)) {
            logger.warn("Participant non autorisé: ID={}", participantId);
            throw new BusinessException("Vous n'êtes pas autorisé à supprimer ce commentaire");
        }

        // 3. Supprimer le commentaire
        try {
            commentaireDAO.delete(commentaire);
            logger.info("Commentaire supprimé avec succès: ID={}", commentaireId);
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression du commentaire", e);
            throw new BusinessException("Erreur lors de la suppression du commentaire", e);
        }
    }

    @Override
    public Commentaire reportCommentaire(Long commentaireId) {
        logger.info("Signalement du commentaire {}", commentaireId);

        // 1. Récupérer le commentaire
        Commentaire commentaire = getCommentaireById(commentaireId);

        // 2. Incrémenter le nombre de signalements
        commentaire.signaler();

        // 3. Auto-cacher si le seuil est atteint
        if (commentaire.getNombreSignalements() >= AUTO_HIDE_THRESHOLD && commentaire.getVisible()) {
            commentaire.setVisible(false);
            logger.warn("Commentaire ID={} automatiquement caché après {} signalements",
                       commentaireId, commentaire.getNombreSignalements());
        }

        // 4. Sauvegarder
        try {
            Commentaire updated = commentaireDAO.update(commentaire);
            logger.info("Commentaire signalé: ID={}, Total signalements={}",
                       commentaireId, updated.getNombreSignalements());
            return updated;
        } catch (Exception e) {
            logger.error("Erreur lors du signalement du commentaire", e);
            throw new BusinessException("Erreur lors du signalement du commentaire", e);
        }
    }

    @Override
    public Commentaire moderateCommentaire(Long commentaireId, boolean approved) {
        logger.info("Modération du commentaire {}: {}", commentaireId, approved ? "APPROUVÉ" : "REJETÉ");

        // 1. Récupérer le commentaire
        Commentaire commentaire = getCommentaireById(commentaireId);

        // 2. Appliquer la modération
        commentaire.setModere(true);
        commentaire.setVisible(approved);

        // 3. Réinitialiser les signalements si approuvé
        if (approved) {
            commentaire.setNombreSignalements(0);
        }

        // 4. Sauvegarder
        try {
            Commentaire updated = commentaireDAO.update(commentaire);
            logger.info("Commentaire modéré avec succès: ID={}, Visible={}",
                       commentaireId, updated.getVisible());
            return updated;
        } catch (Exception e) {
            logger.error("Erreur lors de la modération du commentaire", e);
            throw new BusinessException("Erreur lors de la modération du commentaire", e);
        }
    }

    @Override
    public List<Commentaire> getCommentairesByEvenement(Long evenementId) {
        logger.debug("Récupération des commentaires visibles de l'événement {}", evenementId);

        if (evenementId == null) {
            throw new IllegalArgumentException("L'ID de l'événement ne peut pas être null");
        }

        try {
            List<Commentaire> allCommentaires = commentaireDAO.findByEvenement(evenementId);

            // Filtrer pour ne retourner que les commentaires visibles
            List<Commentaire> visibleCommentaires = allCommentaires.stream()
                .filter(c -> c.getVisible())
                .collect(Collectors.toList());

            logger.debug("{} commentaires visibles récupérés sur {} total",
                        visibleCommentaires.size(), allCommentaires.size());
            return visibleCommentaires;
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des commentaires de l'événement", e);
            throw new BusinessException("Erreur lors de la récupération des commentaires", e);
        }
    }

    @Override
    public List<Commentaire> getCommentairesByParticipant(Long participantId) {
        logger.debug("Récupération des commentaires du participant {}", participantId);

        if (participantId == null) {
            throw new IllegalArgumentException("L'ID du participant ne peut pas être null");
        }

        try {
            return commentaireDAO.findByParticipant(participantId);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des commentaires du participant", e);
            throw new BusinessException("Erreur lors de la récupération des commentaires", e);
        }
    }

    @Override
    public List<Commentaire> getUnmoderatedComments() {
        logger.debug("Récupération des commentaires non modérés");

        try {
            List<Commentaire> allCommentaires = commentaireDAO.findAll();

            // Filtrer les commentaires non modérés
            List<Commentaire> unmoderatedCommentaires = allCommentaires.stream()
                .filter(c -> !c.getModere())
                .sorted((c1, c2) -> c2.getDateCreation().compareTo(c1.getDateCreation()))
                .collect(Collectors.toList());

            logger.debug("{} commentaires non modérés trouvés", unmoderatedCommentaires.size());
            return unmoderatedCommentaires;
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des commentaires non modérés", e);
            throw new BusinessException("Erreur lors de la récupération des commentaires non modérés", e);
        }
    }

    @Override
    public List<Commentaire> getReportedComments() {
        logger.debug("Récupération des commentaires signalés");

        try {
            List<Commentaire> allCommentaires = commentaireDAO.findAll();

            // Filtrer les commentaires signalés
            List<Commentaire> reportedCommentaires = allCommentaires.stream()
                .filter(c -> c.getNombreSignalements() > 0)
                .sorted((c1, c2) -> Integer.compare(c2.getNombreSignalements(), c1.getNombreSignalements()))
                .collect(Collectors.toList());

            logger.debug("{} commentaires signalés trouvés", reportedCommentaires.size());
            return reportedCommentaires;
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des commentaires signalés", e);
            throw new BusinessException("Erreur lors de la récupération des commentaires signalés", e);
        }
    }

    @Override
    public Commentaire getCommentaireById(Long commentaireId) {
        logger.debug("Récupération du commentaire {}", commentaireId);

        if (commentaireId == null) {
            throw new IllegalArgumentException("L'ID du commentaire ne peut pas être null");
        }

        Optional<Commentaire> commentaireOpt = commentaireDAO.findById(commentaireId);
        if (!commentaireOpt.isPresent()) {
            logger.warn("Commentaire non trouvé: ID={}", commentaireId);
            throw new ResourceNotFoundException("Commentaire", commentaireId);
        }

        return commentaireOpt.get();
    }

    /**
     * Valide le texte d'un commentaire.
     *
     * @param texte Le texte à valider
     * @throws BusinessException Si la validation échoue
     */
    private void validateTexte(String texte) {
        if (texte == null || texte.trim().isEmpty()) {
            throw new BusinessException("Le texte du commentaire est obligatoire");
        }

        if (texte.length() < 5) {
            throw new BusinessException("Le commentaire doit contenir au moins 5 caractères");
        }

        if (texte.length() > 500) {
            throw new BusinessException("Le commentaire ne doit pas dépasser 500 caractères");
        }
    }
}
