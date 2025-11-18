package com.example.jee_event_manager.dto;

import com.example.jee_event_manager.model.StatutInscription;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO pour les réponses contenant les informations d'inscription avec détails événement
 */
public class InscriptionDTO {
    
    private Long inscriptionId;
    private LocalDateTime dateInscription;
    private StatutInscription statut;
    private String typeBillet;
    private Integer quantite;
    
    // Détails de l'événement
    private Long evenementId;
    private String evenementTitre;
    private String evenementDescription;
    private LocalDateTime evenementDateDebut;
    private LocalDateTime evenementDateFin;
    private String evenementLieu;
    private Integer evenementCapacite;
    
    // Informations calculées
    private Integer capaciteDisponible;
    private Integer nombreInscrits;
    private String dateInscriptionFormatee;
    private String evenementDateFormatee;
    
    // Constructeurs
    public InscriptionDTO() {}
    
    // Getters et Setters
    public Long getInscriptionId() {
        return inscriptionId;
    }
    
    public void setInscriptionId(Long inscriptionId) {
        this.inscriptionId = inscriptionId;
    }
    
    public LocalDateTime getDateInscription() {
        return dateInscription;
    }
    
    public void setDateInscription(LocalDateTime dateInscription) {
        this.dateInscription = dateInscription;
        if (dateInscription != null) {
            this.dateInscriptionFormatee = dateInscription.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
    }
    
    public StatutInscription getStatut() {
        return statut;
    }
    
    public void setStatut(StatutInscription statut) {
        this.statut = statut;
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
    
    public String getEvenementDescription() {
        return evenementDescription;
    }
    
    public void setEvenementDescription(String evenementDescription) {
        this.evenementDescription = evenementDescription;
    }
    
    public LocalDateTime getEvenementDateDebut() {
        return evenementDateDebut;
    }
    
    public void setEvenementDateDebut(LocalDateTime evenementDateDebut) {
        this.evenementDateDebut = evenementDateDebut;
        if (evenementDateDebut != null) {
            this.evenementDateFormatee = evenementDateDebut.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
    }
    
    public LocalDateTime getEvenementDateFin() {
        return evenementDateFin;
    }
    
    public void setEvenementDateFin(LocalDateTime evenementDateFin) {
        this.evenementDateFin = evenementDateFin;
    }
    
    public String getEvenementLieu() {
        return evenementLieu;
    }
    
    public void setEvenementLieu(String evenementLieu) {
        this.evenementLieu = evenementLieu;
    }
    
    public Integer getEvenementCapacite() {
        return evenementCapacite;
    }
    
    public void setEvenementCapacite(Integer evenementCapacite) {
        this.evenementCapacite = evenementCapacite;
    }
    
    public Integer getCapaciteDisponible() {
        return capaciteDisponible;
    }
    
    public void setCapaciteDisponible(Integer capaciteDisponible) {
        this.capaciteDisponible = capaciteDisponible;
    }
    
    public Integer getNombreInscrits() {
        return nombreInscrits;
    }
    
    public void setNombreInscrits(Integer nombreInscrits) {
        this.nombreInscrits = nombreInscrits;
    }
    
    public String getDateInscriptionFormatee() {
        return dateInscriptionFormatee;
    }
    
    public String getEvenementDateFormatee() {
        return evenementDateFormatee;
    }
    
    /**
     * Vérifier si l'événement est passé
     */
    public boolean isEvenementPasse() {
        return evenementDateFin != null && evenementDateFin.isBefore(LocalDateTime.now());
    }
    
    /**
     * Vérifier si l'inscription peut être annulée
     */
    public boolean isPeutAnnuler() {
        return statut != StatutInscription.ANNULEE && 
               !isEvenementPasse();
    }
    
    /**
     * Vérifier si l'événement est dans la fenêtre de 7 jours après sa fin
     * Permet aux participants de commenter et évaluer après l'événement
     */
    public boolean isEvenementDansFenitrePostEvenement() {
        if (evenementDateFin == null) {
            return false;
        }
        LocalDateTime maintenant = LocalDateTime.now();
        LocalDateTime limiteSept = evenementDateFin.plusDays(7);
        
        return maintenant.isAfter(evenementDateFin) && maintenant.isBefore(limiteSept);
    }
    
    /**
     * Vérifier si le participant peut évaluer l'événement
     * (événement passé ou dans la fenêtre de 7 jours)
     */
    public boolean isPeutEvaluer() {
        return statut == StatutInscription.ACCEPTEE && 
               (isEvenementPasse() || isEvenementDansFenitrePostEvenement());
    }
    
    /**
     * Vérifier si le participant peut commenter l'événement
     * (événement passé ou dans la fenêtre de 7 jours)
     */
    public boolean isPeutCommenter() {
        return statut == StatutInscription.ACCEPTEE && 
               (isEvenementPasse() || isEvenementDansFenitrePostEvenement());
    }
}
