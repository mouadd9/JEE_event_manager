package com.projet.jee.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Participant entity - the default role for all new users.
 *
 * Participants can:
 * - Browse and search for events
 * - Register for events
 * - Comment on events they attended
 * - Rate events they attended
 * - Request upgrade to Organisateur role
 */
@Entity
@Table(name = "participant")
@DiscriminatorValue("participant")
@PrimaryKeyJoinColumn(name = "id_utilisateur")
public class Participant extends Utilisateur {

    private static final long serialVersionUID = 1L;

    @Column(name = "centres_interet")
    private String centresInteret;

    @Column(name = "preferences_notification")
    private String preferencesNotification;

    // One participant can have many comments
    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Commentaire> commentaires = new HashSet<>();

    // One participant can have many evaluations
    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Evaluation> evaluations = new HashSet<>();

    // Constructors
    public Participant() {
        super();
    }

    public Participant(String nom, String prenom, String email, String motDePasse) {
        super(nom, prenom, email, motDePasse);
    }

    // Getters and Setters
    public String getCentresInteret() {
        return centresInteret;
    }

    public void setCentresInteret(String centresInteret) {
        this.centresInteret = centresInteret;
    }

    public String getPreferencesNotification() {
        return preferencesNotification;
    }

    public void setPreferencesNotification(String preferencesNotification) {
        this.preferencesNotification = preferencesNotification;
    }

    public Set<Commentaire> getCommentaires() {
        return commentaires;
    }

    public void setCommentaires(Set<Commentaire> commentaires) {
        this.commentaires = commentaires;
    }

    public Set<Evaluation> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(Set<Evaluation> evaluations) {
        this.evaluations = evaluations;
    }

    // Helper methods for bidirectional relationships
    public void addCommentaire(Commentaire commentaire) {
        commentaires.add(commentaire);
        commentaire.setParticipant(this);
    }

    public void removeCommentaire(Commentaire commentaire) {
        commentaires.remove(commentaire);
        commentaire.setParticipant(null);
    }

    public void addEvaluation(Evaluation evaluation) {
        evaluations.add(evaluation);
        evaluation.setParticipant(this);
    }

    public void removeEvaluation(Evaluation evaluation) {
        evaluations.remove(evaluation);
        evaluation.setParticipant(null);
    }

    @Override
    public String toString() {
        return "Participant{" +
                "id=" + getId() +
                ", nom='" + getNom() + '\'' +
                ", prenom='" + getPrenom() + '\'' +
                ", email='" + getEmail() + '\'' +
                '}';
    }
}
