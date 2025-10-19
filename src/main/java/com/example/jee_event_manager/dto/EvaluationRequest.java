package com.example.jee_event_manager.dto;

import jakarta.validation.constraints.*;

/**
 * DTO pour les requêtes d'ajout/modification d'évaluation
 */
public class EvaluationRequest {
    
    @NotNull(message = "L'identifiant de l'événement est obligatoire")
    private Integer evenementId;
    
    @NotNull(message = "La note est obligatoire")
    @Min(value = 0, message = "La note doit être entre 0 et 5")
    @Max(value = 5, message = "La note doit être entre 0 et 5")
    private Integer note;
    
    @Size(max = 500, message = "Le texte de l'évaluation ne peut pas dépasser 500 caractères")
    private String texte;
    
    // Constructeurs
    public EvaluationRequest() {}
    
    public EvaluationRequest(Integer evenementId, Integer note, String texte) {
        this.evenementId = evenementId;
        this.note = note;
        this.texte = texte;
    }
    
    // Getters et Setters
    public Integer getEvenementId() {
        return evenementId;
    }
    
    public void setEvenementId(Integer evenementId) {
        this.evenementId = evenementId;
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
}
