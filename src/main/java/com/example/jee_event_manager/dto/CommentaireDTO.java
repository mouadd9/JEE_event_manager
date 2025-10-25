package com.example.jee_event_manager.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;

public class CommentaireDTO {
    
    private Long commentaireId;
    private String texte;
    private LocalDateTime horodatage;
    private String horodatageFormate;
    
    private Long participantId;
    private String participantNom;
    
    private Long evenementId;
    private String evenementTitre;
    
    public CommentaireDTO() {}
    
    public Long getCommentaireId() {
        return commentaireId;
    }
    
    public void setCommentaireId(Long commentaireId) {
        this.commentaireId = commentaireId;
    }
    
    public String getTexte() {
        return texte;
    }
    
    public void setTexte(String texte) {
        this.texte = texte;
    }
    
    public LocalDateTime getHorodatage() {
        return horodatage;
    }
    
    public void setHorodatage(LocalDateTime horodatage) {
        this.horodatage = horodatage;
        if (horodatage != null) {
            this.horodatageFormate = horodatage.format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"));
        }
    }
    
    public String getHorodatageFormate() {
        return horodatageFormate;
    }
    
    /**
     * Helper method for JSTL <fmt:formatDate> tag.
     * Converts LocalDateTime to java.util.Date for JSP formatting.
     */
    public java.util.Date getHorodatageAsDate() {
        if (this.horodatage == null) {
            return null;
        }
        return Timestamp.valueOf(this.horodatage);
    }
    
    public Long getParticipantId() {
        return participantId;
    }
    
    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }
    
    public String getParticipantNom() {
        return participantNom;
    }
    
    public void setParticipantNom(String participantNom) {
        this.participantNom = participantNom;
    }
    
    public Long getEvenementId() {
        return evenementId;
    }
    
    public void setEvenementId(Long evenementId) {
        this.evenementId = evenementId;
    }
    
    public String getEvenementTitre() {
        return evenementTitre;
    }
    
    public void setEvenementTitre(String evenementTitre) {
        this.evenementTitre = evenementTitre;
    }
    
    public String getTempsEcoule() {
        if (horodatage == null) return "";
        
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(horodatage, now).toMinutes();
        
        if (minutes < 1) return "À l'instant";
        if (minutes < 60) return minutes + " min";
        
        long heures = minutes / 60;
        if (heures < 24) return heures + "h";
        
        long jours = heures / 24;
        if (jours < 7) return jours + "j";
        
        return horodatageFormate;
    }
}
