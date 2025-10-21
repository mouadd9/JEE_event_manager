package ma.ensa.tetouan.eventmanagement.service;

import ma.ensa.tetouan.eventmanagement.dao.*;
import ma.ensa.tetouan.eventmanagement.exception.BusinessException;
import ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException;
import ma.ensa.tetouan.eventmanagement.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implémentation du service de gestion des participants.
 *
 * @author ENSA Tétouan
 */
public class ParticipantServiceImpl implements ParticipantService {

    private static final Logger logger = LoggerFactory.getLogger(ParticipantServiceImpl.class);

    private final ParticipantDAO participantDAO;
    private final InscriptionDAO inscriptionDAO;
    private final CommentaireDAO commentaireDAO;
    private final EvaluationDAO evaluationDAO;

    /**
     * Constructeur avec injection des DAOs.
     */
    public ParticipantServiceImpl() {
        this.participantDAO = new ParticipantDAOImpl();
        this.inscriptionDAO = new InscriptionDAOImpl();
        this.commentaireDAO = new CommentaireDAOImpl();
        this.evaluationDAO = new EvaluationDAOImpl();
    }

    /**
     * Constructeur pour les tests (injection de dépendance).
     */
    public ParticipantServiceImpl(ParticipantDAO participantDAO, InscriptionDAO inscriptionDAO,
                                   CommentaireDAO commentaireDAO, EvaluationDAO evaluationDAO) {
        this.participantDAO = participantDAO;
        this.inscriptionDAO = inscriptionDAO;
        this.commentaireDAO = commentaireDAO;
        this.evaluationDAO = evaluationDAO;
    }

    @Override
    public Optional<Participant> getParticipantWithInscriptions(Long participantId) {
        logger.debug("Récupération du participant {} avec ses inscriptions", participantId);

        if (participantId == null) {
            return Optional.empty();
        }

        try {
            return participantDAO.findWithInscriptions(participantId);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération du participant avec inscriptions", e);
            throw new BusinessException("Erreur lors de la récupération du participant", e);
        }
    }

    @Override
    public Participant updatePreferences(Long participantId, String preferences) {
        logger.info("Mise à jour des préférences pour le participant {}", participantId);

        // 1. Vérifier que le participant existe
        Participant participant = getParticipantById(participantId);

        // 2. Valider les préférences (optionnelles)
        if (preferences != null && preferences.length() > 500) {
            throw new BusinessException("Les préférences ne doivent pas dépasser 500 caractères");
        }

        // 3. Mettre à jour les préférences
        participant.setPreferences(preferences);

        // 4. Sauvegarder
        try {
            Participant updated = participantDAO.update(participant);
            logger.info("Préférences mises à jour pour le participant {}", participantId);
            return updated;
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour des préférences", e);
            throw new BusinessException("Erreur lors de la mise à jour des préférences", e);
        }
    }

    @Override
    public List<Evenement> getMyUpcomingEvents(Long participantId) {
        logger.debug("Récupération des événements à venir pour le participant {}", participantId);

        // Vérifier que le participant existe
        getParticipantById(participantId);

        try {
            List<Inscription> inscriptions = inscriptionDAO.findByParticipant(participantId);

            // Filtrer les inscriptions avec événements à venir (tous les statuts sauf ANNULEE)
            LocalDateTime now = LocalDateTime.now();
            List<Evenement> upcomingEvents = inscriptions.stream()
                .filter(i -> i.getStatut() != StatutInscription.ANNULEE)
                .map(Inscription::getEvenement)
                .filter(e -> e.getDateDebut().isAfter(now))
                .sorted(Comparator.comparing(Evenement::getDateDebut))
                .distinct()
                .collect(Collectors.toList());

            logger.debug("{} événements à venir trouvés pour le participant {}",
                        upcomingEvents.size(), participantId);
            return upcomingEvents;

        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des événements à venir", e);
            throw new BusinessException("Erreur lors de la récupération des événements à venir", e);
        }
    }

    @Override
    public List<Evenement> getMyPastEvents(Long participantId) {
        logger.debug("Récupération des événements passés pour le participant {}", participantId);

        // Vérifier que le participant existe
        getParticipantById(participantId);

        try {
            List<Inscription> inscriptions = inscriptionDAO.findByParticipant(participantId);

            // Filtrer les inscriptions acceptées avec événements passés
            LocalDateTime now = LocalDateTime.now();
            List<Evenement> pastEvents = inscriptions.stream()
                .filter(i -> i.getStatut() == StatutInscription.ACCEPTEE)
                .map(Inscription::getEvenement)
                .filter(e -> e.getDateFin().isBefore(now))
                .sorted((e1, e2) -> e2.getDateDebut().compareTo(e1.getDateDebut())) // Plus récents d'abord
                .distinct()
                .collect(Collectors.toList());

            logger.debug("{} événements passés trouvés pour le participant {}",
                        pastEvents.size(), participantId);
            return pastEvents;

        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des événements passés", e);
            throw new BusinessException("Erreur lors de la récupération des événements passés", e);
        }
    }

    @Override
    public List<Inscription> getMyInscriptions(Long participantId) {
        logger.debug("Récupération des inscriptions du participant {}", participantId);

        // Vérifier que le participant existe
        getParticipantById(participantId);

        try {
            List<Inscription> inscriptions = inscriptionDAO.findByParticipant(participantId);

            // Trier par date d'inscription (plus récentes d'abord)
            inscriptions.sort((i1, i2) -> i2.getDateInscription().compareTo(i1.getDateInscription()));

            logger.debug("{} inscriptions trouvées pour le participant {}",
                        inscriptions.size(), participantId);
            return inscriptions;

        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des inscriptions", e);
            throw new BusinessException("Erreur lors de la récupération des inscriptions", e);
        }
    }

