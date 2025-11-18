package com.example.jee_event_manager.util;

import com.example.jee_event_manager.dto.*;
import com.example.jee_event_manager.model.*;
import java.util.stream.Collectors;


public class DTOMapper {
    public static InscriptionDTO toInscriptionDTO(Inscription inscription) {
        if (inscription == null) return null;
        
        InscriptionDTO dto = new InscriptionDTO();
        dto.setInscriptionId(inscription.getId());
        dto.setDateInscription(inscription.getDateInscription());
        dto.setStatut(inscription.getStatut());
        dto.setTypeBillet(inscription.getTypeBillet());
        dto.setQuantite(inscription.getQuantite());
        
        // Détails de l'événement
        if (inscription.getEvenement() != null) {
            Evenement evt = inscription.getEvenement();
            dto.setEvenementId(evt.getId());
            dto.setEvenementTitre(evt.getTitre());
            dto.setEvenementDescription(evt.getDescription());
            dto.setEvenementDateDebut(evt.getDateDebut());
            dto.setEvenementDateFin(evt.getDateFin());
            dto.setEvenementLieu(evt.getLieu());
            dto.setEvenementCapacite(evt.getCapacite());
        }
        
        return dto;
    }
    
    /**
     * Convertir un Commentaire en CommentaireDTO
     */
    public static CommentaireDTO toCommentaireDTO(Commentaire commentaire) {
        if (commentaire == null) return null;
        
        CommentaireDTO dto = new CommentaireDTO();
        dto.setCommentaireId(commentaire.getId());
        dto.setTexte(commentaire.getTexte());
        dto.setHorodatage(commentaire.getHorodatage());
        
        // Informations du participant
        if (commentaire.getParticipant() != null) {
            dto.setParticipantId(commentaire.getParticipant().getId());
            dto.setParticipantNom(commentaire.getParticipant().getNom());
        }
        
        // Informations de l'événement
        if (commentaire.getEvenement() != null) {
            dto.setEvenementId(commentaire.getEvenement().getId());
            dto.setEvenementTitre(commentaire.getEvenement().getTitre());
        }
        
        return dto;
    }
    
    /**
     * Convertir une Evaluation en EvaluationDTO
     */
    public static EvaluationDTO toEvaluationDTO(Evaluation evaluation) {
        if (evaluation == null) return null;
        
        EvaluationDTO dto = new EvaluationDTO();
        dto.setEvaluationId(evaluation.getId());
        dto.setNote(evaluation.getNote());
        dto.setTexte(evaluation.getTexte());
        dto.setHorodatage(evaluation.getHorodatage());
        
        // Informations du participant
        if (evaluation.getParticipant() != null) {
            dto.setParticipantId(evaluation.getParticipant().getId());
            dto.setParticipantNom(evaluation.getParticipant().getNom());
        }
        
        // Informations de l'événement
        if (evaluation.getEvenement() != null) {
            dto.setEvenementId(evaluation.getEvenement().getId());
            dto.setEvenementTitre(evaluation.getEvenement().getTitre());
        }
        
        return dto;
    }
    
    /**
     * Convertir un Evenement en EvenementDetailDTO
     */
    public static EvenementDetailDTO toEvenementDetailDTO(Evenement evenement) {
        if (evenement == null) return null;
        
        EvenementDetailDTO dto = new EvenementDetailDTO();
        dto.setEvenementId(evenement.getId());
        dto.setTitre(evenement.getTitre());
        dto.setDescription(evenement.getDescription());
        dto.setDateDebut(evenement.getDateDebut());
        dto.setDateFin(evenement.getDateFin());
        dto.setStatut(evenement.getStatut());
        dto.setLieu(evenement.getLieu());
        dto.setLatitude(evenement.getLatitude());
        dto.setLongitude(evenement.getLongitude());
        dto.setCapacite(evenement.getCapacite());
        dto.setImageUrl(evenement.getImageUrl());
        
        // Informations organisateur
        if (evenement.getOrganisateur() != null) {
            dto.setOrganisateurId(evenement.getOrganisateur().getId());
            dto.setOrganisateurNom(evenement.getOrganisateur().getNom());
            dto.setOrganisateurEmail(evenement.getOrganisateur().getEmail());
        }
        
        // Catégories
        if (evenement.getCategories() != null) {
            dto.setCategories(
                evenement.getCategories().stream()
                    .map(Categorie::getNom)
                    .collect(Collectors.toList())
            );
        }
        
        return dto;
    }
}
