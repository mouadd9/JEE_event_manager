package com.example.jee_event_manager.DAO.impl;

import com.example.jee_event_manager.DAO.CategorieRepository;
import com.example.jee_event_manager.model.Categorie;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CategorieRepositoryImpl implements CategorieRepository {
    
    @Inject
    private EntityManager em;
    
    @Override
    public List<Categorie> findAll() {
        TypedQuery<Categorie> query = em.createQuery("SELECT c FROM Categorie c ORDER BY c.nom", Categorie.class);
        return query.getResultList();
    }
    
    @Override
    public Optional<Categorie> findById(Integer id) {
        Categorie categorie = em.find(Categorie.class, id);
        return Optional.ofNullable(categorie);
    }
    
    @Override
    public Categorie save(Categorie categorie) {
        if (categorie.getId() == null) {
            em.persist(categorie);
        } else {
            categorie = em.merge(categorie);
        }
        return categorie;
    }
    
    @Override
    public void delete(Integer id) {
        Categorie categorie = em.find(Categorie.class, id);
        if (categorie != null) {
            em.remove(categorie);
        }
    }
    
    @Override
    public Categorie update(Categorie categorie) {
        return em.merge(categorie);
    }
    
    @Override
    public Optional<Categorie> findByNom(String nom) {
        TypedQuery<Categorie> query = em.createQuery(
            "SELECT c FROM Categorie c WHERE c.nom = :nom", 
            Categorie.class
        );
        query.setParameter("nom", nom);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    @Override
    public List<Categorie> findByNomContaining(String nom) {
        TypedQuery<Categorie> query = em.createQuery(
            "SELECT c FROM Categorie c WHERE LOWER(c.nom) LIKE LOWER(CONCAT('%', :nom, '%')) ORDER BY c.nom", 
            Categorie.class
        );
        query.setParameter("nom", nom);
        return query.getResultList();
    }
}
