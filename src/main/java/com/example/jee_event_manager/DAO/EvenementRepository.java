package com.example.jee_event_manager.DAO;

import com.example.jee_event_manager.model.Evenement;
import com.example.jee_event_manager.model.StatutEvenement;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EvenementRepository {
    
    // Basic CRUD operations
    List<Evenement> findAll();
    Optional<Evenement> findById(Long id);
    Evenement save(Evenement evenement);
    void delete(Long id);
    Evenement update(Evenement evenement);
    
    // Organizer-specific queries
    List<Evenement> findByOrganisateurId(Long organisateurId);
    
    // Status-based queries
    List<Evenement> findByStatut(StatutEvenement statut);
    List<Evenement> findEvenementsPublies();
    
    // Search and filtering (from Branch A's EvenementService)
    List<Evenement> getEvenementsPublies(LocalDate date, String lieu, String categorie, String search);
    
    // Advanced queries
    List<Evenement> findByLieu(String lieu);
    List<Evenement> findByDateDebut(LocalDate date);
    List<Evenement> findByCategorieId(Long categorieId);
}
