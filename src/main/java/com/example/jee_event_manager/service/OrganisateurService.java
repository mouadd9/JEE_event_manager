package com.example.jee_event_manager.service;

import com.example.jee_event_manager.DAO.OrganisateurRepository;
import com.example.jee_event_manager.DAO.EvenementRepository;
import com.example.jee_event_manager.config.qualifiers.OrganisateurQualifier;
import com.example.jee_event_manager.model.Organisateur;
import com.example.jee_event_manager.model.Evenement;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

@Stateless
public class OrganisateurService {
    
    @Inject
    @OrganisateurQualifier
    private OrganisateurRepository organisateurRepository;
    
    @Inject
    private EvenementRepository evenementRepository;
    
    // === ADMINISTRATION METHODS (COMMENTED OUT) ===
    
    /*
    /**
     * Créer un nouvel organisateur
     */
    /*
    public Organisateur createOrganisateur(String nom, String email, String motDePasse, 
                                         String description, String entreprise, String siret, String siteWeb) {
        Organisateur organisateur = new Organisateur();
        organisateur.setNom(nom);
        organisateur.setEmail(email);
        organisateur.setDescription(description);
        organisateur.setEntreprise(entreprise);
        organisateur.setSiret(siret);
        organisateur.setSiteWeb(siteWeb);
        
        return organisateurRepository.saveOrganisateur(organisateur);
    }
    */
    
    /**
     * Trouver un organisateur par ID
     */
    public Optional<Organisateur> findById(Long id) {
        return organisateurRepository.findOrganisateurById(id);
    }
    
    /**
     * Trouver un organisateur par email
     */
    public Optional<Organisateur> findByEmail(String email) {
        return organisateurRepository.findByEmail(email)
                .map(u -> (Organisateur) u);
    }
    
    /**
     * Récupérer tous les organisateurs
     */
    public List<Organisateur> findAll() {
        return organisateurRepository.findAllOrganisateurs();
    }
    
    // === ADMINISTRATION METHODS (COMMENTED OUT) ===
    
    /*
    /**
     * Mettre à jour un organisateur
     */
    /*
    public Organisateur update(Organisateur organisateur) {
        return organisateurRepository.saveOrganisateur(organisateur);
    }
    */
    
    /*
    /**
     * Supprimer un organisateur
     */
    /*
    public void delete(Long id) {
        organisateurRepository.delete(id);
    }
    */
    
    /**
     * Récupérer tous les événements d'un organisateur
     */
    public List<Evenement> getEvenementsByOrganisateur(Long organisateurId) {
        return evenementRepository.findByOrganisateurId(organisateurId);
    }
    
    // === SEARCH & VALIDATION METHODS (COMMENTED OUT) ===
    
    /*
    /**
     * Vérifier si un organisateur existe par email
     */
    /*
    public boolean existsByEmail(String email) {
        return organisateurRepository.existsByEmail(email);
    }
    */
    
    /*
    /**
     * Rechercher des organisateurs par entreprise
     */
    /*
    public List<Organisateur> findByEntreprise(String entreprise) {
        return organisateurRepository.findByEntreprise(entreprise);
    }
    */
    
    /*
    /**
     * Rechercher des organisateurs par SIRET
     */
    /*
    public List<Organisateur> findBySiret(String siret) {
        return organisateurRepository.findBySiret(siret);
    }
    */
}
