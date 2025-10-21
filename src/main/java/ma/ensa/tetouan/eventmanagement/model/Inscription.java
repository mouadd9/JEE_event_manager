package ma.ensa.tetouan.eventmanagement.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entité représentant l'inscription d'un participant à un événement.
 *
 * @author ENSA Tétouan
 */
@Entity
@Table(name = "inscriptions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"participant_id", "evenement_id"})
})
@NamedQueries({
    @NamedQuery(name = "Inscription.findByParticipant",
                query = "SELECT i FROM Inscription i WHERE i.participant.id = :participantId ORDER BY i.dateInscription DESC"),
    @NamedQuery(name = "Inscription.findByEvenement",
                query = "SELECT i FROM Inscription i WHERE i.evenement.id = :evenementId ORDER BY i.dateInscription DESC"),
    @NamedQuery(name = "Inscription.findByStatut",
                query = "SELECT i FROM Inscription i WHERE i.statut = :statut ORDER BY i.dateInscription DESC")
})
public class Inscription implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "La date d'inscription est obligatoire")
    @Column(name = "date_inscription", nullable = false, updatable = false)
    private LocalDateTime dateInscription;

    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutInscription statut;

    @Column(name = "date_reponse")
    private LocalDateTime dateReponse;

    @Column(name = "message_participant", columnDefinition = "TEXT")
    private String messageParticipant;

    @Column(name = "message_organisateur", columnDefinition = "TEXT")
    private String messageOrganisateur;

    @Column(name = "nombre_places")
    private Integer nombrePlaces;

    @Column(name = "presence_confirmee")
    private Boolean presenceConfirmee;

    @Column(name = "date_annulation")
    private LocalDateTime dateAnnulation;

    @Column(name = "motif_annulation", length = 500)
    private String motifAnnulation;

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
    public Inscription() {
        this.dateInscription = LocalDateTime.now();
        this.statut = StatutInscription.EN_ATTENTE;
        this.nombrePlaces = 1;
        this.presenceConfirmee = false;
    }

    /**
     * Constructeur avec paramètres
     */
    public Inscription(Participant participant, Evenement evenement) {
        this();
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

    public LocalDateTime getDateReponse() {
        return dateReponse;
    }

    public void setDateReponse(LocalDateTime dateReponse) {
        this.dateReponse = dateReponse;
    }

    public String getMessageParticipant() {
        return messageParticipant;
    }

    public void setMessageParticipant(String messageParticipant) {
        this.messageParticipant = messageParticipant;
    }

    public String getMessageOrganisateur() {
        return messageOrganisateur;
    }

    public void setMessageOrganisateur(String messageOrganisateur) {
        this.messageOrganisateur = messageOrganisateur;
    }

    public Integer getNombrePlaces() {
        return nombrePlaces;
    }

    public void setNombrePlaces(Integer nombrePlaces) {
        this.nombrePlaces = nombrePlaces;
    }

    public Boolean getPresenceConfirmee() {
        return presenceConfirmee;
    }

    public void setPresenceConfirmee(Boolean presenceConfirmee) {
        this.presenceConfirmee = presenceConfirmee;
    }

    public LocalDateTime getDateAnnulation() {
        return dateAnnulation;
    }

    public void setDateAnnulation(LocalDateTime dateAnnulation) {
        this.dateAnnulation = dateAnnulation;
    }

    public String getMotifAnnulation() {
        return motifAnnulation;
    }

    public void setMotifAnnulation(String motifAnnulation) {
        this.motifAnnulation = motifAnnulation;
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
     * Accepte l'inscription
     */
    public void accepter(String messageOrganisateur) {
        this.statut = StatutInscription.ACCEPTEE;
        this.dateReponse = LocalDateTime.now();
        this.messageOrganisateur = messageOrganisateur;
    }

    /**
     * Refuse l'inscription
     */
    public void refuser(String messageOrganisateur) {
        this.statut = StatutInscription.REFUSEE;
        this.dateReponse = LocalDateTime.now();
        this.messageOrganisateur = messageOrganisateur;
    }

    /**
     * Annule l'inscription
     */
    public void annuler(String motif) {
        this.statut = StatutInscription.ANNULEE;
        this.dateAnnulation = LocalDateTime.now();
        this.motifAnnulation = motif;
    }

    /**
     * Vérifie si l'inscription est active
     */
    public boolean isActive() {
        return statut == StatutInscription.EN_ATTENTE || statut == StatutInscription.ACCEPTEE;
    }

    /**
     * Méthode de cycle de vie JPA
     */
    @PrePersist
    protected void onCreate() {
        if (dateInscription == null) {
            dateInscription = LocalDateTime.now();
        }
        if (statut == null) {
            statut = StatutInscription.EN_ATTENTE;
        }
        if (nombrePlaces == null) {
            nombrePlaces = 1;
        }
        if (presenceConfirmee == null) {
            presenceConfirmee = false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inscription that = (Inscription) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Inscription{" +
                "id=" + id +
                ", dateInscription=" + dateInscription +
                ", statut=" + statut +
                ", participant=" + (participant != null ? participant.getNom() : "null") +
                ", evenement=" + (evenement != null ? evenement.getTitre() : "null") +
                ", nombrePlaces=" + nombrePlaces +
                '}';
    }
}
