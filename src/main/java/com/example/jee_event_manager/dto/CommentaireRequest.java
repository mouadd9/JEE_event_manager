package com.example.jee_event_manager.dto;

import jakarta.validation.constraints.*;

public class CommentaireRequest {
    
    @NotNull(message = "L'identifiant de l'événement est obligatoire")
    private Integer evenementId;
    
    @NotBlank(message = "Le texte du commentaire ne peut pas être vide")
    @Size(min = 1, max = 1000, message = "Le commentaire doit contenir entre 1 et 1000 caractères")
    private String texte;
    public CommentaireRequest() {}
    
    public CommentaireRequest(Integer evenementId, String texte) {
        this.evenementId = evenementId;
        this.texte = texte;
    }
    
    public Integer getEvenementId() {
        return evenementId;
    }
    
    public void setEvenementId(Integer evenementId) {
        this.evenementId = evenementId;
    }
    
    public String getTexte() {
        return texte;
    }
    
    public void setTexte(String texte) {
        this.texte = texte;
    }
}
