package com.example.jee_event_manager.DAO;

import com.example.jee_event_manager.model.Organisateur;

import java.util.List;
import java.util.Optional;

public interface OrganisateurRepository extends UtilisateurRepository {
    
    // Organisateur-specific queries
    List<Organisateur> findAllOrganisateurs();
    Optional<Organisateur> findOrganisateurById(Long id);
    Organisateur saveOrganisateur(Organisateur organisateur);
    List<Organisateur> findByEntreprise(String entreprise);
    List<Organisateur> findBySiret(String siret);
}
