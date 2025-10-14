package com.example.jee_event_manager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "evenement")
public class Evenement extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evenement_id")
    private Integer id;
    
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
    
    @Min(value = 1, message = "La capacité doit être au moins 1")
    @Column(name = "capacite")
    private int capacite = 100; // Capacité par défaut
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisateur_id", nullable = false, columnDefinition = "BIGINT")
    private Organisateur organisateur;
    
    @ManyToMany
    @JoinTable(
        name = "evenement_categories",
        joinColumns = @JoinColumn(name = "evenement_id"),
        inverseJoinColumns = @JoinColumn(name = "categorie_id")
    )
    private Set<Categorie> categories = new HashSet<>();
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
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

    public Organisateur getOrganisateur() {
        return organisateur;
    }

    public void setOrganisateur(Organisateur organisateur) {
        this.organisateur = organisateur;
    }

    public Set<Categorie> getCategories() {
        return categories;
    }

    public void setCategories(Set<Categorie> categories) {
        this.categories = categories;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }
    
    // Méthodes helper
    public LocalDateTime getDate() {
        return dateDebut;
    }
    
    public Integer getDuree() {
        if (dateDebut != null && dateFin != null) {
            return (int) java.time.Duration.between(dateDebut, dateFin).toHours();
        }
        return 2; // Durée par défaut
    }
    
    @Override
    public boolean validate() {
        if (dateDebut != null && dateFin != null && dateFin.isBefore(dateDebut)) {
            throw new IllegalStateException("La date de fin doit être postérieure à la date de début");
        }
        
        return titre != null && !titre.trim().isEmpty()
                && dateDebut != null
                && dateFin != null
                && lieu != null && !lieu.trim().isEmpty()
                && organisateur != null;
    }
}
