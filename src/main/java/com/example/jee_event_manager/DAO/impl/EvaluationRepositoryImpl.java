package com.example.jee_event_manager.DAO.impl;

import com.example.jee_event_manager.DAO.EvaluationRepository;
import com.example.jee_event_manager.model.Evaluation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class EvaluationRepositoryImpl implements EvaluationRepository {
    
    @Inject
    private EntityManager em;
    
    @Override
    public List<Evaluation> findAll() {
        TypedQuery<Evaluation> query = em.createQuery("SELECT e FROM Evaluation e ORDER BY e.horodatage DESC", Evaluation.class);
        return query.getResultList();
    }
    
    @Override
    public Optional<Evaluation> findById(Long id) {
        Evaluation evaluation = em.find(Evaluation.class, id);
        return Optional.ofNullable(evaluation);
    }
    
    @Override
    public Evaluation save(Evaluation evaluation) {
        if (evaluation.getId() == null) {
            em.persist(evaluation);
        } else {
            evaluation = em.merge(evaluation);
        }
        return evaluation;
    }
    
    @Override
    public void delete(Long id) {
        Evaluation evaluation = em.find(Evaluation.class, id);
        if (evaluation != null) {
            em.remove(evaluation);
        }
    }
    
    @Override
    public Evaluation update(Evaluation evaluation) {
        return em.merge(evaluation);
    }
    
    @Override
    public List<Evaluation> findByEvenement(Long evenementId) {
        TypedQuery<Evaluation> query = em.createQuery(
            "SELECT e FROM Evaluation e WHERE e.evenement.id = :evenementId ORDER BY e.horodatage DESC",
            Evaluation.class
        );
        query.setParameter("evenementId", evenementId);
        return query.getResultList();
    }
    
    @Override
    public List<Evaluation> findByEvenementOrderByDate(Long evenementId) {
        TypedQuery<Evaluation> query = em.createQuery(
            "SELECT e FROM Evaluation e WHERE e.evenement.id = :evenementId ORDER BY e.horodatage ASC",
            Evaluation.class
        );
        query.setParameter("evenementId", evenementId);
        return query.getResultList();
    }
    
    @Override
    public Optional<Evaluation> findByParticipantAndEvenement(Long participantId, Long evenementId) {
        TypedQuery<Evaluation> query = em.createQuery(
            "SELECT e FROM Evaluation e WHERE e.participant.id = :participantId AND e.evenement.id = :evenementId",
            Evaluation.class
        );
        query.setParameter("participantId", participantId);
        query.setParameter("evenementId", evenementId);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    @Override
    public List<Evaluation> findByParticipant(Long participantId) {
        TypedQuery<Evaluation> query = em.createQuery(
            "SELECT e FROM Evaluation e WHERE e.participant.id = :participantId ORDER BY e.horodatage DESC",
            Evaluation.class
        );
        query.setParameter("participantId", participantId);
        return query.getResultList();
    }
    
    @Override
    public Double getMoyenneNoteByEvenement(Long evenementId) {
        TypedQuery<Double> query = em.createQuery(
            "SELECT AVG(e.note) FROM Evaluation e WHERE e.evenement.id = :evenementId",
            Double.class
        );
        query.setParameter("evenementId", evenementId);
        Double result = query.getSingleResult();
        return result != null ? result : 0.0;
    }
    
    @Override
    public Long countByEvenement(Long evenementId) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(e) FROM Evaluation e WHERE e.evenement.id = :evenementId",
            Long.class
        );
        query.setParameter("evenementId", evenementId);
        return query.getSingleResult();
    }
    
    @Override
    public Long countByNote(Long evenementId, Long note) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(e) FROM Evaluation e WHERE e.evenement.id = :evenementId AND e.note = :note",
            Long.class
        );
        query.setParameter("evenementId", evenementId);
        query.setParameter("note", note);
        return query.getSingleResult();
    }
}
