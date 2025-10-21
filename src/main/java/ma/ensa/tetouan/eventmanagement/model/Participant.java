package ma.ensa.tetouan.eventmanagement.model;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un participant aux événements.
 *
 * @author ENSA Tétouan
 */
@Entity
@Table(name = "participants")
@DiscriminatorValue("PARTICIPANT")
@PrimaryKeyJoinColumn(name = "utilisateur_id")
public class Participant extends User {

    private static final long serialVersionUID = 1L;

    @Size(max = 500, message = "Les préférences ne peuvent pas dépasser 500 caractères")
    @Column(name = "preferences", length = 500)
    private String preferences;

    @Size(max = 500, message = "Les centres d'intérêt ne peuvent pas dépasser 500 caractères")
    @Column(name = "interets", length = 500)
    private String interets;

    @Column(name = "ville", length = 100)
    private String ville;

    @Column(name = "date_naissance")
    private java.time.LocalDate dateNaissance;

    @Column(name = "telephone", length = 20)
    private String telephone;

    @Column(name = "nombre_inscriptions")
    private Integer nombreInscriptions;

    // Relation OneToMany avec Inscription
    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inscription> inscriptions;

    // Relation OneToMany avec Commentaire
    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Commentaire> commentaires;

    // Relation OneToMany avec Evaluation
    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evaluation> evaluations;

    /**
     * Constructeur par défaut
     */
    public Participant() {
        super();
        this.inscriptions = new ArrayList<>();
        this.commentaires = new ArrayList<>();
        this.evaluations = new ArrayList<>();
        this.nombreInscriptions = 0;
    }

    /**
     * Constructeur avec paramètres
     */
    public Participant(String nom, String email, String motDePasse) {
        super(nom, email, motDePasse);
        this.inscriptions = new ArrayList<>();
        this.commentaires = new ArrayList<>();
        this.evaluations = new ArrayList<>();
        this.nombreInscriptions = 0;
    }

    // Getters et Setters

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public String getInterets() {
        return interets;
    }

    public void setInterets(String interets) {
        this.interets = interets;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public java.time.LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(java.time.LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Integer getNombreInscriptions() {
        return nombreInscriptions;
    }

    public void setNombreInscriptions(Integer nombreInscriptions) {
        this.nombreInscriptions = nombreInscriptions;
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

    /**
     * Méthode utilitaire pour ajouter une inscription
     */
    public void addInscription(Inscription inscription) {
        inscriptions.add(inscription);
        inscription.setParticipant(this);
        this.nombreInscriptions = inscriptions.size();
    }

    /**
     * Méthode utilitaire pour retirer une inscription
     */
    public void removeInscription(Inscription inscription) {
        inscriptions.remove(inscription);
        inscription.setParticipant(null);
        this.nombreInscriptions = inscriptions.size();
    }

    /**
     * Méthode utilitaire pour ajouter un commentaire
     */
    public void addCommentaire(Commentaire commentaire) {
        commentaires.add(commentaire);
        commentaire.setParticipant(this);
    }

    /**
     * Méthode utilitaire pour retirer un commentaire
     */
    public void removeCommentaire(Commentaire commentaire) {
        commentaires.remove(commentaire);
        commentaire.setParticipant(null);
    }

    /**
     * Méthode utilitaire pour ajouter une évaluation
     */
    public void addEvaluation(Evaluation evaluation) {
        evaluations.add(evaluation);
        evaluation.setParticipant(this);
    }

    /**
     * Méthode utilitaire pour retirer une évaluation
     */
    public void removeEvaluation(Evaluation evaluation) {
        evaluations.remove(evaluation);
        evaluation.setParticipant(null);
    }

    @Override
    public String getRole() {
        return "PARTICIPANT";
    }

    @Override
    public String toString() {
        return "Participant{" +
                "id=" + getId() +
                ", nom='" + getNom() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", ville='" + ville + '\'' +
                ", nombreInscriptions=" + nombreInscriptions +
                '}';
    }
}
