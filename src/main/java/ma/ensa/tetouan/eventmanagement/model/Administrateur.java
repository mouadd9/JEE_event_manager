package ma.ensa.tetouan.eventmanagement.model;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Entité représentant un administrateur du système.
 *
 * @author ENSA Tétouan
 */
@Entity
@Table(name = "administrateurs")
@DiscriminatorValue("ADMINISTRATEUR")
@PrimaryKeyJoinColumn(name = "utilisateur_id")
public class Administrateur extends User {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "Le niveau d'accès est obligatoire")
    @Min(value = 1, message = "Le niveau d'accès minimum est 1")
    @Max(value = 3, message = "Le niveau d'accès maximum est 3")
    @Column(name = "niveau_acces", nullable = false)
    private Integer niveauAcces;

    @Column(name = "departement", length = 100)
    private String departement;

    @Column(name = "fonction", length = 100)
    private String fonction;

    @Column(name = "date_nomination")
    private LocalDateTime dateNomination;

    @Column(name = "peut_gerer_utilisateurs")
    private Boolean peutGererUtilisateurs;

    @Column(name = "peut_gerer_evenements")
    private Boolean peutGererEvenements;

    @Column(name = "peut_voir_statistiques")
    private Boolean peutVoirStatistiques;

    @Column(name = "peut_moderer_contenu")
    private Boolean peutModererContenu;

    /**
     * Constructeur par défaut
     */
    public Administrateur() {
        super();
        this.niveauAcces = 1;
        this.dateNomination = LocalDateTime.now();
        this.peutGererUtilisateurs = false;
        this.peutGererEvenements = false;
        this.peutVoirStatistiques = true;
        this.peutModererContenu = false;
    }

    /**
     * Constructeur avec paramètres
     */
    public Administrateur(String nom, String email, String motDePasse, Integer niveauAcces) {
        super(nom, email, motDePasse);
        this.niveauAcces = niveauAcces;
        this.dateNomination = LocalDateTime.now();
        initializePermissions(niveauAcces);
    }

    /**
     * Initialise les permissions selon le niveau d'accès
     * Niveau 1: Consultation uniquement
     * Niveau 2: Modération et gestion d'événements
     * Niveau 3: Tous les droits (super admin)
     */
    private void initializePermissions(Integer niveau) {
        this.peutVoirStatistiques = true;

        if (niveau >= 2) {
            this.peutGererEvenements = true;
            this.peutModererContenu = true;
        }

        if (niveau >= 3) {
            this.peutGererUtilisateurs = true;
        }
    }

    // Getters et Setters

    public Integer getNiveauAcces() {
        return niveauAcces;
    }

    public void setNiveauAcces(Integer niveauAcces) {
        this.niveauAcces = niveauAcces;
        initializePermissions(niveauAcces);
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public String getFonction() {
        return fonction;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

    public LocalDateTime getDateNomination() {
        return dateNomination;
    }

    public void setDateNomination(LocalDateTime dateNomination) {
        this.dateNomination = dateNomination;
    }

    public Boolean getPeutGererUtilisateurs() {
        return peutGererUtilisateurs;
    }

    public void setPeutGererUtilisateurs(Boolean peutGererUtilisateurs) {
        this.peutGererUtilisateurs = peutGererUtilisateurs;
    }

    public Boolean getPeutGererEvenements() {
        return peutGererEvenements;
    }

    public void setPeutGererEvenements(Boolean peutGererEvenements) {
        this.peutGererEvenements = peutGererEvenements;
    }

    public Boolean getPeutVoirStatistiques() {
        return peutVoirStatistiques;
    }

    public void setPeutVoirStatistiques(Boolean peutVoirStatistiques) {
        this.peutVoirStatistiques = peutVoirStatistiques;
    }

    public Boolean getPeutModererContenu() {
        return peutModererContenu;
    }

    public void setPeutModererContenu(Boolean peutModererContenu) {
        this.peutModererContenu = peutModererContenu;
    }

    /**
     * Vérifie si l'administrateur a les droits de super admin
     */
    public boolean isSuperAdmin() {
        return niveauAcces >= 3;
    }

    /**
     * Vérifie si l'administrateur peut effectuer une action donnée
     */
    public boolean hasPermission(String permission) {
        switch (permission.toUpperCase()) {
            case "GERER_UTILISATEURS":
                return peutGererUtilisateurs;
            case "GERER_EVENEMENTS":
                return peutGererEvenements;
            case "VOIR_STATISTIQUES":
                return peutVoirStatistiques;
            case "MODERER_CONTENU":
                return peutModererContenu;
            default:
                return false;
        }
    }

    @Override
    public String getRole() {
        return "ADMINISTRATEUR";
    }

    @Override
    public String toString() {
        return "Administrateur{" +
                "id=" + getId() +
                ", nom='" + getNom() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", niveauAcces=" + niveauAcces +
                ", departement='" + departement + '\'' +
                ", fonction='" + fonction + '\'' +
                '}';
    }
}
