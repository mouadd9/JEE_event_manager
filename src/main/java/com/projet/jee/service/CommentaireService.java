package com.projet.jee.service;

import com.projet.jee.dao.CommentaireDAO;
import com.projet.jee.dao.UtilisateurDAO;
import com.projet.jee.model.Commentaire;
import com.projet.jee.model.Participant;
import com.projet.jee.model.Utilisateur;
import com.projet.jee.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing comments on events.
 * Handles CRUD operations and business logic for comments.
 */
public class CommentaireService {
    private static final Logger logger = LoggerFactory.getLogger(CommentaireService.class);
    private final CommentaireDAO commentaireDAO;
    private final UtilisateurDAO utilisateurDAO;

    public CommentaireService() {
        this.commentaireDAO = new CommentaireDAO();
        this.utilisateurDAO = new UtilisateurDAO();
    }

    /**
     * Add a comment to an event
     */
    public Commentaire addComment(Long participantId, Long evenementId, String contenu) {
        // Validate participant
        Optional<Utilisateur> userOpt = utilisateurDAO.findById(participantId);
        if (!userOpt.isPresent() || !(userOpt.get() instanceof Participant)) {
            throw new IllegalArgumentException("Participant non trouvé");
        }

        Participant participant = (Participant) userOpt.get();

        // Validate content
        if (!ValidationUtil.isValidLength(contenu, 10, 2000)) {
            throw new IllegalArgumentException("Le commentaire doit contenir entre 10 et 2000 caractères");
        }

        // TODO: Verify that participant attended the event (requires Inscription entity from teammate)
        // For now, we'll allow any participant to comment

        Commentaire commentaire = new Commentaire();
        commentaire.setEvenementId(evenementId);
        commentaire.setParticipant(participant);
        commentaire.setContenu(ValidationUtil.sanitizeForDisplay(contenu));

        Commentaire saved = commentaireDAO.save(commentaire);
        logger.info("Commentaire ajouté par participant ID {} sur événement ID {}", participantId, evenementId);
        return saved;
    }

    /**
     * Update a comment (within edit window)
     */
    public Commentaire updateComment(Long commentId, Long participantId, String newContent) {
        Optional<Commentaire> commentOpt = commentaireDAO.findById(commentId);
        if (!commentOpt.isPresent()) {
            throw new IllegalArgumentException("Commentaire non trouvé");
        }

        Commentaire comment = commentOpt.get();

        // Verify ownership
        if (!comment.getParticipant().getId().equals(participantId)) {
            throw new IllegalArgumentException("Vous ne pouvez modifier que vos propres commentaires");
        }

        // Check if editable (30 minutes window)
        if (!comment.isEditable()) {
            throw new IllegalArgumentException("La période de modification est expirée (30 minutes)");
        }

        // Validate content
        if (!ValidationUtil.isValidLength(newContent, 10, 2000)) {
            throw new IllegalArgumentException("Le commentaire doit contenir entre 10 et 2000 caractères");
        }

        comment.setContenu(ValidationUtil.sanitizeForDisplay(newContent));
        return commentaireDAO.update(comment);
    }

    /**
     * Delete a comment (soft delete)
     */
    public void deleteComment(Long commentId, Long userId) {
        Optional<Commentaire> commentOpt = commentaireDAO.findById(commentId);
        if (!commentOpt.isPresent()) {
            throw new IllegalArgumentException("Commentaire non trouvé");
        }

        Commentaire comment = commentOpt.get();

        // Only comment owner can delete
        if (!comment.getParticipant().getId().equals(userId)) {
            throw new IllegalArgumentException("Vous ne pouvez supprimer que vos propres commentaires");
        }

        commentaireDAO.softDelete(commentId);
        logger.info("Commentaire ID {} supprimé par utilisateur ID {}", commentId, userId);
    }

    /**
     * Get all comments for an event
     */
    public List<Commentaire> getCommentsByEvent(Long evenementId) {
        return commentaireDAO.findByEvenement(evenementId);
    }

    /**
     * Get comments by participant
     */
    public List<Commentaire> getCommentsByParticipant(Long participantId) {
        return commentaireDAO.findByParticipant(participantId);
    }

    /**
     * Moderate a comment (Admin only)
     */
    public void moderateComment(Long commentId, Long adminId, String reason) {
        commentaireDAO.moderate(commentId, adminId, reason);
        logger.info("Commentaire ID {} modéré par admin ID {}", commentId, adminId);
    }

    /**
     * Count comments for an event
     */
    public long countByEvent(Long evenementId) {
        return commentaireDAO.countByEvenement(evenementId);
    }

    /**
     * Get recent comments
     */
    public List<Commentaire> getRecentComments(int limit) {
        return commentaireDAO.findRecent(limit);
    }

    /**
     * Check if participant has commented on event
     */
    public boolean hasParticipantCommented(Long participantId, Long evenementId) {
        return commentaireDAO.hasParticipantCommented(participantId, evenementId);
    }
}
