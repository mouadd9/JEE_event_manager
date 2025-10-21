package ma.ensa.tetouan.eventmanagement.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entité représentant un commentaire sur un événement.
 *
 * @author ENSA Tétouan
 */
@Entity
@Table(name = "commentaires")
@NamedQueries({
    @NamedQuery(name = "Commentaire.findByEvenement",
                query = "SELECT c FROM Commentaire c WHERE c.evenement.id = :evenementId ORDER BY c.dateCreation DESC"),
    @NamedQuery(name = "Commentaire.findByParticipant",
                query = "SELECT c FROM Commentaire c WHERE c.participant.id = :participantId ORDER BY c.dateCreation DESC"),
    @NamedQuery(name = "Commentaire.findRecent",
                query = "SELECT c FROM Commentaire c ORDER BY c.dateCreation DESC")
})
public class Commentaire implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Le texte du commentaire est obligatoire")
    @Size(min = 5, max = 2000, message = "Le commentaire doit contenir entre 5 et 2000 caractères")
    @Column(name = "texte", nullable = false, columnDefinition = "TEXT")
    private String texte;

    @NotNull(message = "La date de création est obligatoire")
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Column(name = "modere")
    private Boolean modere;

    @Column(name = "signale")
    private Boolean signale;

    @Column(name = "nombre_signalements")
    private Integer nombreSignalements;

    @Column(name = "visible")
    private Boolean visible;

    // Relations

    @NotNull(message = "Le participant est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    @NotNull(message = "L'événement est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evenement_id", nullable = false)
    private Evenement evenement;

    /**
     * Constructeur par défaut
     */
    public Commentaire() {
        this.dateCreation = LocalDateTime.now();
        this.modere = false;
        this.signale = false;
        this.nombreSignalements = 0;
        this.visible = true;
    }

    /**
     * Constructeur avec paramètres
     */
    public Commentaire(String texte, Participant participant, Evenement evenement) {
        this();
        this.texte = texte;
        this.participant = participant;
        this.evenement = evenement;
    }

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTexte() {
        return texte;
    }

    public void setTexte(String texte) {
        this.texte = texte;
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

    public Boolean getModere() {
        return modere;
    }

    public void setModere(Boolean modere) {
        this.modere = modere;
    }

    public Boolean getSignale() {
        return signale;
    }

    public void setSignale(Boolean signale) {
        this.signale = signale;
    }

    public Integer getNombreSignalements() {
        return nombreSignalements;
    }

    public void setNombreSignalements(Integer nombreSignalements) {
        this.nombreSignalements = nombreSignalements;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
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

    // Méthodes utilitaires

    /**
     * Modifie le commentaire
     */
    public void modifier(String nouveauTexte) {
        this.texte = nouveauTexte;
        this.dateModification = LocalDateTime.now();
    }

    /**
     * Signale le commentaire
     */
    public void signaler() {
        this.signale = true;
        this.nombreSignalements++;
        // Si plus de 3 signalements, masquer automatiquement
        if (this.nombreSignalements >= 3) {
            this.visible = false;
        }
    }

    /**
     * Modère le commentaire (action de l'administrateur)
     */
    public void moderer(boolean approuve) {
        this.modere = true;
        this.visible = approuve;
        if (approuve) {
            this.signale = false;
            this.nombreSignalements = 0;
        }
    }

    /**
     * Vérifie si le commentaire a été modifié
     */
    public boolean estModifie() {
        return dateModification != null;
    }

    /**
     * Méthode de cycle de vie JPA
     */
    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
        if (modere == null) {
            modere = false;
        }
        if (signale == null) {
            signale = false;
        }
        if (nombreSignalements == null) {
            nombreSignalements = 0;
        }
        if (visible == null) {
            visible = true;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Commentaire that = (Commentaire) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Commentaire{" +
                "id=" + id +
                ", texte='" + (texte != null && texte.length() > 50 ? texte.substring(0, 50) + "..." : texte) + '\'' +
                ", dateCreation=" + dateCreation +
                ", participant=" + (participant != null ? participant.getNom() : "null") +
                ", evenement=" + (evenement != null ? evenement.getTitre() : "null") +
                ", visible=" + visible +
                '}';
    }
}
