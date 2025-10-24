package com.example.jee_event_manager.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class EvaluationReponseDTO implements Serializable {
    private Long id;
    private String texte;
    private LocalDateTime createdAt;
    
    // Organizer info
    private Long organisateurId;
    private String organisateurNom;
    
    // Helper methods
    public String getOrganisateurDisplayName() {
        return organisateurNom != null ? organisateurNom : "Organisateur";
    }
}
