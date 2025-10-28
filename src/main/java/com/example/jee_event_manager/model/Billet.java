package com.example.jee_event_manager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "billet")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Billet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "billet_id")
    private Long id;
    
    @NotNull(message = "Le numéro de billet est obligatoire")
    @Column(name = "numero_billet", unique = true, nullable = false, length = 50)
    private String numeroBillet;
    
    @NotNull(message = "Le type de billet est obligatoire")
    @Column(name = "type_billet", nullable = false, length = 20)
    private String typeBillet; // STANDARD, VIP, PREMIUM
    
    @NotNull(message = "Le statut du billet est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutBillet statut = StatutBillet.VALIDE;
    
    @Column(name = "chemin_fichier", length = 500)
    private String cheminFichier; // Chemin vers le fichier PDF stocké
    
    @Column(name = "date_generation", nullable = false)
    private LocalDateTime dateGeneration = LocalDateTime.now();
    
    @Column(name = "date_utilisation")
    private LocalDateTime dateUtilisation; // Date d'utilisation du billet
    
    @Column(name = "utilise", nullable = false)
    private Boolean utilise = false;
    
    // Relations
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inscription_id", nullable = false, unique = true)
    private Inscription inscription;
    
    // Timestamps
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (dateGeneration == null) {
            dateGeneration = LocalDateTime.now();
        }
        if (utilise == null) {
            utilise = false;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Méthodes utilitaires
    public boolean isValide() {
        return statut == StatutBillet.VALIDE && !utilise;
    }
    
    public void marquerCommeUtilise() {
        this.utilise = true;
        this.dateUtilisation = LocalDateTime.now();
        this.statut = StatutBillet.UTILISE;
    }
    
    public String getNomFichier() {
        return "billet_" + numeroBillet + ".pdf";
    }
}
