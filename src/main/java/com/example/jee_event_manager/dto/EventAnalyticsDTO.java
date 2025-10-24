package com.example.jee_event_manager.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventAnalyticsDTO implements Serializable {
    private Long evenementId;
    private String titre;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String lieu;
    private Integer capacite;
    private String imageUrl;
    
    // Statistics
    private Long nombreInscrits;
    private Integer capaciteDisponible;
    private Double noteMoyenne;
    private Long nombreEvaluations;
    private Long nombreCommentaires;
    
    // Participant details
    private List<ParticipantDetailsDTO> participants;
    
    // Comments with participant info
    private List<CommentaireWithParticipantDTO> commentaires;
    
    // Reviews with participant info
    private List<EvaluationWithParticipantDTO> evaluations;
    
    // Helper methods
    public Integer getPourcentageCapacite() {
        if (capacite == null || capacite <= 0 || nombreInscrits == null) {
            return 0;
        }
        return (int) ((double) nombreInscrits / capacite * 100);
    }
    
    public boolean isComplet() {
        return capacite != null && capaciteDisponible != null && capaciteDisponible <= 0;
    }
    
    public String getStatutCapacite() {
        if (isComplet()) {
            return "Complet";
        } else if (getPourcentageCapacite() >= 80) {
            return "Presque complet";
        } else if (getPourcentageCapacite() >= 50) {
            return "Moitié rempli";
        } else {
            return "Places disponibles";
        }
    }
}
