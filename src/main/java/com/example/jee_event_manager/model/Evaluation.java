package com.example.jee_event_manager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "evaluation",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_evaluation_participant_evenement",
            columnNames = {"participant_id", "evenement_id"}
        )
    }
)
@AllArgsConstructor
@Getter
@Setter
public class Evaluation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evaluation_id")
    private Long id;
    
    @NotNull(message = "La note est obligatoire")
    @Min(value = 0, message = "La note doit être entre 0 et 5")
    @Max(value = 5, message = "La note doit être entre 0 et 5")
    @Column(nullable = false)
    private Integer note;
    
    @Size(max = 500, message = "Le texte de l'évaluation ne peut pas dépasser 500 caractères")
    @Column(columnDefinition = "TEXT")
    private String texte;
    
    @Column(nullable = false)
    private LocalDateTime horodatage;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false, columnDefinition = "BIGINT")
    @NotNull(message = "Le participant est obligatoire")
    private Participant participant;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evenement_id", nullable = false)
    @NotNull(message = "L'événement est obligatoire")
    private Evenement evenement;
    
    // Timestamps
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructeurs
    public Evaluation() {
        this.horodatage = LocalDateTime.now();
    }
    
    public Evaluation(Integer note, Participant participant, Evenement evenement) {
        this();
        this.note = note;
        this.participant = participant;
        this.evenement = evenement;
    }
    
    public Evaluation(Integer note, String texte, Participant participant, Evenement evenement) {
        this(note, participant, evenement);
        this.texte = texte;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (horodatage == null) {
            horodatage = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        horodatage = LocalDateTime.now();
    }
    
    /**
     * Vérifie si l'évaluation a un commentaire textuel
     */
    public boolean hasTexte() {
        return texte != null && !texte.trim().isEmpty();
    }
    
    /**
     * Retourne la note sous forme d'étoiles (pour affichage)
     */
    public String getEtoiles() {
        return "★".repeat(note) + "☆".repeat(5 - note);
    }
    
    @Override
    public String toString() {
        return "Evaluation{" +
                "id=" + id +
                ", note=" + note +
                ", texte='" + (texte != null ? texte.substring(0, Math.min(30, texte.length())) + "..." : "null") + "'" +
                ", horodatage=" + horodatage +
                ", participantId=" + (participant != null ? participant.getId() : null) +
                ", evenementId=" + (evenement != null ? evenement.getId() : null) +
                '}';
    }
}