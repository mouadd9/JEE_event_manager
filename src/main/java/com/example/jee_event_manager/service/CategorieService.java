package com.example.jee_event_manager.service;

import com.example.jee_event_manager.DAO.CategorieRepository;
import com.example.jee_event_manager.model.Categorie;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;

@Stateless
public class CategorieService {

    @Inject
    private CategorieRepository categorieRepository;

    public List<Categorie> findAll() {
        return categorieRepository.findAll();
    }

    public Optional<Categorie> findById(Long id) {
        return categorieRepository.findById(id);
    }

    // === ADMINISTRATION METHODS (COMMENTED OUT) ===
    
    /*
    public Categorie save(Categorie categorie) {
        return categorieRepository.save(categorie);
    }
    */
    
    /*
    public void delete(Long id) {
        categorieRepository.delete(id);
    }
    */
    
    public Optional<Categorie> findByNom(String nom) {
        return categorieRepository.findByNom(nom);
    }
}
