package com.example.jee_event_manager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.sql.Timestamp;

@Entity
@Table(name = "commentaire")
@AllArgsConstructor
@Getter
@Setter
public class Commentaire {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commentaire_id")
    private Long id;
    
    @NotBlank(message = "Le texte du commentaire ne peut pas être vide")
    @Size(max = 1000, message = "Le commentaire ne peut pas dépasser 1000 caractères")
    @Column(nullable = false, columnDefinition = "TEXT")
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
     public Commentaire() {
        this.horodatage = LocalDateTime.now();
    }
    
    public Commentaire(String texte, Participant participant, Evenement evenement) {
        this();
        this.texte = texte;
        this.participant = participant;
        this.evenement = evenement;
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
    }
    
    /**
     * Helper method for JSTL <fmt:formatDate> tag.
     * Converts LocalDateTime to java.util.Date for JSP formatting.
     */
    public java.util.Date getHorodatageAsDate() {
        if (this.horodatage == null) {
            return null;
        }
        return Timestamp.valueOf(this.horodatage);
    }
    
    @Override
    public String toString() {
        return "Commentaire{" +
                "id=" + id +
                ", texte='" + texte.substring(0, Math.min(50, texte.length())) + "...'" +
                ", horodatage=" + horodatage +
                ", participantId=" + (participant != null ? participant.getId() : null) +
                ", evenementId=" + (evenement != null ? evenement.getId() : null) +
                '}';
    }
}