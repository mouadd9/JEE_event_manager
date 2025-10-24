package com.example.jee_event_manager.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EvaluationWithParticipantDTO implements Serializable {
    private Long id;
    private Integer note;
    private String commentaire;
    private LocalDateTime createdAt;
    
    // Participant info
    private Long participantId;
    private String participantNom;
    private String participantEmail;
    private String participantInitials;
    
    // Replies from organizer
    private List<EvaluationReponseDTO> reponses;
    
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
    
    public String getNoteDisplay() {
        if (note == null) return "N/A";
        return note + "/5";
    }
    
    public String getStarsDisplay() {
        if (note == null) return "☆☆☆☆☆";
        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            stars.append(i <= note ? "★" : "☆");
        }
        return stars.toString();
    }
}
