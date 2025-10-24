package com.example.jee_event_manager.service;

import com.example.jee_event_manager.DAO.EvenementRepository;
import com.example.jee_event_manager.DAO.OrganisateurRepository;
import com.example.jee_event_manager.dto.EvenementDTO;
import com.example.jee_event_manager.mappers.EvenementMapper;
import com.example.jee_event_manager.model.Evenement;
import com.example.jee_event_manager.model.Organisateur;
import com.example.jee_event_manager.model.StatutEvenement;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Stateless
public class EvenementService {
    
    @Inject
    private EvenementRepository evenementRepository;
    
    @Inject
    private OrganisateurRepository organisateurRepository;
    
    // ===== CRUD Operations (from Branch B) =====
    
    /**
     * Créer un nouvel événement
     */
    public EvenementDTO createEvent(EvenementDTO eventDto, Long organisateurId) {
        Organisateur organisateur = organisateurRepository.findOrganisateurById(organisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Organisateur avec ID " + organisateurId + " introuvable"));

        Evenement evenement = EvenementMapper.toEntity(eventDto);
        evenement.setOrganisateur(organisateur);
        Evenement saved = evenementRepository.save(evenement);
        return EvenementMapper.toDto(saved);
    }

    /**
     * Mettre à jour un événement existant
     */
    public EvenementDTO updateEvent(EvenementDTO eventDto) {
        Evenement existing = evenementRepository.findById(eventDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Événement avec ID " + eventDto.getId() + " introuvable"));
        
        EvenementMapper.updateEntityFromDto(existing, eventDto);
        Evenement updated = evenementRepository.update(existing);
        return EvenementMapper.toDto(updated);
    }

    /**
     * Publier un événement (changer le statut à PUBLIE)
     */
    public void publishEvent(Long eventId) {
        Evenement evenement = evenementRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Événement avec ID " + eventId + " introuvable"));
        evenement.setStatut(StatutEvenement.PUBLIE);
        evenementRepository.update(evenement);
    }

    /**
     * Dépublier un événement (changer le statut à BROUILLON)
     */
    public void unpublishEvent(Long eventId) {
        Evenement evenement = evenementRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Événement avec ID " + eventId + " introuvable"));
        evenement.setStatut(StatutEvenement.BROUILLON);
        evenementRepository.update(evenement);
    }

    /**
     * Annuler un événement (changer le statut à ANNULE)
     */
    public void cancelEvent(Long eventId) {
        Evenement evenement = evenementRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Événement avec ID " + eventId + " introuvable"));
        evenement.setStatut(StatutEvenement.ANNULE);
        evenementRepository.update(evenement);
    }

    // === ADMINISTRATION METHODS (COMMENTED OUT) ===
    
    /*
    /**
     * Supprimer un événement
     */
    /*
    public void deleteEvent(Long eventId) {
        Evenement evenement = evenementRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Événement avec ID " + eventId + " introuvable"));
        evenementRepository.delete(evenement.getId());
    }
    */

    /**
     * Récupérer un événement par son ID
     */
    public EvenementDTO getEventById(Long eventId) {
        Evenement evenement = evenementRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Événement avec ID " + eventId + " introuvable"));
        return EvenementMapper.toDto(evenement);
    }

    /**
     * Récupérer tous les événements d'un organisateur
     */
    public List<EvenementDTO> getEventsByOrganizer(Long organisateurId) {
        return evenementRepository.findByOrganisateurId(organisateurId).stream()
                .map(EvenementMapper::toDto)
                .toList();
    }
    
    // ===== Advanced Filtering (from Branch A) =====
    
    /**
     * Récupère la liste des événements publiés avec des filtres optionnels
     * @param date Date de l'événement (optionnel)
     * @param lieu Lieu de l'événement (optionnel)
     * @param categorie Catégorie de l'événement (optionnel)
     * @param search Recherche textuelle dans titre et description (optionnel)
     * @return Liste des événements correspondants aux critères
     */
    public List<Evenement> getEvenementsPublies(LocalDate date, String lieu, String categorie, String search) {
        try {
            // Déterminer si on a besoin d'un filtre de catégorie
            boolean hasCategorieFilter = categorie != null && !categorie.trim().isEmpty();
            Long categorieId = null;
            
            if (hasCategorieFilter) {
                try {
                    categorieId = Long.parseLong(categorie.trim());
                } catch (NumberFormatException ex) {
                    System.err.println("ID de catégorie invalide: " + categorie);
                    hasCategorieFilter = false;
                }
            }
            
            // Utiliser la méthode du repository pour la recherche complexe
            return evenementRepository.getEvenementsPublies(date, lieu, categorie, search);
            
        } catch (Exception e) {
            System.err.println("=== ERREUR lors de la récupération des événements:");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    // ===== Basic Repository Operations =====
    
    /**
     * Trouver un événement par ID (retourne l'entité)
     */
    public Optional<Evenement> findById(Long id) {
        return evenementRepository.findById(id);
    }
    
    /**
     * Trouver des événements par statut
     */
    public List<Evenement> findByStatut(StatutEvenement statut) {
        return evenementRepository.findByStatut(statut);
    }
    
    // === ADMINISTRATION METHODS (COMMENTED OUT) ===
    
    /*
    /**
     * Sauvegarder un événement (validation incluse)
     */
    /*
    public Evenement save(Evenement evenement) {
        if (evenement.validate()) {
            return evenementRepository.save(evenement);
        }
        throw new IllegalArgumentException("Événement invalide");
    }
    */
    
    /*
    /**
     * Supprimer un événement par ID
     */
    /*
    public void delete(Long id) {
        Optional<Evenement> evenement = findById(id);
        if (evenement.isPresent()) {
            evenementRepository.delete(id);
        }
    }
    */
    
    /**
     * Récupérer tous les événements
     */
    public List<Evenement> findAll() {
        return evenementRepository.findAll();
    }
}