package com.example.jee_event_manager.DAO;

import com.example.jee_event_manager.model.Utilisateur;
import com.example.jee_event_manager.model.UserType;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository {
    
    // Basic CRUD operations
    List<Utilisateur> findAll();
    Optional<Utilisateur> findById(Long id);
    Utilisateur save(Utilisateur utilisateur);
    void delete(Long id);
    Utilisateur update(Utilisateur utilisateur);
    
    // User-specific queries
    Optional<Utilisateur> findByEmail(String email);
    List<Utilisateur> findByUserType(UserType userType);
    boolean existsByEmail(String email);
}
