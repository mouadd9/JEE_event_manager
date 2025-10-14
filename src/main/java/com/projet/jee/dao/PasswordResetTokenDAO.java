package com.projet.jee.dao;

import com.projet.jee.model.PasswordResetToken;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * DAO for PasswordResetToken entity.
 * Manages password reset tokens for user authentication.
 */
public class PasswordResetTokenDAO extends GenericDAO<PasswordResetToken, Long> {

    public PasswordResetTokenDAO() {
        super(PasswordResetToken.class);
    }

    /**
     * Find a token by its value
     */
    public Optional<PasswordResetToken> findByToken(String token) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<PasswordResetToken> query = em.createQuery(
                    "SELECT t FROM PasswordResetToken t WHERE t.token = :token",
                    PasswordResetToken.class);
            query.setParameter("token", token);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    /**
     * Find active token by user ID
     */
    public Optional<PasswordResetToken> findActiveByUserId(Long userId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<PasswordResetToken> query = em.createQuery(
                    "SELECT t FROM PasswordResetToken t WHERE t.utilisateurId = :userId " +
                            "AND t.estUtilise = false AND t.dateExpiration > :now " +
                            "ORDER BY t.dateCreation DESC",
                    PasswordResetToken.class);
            query.setParameter("userId", userId);
            query.setParameter("now", LocalDateTime.now());
            query.setMaxResults(1);
            return query.getResultList().stream().findFirst();
        } finally {
            em.close();
        }
    }

    /**
     * Invalidate all tokens for a user
     */
    public void invalidateUserTokens(Long userId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery(
                            "UPDATE PasswordResetToken t SET t.estUtilise = true " +
                                    "WHERE t.utilisateurId = :userId AND t.estUtilise = false")
                    .setParameter("userId", userId)
                    .executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur lors de l'invalidation des tokens", e);
        } finally {
            em.close();
        }
    }

    /**
     * Delete expired tokens (cleanup task)
     */
    public int deleteExpiredTokens() {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            int deleted = em.createQuery(
                            "DELETE FROM PasswordResetToken t WHERE t.dateExpiration < :now")
                    .setParameter("now", LocalDateTime.now())
                    .executeUpdate();
            em.getTransaction().commit();
            return deleted;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur lors de la suppression des tokens expirés", e);
        } finally {
            em.close();
        }
    }

    /**
     * Validate and retrieve a valid token
     */
    public Optional<PasswordResetToken> findValidToken(String token) {
        return findByToken(token)
                .filter(PasswordResetToken::isValid);
    }

    /**
     * Mark token as used
     */
    public void markAsUsed(String token) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            PasswordResetToken resetToken = em.createQuery(
                            "SELECT t FROM PasswordResetToken t WHERE t.token = :token",
                            PasswordResetToken.class)
                    .setParameter("token", token)
                    .getSingleResult();
            resetToken.marquerUtilise();
            em.merge(resetToken);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur lors du marquage du token comme utilisé", e);
        } finally {
            em.close();
        }
    }
}
