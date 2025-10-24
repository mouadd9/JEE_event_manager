package com.example.jee_event_manager.dto;

import jakarta.validation.constraints.*;

public class InscriptionRequest {
    
    @NotNull(message = "L'identifiant de l'événement est obligatoire")
    private Long evenementId;
    
    @NotBlank(message = "Le type de billet est obligatoire")
    @Pattern(regexp = "STANDARD|VIP|PREMIUM", message = "Type de billet invalide (STANDARD, VIP ou PREMIUM)")
    private String typeBillet;
    
    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être au minimum 1")
    @Max(value = 10, message = "La quantité ne peut pas dépasser 10")
    private Integer quantite;
    
    // Constructeurs
    public InscriptionRequest() {}
    
    public InscriptionRequest(Long evenementId, String typeBillet, Integer quantite) {
        this.evenementId = evenementId;
        this.typeBillet = typeBillet;
        this.quantite = quantite;
    }
    
    // Getters et Setters
    public Long getEvenementId() {
        return evenementId;
    }
    
    public void setEvenementId(Long evenementId) {
        this.evenementId = evenementId;
    }
    
    public String getTypeBillet() {
        return typeBillet;
    }
    
    public void setTypeBillet(String typeBillet) {
        this.typeBillet = typeBillet;
    }
    
    public Integer getQuantite() {
        return quantite;
    }
    
    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }
}
