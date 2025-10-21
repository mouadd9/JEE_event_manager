package ma.ensa.tetouan.eventmanagement.dao;

import ma.ensa.tetouan.eventmanagement.model.StatutUtilisateur;
import ma.ensa.tetouan.eventmanagement.model.User;
import ma.ensa.tetouan.eventmanagement.util.TransactionUtil;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation du DAO pour l'entité User.
 *
 * @author ENSA Tétouan
 */
public class UserDAOImpl extends GenericDAOImpl<User, Long> implements UserDAO {

    public UserDAOImpl() {
        super(User.class);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        logger.debug("Recherche d'un utilisateur par email: {}", email);

        return TransactionUtil.executeInTransactionWithResult(em -> {
            try {
                TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.email = :email", User.class);
                query.setParameter("email", email);

                User user = query.getSingleResult();
                logger.info("Utilisateur trouvé avec l'email: {}", email);
                return Optional.of(user);
            } catch (NoResultException e) {
                logger.debug("Aucun utilisateur trouvé avec l'email: {}", email);
                return Optional.empty();
            }
        });
    }

    @Override
    public List<User> findByStatut(StatutUtilisateur statut) {
        logger.debug("Recherche des utilisateurs par statut: {}", statut);

        return TransactionUtil.executeInTransactionWithResult(em -> {
            TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.statut = :statut ORDER BY u.dateInscription DESC",
                User.class);
            query.setParameter("statut", statut);

            List<User> users = query.getResultList();
            logger.info("{} utilisateur(s) trouvé(s) avec le statut: {}", users.size(), statut);
            return users;
        });
    }

    @Override
    public Optional<User> authenticate(String email, String motDePasse) {
        logger.debug("Tentative d'authentification pour l'email: {}", email);

        return TransactionUtil.executeInTransactionWithResult(em -> {
            try {
                TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.email = :email AND u.motDePasse = :motDePasse AND u.statut = :statut",
                    User.class);
                query.setParameter("email", email);
                query.setParameter("motDePasse", motDePasse);
                query.setParameter("statut", StatutUtilisateur.ACTIF);

                User user = query.getSingleResult();
                logger.info("Authentification réussie pour l'utilisateur: {}", email);
                return Optional.of(user);
            } catch (NoResultException e) {
                logger.warn("Échec de l'authentification pour l'email: {}", email);
                return Optional.empty();
            }
        });
    }

    @Override
    public boolean existsByEmail(String email) {
        logger.debug("Vérification de l'existence de l'email: {}", email);

        return TransactionUtil.executeInTransactionWithResult(em -> {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class);
            query.setParameter("email", email);

            Long count = query.getSingleResult();
            boolean exists = count > 0;
            logger.debug("Email {} existe: {}", email, exists);
            return exists;
        });
    }

    @Override
    public List<User> findByRole(String role) {
        logger.debug("Recherche des utilisateurs par rôle: {}", role);

        return TransactionUtil.executeInTransactionWithResult(em -> {
            TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE TYPE(u) = :role ORDER BY u.dateInscription DESC",
                User.class);

            // Convertir le rôle en nom de classe
            String className = getClassNameForRole(role);
            query.setParameter("role", className);

            List<User> users = query.getResultList();
            logger.info("{} utilisateur(s) trouvé(s) avec le rôle: {}", users.size(), role);
            return users;
        });
    }

    @Override
    public void updateLastLogin(Long userId) {
        logger.debug("Mise à jour de la dernière connexion pour l'utilisateur ID: {}", userId);

        TransactionUtil.executeInTransaction(em -> {
            User user = em.find(User.class, userId);
            if (user != null) {
                user.setDerniereConnexion(LocalDateTime.now());
                em.merge(user);
                logger.info("Dernière connexion mise à jour pour l'utilisateur ID: {}", userId);
            } else {
                logger.warn("Utilisateur ID {} non trouvé pour mise à jour de connexion", userId);
            }
        });
    }

    @Override
    public void changeStatut(Long userId, StatutUtilisateur statut) {
        logger.debug("Changement de statut pour l'utilisateur ID {} vers: {}", userId, statut);

        TransactionUtil.executeInTransaction(em -> {
            User user = em.find(User.class, userId);
            if (user != null) {
                user.setStatut(statut);
                em.merge(user);
                logger.info("Statut changé pour l'utilisateur ID {} vers: {}", userId, statut);
            } else {
                logger.warn("Utilisateur ID {} non trouvé pour changement de statut", userId);
            }
        });
    }

    @Override
    public long countByStatut(StatutUtilisateur statut) {
        logger.debug("Comptage des utilisateurs par statut: {}", statut);

        return TransactionUtil.executeInTransactionWithResult(em -> {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.statut = :statut", Long.class);
            query.setParameter("statut", statut);

            Long count = query.getSingleResult();
            logger.debug("{} utilisateur(s) avec le statut: {}", count, statut);
            return count;
        });
    }

    /**
     * Convertit un nom de rôle en nom de classe pour la requête TYPE()
     *
     * @param role Le nom du rôle
     * @return Le nom de la classe
     */
    private String getClassNameForRole(String role) {
        switch (role.toUpperCase()) {
            case "ORGANISATEUR":
                return "Organisateur";
            case "PARTICIPANT":
                return "Participant";
            case "ADMINISTRATEUR":
                return "Administrateur";
            default:
                return role;
        }
    }
}
