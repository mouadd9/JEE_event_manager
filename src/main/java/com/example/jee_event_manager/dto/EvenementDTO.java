package com.example.jee_event_manager.dto;

import com.example.jee_event_manager.model.StatutEvenement;
import com.example.jee_event_manager.model.StatutInscription;
import lombok.Data;

import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO unifié pour les événements - combine EventDto (Branch B) et EvenementDetailDTO (Branch A)
 */
@Data
public class EvenementDTO {
    
    // === Champs de base (de EventDto) ===
    private Long id;
    private String titre;
    private String description;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String lieu;
    private StatutEvenement statut;
    private String organizerName; // Nom de l'organisateur (pour compatibilité)
    private Double latitude;
    private Double longitude;
    
    // === Champs étendus (de EvenementDetailDTO) ===
    private Integer capacite;
    private String imageUrl;
    
    // === Informations organisateur ===
    private Long organisateurId;
    private String organisateurNom;
    
    // === Catégories ===
    private List<String> categories = new ArrayList<>();
    
    // === Statistiques ===
    private Double noteMoyenne;
    private Long nombreEvaluations;
    private Long nombreInscrits;
    private Integer capaciteDisponible;
    private Long nombreCommentaires;
    
    // === Statut pour le participant connecté (optionnel) ===
    private StatutInscription statutInscription;
    private boolean participantInscrit;
    private boolean participantPeutEvaluer;
    private Integer evaluationParticipant; // Note déjà donnée par le participant
    
    // === Méthodes utilitaires pour JSTL ===
    
    /**
     * Helper method for JSTL <fmt:formatDate> tag.
     * It converts the modern LocalDateTime to a java.util.Date.
     */
    public java.util.Date getDateDebutAsDate() {
        if (this.dateDebut == null) {
            return null;
        }
        return Timestamp.valueOf(this.dateDebut);
    }

    /**
     * Helper method for JSTL <fmt:formatDate> tag.
     * It converts the modern LocalDateTime to a java.util.Date.
     */
    public java.util.Date getDateFinAsDate() {
        if (this.dateFin == null) {
            return null;
        }
        return Timestamp.valueOf(this.dateFin);
    }
    
    /**
     * Formate la date de début pour l'affichage
     */
    public String getDateDebutFormatee() {
        if (this.dateDebut == null) {
            return null;
        }
        return this.dateDebut.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
    
    /**
     * Formate la date de fin pour l'affichage
     */
    public String getDateFinFormatee() {
        if (this.dateFin == null) {
            return null;
        }
        return this.dateFin.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
    
    /**
     * Vérifie si l'événement est complet (capacité atteinte)
     */
    public boolean isComplet() {
        return capaciteDisponible != null && capaciteDisponible <= 0;
    }
    
    /**
     * Vérifie si l'événement est publié
     */
    public boolean isPublie() {
        return statut == StatutEvenement.PUBLIE;
    }
    
    /**
     * Vérifie si l'événement est annulé
     */
    public boolean isAnnule() {
        return statut == StatutEvenement.ANNULE;
    }
    
    /**
     * Retourne le pourcentage de capacité utilisée
     */
    public double getPourcentageCapacite() {
        if (capacite == null || capacite == 0) {
            return 0.0;
        }
        long placesOccupees = capacite - (capaciteDisponible != null ? capaciteDisponible : 0);
        return (double) placesOccupees / capacite * 100.0;
    }
}
