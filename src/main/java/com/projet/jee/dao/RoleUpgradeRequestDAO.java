package com.projet.jee.dao;

import com.projet.jee.model.RoleUpgradeRequest;
import com.projet.jee.model.RoleUpgradeRequest.StatutDemande;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * DAO for RoleUpgradeRequest entity.
 * Manages requests from participants to upgrade to organizer role.
 */
public class RoleUpgradeRequestDAO extends GenericDAO<RoleUpgradeRequest, Long> {

    public RoleUpgradeRequestDAO() {
        super(RoleUpgradeRequest.class);
    }

    /**
     * Find all pending requests
     */
    public List<RoleUpgradeRequest> findPendingRequests() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<RoleUpgradeRequest> query = em.createQuery(
                    "SELECT r FROM RoleUpgradeRequest r WHERE r.statut = :statut " +
                            "ORDER BY r.dateDemande ASC",
                    RoleUpgradeRequest.class);
            query.setParameter("statut", StatutDemande.EN_ATTENTE);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Find requests by participant
     */
    public List<RoleUpgradeRequest> findByParticipant(Long participantId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<RoleUpgradeRequest> query = em.createQuery(
                    "SELECT r FROM RoleUpgradeRequest r WHERE r.participantId = :participantId " +
                            "ORDER BY r.dateDemande DESC",
                    RoleUpgradeRequest.class);
            query.setParameter("participantId", participantId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Find pending request by participant
     */
    public Optional<RoleUpgradeRequest> findPendingByParticipant(Long participantId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<RoleUpgradeRequest> query = em.createQuery(
                    "SELECT r FROM RoleUpgradeRequest r WHERE r.participantId = :participantId " +
                            "AND r.statut = :statut",
                    RoleUpgradeRequest.class);
            query.setParameter("participantId", participantId);
            query.setParameter("statut", StatutDemande.EN_ATTENTE);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    /**
     * Check if participant has a pending request
     */
    public boolean hasPendingRequest(Long participantId) {
        return findPendingByParticipant(participantId).isPresent();
    }

    /**
     * Find all approved requests
     */
    public List<RoleUpgradeRequest> findApprovedRequests() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<RoleUpgradeRequest> query = em.createQuery(
                    "SELECT r FROM RoleUpgradeRequest r WHERE r.statut = :statut " +
                            "ORDER BY r.dateTraitement DESC",
                    RoleUpgradeRequest.class);
            query.setParameter("statut", StatutDemande.APPROUVEE);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Find all rejected requests
     */
    public List<RoleUpgradeRequest> findRejectedRequests() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<RoleUpgradeRequest> query = em.createQuery(
                    "SELECT r FROM RoleUpgradeRequest r WHERE r.statut = :statut " +
                            "ORDER BY r.dateTraitement DESC",
                    RoleUpgradeRequest.class);
            query.setParameter("statut", StatutDemande.REFUSEE);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Count pending requests
     */
    public long countPendingRequests() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT COUNT(r) FROM RoleUpgradeRequest r WHERE r.statut = :statut", Long.class)
                    .setParameter("statut", StatutDemande.EN_ATTENTE)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Find requests by status
     */
    public List<RoleUpgradeRequest> findByStatus(StatutDemande statut) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<RoleUpgradeRequest> query = em.createQuery(
                    "SELECT r FROM RoleUpgradeRequest r WHERE r.statut = :statut " +
                            "ORDER BY r.dateDemande DESC",
                    RoleUpgradeRequest.class);
            query.setParameter("statut", statut);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
