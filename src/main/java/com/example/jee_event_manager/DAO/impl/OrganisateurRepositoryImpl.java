package com.example.jee_event_manager.DAO.impl;

import com.example.jee_event_manager.DAO.OrganisateurRepository;
import com.example.jee_event_manager.model.Organisateur;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class OrganisateurRepositoryImpl implements OrganisateurRepository {
    
    @Inject
    private EntityManager em;
    
    @Override
    public List<Organisateur> findAllOrganisateurs() {
        TypedQuery<Organisateur> query = em.createQuery("SELECT o FROM Organisateur o", Organisateur.class);
        return query.getResultList();
    }
    
    @Override
    public Optional<Organisateur> findOrganisateurById(Long id) {
        Organisateur organisateur = em.find(Organisateur.class, id);
        return Optional.ofNullable(organisateur);
    }
    
    @Override
    public Organisateur saveOrganisateur(Organisateur organisateur) {
        if (organisateur.getId() == null) {
            em.persist(organisateur);
        } else {
            organisateur = em.merge(organisateur);
        }
        return organisateur;
    }
    
    @Override
    public List<Organisateur> findByEntreprise(String entreprise) {
        TypedQuery<Organisateur> query = em.createQuery(
            "SELECT o FROM Organisateur o WHERE o.entreprise = :entreprise", 
            Organisateur.class
        );
        query.setParameter("entreprise", entreprise);
        return query.getResultList();
    }
    
    @Override
    public List<Organisateur> findBySiret(String siret) {
        TypedQuery<Organisateur> query = em.createQuery(
            "SELECT o FROM Organisateur o WHERE o.siret = :siret", 
            Organisateur.class
        );
        query.setParameter("siret", siret);
        return query.getResultList();
    }
    
    // Delegate to UtilisateurRepository methods
    @Override
    public List<com.example.jee_event_manager.model.Utilisateur> findAll() {
        return findAllOrganisateurs().stream()
                .map(o -> (com.example.jee_event_manager.model.Utilisateur) o)
                .toList();
    }
    
    @Override
    public Optional<com.example.jee_event_manager.model.Utilisateur> findById(Long id) {
        return findOrganisateurById(id).map(o -> (com.example.jee_event_manager.model.Utilisateur) o);
    }
    
    @Override
    public com.example.jee_event_manager.model.Utilisateur save(com.example.jee_event_manager.model.Utilisateur utilisateur) {
        if (utilisateur instanceof Organisateur) {
            return saveOrganisateur((Organisateur) utilisateur);
        }
        throw new IllegalArgumentException("Expected Organisateur instance");
    }
    
    @Override
    public void delete(Long id) {
        Organisateur organisateur = em.find(Organisateur.class, id);
        if (organisateur != null) {
            em.remove(organisateur);
        }
    }
    
    @Override
    public com.example.jee_event_manager.model.Utilisateur update(com.example.jee_event_manager.model.Utilisateur utilisateur) {
        if (utilisateur instanceof Organisateur) {
            return em.merge((Organisateur) utilisateur);
        }
        throw new IllegalArgumentException("Expected Organisateur instance");
    }
    
    @Override
    public Optional<com.example.jee_event_manager.model.Utilisateur> findByEmail(String email) {
        TypedQuery<Organisateur> query = em.createQuery(
            "SELECT o FROM Organisateur o WHERE o.email = :email", 
            Organisateur.class
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
        if (userType == com.example.jee_event_manager.model.UserType.ORGANISATEUR) {
            return findAllOrganisateurs().stream()
                    .map(o -> (com.example.jee_event_manager.model.Utilisateur) o)
                    .toList();
        }
        return List.of();
    }
    
    @Override
    public boolean existsByEmail(String email) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(o) FROM Organisateur o WHERE o.email = :email", 
            Long.class
        );
        query.setParameter("email", email);
        return query.getSingleResult() > 0;
    }
}
