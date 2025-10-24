package com.example.jee_event_manager.DAO.impl;

import com.example.jee_event_manager.DAO.InscriptionRepository;
import com.example.jee_event_manager.model.Inscription;
import com.example.jee_event_manager.model.StatutInscription;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class InscriptionRepositoryImpl implements InscriptionRepository {
    
    @Inject
    private EntityManager em;
    
    @Override
    public List<Inscription> findAll() {
        TypedQuery<Inscription> query = em.createQuery("SELECT i FROM Inscription i ORDER BY i.dateInscription DESC", Inscription.class);
        return query.getResultList();
    }
    
    @Override
    public Optional<Inscription> findById(Long id) {
        Inscription inscription = em.find(Inscription.class, id);
        return Optional.ofNullable(inscription);
    }
    
    @Override
    public Inscription save(Inscription inscription) {
        if (inscription.getId() == null) {
            em.persist(inscription);
        } else {
            inscription = em.merge(inscription);
        }
        return inscription;
    }
    
    @Override
    public void delete(Long id) {
        Inscription inscription = em.find(Inscription.class, id);
        if (inscription != null) {
            em.remove(inscription);
        }
    }
    
    @Override
    public Inscription update(Inscription inscription) {
        return em.merge(inscription);
    }
    
    @Override
    public List<Inscription> findByParticipant(Long participantId) {
        TypedQuery<Inscription> query = em.createQuery(
            "SELECT i FROM Inscription i WHERE i.participant.id = :participantId ORDER BY i.dateInscription DESC",
            Inscription.class
        );
        query.setParameter("participantId", participantId);
        return query.getResultList();
    }
    
    @Override
    public List<Inscription> findByParticipantAndStatut(Long participantId, StatutInscription statut) {
        TypedQuery<Inscription> query = em.createQuery(
            "SELECT i FROM Inscription i WHERE i.participant.id = :participantId AND i.statut = :statut ORDER BY i.dateInscription DESC",
            Inscription.class
        );
        query.setParameter("participantId", participantId);
        query.setParameter("statut", statut);
        return query.getResultList();
    }
    
    @Override
    public Optional<Inscription> findByParticipantAndEvenement(Long participantId, Long evenementId) {
        TypedQuery<Inscription> query = em.createQuery(
            "SELECT i FROM Inscription i WHERE i.participant.id = :participantId AND i.evenement.id = :evenementId",
            Inscription.class
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
    public boolean isParticipantInscrit(Long participantId, Long evenementId) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(i) FROM Inscription i WHERE i.participant.id = :participantId AND i.evenement.id = :evenementId",
            Long.class
        );
        query.setParameter("participantId", participantId);
        query.setParameter("evenementId", evenementId);
        return query.getSingleResult() > 0;
    }
    
    @Override
    public boolean isOwner(Long inscriptionId, Long participantId) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(i) FROM Inscription i WHERE i.id = :inscriptionId AND i.participant.id = :participantId",
            Long.class
        );
        query.setParameter("inscriptionId", inscriptionId);
        query.setParameter("participantId", participantId);
        return query.getSingleResult() > 0;
    }
    
    @Override
    public List<Inscription> findByEvenement(Long evenementId) {
        TypedQuery<Inscription> query = em.createQuery(
            "SELECT i FROM Inscription i WHERE i.evenement.id = :evenementId ORDER BY i.dateInscription DESC",
            Inscription.class
        );
        query.setParameter("evenementId", evenementId);
        return query.getResultList();
    }
    
    @Override
    public List<Inscription> findByEvenementAndStatut(Long evenementId, StatutInscription statut) {
        TypedQuery<Inscription> query = em.createQuery(
            "SELECT i FROM Inscription i WHERE i.evenement.id = :evenementId AND i.statut = :statut ORDER BY i.dateInscription DESC",
            Inscription.class
        );
        query.setParameter("evenementId", evenementId);
        query.setParameter("statut", statut);
        return query.getResultList();
    }
    
    @Override
    public Long countByEvenement(Long evenementId) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(i) FROM Inscription i WHERE i.evenement.id = :evenementId",
            Long.class
        );
        query.setParameter("evenementId", evenementId);
        return query.getSingleResult();
    }
    
    @Override
    public Long countPlacesReservees(Long evenementId) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COALESCE(SUM(i.quantite), 0) FROM Inscription i WHERE i.evenement.id = :evenementId AND i.statut IN ('EN_ATTENTE', 'ACCEPTEE')",
            Long.class
        );
        query.setParameter("evenementId", evenementId);
        return query.getSingleResult();
    }
    
    @Override
    public void cancelInscription(Long inscriptionId) {
        TypedQuery<Inscription> query = em.createQuery(
            "SELECT i FROM Inscription i WHERE i.id = :inscriptionId",
            Inscription.class
        );
        query.setParameter("inscriptionId", inscriptionId);
        Inscription inscription = query.getSingleResult();
        inscription.setStatut(StatutInscription.ANNULEE);
        em.merge(inscription);
    }
    
    @Override
    public Optional<StatutInscription> getStatutInscription(Long participantId, Long evenementId) {
        Optional<Inscription> inscription = findByParticipantAndEvenement(participantId, evenementId);
        return inscription.map(Inscription::getStatut);
    }
}
