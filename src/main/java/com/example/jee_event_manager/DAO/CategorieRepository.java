package com.example.jee_event_manager.DAO;

import com.example.jee_event_manager.model.Categorie;

import java.util.List;
import java.util.Optional;

public interface CategorieRepository {
    
    // Basic CRUD operations
    List<Categorie> findAll();
    Optional<Categorie> findById(Long id);
    Categorie save(Categorie categorie);
    void delete(Long id);
    Categorie update(Categorie categorie);
    
    // Search queries
    Optional<Categorie> findByNom(String nom);
    List<Categorie> findByNomContaining(String nom);
}
