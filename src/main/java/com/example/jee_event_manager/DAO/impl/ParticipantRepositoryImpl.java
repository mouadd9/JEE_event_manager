package com.example.jee_event_manager.DAO.impl;

import com.example.jee_event_manager.DAO.ParticipantRepository;
import com.example.jee_event_manager.config.qualifiers.ParticipantQualifier;
import com.example.jee_event_manager.model.Participant;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@ParticipantQualifier
public class ParticipantRepositoryImpl implements ParticipantRepository {
    
    @Inject
    private EntityManager em;
    
    @Override
    public List<Participant> findAllParticipants() {
        TypedQuery<Participant> query = em.createQuery("SELECT p FROM Participant p", Participant.class);
        return query.getResultList();
    }
    
    @Override
    public Optional<Participant> findParticipantById(Long id) {
        Participant participant = em.find(Participant.class, id);
        return Optional.ofNullable(participant);
    }
    
    @Override
    public Participant saveParticipant(Participant participant) {
        try {
            em.getTransaction().begin();
            
            if (participant.getId() == null) {
                em.persist(participant);
            } else {
                participant = em.merge(participant);
            }
            
            em.getTransaction().commit();
            return participant;
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Failed to save Participant: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Participant> findByPreferences(String preferences) {
        TypedQuery<Participant> query = em.createQuery(
            "SELECT p FROM Participant p WHERE p.preferences LIKE :preferences", 
            Participant.class
        );
        query.setParameter("preferences", "%" + preferences + "%");
        return query.getResultList();
    }
    
    @Override
    public List<Participant> findByTelephone(String telephone) {
        TypedQuery<Participant> query = em.createQuery(
            "SELECT p FROM Participant p WHERE p.telephone = :telephone", 
            Participant.class
        );
        query.setParameter("telephone", telephone);
        return query.getResultList();
    }
    
    // Delegate to UtilisateurRepository methods
    @Override
    public List<com.example.jee_event_manager.model.Utilisateur> findAll() {
        return findAllParticipants().stream()
                .map(p -> (com.example.jee_event_manager.model.Utilisateur) p)
                .toList();
    }
    
    @Override
    public Optional<com.example.jee_event_manager.model.Utilisateur> findById(Long id) {
        return findParticipantById(id).map(p -> (com.example.jee_event_manager.model.Utilisateur) p);
    }
    
    @Override
    public com.example.jee_event_manager.model.Utilisateur save(com.example.jee_event_manager.model.Utilisateur utilisateur) {
        if (utilisateur instanceof Participant) {
            return saveParticipant((Participant) utilisateur);
        }
        throw new IllegalArgumentException("Expected Participant instance");
    }
    
    @Override
    public void delete(Long id) {
        try {
            em.getTransaction().begin();
            
            Participant participant = em.find(Participant.class, id);
            if (participant != null) {
                em.remove(participant);
            }
            
            em.getTransaction().commit();
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Failed to delete Participant: " + e.getMessage(), e);
        }
    }
    
    @Override
    public com.example.jee_event_manager.model.Utilisateur update(com.example.jee_event_manager.model.Utilisateur utilisateur) {
        try {
            em.getTransaction().begin();
            
            if (utilisateur instanceof Participant) {
                utilisateur = em.merge((Participant) utilisateur);
            } else {
                throw new IllegalArgumentException("Expected Participant instance");
            }
            
            em.getTransaction().commit();
            return utilisateur;
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Failed to update Participant: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Optional<com.example.jee_event_manager.model.Utilisateur> findByEmail(String email) {
        TypedQuery<Participant> query = em.createQuery(
            "SELECT p FROM Participant p WHERE p.email = :email", 
            Participant.class
        );
        query.setParameter("email", email);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    @Override
    public List<com.example.jee_event_manager.model.Utilisateur> findByUserType(com.example.jee_event_manager.model.UserType userType) {
        if (userType == com.example.jee_event_manager.model.UserType.PARTICIPANT) {
            return findAllParticipants().stream()
                    .map(p -> (com.example.jee_event_manager.model.Utilisateur) p)
                    .toList();
        }
        return List.of();
    }
    
    @Override
    public boolean existsByEmail(String email) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(p) FROM Participant p WHERE p.email = :email", 
            Long.class
        );
        query.setParameter("email", email);
        return query.getSingleResult() > 0;
    }
}
