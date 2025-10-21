package ma.ensa.tetouan.eventmanagement.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

/**
 * Utilitaire pour gérer l'EntityManagerFactory et fournir des instances d'EntityManager.
 * Utilise le pattern Singleton pour garantir une seule instance d'EntityManagerFactory.
 *
 * @author ENSA Tétouan
 */
public class JPAUtil {

    private static final Logger logger = LoggerFactory.getLogger(JPAUtil.class);
    private static final String PERSISTENCE_UNIT_NAME = "EventManagementPU";

    private static EntityManagerFactory entityManagerFactory;
    private static volatile JPAUtil instance;

    /**
     * Constructeur privé pour empêcher l'instanciation directe
     */
    private JPAUtil() {
        // Constructeur privé
    }

    /**
     * Obtient l'instance singleton de JPAUtil
     *
     * @return Instance unique de JPAUtil
     */
    public static JPAUtil getInstance() {
        if (instance == null) {
            synchronized (JPAUtil.class) {
                if (instance == null) {
                    instance = new JPAUtil();
                }
            }
        }
        return instance;
    }

    /**
     * Initialise l'EntityManagerFactory
     * Cette méthode est thread-safe et garantit qu'une seule instance est créée.
     *
     * @throws PersistenceException Si l'initialisation échoue
     */
    private static synchronized void initEntityManagerFactory() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            try {
                logger.info("Initialisation de l'EntityManagerFactory avec l'unité de persistance: {}",
                           PERSISTENCE_UNIT_NAME);
                entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
                logger.info("EntityManagerFactory initialisé avec succès");

                // Ajouter un shutdown hook pour fermer proprement l'EMF
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    logger.info("Shutdown hook: Fermeture de l'EntityManagerFactory");
                    shutdown();
                }));

            } catch (PersistenceException e) {
                logger.error("Erreur lors de l'initialisation de l'EntityManagerFactory", e);
                throw new PersistenceException("Impossible d'initialiser l'EntityManagerFactory", e);
            }
        }
    }

    /**
     * Obtient l'EntityManagerFactory
     * Initialise l'EMF si ce n'est pas déjà fait.
     *
     * @return L'EntityManagerFactory
     * @throws PersistenceException Si l'EntityManagerFactory ne peut pas être créé
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            initEntityManagerFactory();
        }
        return entityManagerFactory;
    }

    /**
     * Crée et retourne une nouvelle instance d'EntityManager
     * IMPORTANT: L'appelant est responsable de fermer l'EntityManager.
     *
     * @return Une nouvelle instance d'EntityManager
     * @throws PersistenceException Si l'EntityManager ne peut pas être créé
     */
    public static EntityManager getEntityManager() {
        try {
            EntityManager em = getEntityManagerFactory().createEntityManager();
            logger.debug("EntityManager créé avec succès");
            return em;
        } catch (PersistenceException e) {
            logger.error("Erreur lors de la création de l'EntityManager", e);
            throw new PersistenceException("Impossible de créer l'EntityManager", e);
        }
    }

    /**
     * Ferme un EntityManager de manière sécurisée
     *
     * @param entityManager L'EntityManager à fermer
     */
    public static void closeEntityManager(EntityManager entityManager) {
        if (entityManager != null && entityManager.isOpen()) {
            try {
                entityManager.close();
                logger.debug("EntityManager fermé avec succès");
            } catch (Exception e) {
                logger.error("Erreur lors de la fermeture de l'EntityManager", e);
            }
        }
    }

    /**
     * Ferme l'EntityManagerFactory
     * Cette méthode doit être appelée lors de l'arrêt de l'application.
     */
    public static synchronized void shutdown() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            try {
                logger.info("Fermeture de l'EntityManagerFactory");
                entityManagerFactory.close();
                logger.info("EntityManagerFactory fermé avec succès");
            } catch (Exception e) {
                logger.error("Erreur lors de la fermeture de l'EntityManagerFactory", e);
            } finally {
                entityManagerFactory = null;
            }
        }
    }

    /**
     * Vérifie si l'EntityManagerFactory est initialisé et ouvert
     *
     * @return true si l'EMF est ouvert, false sinon
     */
    public static boolean isOpen() {
        return entityManagerFactory != null && entityManagerFactory.isOpen();
    }

    /**
     * Teste la connexion à la base de données
     *
     * @return true si la connexion est réussie, false sinon
     */
    public static boolean testConnection() {
        EntityManager em = null;
        try {
            em = getEntityManager();
            // Tenter une requête simple pour vérifier la connexion
            em.createNativeQuery("SELECT 1").getSingleResult();
            logger.info("Test de connexion à la base de données réussi");
            return true;
        } catch (Exception e) {
            logger.error("Test de connexion à la base de données échoué", e);
            return false;
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * Réinitialise l'EntityManagerFactory (utile pour les tests)
     * ATTENTION: Cette méthode ferme toutes les connexions existantes.
     */
    public static synchronized void reset() {
        logger.warn("Réinitialisation de l'EntityManagerFactory");
        shutdown();
        entityManagerFactory = null;
    }
}
