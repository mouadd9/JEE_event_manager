package com.example.jee_event_manager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categorie")
public class Categorie extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categorie_id")
    private Integer id;
    
    @NotBlank(message = "Le nom de la catégorie est obligatoire")
    @Column(nullable = false, unique = true)
    private String nom;
    
    @ManyToMany(mappedBy = "categories")
    private Set<Evenement> evenements = new HashSet<>();
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Set<Evenement> getEvenements() {
        return evenements;
    }

    public void setEvenements(Set<Evenement> evenements) {
        this.evenements = evenements;
    }
    
    @Override
    public boolean validate() {
        return nom != null && !nom.trim().isEmpty();
    }
}
