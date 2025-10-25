package com.example.jee_event_manager.DAO.impl;

import com.example.jee_event_manager.DAO.AdminRepository;
import com.example.jee_event_manager.config.qualifiers.AdminQualifier;
import com.example.jee_event_manager.model.Admin;
import com.example.jee_event_manager.model.Utilisateur;
import com.example.jee_event_manager.model.UserType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@AdminQualifier
public class AdminRepositoryImpl implements AdminRepository {
    
    @Inject
    private EntityManager em;
    
    @Override
    public Admin saveAdmin(Admin admin) {
        try {
            em.getTransaction().begin();
            
            if (admin.getId() == null) {
                em.persist(admin);
            } else {
                admin = em.merge(admin);
            }
            
            em.getTransaction().commit();
            return admin;
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Failed to save Admin: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Admin> findAllAdmins() {
        TypedQuery<Admin> query = em.createQuery(
            "SELECT a FROM Admin a ORDER BY a.createdAt DESC", 
            Admin.class
        );
        return query.getResultList();
    }
    
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
        if (utilisateur instanceof Admin) {
            return saveAdmin((Admin) utilisateur);
        }
        
        try {
            em.getTransaction().begin();
            
            if (utilisateur.getId() == null) {
                em.persist(utilisateur);
            } else {
                utilisateur = em.merge(utilisateur);
            }
            
            em.getTransaction().commit();
            return utilisateur;
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Failed to save Utilisateur: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void delete(Long id) {
        try {
            em.getTransaction().begin();
            
            Utilisateur utilisateur = em.find(Utilisateur.class, id);
            if (utilisateur != null) {
                em.remove(utilisateur);
            }
            
            em.getTransaction().commit();
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Failed to delete Utilisateur: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Utilisateur update(Utilisateur utilisateur) {
        try {
            em.getTransaction().begin();
            
            utilisateur = em.merge(utilisateur);
            
            em.getTransaction().commit();
            return utilisateur;
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Failed to update Utilisateur: " + e.getMessage(), e);
        }
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
