package com.example.jee_event_manager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "organisateur")
@PrimaryKeyJoinColumn(name = "utilisateur_id")
@AllArgsConstructor
@Getter
@Setter
public class Organisateur extends Utilisateur {
    
    // Business fields from database
    @Column(name = "description")
    private String description;
    
    @Column(name = "entreprise")
    private String entreprise;
    
    @Column(name = "siret")
    private String siret;
    
    @Column(name = "site_web")
    private String siteWeb;
    
    // Relationship to events
    @OneToMany(mappedBy = "organisateur", cascade = CascadeType.ALL)
    private List<Evenement> evenements;
    
    public Organisateur() {
        this.setUserType(UserType.ORGANISATEUR);
    }
}