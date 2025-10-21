package ma.ensa.tetouan.eventmanagement.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entité représentant un événement.
 *
 * @author ENSA Tétouan
 */
@Entity
@Table(name = "evenements")
@NamedQueries({
    @NamedQuery(name = "Evenement.findAll", query = "SELECT e FROM Evenement e ORDER BY e.dateDebut DESC"),
    @NamedQuery(name = "Evenement.findByStatut", query = "SELECT e FROM Evenement e WHERE e.statut = :statut ORDER BY e.dateDebut DESC"),
    @NamedQuery(name = "Evenement.findPublished", query = "SELECT e FROM Evenement e WHERE e.statut = 'PUBLIE' AND e.dateDebut > :now ORDER BY e.dateDebut ASC")
})
public class Evenement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 5, max = 200, message = "Le titre doit contenir entre 5 et 200 caractères")
    @Column(name = "titre", nullable = false, length = 200)
    private String titre;

    @NotBlank(message = "La description est obligatoire")
    @Size(min = 20, max = 5000, message = "La description doit contenir entre 20 et 5000 caractères")
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "La date de début est obligatoire")
    @Column(name = "date_debut", nullable = false)
    private LocalDateTime dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    @Column(name = "date_fin", nullable = false)
    private LocalDateTime dateFin;

    @NotBlank(message = "Le lieu est obligatoire")
    @Size(max = 255, message = "Le lieu ne peut pas dépasser 255 caractères")
    @Column(name = "lieu", nullable = false, length = 255)
    private String lieu;

    @Column(name = "adresse_complete", length = 500)
    private String adresseComplete;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @NotNull(message = "La capacité est obligatoire")
    @Min(value = 1, message = "La capacité doit être au moins de 1 personne")
    @Max(value = 100000, message = "La capacité ne peut pas dépasser 100000 personnes")
    @Column(name = "capacite", nullable = false)
    private Integer capacite;

    @Column(name = "places_disponibles")
    private Integer placesDisponibles;

    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutEvenement statut;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "prix")
    private Double prix;

    @Column(name = "gratuit")
    private Boolean gratuit;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Column(name = "nombre_vues")
    private Integer nombreVues;

    @Column(name = "nombre_inscriptions")
    private Integer nombreInscriptions;

    @Column(name = "note_moyenne")
    private Double noteMoyenne;

    // Relations

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisateur_id", nullable = false)
    private Organisateur organisateur;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinTable(
        name = "evenement_categorie",
        joinColumns = @JoinColumn(name = "evenement_id"),
        inverseJoinColumns = @JoinColumn(name = "categorie_id")
    )
    private List<Categorie> categories;

    @OneToMany(mappedBy = "evenement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inscription> inscriptions;

    @OneToMany(mappedBy = "evenement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Commentaire> commentaires;

    @OneToMany(mappedBy = "evenement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evaluation> evaluations;

    /**
     * Constructeur par défaut
     */
    public Evenement() {
        this.dateCreation = LocalDateTime.now();
        this.statut = StatutEvenement.BROUILLON;
        this.categories = new ArrayList<>();
        this.inscriptions = new ArrayList<>();
        this.commentaires = new ArrayList<>();
        this.evaluations = new ArrayList<>();
        this.nombreVues = 0;
        this.nombreInscriptions = 0;
        this.noteMoyenne = 0.0;
        this.gratuit = false;
    }

    /**
     * Constructeur avec paramètres
     */
    public Evenement(String titre, String description, LocalDateTime dateDebut, LocalDateTime dateFin,
                     String lieu, Integer capacite, Organisateur organisateur) {
        this();
        this.titre = titre;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieu = lieu;
        this.capacite = capacite;
        this.placesDisponibles = capacite;
        this.organisateur = organisateur;
    }

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public String getAdresseComplete() {
        return adresseComplete;
    }

    public void setAdresseComplete(String adresseComplete) {
        this.adresseComplete = adresseComplete;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getCapacite() {
        return capacite;
    }

    public void setCapacite(Integer capacite) {
        this.capacite = capacite;
    }

    public Integer getPlacesDisponibles() {
        return placesDisponibles;
    }

    public void setPlacesDisponibles(Integer placesDisponibles) {
        this.placesDisponibles = placesDisponibles;
    }

    public StatutEvenement getStatut() {
        return statut;
    }

    public void setStatut(StatutEvenement statut) {
        this.statut = statut;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public Boolean getGratuit() {
        return gratuit;
    }

    public void setGratuit(Boolean gratuit) {
        this.gratuit = gratuit;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    public Integer getNombreVues() {
        return nombreVues;
    }

    public void setNombreVues(Integer nombreVues) {
        this.nombreVues = nombreVues;
    }

    public Integer getNombreInscriptions() {
        return nombreInscriptions;
    }

    public void setNombreInscriptions(Integer nombreInscriptions) {
        this.nombreInscriptions = nombreInscriptions;
    }

    public Double getNoteMoyenne() {
        return noteMoyenne;
    }

    public void setNoteMoyenne(Double noteMoyenne) {
        this.noteMoyenne = noteMoyenne;
    }

    public Organisateur getOrganisateur() {
        return organisateur;
    }

    public void setOrganisateur(Organisateur organisateur) {
        this.organisateur = organisateur;
    }

    public List<Categorie> getCategories() {
        return categories;
    }

    public void setCategories(List<Categorie> categories) {
        this.categories = categories;
    }

    public List<Inscription> getInscriptions() {
        return inscriptions;
    }

    public void setInscriptions(List<Inscription> inscriptions) {
        this.inscriptions = inscriptions;
    }

    public List<Commentaire> getCommentaires() {
        return commentaires;
    }

    public void setCommentaires(List<Commentaire> commentaires) {
        this.commentaires = commentaires;
    }

    public List<Evaluation> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(List<Evaluation> evaluations) {
        this.evaluations = evaluations;
    }

    // Méthodes utilitaires

    /**
     * Ajoute une catégorie à l'événement
     */
    public void addCategorie(Categorie categorie) {
        categories.add(categorie);
        categorie.getEvenements().add(this);
    }

    /**
     * Retire une catégorie de l'événement
     */
    public void removeCategorie(Categorie categorie) {
        categories.remove(categorie);
        categorie.getEvenements().remove(this);
    }

    /**
     * Incrémente le nombre de vues
     */
    public void incrementerVues() {
        this.nombreVues++;
    }

    /**
     * Vérifie si l'événement est complet
     */
    public boolean isComplet() {
        return placesDisponibles <= 0;
    }

    /**
     * Vérifie si l'événement est en cours
     */
    public boolean isEnCours() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(dateDebut) && now.isBefore(dateFin);
    }

    /**
     * Vérifie si l'événement est terminé
     */
    public boolean isTermine() {
        return LocalDateTime.now().isAfter(dateFin);
    }

    /**
     * Calcule la note moyenne à partir des évaluations
     */
    public void calculerNoteMoyenne() {
        if (evaluations == null || evaluations.isEmpty()) {
            this.noteMoyenne = 0.0;
            return;
        }
        double sum = evaluations.stream()
                .mapToDouble(Evaluation::getNote)
                .sum();
        this.noteMoyenne = sum / evaluations.size();
    }

    /**
     * Méthode de cycle de vie JPA - avant persistance
     */
    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        if (statut == null) {
            statut = StatutEvenement.BROUILLON;
        }
        if (placesDisponibles == null && capacite != null) {
            placesDisponibles = capacite;
        }
        if (nombreVues == null) {
            nombreVues = 0;
        }
        if (nombreInscriptions == null) {
            nombreInscriptions = 0;
        }
        if (gratuit == null) {
            gratuit = false;
        }
    }

    /**
     * Méthode de cycle de vie JPA - avant mise à jour
     */
    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evenement evenement = (Evenement) o;
        return Objects.equals(id, evenement.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Evenement{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", lieu='" + lieu + '\'' +
                ", capacite=" + capacite +
                ", statut=" + statut +
                ", organisateur=" + (organisateur != null ? organisateur.getNom() : "null") +
                '}';
    }
}
