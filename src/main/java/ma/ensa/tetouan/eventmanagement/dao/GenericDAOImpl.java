package ma.ensa.tetouan.eventmanagement.dao;

import ma.ensa.tetouan.eventmanagement.util.JPAUtil;
import ma.ensa.tetouan.eventmanagement.util.TransactionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation générique abstraite pour les opérations CRUD.
 * Les classes DAO concrètes doivent étendre cette classe.
 *
 * @param <T> Le type de l'entité
 * @param <ID> Le type de l'identifiant de l'entité
 *
 * @author ENSA Tétouan
 */
public abstract class GenericDAOImpl<T, ID extends Serializable> implements GenericDAO<T, ID> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final Class<T> entityClass;

    /**
     * Constructeur
     *
     * @param entityClass La classe de l'entité
     */
    protected GenericDAOImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Obtient un EntityManager
     * Les sous-classes peuvent surcharger cette méthode si nécessaire
     *
     * @return EntityManager
     */
    protected EntityManager getEntityManager() {
        return JPAUtil.getEntityManager();
    }

    @Override
    public T save(T entity) {
        logger.debug("Sauvegarde de l'entité: {}", entity);
        return TransactionUtil.executeInTransactionWithResult(em -> {
            em.persist(entity);
            logger.info("Entité {} sauvegardée avec succès", entityClass.getSimpleName());
            return entity;
        });
    }

    @Override
    public T update(T entity) {
        logger.debug("Mise à jour de l'entité: {}", entity);
        return TransactionUtil.executeInTransactionWithResult(em -> {
            T merged = em.merge(entity);
            logger.info("Entité {} mise à jour avec succès", entityClass.getSimpleName());
            return merged;
        });
    }

    @Override
    public void delete(T entity) {
        logger.debug("Suppression de l'entité: {}", entity);
        TransactionUtil.executeInTransaction(em -> {
            T managedEntity = em.merge(entity);
            em.remove(managedEntity);
            logger.info("Entité {} supprimée avec succès", entityClass.getSimpleName());
        });
    }

    @Override
    public void deleteById(ID id) {
        logger.debug("Suppression de l'entité {} par ID: {}", entityClass.getSimpleName(), id);
        TransactionUtil.executeInTransaction(em -> {
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
                logger.info("Entité {} avec ID {} supprimée avec succès", entityClass.getSimpleName(), id);
            } else {
                logger.warn("Entité {} avec ID {} non trouvée pour suppression", entityClass.getSimpleName(), id);
            }
        });
    }

    @Override
    public Optional<T> findById(ID id) {
        logger.debug("Recherche de l'entité {} par ID: {}", entityClass.getSimpleName(), id);
        return TransactionUtil.executeInTransactionWithResult(em -> {
            T entity = em.find(entityClass, id);
            if (entity != null) {
                logger.debug("Entité {} avec ID {} trouvée", entityClass.getSimpleName(), id);
            } else {
                logger.debug("Entité {} avec ID {} non trouvée", entityClass.getSimpleName(), id);
            }
            return Optional.ofNullable(entity);
        });
    }

    @Override
    public List<T> findAll() {
        logger.debug("Récupération de toutes les entités {}", entityClass.getSimpleName());
        return TransactionUtil.executeInTransactionWithResult(em -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            Root<T> root = cq.from(entityClass);
            cq.select(root);

            List<T> results = em.createQuery(cq).getResultList();
            logger.info("{} entité(s) {} récupérée(s)", results.size(), entityClass.getSimpleName());
            return results;
        });
    }

    @Override
    public List<T> findAll(int page, int pageSize) {
        if (page < 0) {
            throw new IllegalArgumentException("Le numéro de page doit être >= 0");
        }
        if (pageSize <= 0) {
            throw new IllegalArgumentException("La taille de page doit être > 0");
        }

        logger.debug("Récupération des entités {} - page: {}, taille: {}",
                    entityClass.getSimpleName(), page, pageSize);

        return TransactionUtil.executeInTransactionWithResult(em -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            Root<T> root = cq.from(entityClass);
            cq.select(root);

            TypedQuery<T> query = em.createQuery(cq);
            query.setFirstResult(page * pageSize);
            query.setMaxResults(pageSize);

            List<T> results = query.getResultList();
            logger.info("{} entité(s) {} récupérée(s) pour la page {}",
                       results.size(), entityClass.getSimpleName(), page);
            return results;
        });
    }

    @Override
    public long count() {
        logger.debug("Comptage des entités {}", entityClass.getSimpleName());
        return TransactionUtil.executeInTransactionWithResult(em -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<T> root = cq.from(entityClass);
            cq.select(cb.count(root));

            Long count = em.createQuery(cq).getSingleResult();
            logger.debug("Nombre total d'entités {}: {}", entityClass.getSimpleName(), count);
            return count;
        });
    }

    @Override
    public boolean existsById(ID id) {
        logger.debug("Vérification de l'existence de l'entité {} avec ID: {}",
                    entityClass.getSimpleName(), id);
        return findById(id).isPresent();
    }

    @Override
    public void refresh(T entity) {
        logger.debug("Rafraîchissement de l'entité: {}", entity);
        TransactionUtil.executeInTransaction(em -> {
            em.refresh(entity);
            logger.debug("Entité {} rafraîchie avec succès", entityClass.getSimpleName());
        });
    }

    @Override
    public void detach(T entity) {
        logger.debug("Détachement de l'entité: {}", entity);
        TransactionUtil.executeInTransaction(em -> {
            em.detach(entity);
            logger.debug("Entité {} détachée avec succès", entityClass.getSimpleName());
        });
    }

    @Override
    public void clear() {
        logger.debug("Nettoyage du contexte de persistance pour {}", entityClass.getSimpleName());
        TransactionUtil.executeInTransaction(em -> {
            em.clear();
            logger.debug("Contexte de persistance nettoyé");
        });
    }

    @Override
    public void flush() {
        logger.debug("Synchronisation du contexte de persistance pour {}", entityClass.getSimpleName());
        TransactionUtil.executeInTransaction(em -> {
            em.flush();
            logger.debug("Contexte de persistance synchronisé");
        });
    }

    /**
     * Méthode utilitaire pour exécuter une requête nommée sans paramètres
     *
     * @param queryName Le nom de la requête nommée
     * @return La liste des résultats
     */
    protected List<T> executeNamedQuery(String queryName) {
        logger.debug("Exécution de la requête nommée: {}", queryName);
        return TransactionUtil.executeInTransactionWithResult(em -> {
            List<T> results = em.createNamedQuery(queryName, entityClass).getResultList();
            logger.debug("Requête {} a retourné {} résultat(s)", queryName, results.size());
            return results;
        });
    }

    /**
     * Méthode utilitaire pour exécuter une requête nommée avec un seul résultat
     *
     * @param queryName Le nom de la requête nommée
     * @return Optional contenant le résultat ou empty si aucun résultat
     */
    protected Optional<T> executeNamedQuerySingleResult(String queryName) {
        logger.debug("Exécution de la requête nommée (résultat unique): {}", queryName);
        return TransactionUtil.executeInTransactionWithResult(em -> {
            try {
                T result = em.createNamedQuery(queryName, entityClass).getSingleResult();
                return Optional.of(result);
            } catch (javax.persistence.NoResultException e) {
                logger.debug("Aucun résultat pour la requête: {}", queryName);
                return Optional.empty();
            }
        });
    }

    /**
     * Méthode utilitaire pour créer une requête typée
     *
     * @param jpql La requête JPQL
     * @return TypedQuery
     */
    protected TypedQuery<T> createTypedQuery(EntityManager em, String jpql) {
        return em.createQuery(jpql, entityClass);
    }

    /**
     * Obtient la classe de l'entité
     *
     * @return La classe de l'entité
     */
    protected Class<T> getEntityClass() {
        return entityClass;
    }
}
