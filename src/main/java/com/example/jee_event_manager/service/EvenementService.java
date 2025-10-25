package com.example.jee_event_manager.service;

import com.example.jee_event_manager.DAO.EvenementRepository;
import com.example.jee_event_manager.DAO.OrganisateurRepository;
import com.example.jee_event_manager.config.qualifiers.OrganisateurQualifier;
import com.example.jee_event_manager.dto.EvenementDTO;
import com.example.jee_event_manager.mappers.EvenementMapper;
import com.example.jee_event_manager.model.Commentaire;
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
    @OrganisateurQualifier
    private OrganisateurRepository organisateurRepository;
    
    @Inject
    private CommentaireService commentaireService;
    
    @Inject
    private EvaluationService evaluationService;
    
    @Inject
    private InscriptionService inscriptionService;
    
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
        return enrichirEvenementDTO(evenement);
    }

    /**
     * Récupérer tous les événements d'un organisateur
     */
    public List<EvenementDTO> getEventsByOrganizer(Long organisateurId) {
        return evenementRepository.findByOrganisateurId(organisateurId).stream()
                .map(this::enrichirEvenementDTO)
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
            System.out.println("=== DEBUG EvenementService.getEvenementsPublies ===");
            System.out.println("Parameters: date=" + date + ", lieu=" + lieu + ", categorie=" + categorie + ", search=" + search);
            
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
            List<Evenement> result = evenementRepository.getEvenementsPublies(date, lieu, categorie, search);
            System.out.println("Repository returned " + (result != null ? result.size() : "null") + " events");
            
            if (result != null && !result.isEmpty()) {
                System.out.println("First event from repository: " + result.get(0).getTitre());
            }
            
            return result;
            
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
    
    /**
     * Récupérer les commentaires d'un événement
     */
    public List<Commentaire> getCommentsByEventId(Long eventId) {
        return commentaireService.getCommentairesEvenement(eventId);
    }
    
    /**
     * Enrichir un EvenementDTO avec les statistiques
     */
    private EvenementDTO enrichirEvenementDTO(Evenement evenement) {
        EvenementDTO dto = EvenementMapper.toDto(evenement);
        
        // Ajouter les statistiques
        dto.setNoteMoyenne(evaluationService.getMoyenneEvenement(evenement.getId()));
        dto.setNombreEvaluations(evaluationService.countEvaluationsEvenement(evenement.getId()));
        dto.setNombreInscrits(inscriptionService.countInscritsEvenement(evenement.getId()));
        dto.setCapaciteDisponible(inscriptionService.getCapaciteDisponible(evenement.getId()));
        dto.setNombreCommentaires(commentaireService.countByEvenement(evenement.getId()));
        
        return dto;
    }
}