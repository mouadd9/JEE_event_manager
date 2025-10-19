package com.example.jee_event_manager.dto;

import com.example.jee_event_manager.model.StatutEvenement;
import com.example.jee_event_manager.model.StatutInscription;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO pour les détails complets d'un événement incluant statistiques et statut participant
 */
public class EvenementDetailDTO {
    
    private Integer evenementId;
    private String titre;
    private String description;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String dateDebutFormatee;
    private String dateFinFormatee;
    private StatutEvenement statut;
    private String lieu;
    private Integer capacite;
    private String imageUrl;
    
    // Informations organisateur
    private Long organisateurId;
    private String organisateurNom;
    
    // Catégories
    private List<String> categories = new ArrayList<>();
    
    // Statistiques
    private Double noteMoyenne;
    private Long nombreEvaluations;
    private Long nombreInscrits;
    private Integer capaciteDisponible;
    private Long nombreCommentaires;
    
    // Statut pour le participant connecté (optionnel)
    private StatutInscription statutInscription;
    private boolean participantInscrit;
    private boolean participantPeutEvaluer;
    private Integer evaluationParticipant; // Note déjà donnée par le participant
    
    // Constructeurs
    public EvenementDetailDTO() {}
    
    // Getters et Setters
    public Integer getEvenementId() {
        return evenementId;
    }
    
    public void setEvenementId(Integer evenementId) {
        this.evenementId = evenementId;
    }
    
    public String getTitre() {
        return titre;
    }
    
    public void setTitre(String titre) {
        this.titre = titre;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getDateDebut() {
        return dateDebut;
    }
    
    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
        if (dateDebut != null) {
            this.dateDebutFormatee = dateDebut.format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"));
        }
    }
    
    public LocalDateTime getDateFin() {
        return dateFin;
    }
    
    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
        if (dateFin != null) {
            this.dateFinFormatee = dateFin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"));
        }
    }
    
    public String getDateDebutFormatee() {
        return dateDebutFormatee;
    }
    
    public String getDateFinFormatee() {
        return dateFinFormatee;
    }
    
    public StatutEvenement getStatut() {
        return statut;
    }
    
    public void setStatut(StatutEvenement statut) {
        this.statut = statut;
    }
    
    public String getLieu() {
        return lieu;
    }
    
    public void setLieu(String lieu) {
        this.lieu = lieu;
    }
    
    public Integer getCapacite() {
        return capacite;
    }
    
    public void setCapacite(Integer capacite) {
        this.capacite = capacite;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public Long getOrganisateurId() {
        return organisateurId;
    }
    
    public void setOrganisateurId(Long organisateurId) {
        this.organisateurId = organisateurId;
    }
    
    public String getOrganisateurNom() {
        return organisateurNom;
    }
    
    public void setOrganisateurNom(String organisateurNom) {
        this.organisateurNom = organisateurNom;
    }
    
    public List<String> getCategories() {
        return categories;
    }
    
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
    
    public Double getNoteMoyenne() {
        return noteMoyenne;
    }
    
    public void setNoteMoyenne(Double noteMoyenne) {
        this.noteMoyenne = noteMoyenne;
    }
    
    public Long getNombreEvaluations() {
        return nombreEvaluations;
    }
    
    public void setNombreEvaluations(Long nombreEvaluations) {
        this.nombreEvaluations = nombreEvaluations;
    }
    
    public Long getNombreInscrits() {
        return nombreInscrits;
    }
    
    public void setNombreInscrits(Long nombreInscrits) {
        this.nombreInscrits = nombreInscrits;
    }
    
    public Integer getCapaciteDisponible() {
        return capaciteDisponible;
    }
    
    public void setCapaciteDisponible(Integer capaciteDisponible) {
        this.capaciteDisponible = capaciteDisponible;
    }
    
    public Long getNombreCommentaires() {
        return nombreCommentaires;
    }
    
    public void setNombreCommentaires(Long nombreCommentaires) {
        this.nombreCommentaires = nombreCommentaires;
    }
    
    public StatutInscription getStatutInscription() {
        return statutInscription;
    }
    
    public void setStatutInscription(StatutInscription statutInscription) {
        this.statutInscription = statutInscription;
        this.participantInscrit = (statutInscription != null && statutInscription != StatutInscription.ANNULEE);
        this.participantPeutEvaluer = this.participantInscrit;
    }
    
    public boolean isParticipantInscrit() {
        return participantInscrit;
    }
    
    public boolean isParticipantPeutEvaluer() {
        return participantPeutEvaluer;
    }
    
    public Integer getEvaluationParticipant() {
        return evaluationParticipant;
    }
    
    public void setEvaluationParticipant(Integer evaluationParticipant) {
        this.evaluationParticipant = evaluationParticipant;
    }
    
    /**
     * Retourne la note moyenne formatée (ex: "4.5/5")
     */
    public String getNoteMoyenneFormatee() {
        if (noteMoyenne == null || noteMoyenne == 0.0) return "Aucune évaluation";
        return String.format("%.1f/5", noteMoyenne);
    }
    
    /**
     * Retourne les étoiles pour la note moyenne
     */
    public String getEtoilesMoyenne() {
        if (noteMoyenne == null || noteMoyenne == 0.0) return "☆☆☆☆☆";
        int noteArrondie = (int) Math.round(noteMoyenne);
        return "★".repeat(noteArrondie) + "☆".repeat(5 - noteArrondie);
    }
    
    /**
     * Vérifie si l'événement est complet
     */
    public boolean isComplet() {
        return capaciteDisponible != null && capaciteDisponible <= 0;
    }
    
    /**
     * Vérifie si l'événement est passé
     */
    public boolean isPasse() {
        return dateFin != null && dateFin.isBefore(LocalDateTime.now());
    }
    
    /**
     * Vérifie si l'inscription est possible
     */
    public boolean isPeutInscrire() {
        return !isComplet() && !isPasse() && statut == StatutEvenement.PUBLIE && !participantInscrit;
    }
}
