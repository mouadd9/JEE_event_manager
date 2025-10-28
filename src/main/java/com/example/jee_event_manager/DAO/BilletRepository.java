package com.example.jee_event_manager.DAO;

import com.example.jee_event_manager.model.Billet;
import com.example.jee_event_manager.model.StatutBillet;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@ApplicationScoped
public class BilletRepository {
    
    private static final Logger logger = Logger.getLogger(BilletRepository.class.getName());
    
    @Inject
    private EntityManager entityManager;
    
    public Billet save(Billet billet) {
        try {
            entityManager.getTransaction().begin();
            
            if (billet.getId() == null) {
                entityManager.persist(billet);
                logger.info("Billet persisted: " + billet.getNumeroBillet());
            } else {
                billet = entityManager.merge(billet);
                logger.info("Billet merged: " + billet.getNumeroBillet());
            }
            
            entityManager.getTransaction().commit();
            return billet;
            
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            logger.severe("Error saving billet: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to save Billet: " + e.getMessage(), e);
        }
    }
    
    public Optional<Billet> findById(Long id) {
        Billet billet = entityManager.find(Billet.class, id);
        return Optional.ofNullable(billet);
    }
    
    public Optional<Billet> findByNumeroBillet(String numeroBillet) {
        String jpql = "SELECT b FROM Billet b WHERE b.numeroBillet = :numeroBillet";
        TypedQuery<Billet> query = entityManager.createQuery(jpql, Billet.class);
        query.setParameter("numeroBillet", numeroBillet);
        
        try {
            Billet billet = query.getSingleResult();
            return Optional.of(billet);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    public List<Billet> findByInscriptionId(Long inscriptionId) {
        if (entityManager == null) {
            logger.severe("EntityManager is null in findByInscriptionId!");
            throw new IllegalStateException("EntityManager not initialized");
        }
        
        String jpql = "SELECT b FROM Billet b WHERE b.inscription.id = :inscriptionId";
        TypedQuery<Billet> query = entityManager.createQuery(jpql, Billet.class);
        query.setParameter("inscriptionId", inscriptionId);
        List<Billet> result = query.getResultList();
        logger.info("Found " + result.size() + " billets for inscription ID: " + inscriptionId);
        return result;
    }
    
    public List<Billet> findByParticipantId(Long participantId) {
        String jpql = """
            SELECT b FROM Billet b 
            JOIN b.inscription i 
            WHERE i.participant.id = :participantId
            ORDER BY b.dateGeneration DESC
            """;
        TypedQuery<Billet> query = entityManager.createQuery(jpql, Billet.class);
        query.setParameter("participantId", participantId);
        return query.getResultList();
    }
    
    public List<Billet> findByEvenementId(Long evenementId) {
        String jpql = """
            SELECT b FROM Billet b 
            JOIN b.inscription i 
            WHERE i.evenement.id = :evenementId
            ORDER BY b.dateGeneration DESC
            """;
        TypedQuery<Billet> query = entityManager.createQuery(jpql, Billet.class);
        query.setParameter("evenementId", evenementId);
        return query.getResultList();
    }
    
    public List<Billet> findByStatut(StatutBillet statut) {
        String jpql = "SELECT b FROM Billet b WHERE b.statut = :statut ORDER BY b.dateGeneration DESC";
        TypedQuery<Billet> query = entityManager.createQuery(jpql, Billet.class);
        query.setParameter("statut", statut);
        return query.getResultList();
    }
    
    public List<Billet> findBilletsValidesByParticipant(Long participantId) {
        String jpql = """
            SELECT b FROM Billet b 
            JOIN b.inscription i 
            WHERE i.participant.id = :participantId
            AND b.statut = 'VALIDE'
            AND b.utilise = false
            ORDER BY b.dateGeneration DESC
            """;
        TypedQuery<Billet> query = entityManager.createQuery(jpql, Billet.class);
        query.setParameter("participantId", participantId);
        return query.getResultList();
    }
    
    public void delete(Billet billet) {
        try {
            entityManager.getTransaction().begin();
            entityManager.remove(entityManager.contains(billet) ? billet : entityManager.merge(billet));
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Failed to delete Billet: " + e.getMessage(), e);
        }
    }
    
    public long count() {
        String jpql = "SELECT COUNT(b) FROM Billet b";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        return query.getSingleResult();
    }
    
    public long countByStatut(StatutBillet statut) {
        String jpql = "SELECT COUNT(b) FROM Billet b WHERE b.statut = :statut";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("statut", statut);
        return query.getSingleResult();
    }
}
