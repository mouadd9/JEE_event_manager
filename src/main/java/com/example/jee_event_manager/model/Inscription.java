package com.example.jee_event_manager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "inscription", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"participant_id", "evenement_id"})
})
public class Inscription extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inscription_id")
    private Integer id;
    
    @NotNull(message = "La date d'inscription est obligatoire")
    @Column(name = "date_inscription", nullable = false)
    private LocalDateTime dateInscription = LocalDateTime.now();
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutInscription statut = StatutInscription.EN_ATTENTE;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false, columnDefinition = "INTEGER")
    private Participant participant;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evenement_id", nullable = false, columnDefinition = "INTEGER")
    private Evenement evenement;
    
    @Column(name = "type_billet", length = 50)
    private String typeBillet = "STANDARD"; // STANDARD, VIP, PREMIUM, etc.
    
    @Column(name = "quantite")
    private int quantite = 1; // Nombre de places réservées
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public LocalDateTime getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDateTime dateInscription) {
        this.dateInscription = dateInscription;
    }

    public StatutInscription getStatut() {
        return statut;
    }

    public void setStatut(StatutInscription statut) {
        this.statut = statut;
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

    public String getTypeBillet() {
        return typeBillet;
    }

    public void setTypeBillet(String typeBillet) {
        this.typeBillet = typeBillet;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
    
    @Override
    public boolean validate() {
        return participant != null 
                && evenement != null 
                && dateInscription != null 
                && statut != null
                && quantite > 0;
    }
}
