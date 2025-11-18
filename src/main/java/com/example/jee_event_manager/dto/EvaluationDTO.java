package com.example.jee_event_manager.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;

public class EvaluationDTO {
    
    private Long evaluationId;
    private Integer note;
    private String texte;
    private LocalDateTime horodatage;
    private String horodatageFormate;
    
    // Informations du participant
    private Long participantId;
    private String participantNom;
    
    // Informations de l'événement
    private Long evenementId;
    private String evenementTitre;
    
    // Constructeurs
    public EvaluationDTO() {}
    
    // Getters et Setters
    public Long getEvaluationId() {
        return evaluationId;
    }
    
    public void setEvaluationId(Long evaluationId) {
        this.evaluationId = evaluationId;
    }
    
    public Integer getNote() {
        return note;
    }
    
    public void setNote(Integer note) {
        this.note = note;
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
    
    /**
     * Retourne les étoiles sous forme de chaîne
     */
    public String getEtoiles() {
        if (note == null) return "";
        return "★".repeat(note) + "☆".repeat(5 - note);
    }
    
    /**
     * Vérifie si l'évaluation a un texte
     */
    public boolean hasTexte() {
        return texte != null && !texte.trim().isEmpty();
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
}
