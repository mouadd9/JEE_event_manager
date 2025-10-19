package com.example.jee_event_manager.DAO;

import com.example.jee_event_manager.model.Inscription;
import com.example.jee_event_manager.model.StatutInscription;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.inject.Inject;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class InscriptionDAO {
    
    @Inject
    private EntityManager em;
    public Optional<Inscription> findById(Integer id) {
        Inscription inscription = em.find(Inscription.class, id);
        return Optional.ofNullable(inscription);
    }
    public List<Inscription> findByParticipant(Long participantId) {
        TypedQuery<Inscription> query = em.createQuery(
            "SELECT i FROM Inscription i " +
            "WHERE i.participant.id = :participantId " +
            "ORDER BY i.dateInscription DESC",
            Inscription.class
        );
        query.setParameter("participantId", participantId);
        return query.getResultList();
    }
    public List<Inscription> findByParticipantAndStatut(Long participantId, StatutInscription statut) {
        TypedQuery<Inscription> query = em.createQuery(
            "SELECT i FROM Inscription i " +
            "WHERE i.participant.id = :participantId " +
            "AND i.statut = :statut " +
            "ORDER BY i.dateInscription DESC",
            Inscription.class
        );
        query.setParameter("participantId", participantId);
        query.setParameter("statut", statut);
        return query.getResultList();
    }
    
    public Inscription save(Inscription inscription) {
        if (inscription.getId() == null) {
            em.persist(inscription);
            return inscription;
        } else {
            return em.merge(inscription);
        }
    }
    public void delete(Inscription inscription) {
        em.remove(em.contains(inscription) ? inscription : em.merge(inscription));
    }
    public Optional<Inscription> findByParticipantAndEvenement(Long participantId, Integer evenementId) {
        TypedQuery<Inscription> query = em.createQuery(
            "SELECT i FROM Inscription i " +
            "WHERE i.participant.id = :participantId " +
            "AND i.evenement.id = :evenementId",
            Inscription.class
        );
        query.setParameter("participantId", participantId);
        query.setParameter("evenementId", evenementId);
        
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    
    public boolean isParticipantInscrit(Long participantId, Integer evenementId) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(i) FROM Inscription i " +
            "WHERE i.participant.id = :participantId " +
            "AND i.evenement.id = :evenementId " +
            "AND i.statut != :statutAnnulee",
            Long.class
        );
        query.setParameter("participantId", participantId);
        query.setParameter("evenementId", evenementId);
        query.setParameter("statutAnnulee", StatutInscription.ANNULEE);
        return query.getSingleResult() > 0;
    }
    
    public Long countByEvenement(Integer evenementId) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(i) FROM Inscription i " +
            "WHERE i.evenement.id = :evenementId " +
            "AND i.statut = :statut",
            Long.class
        );
        query.setParameter("evenementId", evenementId);
        query.setParameter("statut", StatutInscription.ACCEPTEE);
        return query.getSingleResult();
    }
    public Long countPlacesReservees(Integer evenementId) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COALESCE(SUM(i.quantite), 0) FROM Inscription i " +
            "WHERE i.evenement.id = :evenementId " +
            "AND i.statut = :statut",
            Long.class
        );
        query.setParameter("evenementId", evenementId);
        query.setParameter("statut", StatutInscription.ACCEPTEE);
        return query.getSingleResult();
    }
    public void cancelInscription(Integer inscriptionId) {
        Inscription inscription = em.find(Inscription.class, inscriptionId);
        if (inscription != null) {
            inscription.setStatut(StatutInscription.ANNULEE);
            em.merge(inscription);
        }
    }
    public List<Inscription> findByEvenement(Integer evenementId) {
        TypedQuery<Inscription> query = em.createQuery(
            "SELECT i FROM Inscription i " +
            "WHERE i.evenement.id = :evenementId " +
            "ORDER BY i.dateInscription DESC",
            Inscription.class
        );
        query.setParameter("evenementId", evenementId);
        return query.getResultList();
    }
    
    public List<Inscription> findByEvenementAndStatut(Integer evenementId, StatutInscription statut) {
        TypedQuery<Inscription> query = em.createQuery(
            "SELECT i FROM Inscription i " +
            "WHERE i.evenement.id = :evenementId " +
            "AND i.statut = :statut " +
            "ORDER BY i.dateInscription DESC",
            Inscription.class
        );
        query.setParameter("evenementId", evenementId);
        query.setParameter("statut", statut);
        return query.getResultList();
    }
    
    public boolean isOwner(Integer inscriptionId, Long participantId) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(i) FROM Inscription i " +
            "WHERE i.id = :inscriptionId AND i.participant.id = :participantId",
            Long.class
        );
        query.setParameter("inscriptionId", inscriptionId);
        query.setParameter("participantId", participantId);
        return query.getSingleResult() > 0;
    }
}
