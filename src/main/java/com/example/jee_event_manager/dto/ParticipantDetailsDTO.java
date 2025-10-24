package com.example.jee_event_manager.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ParticipantDetailsDTO implements Serializable {
    private Long id;
    private String nom;
    private String email;
    private String imageUrl; // Placeholder avatar
    private LocalDateTime createdAt;
    private String telephone;
    private String preferences;
    
    // Statistics
    private Long nombreInscriptions;
    private Long nombreCommentaires;
    private Long nombreEvaluations;
    
    // Helper methods for display
    public String getInitials() {
        if (nom == null || nom.trim().isEmpty()) {
            return "??";
        }
        String[] parts = nom.trim().split("\\s+");
        if (parts.length >= 2) {
            return (parts[0].charAt(0) + "" + parts[1].charAt(0)).toUpperCase();
        } else {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
    }
    
    public String getDisplayName() {
        return nom != null ? nom : "Participant";
    }
    
    public String getAccountAge() {
        if (createdAt == null) {
            return "Inconnu";
        }
        
        LocalDateTime now = LocalDateTime.now();
        long days = java.time.Duration.between(createdAt, now).toDays();
        
        if (days < 1) {
            return "Aujourd'hui";
        } else if (days < 7) {
            return days + " jour" + (days > 1 ? "s" : "");
        } else if (days < 30) {
            long weeks = days / 7;
            return weeks + " semaine" + (weeks > 1 ? "s" : "");
        } else if (days < 365) {
            long months = days / 30;
            return months + " mois";
        } else {
            long years = days / 365;
            return years + " an" + (years > 1 ? "s" : "");
        }
    }
    
    public boolean hasPhone() {
        return telephone != null && !telephone.trim().isEmpty();
    }
    
    public boolean hasPreferences() {
        return preferences != null && !preferences.trim().isEmpty();
    }
}
