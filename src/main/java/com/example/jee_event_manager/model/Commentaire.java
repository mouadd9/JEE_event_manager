package com.example.jee_event_manager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "commentaire")
public class Commentaire extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commentaire_id")
    private Integer id;
    
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
    
    // Getters et Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
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
        // Validation du texte
        if (texte == null || texte.trim().isEmpty()) {
            throw new IllegalStateException("Le texte du commentaire ne peut pas être vide");
        }
        
        if (texte.length() > 1000) {
            throw new IllegalStateException("Le commentaire ne peut pas dépasser 1000 caractères");
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
