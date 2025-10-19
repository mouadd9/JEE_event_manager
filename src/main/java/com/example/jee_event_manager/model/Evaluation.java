package com.example.jee_event_manager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
public class Evaluation extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evaluation_id")
    private Integer id;
    
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
    
    // Getters et Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
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
    }
    
    public Participant getParticipant() {
        return participant;
    }
    
    public void setParticipant(Participant participant) {
        this.participant = participant;
    }
    
    public Evenement getEvenement() {
        return evenement;
    }
    
    public void setEvenement(Evenement evenement) {
        this.evenement = evenement;
    }
    
    // Méthode de validation personnalisée
    @Override
    public boolean validate() {
        // Validation de la note
        if (note == null) {
            throw new IllegalStateException("La note est obligatoire");
        }
        
        if (note < 0 || note > 5) {
            throw new IllegalStateException("La note doit être entre 0 et 5 étoiles");
        }
        
        // Validation du texte optionnel
        if (texte != null && texte.length() > 500) {
            throw new IllegalStateException("Le texte de l'évaluation ne peut pas dépasser 500 caractères");
        }
        
        // Validation des relations
        if (participant == null) {
            throw new IllegalStateException("Le participant est obligatoire");
        }
        
        if (evenement == null) {
            throw new IllegalStateException("L'événement est obligatoire");
        }
        
        return true;
    }
    
    // Méthode PrePersist pour initialiser l'horodatage
    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
        if (horodatage == null) {
            horodatage = LocalDateTime.now();
        }
    }
    
    // Méthode PreUpdate pour mettre à jour l'horodatage lors de modification
    @PreUpdate
    @Override
    protected void onUpdate() {
        super.onUpdate();
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
