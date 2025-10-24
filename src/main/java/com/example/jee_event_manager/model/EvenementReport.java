package com.example.jee_event_manager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entité pour les signalements d'événements par les participants
 * À implémenter par l'équipe d'authentification
 */
@Entity
@Table(name = "evenement_report")
@Getter
@Setter
@NoArgsConstructor
public class EvenementReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "evenement_id", nullable = false)
    private Evenement evenement;
    
    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;
    
    @Column(nullable = false)
    private String reason; // INAPPROPRIATE, SPAM, MISLEADING, etc.
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Enum pour les raisons de signalement
    public enum ReportReason {
        INAPPROPRIATE("Contenu inapproprié"),
        SPAM("Spam"),
        MISLEADING("Information trompeuse"),
        DUPLICATE("Événement en double"),
        OTHER("Autre");
        
        private final String displayName;
        
        ReportReason(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
