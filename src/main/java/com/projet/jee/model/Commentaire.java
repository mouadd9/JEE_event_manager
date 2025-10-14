package com.projet.jee.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Commentaire entity - represents comments left by participants on events.
 *
 * Business Rules:
 * - Only participants who attended an event can comment on it
 * - Comments can be moderated by administrators
 * - Comments support soft delete (marked as deleted but not removed from database)
 */
@Entity
@Table(name = "commentaire", indexes = {
        @Index(name = "idx_commentaire_evenement", columnList = "id_evenement"),
        @Index(name = "idx_commentaire_participant", columnList = "id_participant"),
        @Index(name = "idx_commentaire_date", columnList = "date_creation")
})
public class Commentaire implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_commentaire")
    private Long id;

    @NotNull(message = "L'événement est obligatoire")
    @Column(name = "id_evenement", nullable = false)
    private Long evenementId;

    @NotNull(message = "Le participant est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_participant", nullable = false)
    private Participant participant;

    @NotBlank(message = "Le contenu du commentaire est obligatoire")
    @Size(min = 10, max = 2000, message = "Le commentaire doit contenir entre 10 et 2000 caractères")
    @Column(name = "contenu", columnDefinition = "TEXT", nullable = false)
    private String contenu;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Column(name = "est_modere", nullable = false)
    private Boolean estModere = false;

    @Column(name = "est_supprime", nullable = false)
    private Boolean estSupprime = false;

    @Column(name = "raison_moderation")
    private String raisonModeration;

    @Column(name = "id_moderateur")
    private Long moderateurId;

    // Constructors
    public Commentaire() {
    }

    public Commentaire(Long evenementId, Participant participant, String contenu) {
        this.evenementId = evenementId;
        this.participant = participant;
        this.contenu = contenu;
    }

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEvenementId() {
        return evenementId;
    }

    public void setEvenementId(Long evenementId) {
        this.evenementId = evenementId;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    public Boolean getEstModere() {
        return estModere;
    }

    public void setEstModere(Boolean estModere) {
        this.estModere = estModere;
    }

    public Boolean getEstSupprime() {
        return estSupprime;
    }

    public void setEstSupprime(Boolean estSupprime) {
        this.estSupprime = estSupprime;
    }

    public String getRaisonModeration() {
        return raisonModeration;
    }

    public void setRaisonModeration(String raisonModeration) {
        this.raisonModeration = raisonModeration;
    }

    public Long getModerateurId() {
        return moderateurId;
    }

    public void setModerateurId(Long moderateurId) {
        this.moderateurId = moderateurId;
    }

    // Business methods
    public void moderer(Long moderateurId, String raison) {
        this.estModere = true;
        this.moderateurId = moderateurId;
        this.raisonModeration = raison;
    }

    public void supprimerSoftDelete() {
        this.estSupprime = true;
    }

    public boolean isEditable() {
        // Comments can be edited within 30 minutes of creation
        if (dateCreation == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(dateCreation.plusMinutes(30));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Commentaire)) return false;
        Commentaire that = (Commentaire) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Commentaire{" +
                "id=" + id +
                ", evenementId=" + evenementId +
                ", participantId=" + (participant != null ? participant.getId() : null) +
                ", dateCreation=" + dateCreation +
                ", estSupprime=" + estSupprime +
                '}';
    }
}
