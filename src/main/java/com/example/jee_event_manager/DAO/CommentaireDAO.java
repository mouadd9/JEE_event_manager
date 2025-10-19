package com.example.jee_event_manager.DAO;

import com.example.jee_event_manager.model.Commentaire;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.inject.Inject;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CommentaireDAO {
    
    @Inject
    private EntityManager em;
    
    @Transactional
    public Commentaire save(Commentaire commentaire) {
        if (commentaire.getId() == null) {
            em.persist(commentaire);
            return commentaire;
        } else {
            return em.merge(commentaire);
        }
    }
    public Optional<Commentaire> findById(Integer id) {
        Commentaire commentaire = em.find(Commentaire.class, id);
        return Optional.ofNullable(commentaire);
    }
    
    public List<Commentaire> findByEvenement(Integer evenementId) {
        TypedQuery<Commentaire> query = em.createQuery(
            "SELECT c FROM Commentaire c " +
            "WHERE c.evenement.id = :evenementId " +
            "ORDER BY c.horodatage DESC",
            Commentaire.class
        );
        query.setParameter("evenementId", evenementId);
        return query.getResultList();
    }
    public List<Commentaire> findByParticipant(Long participantId) {
        TypedQuery<Commentaire> query = em.createQuery(
            "SELECT c FROM Commentaire c " +
            "WHERE c.participant.id = :participantId " +
            "ORDER BY c.horodatage DESC",
            Commentaire.class
        );
        query.setParameter("participantId", participantId);
        return query.getResultList();
    }
    public List<Commentaire> findByParticipantAndEvenement(Long participantId, Integer evenementId) {
        TypedQuery<Commentaire> query = em.createQuery(
            "SELECT c FROM Commentaire c " +
            "WHERE c.participant.id = :participantId " +
            "AND c.evenement.id = :evenementId " +
            "ORDER BY c.horodatage DESC",
            Commentaire.class
        );
        query.setParameter("participantId", participantId);
        query.setParameter("evenementId", evenementId);
        return query.getResultList();
    }
    
    public Long countByEvenement(Integer evenementId) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(c) FROM Commentaire c WHERE c.evenement.id = :evenementId",
            Long.class
        );
        query.setParameter("evenementId", evenementId);
        return query.getSingleResult();
    }
    
    @Transactional
    public void delete(Integer commentaireId) {
        Commentaire commentaire = em.find(Commentaire.class, commentaireId);
        if (commentaire != null) {
            em.remove(commentaire);
        }
    }
    
    @Transactional
    public void delete(Commentaire commentaire) {
        if (commentaire != null) {
            em.remove(em.contains(commentaire) ? commentaire : em.merge(commentaire));
        }
    }
    public boolean isOwner(Integer commentaireId, Long participantId) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(c) FROM Commentaire c " +
            "WHERE c.id = :commentaireId AND c.participant.id = :participantId",
            Long.class
        );
        query.setParameter("commentaireId", commentaireId);
        query.setParameter("participantId", participantId);
        return query.getSingleResult() > 0;
    }
    
    public List<Commentaire> findAll() {
        TypedQuery<Commentaire> query = em.createQuery(
            "SELECT c FROM Commentaire c ORDER BY c.horodatage DESC",
            Commentaire.class
        );
        return query.getResultList();
    }
}
