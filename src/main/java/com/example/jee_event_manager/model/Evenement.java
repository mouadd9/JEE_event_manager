package com.example.jee_event_manager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "evenement")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Evenement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evenement_id")
    private Long id; // Changed from Integer to Long as specified
    
    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 5, max = 100, message = "Le titre doit contenir entre 5 et 100 caractères")
    @Column(nullable = false)
    private String titre;
    
    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;
    
    @NotNull(message = "La date de début est obligatoire")
    @Column(name = "date_debut", nullable = false)
    private LocalDateTime dateDebut;
    
    @NotNull(message = "La date de fin est obligatoire")
    @Column(name = "date_fin", nullable = false)
    private LocalDateTime dateFin;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutEvenement statut = StatutEvenement.BROUILLON;
    
    @NotBlank(message = "Le lieu est obligatoire")
    @Column(nullable = false)
    private String lieu;
    
    // Coordinates from Branch B
    @Column
    private Double latitude;
    
    @Column
    private Double longitude;
    
    @Min(value = 1, message = "La capacité doit être au moins 1")
    @Column(name = "capacite")
    private int capacite = 100; // Capacité par défaut
    
    @Size(max = 500, message = "L'URL de l'image ne peut pas dépasser 500 caractères")
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    // Timestamps
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisateur_id", nullable = false, columnDefinition = "BIGINT")
    private Organisateur organisateur;
    
    @ManyToMany
    @JoinTable(
        name = "evenement_categorie",
        joinColumns = @JoinColumn(name = "evenement_id"),
        inverseJoinColumns = @JoinColumn(name = "categorie_id")
    )
    private Set<Categorie> categories = new HashSet<>();
    
    // Relationships to comments and reviews
    @OneToMany(mappedBy = "evenement")
    private List<Commentaire> commentaires;
    
    @OneToMany(mappedBy = "evenement")
    private List<Evaluation> evaluations;
    
    @OneToMany(mappedBy = "evenement")
    private List<Inscription> inscriptions;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Helper methods
    public LocalDateTime getDate() {
        return dateDebut;
    }
    
    public Integer getDuree() {
        if (dateDebut != null && dateFin != null) {
            return (int) java.time.Duration.between(dateDebut, dateFin).toHours();
        }
        return 2; // Durée par défaut
    }
}