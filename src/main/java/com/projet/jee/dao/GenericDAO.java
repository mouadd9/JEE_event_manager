package com.projet.jee.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.Optional;

/**
 * Generic DAO providing common CRUD operations for all entities.
 * Implements the DAO pattern with JPA/Hibernate.
 *
 * @param <T> The entity type
 * @param <ID> The primary key type
 */
public abstract class GenericDAO<T, ID> {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("EventManagementPU");

    protected Class<T> entityClass;

    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Get an EntityManager instance
     */
    protected EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * Persist a new entity
     */
    public T save(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return entity;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur lors de la sauvegarde: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    /**
     * Update an existing entity
     */
    public T update(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            T merged = em.merge(entity);
            em.getTransaction().commit();
            return merged;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur lors de la mise à jour: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    /**
     * Delete an entity by ID
     */
    public void delete(ID id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur lors de la suppression: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    /**
     * Delete an entity object
     */
    public void deleteEntity(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            if (!em.contains(entity)) {
                entity = em.merge(entity);
            }
            em.remove(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur lors de la suppression: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    /**
     * Find entity by ID
     */
    public Optional<T> findById(ID id) {
        EntityManager em = getEntityManager();
        try {
            T entity = em.find(entityClass, id);
            return Optional.ofNullable(entity);
        } finally {
            em.close();
        }
    }

    /**
     * Find all entities
     */
    public List<T> findAll() {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            cq.from(entityClass);
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Find all entities with pagination
     */
    public List<T> findAll(int offset, int limit) {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            cq.from(entityClass);
            TypedQuery<T> query = em.createQuery(cq);
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Count all entities
     */
    public long count() {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            cq.select(cb.count(cq.from(entityClass)));
            return em.createQuery(cq).getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Check if entity exists by ID
     */
    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }

    /**
     * Execute a named query
     */
    protected List<T> executeNamedQuery(String queryName, Object... params) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<T> query = em.createNamedQuery(queryName, entityClass);
            for (int i = 0; i < params.length; i += 2) {
                query.setParameter((String) params[i], params[i + 1]);
            }
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Shutdown EntityManagerFactory (call on application shutdown)
     */
    public static void closeEntityManagerFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
