package com.example.jee_event_manager.DAO;

import com.example.jee_event_manager.model.Evaluation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;


@ApplicationScoped
public class EvaluationDAO {
    
    @Inject
    private EntityManager em;
    
    @Transactional
    public Evaluation save(Evaluation evaluation) {
        if (evaluation.getId() == null) {
            em.persist(evaluation);
            return evaluation;
        } else {
            return em.merge(evaluation);
        }
    }
    public Optional<Evaluation> findById(Integer id) {
        Evaluation evaluation = em.find(Evaluation.class, id);
        return Optional.ofNullable(evaluation);
    }
    public List<Evaluation> findByEvenement(Integer evenementId) {
        TypedQuery<Evaluation> query = em.createQuery(
            "SELECT e FROM Evaluation e " +
            "WHERE e.evenement.id = :evenementId " +
            "ORDER BY e.horodatage DESC",
            Evaluation.class
        );
        query.setParameter("evenementId", evenementId);
        return query.getResultList();
    }
    
    public List<Evaluation> findByParticipant(Long participantId) {
        TypedQuery<Evaluation> query = em.createQuery(
            "SELECT e FROM Evaluation e " +
            "WHERE e.participant.id = :participantId " +
            "ORDER BY e.horodatage DESC",
            Evaluation.class
        );
        query.setParameter("participantId", participantId);
        return query.getResultList();
    }
    
    public Optional<Evaluation> findByParticipantAndEvenement(Long participantId, Integer evenementId) {
        TypedQuery<Evaluation> query = em.createQuery(
            "SELECT e FROM Evaluation e " +
            "WHERE e.participant.id = :participantId " +
            "AND e.evenement.id = :evenementId",
            Evaluation.class
        );
        query.setParameter("participantId", participantId);
        query.setParameter("evenementId", evenementId);
        
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    
    public boolean hasEvaluated(Long participantId, Integer evenementId) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(e) FROM Evaluation e " +
            "WHERE e.participant.id = :participantId " +
            "AND e.evenement.id = :evenementId",
            Long.class
        );
        query.setParameter("participantId", participantId);
        query.setParameter("evenementId", evenementId);
        return query.getSingleResult() > 0;
    }
    
    public Double getAverageRating(Integer evenementId) {
        TypedQuery<Double> query = em.createQuery(
            "SELECT AVG(e.note) FROM Evaluation e WHERE e.evenement.id = :evenementId",
            Double.class
        );
        query.setParameter("evenementId", evenementId);
        Double average = query.getSingleResult();
        return average != null ? average : 0.0;
    }
    
    public Long countByEvenement(Integer evenementId) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(e) FROM Evaluation e WHERE e.evenement.id = :evenementId",
            Long.class
        );
        query.setParameter("evenementId", evenementId);
        return query.getSingleResult();
    }
    
    public List<Object[]> getRatingDistribution(Integer evenementId) {
        TypedQuery<Object[]> query = em.createQuery(
            "SELECT e.note, COUNT(e) FROM Evaluation e " +
            "WHERE e.evenement.id = :evenementId " +
            "GROUP BY e.note " +
            "ORDER BY e.note DESC",
            Object[].class
        );
        query.setParameter("evenementId", evenementId);
        return query.getResultList();
    }
    
    /**
     * Supprimer une évaluation par ID
     */
    @Transactional
    public void delete(Integer evaluationId) {
        Evaluation evaluation = em.find(Evaluation.class, evaluationId);
        if (evaluation != null) {
            em.remove(evaluation);
        }
    }
    
    /**
     * Supprimer une évaluation (avec objet)
     */
    @Transactional
    public void delete(Evaluation evaluation) {
        if (evaluation != null) {
            em.remove(em.contains(evaluation) ? evaluation : em.merge(evaluation));
        }
    }
    
    /**
     * Vérifier si une évaluation appartient à un participant
     */
    public boolean isOwner(Integer evaluationId, Long participantId) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(e) FROM Evaluation e " +
            "WHERE e.id = :evaluationId AND e.participant.id = :participantId",
            Long.class
        );
        query.setParameter("evaluationId", evaluationId);
        query.setParameter("participantId", participantId);
        return query.getSingleResult() > 0;
    }
    
    /**
     * Récupérer toutes les évaluations
     */
    public List<Evaluation> findAll() {
        TypedQuery<Evaluation> query = em.createQuery(
            "SELECT e FROM Evaluation e ORDER BY e.horodatage DESC",
            Evaluation.class
        );
        return query.getResultList();
    }
}
