package ma.ensa.tetouan.eventmanagement.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entité représentant l'évaluation (note et avis) d'un événement par un participant.
 *
 * @author ENSA Tétouan
 */
@Entity
@Table(name = "evaluations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"participant_id", "evenement_id"})
})
@NamedQueries({
    @NamedQuery(name = "Evaluation.findByEvenement",
                query = "SELECT e FROM Evaluation e WHERE e.evenement.id = :evenementId ORDER BY e.horodatage DESC"),
    @NamedQuery(name = "Evaluation.findByParticipant",
                query = "SELECT e FROM Evaluation e WHERE e.participant.id = :participantId ORDER BY e.horodatage DESC"),
    @NamedQuery(name = "Evaluation.calculateAverage",
                query = "SELECT AVG(e.note) FROM Evaluation e WHERE e.evenement.id = :evenementId")
})
public class Evaluation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "La note est obligatoire")
    @Min(value = 1, message = "La note minimale est 1")
    @Max(value = 5, message = "La note maximale est 5")
    @Column(name = "note", nullable = false)
    private Integer note;

    @Size(max = 1000, message = "Le texte de l'évaluation ne peut pas dépasser 1000 caractères")
    @Column(name = "texte", columnDefinition = "TEXT")
    private String texte;

    @NotNull(message = "L'horodatage est obligatoire")
    @Column(name = "horodatage", nullable = false, updatable = false)
    private LocalDateTime horodatage;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Column(name = "visible")
    private Boolean visible;

    @Column(name = "verifie")
    private Boolean verifie;

    @Column(name = "utile_count")
    private Integer utileCount;

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
    public Evaluation() {
        this.horodatage = LocalDateTime.now();
        this.visible = true;
        this.verifie = false;
        this.utileCount = 0;
    }

    /**
     * Constructeur avec paramètres
     */
    public Evaluation(Integer note, String texte, Participant participant, Evenement evenement) {
        this();
        this.note = note;
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

    public Integer getNote() {
        return note;
    }

    public void setNote(Integer note) {
        this.note = note;
    }

    public String getTexte() {
        return texte;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }

    public LocalDateTime getHorodatage() {
        return horodatage;
    }

    public void setHorodatage(LocalDateTime horodatage) {
        this.horodatage = horodatage;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Boolean getVerifie() {
        return verifie;
    }

    public void setVerifie(Boolean verifie) {
        this.verifie = verifie;
    }

    public Integer getUtileCount() {
        return utileCount;
    }

    public void setUtileCount(Integer utileCount) {
        this.utileCount = utileCount;
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
     * Modifie l'évaluation
     */
    public void modifier(Integer nouvelleNote, String nouveauTexte) {
        this.note = nouvelleNote;
        this.texte = nouveauTexte;
        this.dateModification = LocalDateTime.now();
    }

    /**
     * Vérifie l'évaluation (par l'administrateur)
     */
    public void verifier() {
        this.verifie = true;
    }

    /**
     * Incrémente le compteur "utile"
     */
    public void marquerUtile() {
        this.utileCount++;
    }

    /**
     * Vérifie si l'évaluation a été modifiée
     */
    public boolean estModifiee() {
        return dateModification != null;
    }

    /**
     * Obtient une représentation textuelle de la note (étoiles)
     */
    public String getNoteEtoiles() {
        StringBuilder etoiles = new StringBuilder();
        for (int i = 0; i < note; i++) {
            etoiles.append("★");
        }
        for (int i = note; i < 5; i++) {
            etoiles.append("☆");
        }
        return etoiles.toString();
    }

    /**
     * Valide que la note est dans la plage valide
     */
    @AssertTrue(message = "La note doit être entre 1 et 5")
    private boolean isNoteValide() {
        return note != null && note >= 1 && note <= 5;
    }

    /**
     * Méthode de cycle de vie JPA
     */
    @PrePersist
    protected void onCreate() {
        if (horodatage == null) {
            horodatage = LocalDateTime.now();
        }
        if (visible == null) {
            visible = true;
        }
        if (verifie == null) {
            verifie = false;
        }
        if (utileCount == null) {
            utileCount = 0;
        }
    }

    /**
     * Méthode de cycle de vie JPA - après chargement
     */
    @PostLoad
    protected void onLoad() {
        // Mettre à jour la note moyenne de l'événement si nécessaire
        if (evenement != null) {
            evenement.calculerNoteMoyenne();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evaluation that = (Evaluation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Evaluation{" +
                "id=" + id +
                ", note=" + note +
                ", texte='" + (texte != null && texte.length() > 50 ? texte.substring(0, 50) + "..." : texte) + '\'' +
                ", horodatage=" + horodatage +
                ", participant=" + (participant != null ? participant.getNom() : "null") +
                ", evenement=" + (evenement != null ? evenement.getTitre() : "null") +
                ", visible=" + visible +
                ", verifie=" + verifie +
                '}';
    }
}
