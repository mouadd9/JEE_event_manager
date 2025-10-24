package com.example.jee_event_manager.mappers;

import com.example.jee_event_manager.dto.EvenementDTO;
import com.example.jee_event_manager.model.Evenement;
import com.example.jee_event_manager.model.Organisateur;
import com.example.jee_event_manager.model.Categorie;

import java.util.stream.Collectors;

/**
 * Mapper pour convertir entre Evenement (entité) et EvenementDTO
 * Combine la logique de EventMapper (Branch B) avec les fonctionnalités étendues
 */
public class EvenementMapper {
    
    /**
     * Convertit une entité Evenement en DTO
     */
    public static EvenementDTO toDto(Evenement evenement) {
        if (evenement == null) {
            return null;
        }

        EvenementDTO dto = new EvenementDTO();
        
        // === Champs de base ===
        dto.setId(evenement.getId());
        dto.setTitre(evenement.getTitre());
        dto.setDescription(evenement.getDescription());
        dto.setDateDebut(evenement.getDateDebut());
        dto.setDateFin(evenement.getDateFin());
        dto.setLieu(evenement.getLieu());
        dto.setStatut(evenement.getStatut());
        dto.setLatitude(evenement.getLatitude());
        dto.setLongitude(evenement.getLongitude());
        
        // === Champs étendus ===
        dto.setCapacite(evenement.getCapacite());
        dto.setImageUrl(evenement.getImageUrl());
        
        // === Informations organisateur ===
        if (evenement.getOrganisateur() != null) {
            Organisateur organisateur = evenement.getOrganisateur();
            dto.setOrganisateurId(organisateur.getId());
            dto.setOrganisateurNom(organisateur.getNom());
            dto.setOrganizerName(organisateur.getNom()); // Pour compatibilité
        } else {
            dto.setOrganizerName("N/A");
        }
        
        // === Catégories ===
        if (evenement.getCategories() != null) {
            dto.setCategories(evenement.getCategories().stream()
                    .map(Categorie::getNom)
                    .collect(Collectors.toList()));
        }
        
        // === Statistiques (à calculer par le service) ===
        // Ces valeurs seront définies par le service qui utilise ce mapper
        
        return dto;
    }
    
    /**
     * Convertit un DTO en entité Evenement
     */
    public static Evenement toEntity(EvenementDTO dto) {
        if (dto == null) {
            return null;
        }

        Evenement evenement = new Evenement();
        
        // === Champs de base ===
        evenement.setId(dto.getId());
        evenement.setTitre(dto.getTitre());
        evenement.setDescription(dto.getDescription());
        evenement.setDateDebut(dto.getDateDebut());
        evenement.setDateFin(dto.getDateFin());
        evenement.setLieu(dto.getLieu());
        evenement.setStatut(dto.getStatut());
        evenement.setLatitude(dto.getLatitude());
        evenement.setLongitude(dto.getLongitude());
        
        // === Champs étendus ===
        evenement.setCapacite(dto.getCapacite() != null ? dto.getCapacite() : 100);
        evenement.setImageUrl(dto.getImageUrl());
        
        // Note: L'organisateur et les catégories sont gérés par le service layer
        // car ils nécessitent des relations avec d'autres entités
        
        return evenement;
    }
    
    /**
     * Met à jour une entité Evenement existante avec les valeurs du DTO
     */
    public static void updateEntityFromDto(Evenement evenement, EvenementDTO dto) {
        if (dto == null || evenement == null) {
            return;
        }

        // Only update fields if the new value from the DTO is not null
        if (dto.getTitre() != null) {
            evenement.setTitre(dto.getTitre());
        }
        if (dto.getDescription() != null) {
            evenement.setDescription(dto.getDescription());
        }
        if (dto.getDateDebut() != null) {
            evenement.setDateDebut(dto.getDateDebut());
        }
        if (dto.getDateFin() != null) {
            evenement.setDateFin(dto.getDateFin());
        }
        if (dto.getLieu() != null) {
            evenement.setLieu(dto.getLieu());
        }
        if (dto.getStatut() != null) {
            evenement.setStatut(dto.getStatut());
        }
        if (dto.getLatitude() != null) {
            evenement.setLatitude(dto.getLatitude());
        }
        if (dto.getLongitude() != null) {
            evenement.setLongitude(dto.getLongitude());
        }
        if (dto.getCapacite() != null) {
            evenement.setCapacite(dto.getCapacite());
        }
        if (dto.getImageUrl() != null) {
            evenement.setImageUrl(dto.getImageUrl());
        }
    }
    
    /**
     * Crée un DTO avec des statistiques calculées
     * Utilisé par les services pour enrichir le DTO avec des données calculées
     */
    public static EvenementDTO toDtoWithStats(Evenement evenement, 
                                            Double noteMoyenne, 
                                            Long nombreEvaluations,
                                            Long nombreInscrits,
                                            Integer capaciteDisponible,
                                            Long nombreCommentaires) {
        EvenementDTO dto = toDto(evenement);
        
        if (dto != null) {
            dto.setNoteMoyenne(noteMoyenne);
            dto.setNombreEvaluations(nombreEvaluations);
            dto.setNombreInscrits(nombreInscrits);
            dto.setCapaciteDisponible(capaciteDisponible);
            dto.setNombreCommentaires(nombreCommentaires);
        }
        
        return dto;
    }
}