    @Override
    public Map<String, Object> getParticipantStats(Long participantId) {
        logger.debug("Calcul des statistiques pour le participant {}", participantId);

        // Vérifier que le participant existe
        getParticipantById(participantId);

        try {
            Map<String, Object> stats = new LinkedHashMap<>();

            // Récupérer toutes les inscriptions
            List<Inscription> inscriptions = inscriptionDAO.findByParticipant(participantId);

            // Nombre total d'inscriptions
            stats.put("totalRegistrations", inscriptions.size());

            // Inscriptions par statut
            long acceptedRegistrations = inscriptions.stream()
                .filter(i -> i.getStatut() == StatutInscription.ACCEPTEE)
                .count();
            long waitingRegistrations = inscriptions.stream()
                .filter(i -> i.getStatut() == StatutInscription.EN_ATTENTE)
                .count();
            long refusedRegistrations = inscriptions.stream()
                .filter(i -> i.getStatut() == StatutInscription.REFUSEE)
                .count();
            long cancelledRegistrations = inscriptions.stream()
                .filter(i -> i.getStatut() == StatutInscription.ANNULEE)
                .count();

            stats.put("acceptedRegistrations", acceptedRegistrations);
            stats.put("waitingRegistrations", waitingRegistrations);
            stats.put("refusedRegistrations", refusedRegistrations);
            stats.put("cancelledRegistrations", cancelledRegistrations);

            // Événements assistés (acceptés et terminés)
            LocalDateTime now = LocalDateTime.now();
            long attendedEvents = inscriptions.stream()
                .filter(i -> i.getStatut() == StatutInscription.ACCEPTEE)
                .map(Inscription::getEvenement)
                .filter(e -> e.getDateFin().isBefore(now))
                .count();
            stats.put("attendedEvents", attendedEvents);

            // Événements à venir
            long upcomingEvents = inscriptions.stream()
                .filter(i -> i.getStatut() == StatutInscription.ACCEPTEE)
                .map(Inscription::getEvenement)
                .filter(e -> e.getDateDebut().isAfter(now))
                .count();
            stats.put("upcomingEvents", upcomingEvents);

            // Nombre d'évaluations données
            List<Evaluation> evaluations = evaluationDAO.findByParticipant(participantId);
            stats.put("ratingsGiven", evaluations.size());

            // Note moyenne donnée
            if (!evaluations.isEmpty()) {
                double avgRatingGiven = evaluations.stream()
                    .mapToInt(Evaluation::getNote)
                    .average()
                    .orElse(0.0);
                stats.put("averageRatingGiven", Math.round(avgRatingGiven * 100.0) / 100.0);
            } else {
                stats.put("averageRatingGiven", null);
            }

            // Nombre de commentaires postés
            List<Commentaire> commentaires = commentaireDAO.findByParticipant(participantId);
            stats.put("commentsPosted", commentaires.size());

            // Commentaires visibles
            long visibleComments = commentaires.stream()
                .filter(c -> c.getVisible())
                .count();
            stats.put("visibleComments", visibleComments);

            // Taux d'acceptation des inscriptions
            if (inscriptions.size() > 0) {
                double acceptanceRate = (acceptedRegistrations * 100.0) / inscriptions.size();
                stats.put("acceptanceRate", Math.round(acceptanceRate * 100.0) / 100.0);
            } else {
                stats.put("acceptanceRate", null);
            }

            // Taux de présence (événements assistés / événements acceptés passés)
            long pastAcceptedEvents = inscriptions.stream()
                .filter(i -> i.getStatut() == StatutInscription.ACCEPTEE)
                .map(Inscription::getEvenement)
                .filter(e -> e.getDateFin().isBefore(now))
                .count();

            if (pastAcceptedEvents > 0) {
                // Assuming all past accepted events were attended for now
                // In a real system, you'd track actual attendance
                stats.put("attendanceRate", 100.0);
            } else {
                stats.put("attendanceRate", null);
            }

            logger.debug("Statistiques calculées pour le participant {}: {} inscriptions",
                        participantId, stats.get("totalRegistrations"));

            return stats;

        } catch (Exception e) {
            logger.error("Erreur lors du calcul des statistiques du participant", e);
            throw new BusinessException("Erreur lors du calcul des statistiques", e);
        }
    }

    @Override
    public Participant getParticipantById(Long participantId) {
        logger.debug("Récupération du participant {}", participantId);

        if (participantId == null) {
            throw new IllegalArgumentException("L'ID du participant ne peut pas être null");
        }

        Optional<Participant> participantOpt = participantDAO.findById(participantId);
        if (!participantOpt.isPresent()) {
            logger.warn("Participant non trouvé: ID={}", participantId);
            throw new ResourceNotFoundException("Participant", participantId);
        }

        return participantOpt.get();
    }

    @Override
    public List<Participant> getAllParticipants(int page, int pageSize) {
        logger.debug("Récupération de tous les participants - page: {}, taille: {}", page, pageSize);

        if (page < 0 || pageSize <= 0) {
            throw new IllegalArgumentException("Les paramètres de pagination doivent être valides");
        }

        try {
            return participantDAO.findAll(page, pageSize);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des participants", e);
            throw new BusinessException("Erreur lors de la récupération des participants", e);
        }
    }

    @Override
    public long getTotalParticipants() {
        logger.debug("Comptage du nombre total de participants");

        try {
            return participantDAO.count();
        } catch (Exception e) {
            logger.error("Erreur lors du comptage des participants", e);
            throw new BusinessException("Erreur lors du comptage des participants", e);
        }
    }
}
