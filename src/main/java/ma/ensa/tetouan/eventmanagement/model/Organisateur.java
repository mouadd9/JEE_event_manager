package ma.ensa.tetouan.eventmanagement.model;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un organisateur d'événements.
 *
 * @author ENSA Tétouan
 */
@Entity
@Table(name = "organisateurs")
@DiscriminatorValue("ORGANISATEUR")
@PrimaryKeyJoinColumn(name = "utilisateur_id")
public class Organisateur extends User {

    private static final long serialVersionUID = 1L;

    @Size(max = 150, message = "Le nom de l'organisation ne peut pas dépasser 150 caractères")
    @Column(name = "organisation", length = 150)
    private String organisation;

    @Pattern(regexp = "^(\\+212|0)[5-7][0-9]{8}$", message = "Format de téléphone invalide (ex: +212612345678 ou 0612345678)")
    @Column(name = "telephone", length = 20)
    private String telephone;

    @Column(name = "site_web", length = 255)
    private String siteWeb;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "adresse", length = 255)
    private String adresse;

    @Column(name = "nombre_evenements_organises")
    private Integer nombreEvenementsOrganises;

    @Column(name = "approved", nullable = false)
    private boolean approved = false;

    // Relation OneToMany avec Event
    @OneToMany(mappedBy = "organisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evenement> evenements;

    /**
     * Constructeur par défaut
     */
    public Organisateur() {
        super();
        this.evenements = new ArrayList<>();
        this.nombreEvenementsOrganises = 0;
        this.approved = false;
    }

    /**
     * Constructeur avec paramètres
     */
    public Organisateur(String nom, String email, String motDePasse, String organisation, String telephone) {
        super(nom, email, motDePasse);
        this.organisation = organisation;
        this.telephone = telephone;
        this.evenements = new ArrayList<>();
        this.nombreEvenementsOrganises = 0;
        this.approved = false;
    }

    // Getters et Setters

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getSiteWeb() {
        return siteWeb;
    }

    public void setSiteWeb(String siteWeb) {
        this.siteWeb = siteWeb;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public Integer getNombreEvenementsOrganises() {
        return nombreEvenementsOrganises;
    }

    public void setNombreEvenementsOrganises(Integer nombreEvenementsOrganises) {
        this.nombreEvenementsOrganises = nombreEvenementsOrganises;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public List<Evenement> getEvenements() {
        return evenements;
    }

    public void setEvenements(List<Evenement> evenements) {
        this.evenements = evenements;
    }

    /**
     * Méthode utilitaire pour ajouter un événement
     */
    public void addEvenement(Evenement evenement) {
        evenements.add(evenement);
        evenement.setOrganisateur(this);
        this.nombreEvenementsOrganises = evenements.size();
    }

    /**
     * Méthode utilitaire pour retirer un événement
     */
    public void removeEvenement(Evenement evenement) {
        evenements.remove(evenement);
        evenement.setOrganisateur(null);
        this.nombreEvenementsOrganises = evenements.size();
    }

    @Override
    public String getRole() {
        return "ORGANISATEUR";
    }

    @Override
    public String toString() {
        return "Organisateur{" +
                "id=" + getId() +
                ", nom='" + getNom() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", organisation='" + organisation + '\'' +
                ", telephone='" + telephone + '\'' +
                ", nombreEvenements=" + nombreEvenementsOrganises +
                '}';
    }
}
