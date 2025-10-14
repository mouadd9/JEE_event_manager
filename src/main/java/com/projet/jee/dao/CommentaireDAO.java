package com.projet.jee.dao;

import com.projet.jee.model.Commentaire;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * DAO for Commentaire entity.
 * Provides methods for managing comments on events.
 */
public class CommentaireDAO extends GenericDAO<Commentaire, Long> {

    public CommentaireDAO() {
        super(Commentaire.class);
    }

    /**
     * Find all comments for a specific event
     */
    public List<Commentaire> findByEvenement(Long evenementId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Commentaire> query = em.createQuery(
                    "SELECT c FROM Commentaire c WHERE c.evenementId = :evenementId " +
                            "AND c.estSupprime = false ORDER BY c.dateCreation DESC",
                    Commentaire.class);
            query.setParameter("evenementId", evenementId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Find all comments by a specific participant
     */
    public List<Commentaire> findByParticipant(Long participantId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Commentaire> query = em.createQuery(
                    "SELECT c FROM Commentaire c WHERE c.participant.id = :participantId " +
                            "AND c.estSupprime = false ORDER BY c.dateCreation DESC",
                    Commentaire.class);
            query.setParameter("participantId", participantId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Find all non-moderated comments for an event
     */
    public List<Commentaire> findNonModeratedByEvenement(Long evenementId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Commentaire> query = em.createQuery(
                    "SELECT c FROM Commentaire c WHERE c.evenementId = :evenementId " +
                            "AND c.estModere = false AND c.estSupprime = false " +
                            "ORDER BY c.dateCreation DESC",
                    Commentaire.class);
            query.setParameter("evenementId", evenementId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Find all moderated comments (for admin review)
     */
    public List<Commentaire> findAllModerated() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Commentaire> query = em.createQuery(
                    "SELECT c FROM Commentaire c WHERE c.estModere = true " +
                            "ORDER BY c.dateModification DESC",
                    Commentaire.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Count comments for an event
     */
    public long countByEvenement(Long evenementId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT COUNT(c) FROM Commentaire c WHERE c.evenementId = :evenementId " +
                                    "AND c.estSupprime = false", Long.class)
                    .setParameter("evenementId", evenementId)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Count comments by participant
     */
    public long countByParticipant(Long participantId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT COUNT(c) FROM Commentaire c WHERE c.participant.id = :participantId " +
                                    "AND c.estSupprime = false", Long.class)
                    .setParameter("participantId", participantId)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Soft delete a comment
     */
    public void softDelete(Long commentId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Commentaire comment = em.find(Commentaire.class, commentId);
            if (comment != null) {
                comment.supprimerSoftDelete();
                em.merge(comment);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur lors de la suppression du commentaire", e);
        } finally {
            em.close();
        }
    }

    /**
     * Moderate a comment
     */
    public void moderate(Long commentId, Long moderatorId, String reason) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Commentaire comment = em.find(Commentaire.class, commentId);
            if (comment != null) {
                comment.moderer(moderatorId, reason);
                em.merge(comment);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur lors de la modération du commentaire", e);
        } finally {
            em.close();
        }
    }

    /**
     * Find recent comments with pagination
     */
    public List<Commentaire> findRecent(int limit) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Commentaire> query = em.createQuery(
                    "SELECT c FROM Commentaire c WHERE c.estSupprime = false " +
                            "ORDER BY c.dateCreation DESC",
                    Commentaire.class);
            query.setMaxResults(limit);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Check if a participant has already commented on an event
     */
    public boolean hasParticipantCommented(Long participantId, Long evenementId) {
        EntityManager em = getEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(c) FROM Commentaire c WHERE c.participant.id = :participantId " +
                                    "AND c.evenementId = :evenementId AND c.estSupprime = false", Long.class)
                    .setParameter("participantId", participantId)
                    .setParameter("evenementId", evenementId)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }
}
