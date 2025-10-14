package com.projet.jee.dao;

import com.projet.jee.model.*;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * DAO for Utilisateur entity and its subclasses.
 * Provides methods for authentication, registration, and user management.
 */
public class UtilisateurDAO extends GenericDAO<Utilisateur, Long> {

    public UtilisateurDAO() {
        super(Utilisateur.class);
    }

    /**
     * Find a user by email address
     */
    public Optional<Utilisateur> findByEmail(String email) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Utilisateur> query = em.createQuery(
                    "SELECT u FROM Utilisateur u WHERE u.email = :email", Utilisateur.class);
            query.setParameter("email", email);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    /**
     * Check if an email already exists
     */
    public boolean emailExists(String email) {
        EntityManager em = getEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(u) FROM Utilisateur u WHERE u.email = :email", Long.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    /**
     * Find all participants
     */
    public List<Participant> findAllParticipants() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Participant> query = em.createQuery(
                    "SELECT p FROM Participant p", Participant.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Find all organizers
     */
    public List<Organisateur> findAllOrganisateurs() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Organisateur> query = em.createQuery(
                    "SELECT o FROM Organisateur o", Organisateur.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Find all administrators
     */
    public List<Administrateur> findAllAdministrateurs() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Administrateur> query = em.createQuery(
                    "SELECT a FROM Administrateur a", Administrateur.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Find all active users
     */
    public List<Utilisateur> findAllActive() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Utilisateur> query = em.createQuery(
                    "SELECT u FROM Utilisateur u WHERE u.estActif = true", Utilisateur.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Find users by role type
     */
    public List<Utilisateur> findByUserType(String userType) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Utilisateur> query = em.createQuery(
                    "SELECT u FROM Utilisateur u WHERE u.userType = :userType", Utilisateur.class);
            query.setParameter("userType", userType);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Search users by name or email
     */
    public List<Utilisateur> searchUsers(String keyword) {
        EntityManager em = getEntityManager();
        try {
            String searchPattern = "%" + keyword.toLowerCase() + "%";
            TypedQuery<Utilisateur> query = em.createQuery(
                    "SELECT u FROM Utilisateur u WHERE " +
                            "LOWER(u.nom) LIKE :keyword OR " +
                            "LOWER(u.prenom) LIKE :keyword OR " +
                            "LOWER(u.email) LIKE :keyword",
                    Utilisateur.class);
            query.setParameter("keyword", searchPattern);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Activate a user account
     */
    public void activateUser(Long userId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Utilisateur user = em.find(Utilisateur.class, userId);
            if (user != null) {
                user.setEstActif(true);
                em.merge(user);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur lors de l'activation de l'utilisateur", e);
        } finally {
            em.close();
        }
    }

    /**
     * Deactivate a user account
     */
    public void deactivateUser(Long userId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Utilisateur user = em.find(Utilisateur.class, userId);
            if (user != null) {
                user.setEstActif(false);
                em.merge(user);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur lors de la désactivation de l'utilisateur", e);
        } finally {
            em.close();
        }
    }

    /**
     * Upgrade participant to organizer
     * This method transforms a Participant into an Organisateur using native SQL
     * to properly handle JOINED inheritance strategy
     */
    public Organisateur upgradeToOrganisateur(Long participantId, String nomOrganisation,
                                               String descriptionOrganisation) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            // Step 1: Find the participant to get all data
            Participant participant = em.find(Participant.class, participantId);
            if (participant == null) {
                throw new RuntimeException("Participant non trouvé");
            }

            Long userId = participant.getId();

            // Step 2: Update user_type in utilisateur table
            em.createNativeQuery("UPDATE utilisateur SET user_type = 'organisateur' WHERE id_utilisateur = ?")
                .setParameter(1, userId)
                .executeUpdate();

            // Step 3: Delete from participant table
            em.createNativeQuery("DELETE FROM participant WHERE id_utilisateur = ?")
                .setParameter(1, userId)
                .executeUpdate();

            // Step 4: Insert into organisateur table
            em.createNativeQuery(
                "INSERT INTO organisateur (id_utilisateur, nom_organisation, description_organisation, " +
                "est_verifie, nombre_evenements_organises) VALUES (?, ?, ?, ?, ?)")
                .setParameter(1, userId)
                .setParameter(2, nomOrganisation)
                .setParameter(3, descriptionOrganisation)
                .setParameter(4, true)
                .setParameter(5, 0)
                .executeUpdate();

            // Step 5: Reload as Organisateur (before closing EntityManager)
            Organisateur organisateur = em.find(Organisateur.class, userId);

            em.getTransaction().commit();

            return organisateur;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur lors de la mise à niveau vers organisateur: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    /**
     * Count users by type
     */
    public long countByUserType(String userType) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT COUNT(u) FROM Utilisateur u WHERE u.userType = :userType", Long.class)
                    .setParameter("userType", userType)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }
}
