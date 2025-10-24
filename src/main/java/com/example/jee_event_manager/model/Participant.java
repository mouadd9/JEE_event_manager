package com.example.jee_event_manager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "participant")
@PrimaryKeyJoinColumn(name = "utilisateur_id")
@AllArgsConstructor
@Getter
@Setter
public class Participant extends Utilisateur {
    
    // Personal Info fields from database
    @Column(name = "date_naissance")
    private LocalDate dateNaissance;
    
    @Column(name = "preferences")
    private String preferences;
    
    @Column(name = "telephone")
    private String telephone;
    
    // Relationships
    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL)
    private List<Inscription> inscriptions;
    
    @OneToMany(mappedBy = "participant")
    private List<Commentaire> commentaires;
    
    @OneToMany(mappedBy = "participant")
    private List<Evaluation> evaluations;
    
    public Participant() {
        this.setUserType(UserType.PARTICIPANT);
    }
}