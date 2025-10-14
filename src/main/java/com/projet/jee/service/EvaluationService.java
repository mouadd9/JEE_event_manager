package com.projet.jee.service;

import com.projet.jee.dao.EvaluationDAO;
import com.projet.jee.dao.UtilisateurDAO;
import com.projet.jee.model.Evaluation;
import com.projet.jee.model.Participant;
import com.projet.jee.model.Utilisateur;
import com.projet.jee.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing event ratings/evaluations.
 * Handles rating submission, updates, and aggregation.
 */
public class EvaluationService {
    private static final Logger logger = LoggerFactory.getLogger(EvaluationService.class);
    private final EvaluationDAO evaluationDAO;
    private final UtilisateurDAO utilisateurDAO;

    public EvaluationService() {
        this.evaluationDAO = new EvaluationDAO();
        this.utilisateurDAO = new UtilisateurDAO();
    }

    /**
     * Add or update a rating for an event
     */
    public Evaluation rateEvent(Long participantId, Long evenementId, Integer note, String commentaire) {
        // Validate participant
        Optional<Utilisateur> userOpt = utilisateurDAO.findById(participantId);
        if (!userOpt.isPresent() || !(userOpt.get() instanceof Participant)) {
            throw new IllegalArgumentException("Participant non trouvé");
        }

        Participant participant = (Participant) userOpt.get();

        // Validate rating
        if (!ValidationUtil.isValidRating(note)) {
            throw new IllegalArgumentException("La note doit être entre 1 et 5");
        }

        // Check if participant already rated this event
        Optional<Evaluation> existingOpt = evaluationDAO.findByParticipantAndEvenement(participantId, evenementId);

        if (existingOpt.isPresent()) {
            // Update existing rating
            Evaluation existing = existingOpt.get();
            if (!existing.canBeModified()) {
                throw new IllegalArgumentException("La période de modification est expirée (7 jours)");
            }
            existing.setNote(note);
            if (commentaire != null && !commentaire.trim().isEmpty()) {
                existing.setCommentaireNote(ValidationUtil.sanitizeForDisplay(commentaire));
            }
            Evaluation updated = evaluationDAO.update(existing);
            logger.info("Évaluation mise à jour par participant ID {} pour événement ID {}", participantId, evenementId);
            return updated;
        } else {
            // Create new rating
            Evaluation evaluation = new Evaluation();
            evaluation.setEvenementId(evenementId);
            evaluation.setParticipant(participant);
            evaluation.setNote(note);
            if (commentaire != null && !commentaire.trim().isEmpty()) {
                evaluation.setCommentaireNote(ValidationUtil.sanitizeForDisplay(commentaire));
            }

            Evaluation saved = evaluationDAO.save(evaluation);
            logger.info("Nouvelle évaluation ajoutée par participant ID {} pour événement ID {}", participantId, evenementId);
            return saved;
        }
    }

    /**
     * Get average rating for an event
     */
    public Double getAverageRating(Long evenementId) {
        return evaluationDAO.calculateAverageRating(evenementId);
    }

    /**
     * Get rating distribution (how many 1-star, 2-star, etc.)
     */
    public long[] getRatingDistribution(Long evenementId) {
        return evaluationDAO.getRatingDistribution(evenementId);
    }

    /**
     * Get all ratings for an event
     */
    public List<Evaluation> getRatingsByEvent(Long evenementId) {
        return evaluationDAO.findByEvenement(evenementId);
    }

    /**
     * Get ratings by participant
     */
    public List<Evaluation> getRatingsByParticipant(Long participantId) {
        return evaluationDAO.findByParticipant(participantId);
    }

    /**
     * Check if participant has rated an event
     */
    public boolean hasParticipantRated(Long participantId, Long evenementId) {
        return evaluationDAO.hasParticipantRated(participantId, evenementId);
    }

    /**
     * Get participant's rating for a specific event
     */
    public Optional<Evaluation> getParticipantRating(Long participantId, Long evenementId) {
        return evaluationDAO.findByParticipantAndEvenement(participantId, evenementId);
    }

    /**
     * Delete a rating
     */
    public void deleteRating(Long evaluationId, Long participantId) {
        Optional<Evaluation> evalOpt = evaluationDAO.findById(evaluationId);
        if (!evalOpt.isPresent()) {
            throw new IllegalArgumentException("Évaluation non trouvée");
        }

        Evaluation evaluation = evalOpt.get();
        if (!evaluation.getParticipant().getId().equals(participantId)) {
            throw new IllegalArgumentException("Vous ne pouvez supprimer que vos propres évaluations");
        }

        evaluationDAO.delete(evaluationId);
        logger.info("Évaluation ID {} supprimée par participant ID {}", evaluationId, participantId);
    }

    /**
     * Count ratings for an event
     */
    public long countByEvent(Long evenementId) {
        return evaluationDAO.countByEvenement(evenementId);
    }

    /**
     * Get recent ratings
     */
    public List<Evaluation> getRecentRatings(int limit) {
        return evaluationDAO.findRecent(limit);
    }

    /**
     * Get evaluations with comments
     */
    public List<Evaluation> getEvaluationsWithComments(Long evenementId) {
        return evaluationDAO.findWithComments(evenementId);
    }
}
