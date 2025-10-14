package com.example.jee_event_manager.DAO;

import com.example.jee_event_manager.model.Evenement;
import com.example.jee_event_manager.model.StatutEvenement;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;

@ApplicationScoped
public class EvenementDAO {
    
    @Inject
    private EntityManager em;
    
    public EntityManager getEntityManager() {
        return em;
    }
    
    public Evenement findById(Long id) {
        return em.find(Evenement.class, id);
    }
    
    public List<Evenement> findByStatut(StatutEvenement statut) {
        return em.createQuery("SELECT e FROM Evenement e WHERE e.statut = :statut", Evenement.class)
                .setParameter("statut", statut)
                .getResultList();
    }
    
    public void save(Evenement evenement) {
        if (evenement.getId() == null) {
            em.persist(evenement);
        } else {
            em.merge(evenement);
        }
    }
    
    public void delete(Evenement evenement) {
        em.remove(em.contains(evenement) ? evenement : em.merge(evenement));
    }
}
