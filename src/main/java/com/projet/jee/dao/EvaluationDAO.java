package com.projet.jee.dao;

import com.projet.jee.model.Evaluation;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * DAO for Evaluation entity.
 * Provides methods for managing event ratings.
 */
public class EvaluationDAO extends GenericDAO<Evaluation, Long> {

    public EvaluationDAO() {
        super(Evaluation.class);
    }

    /**
     * Find all evaluations for a specific event
     */
    public List<Evaluation> findByEvenement(Long evenementId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Evaluation> query = em.createQuery(
                    "SELECT e FROM Evaluation e WHERE e.evenementId = :evenementId " +
                            "ORDER BY e.dateCreation DESC",
                    Evaluation.class);
            query.setParameter("evenementId", evenementId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Find evaluation by participant and event
     */
    public Optional<Evaluation> findByParticipantAndEvenement(Long participantId, Long evenementId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Evaluation> query = em.createQuery(
                    "SELECT e FROM Evaluation e WHERE e.participant.id = :participantId " +
                            "AND e.evenementId = :evenementId",
                    Evaluation.class);
            query.setParameter("participantId", participantId);
            query.setParameter("evenementId", evenementId);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    /**
     * Find all evaluations by a participant
     */
    public List<Evaluation> findByParticipant(Long participantId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Evaluation> query = em.createQuery(
                    "SELECT e FROM Evaluation e WHERE e.participant.id = :participantId " +
                            "ORDER BY e.dateCreation DESC",
                    Evaluation.class);
            query.setParameter("participantId", participantId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Calculate average rating for an event
     */
    public Double calculateAverageRating(Long evenementId) {
        EntityManager em = getEntityManager();
        try {
            Double avg = em.createQuery(
                            "SELECT AVG(e.note) FROM Evaluation e WHERE e.evenementId = :evenementId",
                            Double.class)
                    .setParameter("evenementId", evenementId)
                    .getSingleResult();
            return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
        } finally {
            em.close();
        }
    }

    /**
     * Count evaluations for an event
     */
    public long countByEvenement(Long evenementId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT COUNT(e) FROM Evaluation e WHERE e.evenementId = :evenementId", Long.class)
                    .setParameter("evenementId", evenementId)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Get rating distribution for an event (count by stars)
     */
    public long[] getRatingDistribution(Long evenementId) {
        EntityManager em = getEntityManager();
        try {
            long[] distribution = new long[6]; // Index 0 unused, 1-5 for stars
            for (int i = 1; i <= 5; i++) {
                Long count = em.createQuery(
                                "SELECT COUNT(e) FROM Evaluation e WHERE e.evenementId = :evenementId " +
                                        "AND e.note = :note", Long.class)
                        .setParameter("evenementId", evenementId)
                        .setParameter("note", i)
                        .getSingleResult();
                distribution[i] = count;
            }
            return distribution;
        } finally {
            em.close();
        }
    }

    /**
     * Check if a participant has already rated an event
     */
    public boolean hasParticipantRated(Long participantId, Long evenementId) {
        EntityManager em = getEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(e) FROM Evaluation e WHERE e.participant.id = :participantId " +
                                    "AND e.evenementId = :evenementId", Long.class)
                    .setParameter("participantId", participantId)
                    .setParameter("evenementId", evenementId)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    /**
     * Find recent evaluations with pagination
     */
    public List<Evaluation> findRecent(int limit) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Evaluation> query = em.createQuery(
                    "SELECT e FROM Evaluation e ORDER BY e.dateCreation DESC",
                    Evaluation.class);
            query.setMaxResults(limit);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Find evaluations with comments
     */
    public List<Evaluation> findWithComments(Long evenementId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Evaluation> query = em.createQuery(
                    "SELECT e FROM Evaluation e WHERE e.evenementId = :evenementId " +
                            "AND e.commentaireNote IS NOT NULL AND e.commentaireNote != '' " +
                            "ORDER BY e.dateCreation DESC",
                    Evaluation.class);
            query.setParameter("evenementId", evenementId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Count total evaluations by a participant
     */
    public long countByParticipant(Long participantId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT COUNT(e) FROM Evaluation e WHERE e.participant.id = :participantId", Long.class)
                    .setParameter("participantId", participantId)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }
}
