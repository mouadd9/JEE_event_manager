package com.example.jee_event_manager.DAO.impl;

import com.example.jee_event_manager.DAO.CommentaireRepository;
import com.example.jee_event_manager.model.Commentaire;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CommentaireRepositoryImpl implements CommentaireRepository {
    
    @Inject
    private EntityManager em;
    
    @Override
    public List<Commentaire> findAll() {
        TypedQuery<Commentaire> query = em.createQuery("SELECT c FROM Commentaire c ORDER BY c.horodatage DESC", Commentaire.class);
        return query.getResultList();
    }
    
    @Override
    public Optional<Commentaire> findById(Long id) {
        Commentaire commentaire = em.find(Commentaire.class, id);
        return Optional.ofNullable(commentaire);
    }
    
    @Override
    public Commentaire save(Commentaire commentaire) {
        if (commentaire.getId() == null) {
            em.persist(commentaire);
        } else {
            commentaire = em.merge(commentaire);
        }
        return commentaire;
    }
    
    @Override
    public void delete(Long id) {
        Commentaire commentaire = em.find(Commentaire.class, id);
        if (commentaire != null) {
            em.remove(commentaire);
        }
    }
    
    @Override
    public Commentaire update(Commentaire commentaire) {
        return em.merge(commentaire);
    }
    
    @Override
    public List<Commentaire> findByEvenement(Long evenementId) {
        TypedQuery<Commentaire> query = em.createQuery(
            "SELECT c FROM Commentaire c WHERE c.evenement.id = :evenementId ORDER BY c.horodatage DESC",
            Commentaire.class
        );
        query.setParameter("evenementId", evenementId);
        return query.getResultList();
    }
    
    @Override
    public List<Commentaire> findByEvenementOrderByDate(Long evenementId) {
        TypedQuery<Commentaire> query = em.createQuery(
            "SELECT c FROM Commentaire c WHERE c.evenement.id = :evenementId ORDER BY c.horodatage ASC",
            Commentaire.class
        );
        query.setParameter("evenementId", evenementId);
        return query.getResultList();
    }
    
    @Override
    public Long countByEvenement(Long evenementId) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(c) FROM Commentaire c WHERE c.evenement.id = :evenementId",
            Long.class
        );
        query.setParameter("evenementId", evenementId);
        return query.getSingleResult();
    }
    
    @Override
    public List<Commentaire> findByParticipant(Long participantId) {
        TypedQuery<Commentaire> query = em.createQuery(
            "SELECT c FROM Commentaire c WHERE c.participant.id = :participantId ORDER BY c.horodatage DESC",
            Commentaire.class
        );
        query.setParameter("participantId", participantId);
        return query.getResultList();
    }
    
    @Override
    public List<Commentaire> findByParticipantAndEvenement(Long participantId, Long evenementId) {
        TypedQuery<Commentaire> query = em.createQuery(
            "SELECT c FROM Commentaire c WHERE c.participant.id = :participantId AND c.evenement.id = :evenementId ORDER BY c.horodatage DESC",
            Commentaire.class
        );
        query.setParameter("participantId", participantId);
        query.setParameter("evenementId", evenementId);
        return query.getResultList();
    }
}
