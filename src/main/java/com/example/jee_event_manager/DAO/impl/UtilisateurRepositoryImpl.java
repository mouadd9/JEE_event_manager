package com.example.jee_event_manager.DAO.impl;

import com.example.jee_event_manager.DAO.UtilisateurRepository;
import com.example.jee_event_manager.model.Utilisateur;
import com.example.jee_event_manager.model.UserType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UtilisateurRepositoryImpl implements UtilisateurRepository {
    
    @Inject
    private EntityManager em;
    
    @Override
    public List<Utilisateur> findAll() {
        TypedQuery<Utilisateur> query = em.createQuery("SELECT u FROM Utilisateur u", Utilisateur.class);
        return query.getResultList();
    }
    
    @Override
    public Optional<Utilisateur> findById(Long id) {
        Utilisateur utilisateur = em.find(Utilisateur.class, id);
        return Optional.ofNullable(utilisateur);
    }
    
    @Override
    public Utilisateur save(Utilisateur utilisateur) {
        if (utilisateur.getId() == null) {
            em.persist(utilisateur);
        } else {
            utilisateur = em.merge(utilisateur);
        }
        return utilisateur;
    }
    
    @Override
    public void delete(Long id) {
        Utilisateur utilisateur = em.find(Utilisateur.class, id);
        if (utilisateur != null) {
            em.remove(utilisateur);
        }
    }
    
    @Override
    public Utilisateur update(Utilisateur utilisateur) {
        return em.merge(utilisateur);
    }
    
    @Override
    public Optional<Utilisateur> findByEmail(String email) {
        TypedQuery<Utilisateur> query = em.createQuery(
            "SELECT u FROM Utilisateur u WHERE u.email = :email", 
            Utilisateur.class
        );
        query.setParameter("email", email);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    @Override
    public List<Utilisateur> findByUserType(UserType userType) {
        TypedQuery<Utilisateur> query = em.createQuery(
            "SELECT u FROM Utilisateur u WHERE u.userType = :userType", 
            Utilisateur.class
        );
        query.setParameter("userType", userType);
        return query.getResultList();
    }
    
    @Override
    public boolean existsByEmail(String email) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(u) FROM Utilisateur u WHERE u.email = :email", 
            Long.class
        );
        query.setParameter("email", email);
        return query.getSingleResult() > 0;
    }
}
