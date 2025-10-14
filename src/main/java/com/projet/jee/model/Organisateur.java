package com.projet.jee.model;

import javax.persistence.*;

/**
 * Organisateur entity - users who have been granted permission to create and manage events.
 *
 * Organisateurs are Participants who have been approved by an Administrator.
 * They inherit all Participant capabilities and gain additional privileges:
 * - Create, edit, and delete events
 * - Manage event registrations
 * - View event analytics
 */
@Entity
@Table(name = "organisateur")
@DiscriminatorValue("organisateur")
@PrimaryKeyJoinColumn(name = "id_utilisateur")
public class Organisateur extends Participant {

    private static final long serialVersionUID = 1L;

    @Column(name = "nom_organisation", length = 200)
    private String nomOrganisation;

    @Column(name = "description_organisation", columnDefinition = "TEXT")
    private String descriptionOrganisation;

    @Column(name = "site_web", length = 255)
    private String siteWeb;

    @Column(name = "num_siret", length = 14)
    private String numSiret;

    @Column(name = "est_verifie", nullable = false)
    private Boolean estVerifie = false;

    @Column(name = "note_moyenne")
    private Double noteMoyenne;

    @Column(name = "nombre_evenements_organises")
    private Integer nombreEvenementsOrganises = 0;

    // Constructors
    public Organisateur() {
        super();
    }

    public Organisateur(String nom, String prenom, String email, String motDePasse) {
        super(nom, prenom, email, motDePasse);
    }

    // Getters and Setters
    public String getNomOrganisation() {
        return nomOrganisation;
    }

    public void setNomOrganisation(String nomOrganisation) {
        this.nomOrganisation = nomOrganisation;
    }

    public String getDescriptionOrganisation() {
        return descriptionOrganisation;
    }

    public void setDescriptionOrganisation(String descriptionOrganisation) {
        this.descriptionOrganisation = descriptionOrganisation;
    }

    public String getSiteWeb() {
        return siteWeb;
    }

    public void setSiteWeb(String siteWeb) {
        this.siteWeb = siteWeb;
    }

    public String getNumSiret() {
        return numSiret;
    }

    public void setNumSiret(String numSiret) {
        this.numSiret = numSiret;
    }

    public Boolean getEstVerifie() {
        return estVerifie;
    }

    public void setEstVerifie(Boolean estVerifie) {
        this.estVerifie = estVerifie;
    }

    public Double getNoteMoyenne() {
        return noteMoyenne;
    }

    public void setNoteMoyenne(Double noteMoyenne) {
        this.noteMoyenne = noteMoyenne;
    }

    public Integer getNombreEvenementsOrganises() {
        return nombreEvenementsOrganises;
    }

    public void setNombreEvenementsOrganises(Integer nombreEvenementsOrganises) {
        this.nombreEvenementsOrganises = nombreEvenementsOrganises;
    }

    // Business methods
    public void incrementNombreEvenements() {
        this.nombreEvenementsOrganises++;
    }

    public void decrementNombreEvenements() {
        if (this.nombreEvenementsOrganises > 0) {
            this.nombreEvenementsOrganises--;
        }
    }

    @Override
    public String toString() {
        return "Organisateur{" +
                "id=" + getId() +
                ", nom='" + getNom() + '\'' +
                ", prenom='" + getPrenom() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", nomOrganisation='" + nomOrganisation + '\'' +
                ", estVerifie=" + estVerifie +
                '}';
    }
}
