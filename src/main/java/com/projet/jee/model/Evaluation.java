package com.projet.jee.model;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Evaluation entity - represents ratings (1-5 stars) given by participants on events.
 *
 * Business Rules:
 * - Only participants who attended an event can rate it
 * - One participant can rate an event only once
 * - Ratings are on a scale of 1 to 5 stars
 * - Ratings contribute to the overall event and organizer rating
 */
@Entity
@Table(name = "evaluation",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_evaluation_participant_evenement",
                columnNames = {"id_evenement", "id_participant"}
        ),
        indexes = {
                @Index(name = "idx_evaluation_evenement", columnList = "id_evenement"),
                @Index(name = "idx_evaluation_participant", columnList = "id_participant")
        })
public class Evaluation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluation")
    private Long id;

    @NotNull(message = "L'événement est obligatoire")
    @Column(name = "id_evenement", nullable = false)
    private Long evenementId;

    @NotNull(message = "Le participant est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_participant", nullable = false)
    private Participant participant;

    @NotNull(message = "La note est obligatoire")
    @Min(value = 1, message = "La note minimale est 1")
    @Max(value = 5, message = "La note maximale est 5")
    @Column(name = "note", nullable = false)
    private Integer note;

    @Column(name = "commentaire_note", columnDefinition = "TEXT")
    private String commentaireNote;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    // Constructors
    public Evaluation() {
    }

    public Evaluation(Long evenementId, Participant participant, Integer note) {
        this.evenementId = evenementId;
        this.participant = participant;
        this.note = note;
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

    public Integer getNote() {
        return note;
    }

    public void setNote(Integer note) {
        if (note < 1 || note > 5) {
            throw new IllegalArgumentException("La note doit être entre 1 et 5");
        }
        this.note = note;
    }

    public String getCommentaireNote() {
        return commentaireNote;
    }

    public void setCommentaireNote(String commentaireNote) {
        this.commentaireNote = commentaireNote;
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

    // Business methods
    public boolean canBeModified() {
        // Ratings can be modified within 7 days of creation
        if (dateCreation == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(dateCreation.plusDays(7));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Evaluation)) return false;
        Evaluation that = (Evaluation) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Evaluation{" +
                "id=" + id +
                ", evenementId=" + evenementId +
                ", participantId=" + (participant != null ? participant.getId() : null) +
                ", note=" + note +
                ", dateCreation=" + dateCreation +
                '}';
    }
}
