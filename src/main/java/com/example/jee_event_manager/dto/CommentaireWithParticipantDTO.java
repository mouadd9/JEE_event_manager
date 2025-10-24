package com.example.jee_event_manager.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentaireWithParticipantDTO implements Serializable {
    private Long id;
    private String texte;
    private LocalDateTime createdAt;
    
    // Participant info
    private Long participantId;
    private String participantNom;
    private String participantEmail;
    private String participantInitials;
    
    // Replies from organizer
    private List<CommentaireReponseDTO> reponses;
    
    // Helper methods
    public String getParticipantDisplayName() {
        return participantNom != null ? participantNom : "Participant";
    }
    
    public String getParticipantInitials() {
        if (participantNom == null || participantNom.trim().isEmpty()) {
            return "??";
        }
        String[] parts = participantNom.trim().split("\\s+");
        if (parts.length >= 2) {
            return (parts[0].charAt(0) + "" + parts[1].charAt(0)).toUpperCase();
        } else {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
    }
    
    public boolean hasReponses() {
        return reponses != null && !reponses.isEmpty();
    }
}
