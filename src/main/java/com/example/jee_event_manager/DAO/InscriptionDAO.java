package com.example.jee_event_manager.DAO;

import com.example.jee_event_manager.model.Inscription;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class InscriptionDAO {
    
    @PersistenceContext
    private EntityManager em;
    
    public Inscription findById(Long id) {
        return em.find(Inscription.class, id);
    }
    
    public List<Inscription> findByParticipantId(Long participantId) {
        return em.createQuery("SELECT i FROM Inscription i WHERE i.participant.id = :participantId", Inscription.class)
                .setParameter("participantId", participantId)
                .getResultList();
    }
    
    public void save(Inscription inscription) {
        if (inscription.getId() == null) {
            em.persist(inscription);
        } else {
            em.merge(inscription);
        }
    }
    
    public void delete(Inscription inscription) {
        em.remove(em.contains(inscription) ? inscription : em.merge(inscription));
    }
}
