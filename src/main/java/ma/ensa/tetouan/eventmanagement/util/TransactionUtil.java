package ma.ensa.tetouan.eventmanagement.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utilitaire pour gérer les transactions JPA de manière simplifiée.
 * Fournit des méthodes pour exécuter du code dans un contexte transactionnel.
 *
 * @author ENSA Tétouan
 */
public class TransactionUtil {

    private static final Logger logger = LoggerFactory.getLogger(TransactionUtil.class);

    /**
     * Constructeur privé pour empêcher l'instanciation
     */
    private TransactionUtil() {
        // Classe utilitaire
    }

    /**
     * Exécute une action dans un contexte transactionnel sans retourner de résultat.
     * Gère automatiquement l'ouverture, la validation et le rollback de la transaction.
     *
     * @param action L'action à exécuter avec l'EntityManager
     * @throws PersistenceException Si une erreur se produit pendant l'exécution
     *
     * @example
     * TransactionUtil.executeInTransaction(em -> {
     *     User user = new User("John", "john@example.com");
     *     em.persist(user);
     * });
     */
    public static void executeInTransaction(Consumer<EntityManager> action) {
        EntityManager em = null;
        EntityTransaction transaction = null;

        try {
            em = JPAUtil.getEntityManager();
            transaction = em.getTransaction();

            logger.debug("Début de la transaction");
            transaction.begin();

            // Exécuter l'action
            action.accept(em);

            // Commit de la transaction
            transaction.commit();
            logger.debug("Transaction validée avec succès");

        } catch (Exception e) {
            logger.error("Erreur lors de l'exécution de la transaction", e);

            // Rollback en cas d'erreur
            if (transaction != null && transaction.isActive()) {
                try {
                    logger.warn("Rollback de la transaction");
                    transaction.rollback();
                } catch (Exception rollbackException) {
                    logger.error("Erreur lors du rollback de la transaction", rollbackException);
                }
            }

            // Relancer l'exception
            throw new PersistenceException("Erreur lors de l'exécution de la transaction", e);

        } finally {
            // Fermer l'EntityManager
            JPAUtil.closeEntityManager(em);
        }
    }

    /**
     * Exécute une action dans un contexte transactionnel et retourne un résultat.
     * Gère automatiquement l'ouverture, la validation et le rollback de la transaction.
     *
     * @param <T> Le type du résultat
     * @param action La fonction à exécuter avec l'EntityManager
     * @return Le résultat de la fonction
     * @throws PersistenceException Si une erreur se produit pendant l'exécution
     *
     * @example
     * User user = TransactionUtil.executeInTransactionWithResult(em -> {
     *     return em.find(User.class, 1L);
     * });
     */
    public static <T> T executeInTransactionWithResult(Function<EntityManager, T> action) {
        EntityManager em = null;
        EntityTransaction transaction = null;

        try {
            em = JPAUtil.getEntityManager();
            transaction = em.getTransaction();

            logger.debug("Début de la transaction");
            transaction.begin();

            // Exécuter l'action et récupérer le résultat
            T result = action.apply(em);

            // Commit de la transaction
            transaction.commit();
            logger.debug("Transaction validée avec succès");

            return result;

        } catch (Exception e) {
            logger.error("Erreur lors de l'exécution de la transaction", e);

            // Rollback en cas d'erreur
            if (transaction != null && transaction.isActive()) {
                try {
                    logger.warn("Rollback de la transaction");
                    transaction.rollback();
                } catch (Exception rollbackException) {
                    logger.error("Erreur lors du rollback de la transaction", rollbackException);
                }
            }

            // Relancer l'exception
            throw new PersistenceException("Erreur lors de l'exécution de la transaction", e);

        } finally {
            // Fermer l'EntityManager
            JPAUtil.closeEntityManager(em);
        }
    }

    /**
     * Exécute une action en lecture seule (sans transaction).
     * Utile pour les requêtes SELECT simples qui ne modifient pas les données.
     *
     * @param <T> Le type du résultat
     * @param action La fonction à exécuter avec l'EntityManager
     * @return Le résultat de la fonction
     * @throws PersistenceException Si une erreur se produit pendant l'exécution
     *
     * @example
     * List<User> users = TransactionUtil.executeReadOnly(em -> {
     *     return em.createQuery("SELECT u FROM User u", User.class).getResultList();
     * });
     */
    public static <T> T executeReadOnly(Function<EntityManager, T> action) {
        EntityManager em = null;

        try {
            em = JPAUtil.getEntityManager();
            logger.debug("Exécution d'une opération en lecture seule");

            // Exécuter l'action
            return action.apply(em);

        } catch (Exception e) {
            logger.error("Erreur lors de l'exécution de l'opération en lecture seule", e);
            throw new PersistenceException("Erreur lors de l'exécution de l'opération en lecture seule", e);

        } finally {
            // Fermer l'EntityManager
            JPAUtil.closeEntityManager(em);
        }
    }

    /**
     * Exécute plusieurs actions dans une seule transaction.
     * Toutes les actions sont validées ensemble ou annulées ensemble.
     *
     * @param actions Les actions à exécuter
     * @throws PersistenceException Si une erreur se produit pendant l'exécution
     *
     * @example
     * TransactionUtil.executeInBatchTransaction(
     *     em -> em.persist(user1),
     *     em -> em.persist(user2),
     *     em -> em.persist(user3)
     * );
     */
    @SafeVarargs
    public static void executeInBatchTransaction(Consumer<EntityManager>... actions) {
        executeInTransaction(em -> {
            for (Consumer<EntityManager> action : actions) {
                action.accept(em);
            }
        });
    }

    /**
     * Vérifie si une transaction est active sur l'EntityManager donné
     *
     * @param em L'EntityManager à vérifier
     * @return true si une transaction est active, false sinon
     */
    public static boolean isTransactionActive(EntityManager em) {
        if (em == null) {
            return false;
        }
        EntityTransaction transaction = em.getTransaction();
        return transaction != null && transaction.isActive();
    }

    /**
     * Effectue un commit manuel de la transaction si elle est active
     *
     * @param em L'EntityManager
     * @throws PersistenceException Si le commit échoue
     */
    public static void commitTransaction(EntityManager em) {
        if (em != null && isTransactionActive(em)) {
            try {
                logger.debug("Commit manuel de la transaction");
                em.getTransaction().commit();
            } catch (Exception e) {
                logger.error("Erreur lors du commit de la transaction", e);
                throw new PersistenceException("Erreur lors du commit de la transaction", e);
            }
        }
    }

    /**
     * Effectue un rollback manuel de la transaction si elle est active
     *
     * @param em L'EntityManager
     */
    public static void rollbackTransaction(EntityManager em) {
        if (em != null && isTransactionActive(em)) {
            try {
                logger.warn("Rollback manuel de la transaction");
                em.getTransaction().rollback();
            } catch (Exception e) {
                logger.error("Erreur lors du rollback de la transaction", e);
            }
        }
    }
}
