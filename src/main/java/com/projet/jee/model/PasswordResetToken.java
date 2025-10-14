package com.projet.jee.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * PasswordResetToken entity - represents tokens for password reset functionality.
 *
 * Business Rules:
 * - Tokens are valid for 24 hours
 * - One token per user at a time (new token invalidates previous ones)
 * - Tokens are single-use (marked as used after password reset)
 * - Tokens contain a random UUID for security
 */
@Entity
@Table(name = "password_reset_token",
        indexes = {
                @Index(name = "idx_reset_token", columnList = "token"),
                @Index(name = "idx_reset_user", columnList = "id_utilisateur")
        })
public class PasswordResetToken implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int EXPIRATION_HOURS = 24;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_token")
    private Long id;

    @NotBlank(message = "Le token est obligatoire")
    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @NotNull(message = "L'utilisateur est obligatoire")
    @Column(name = "id_utilisateur", nullable = false)
    private Long utilisateurId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_expiration", nullable = false)
    private LocalDateTime dateExpiration;

    @Column(name = "est_utilise", nullable = false)
    private Boolean estUtilise = false;

    @Column(name = "date_utilisation")
    private LocalDateTime dateUtilisation;

    @Column(name = "adresse_ip")
    private String adresseIp;

    // Constructors
    public PasswordResetToken() {
    }

    public PasswordResetToken(Long utilisateurId, String email) {
        this.utilisateurId = utilisateurId;
        this.email = email;
        this.token = UUID.randomUUID().toString();
        this.dateCreation = LocalDateTime.now();
        this.dateExpiration = dateCreation.plusHours(EXPIRATION_HOURS);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(Long utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(LocalDateTime dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public Boolean getEstUtilise() {
        return estUtilise;
    }

    public void setEstUtilise(Boolean estUtilise) {
        this.estUtilise = estUtilise;
    }

    public LocalDateTime getDateUtilisation() {
        return dateUtilisation;
    }

    public void setDateUtilisation(LocalDateTime dateUtilisation) {
        this.dateUtilisation = dateUtilisation;
    }

    public String getAdresseIp() {
        return adresseIp;
    }

    public void setAdresseIp(String adresseIp) {
        this.adresseIp = adresseIp;
    }

    // Business methods
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(dateExpiration);
    }

    public boolean isValid() {
        return !estUtilise && !isExpired();
    }

    public void marquerUtilise() {
        this.estUtilise = true;
        this.dateUtilisation = LocalDateTime.now();
    }

    public long getHeuresRestantes() {
        if (isExpired()) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), dateExpiration).toHours();
    }

    @Override
    public String toString() {
        return "PasswordResetToken{" +
                "id=" + id +
                ", utilisateurId=" + utilisateurId +
                ", email='" + email + '\'' +
                ", dateExpiration=" + dateExpiration +
                ", estUtilise=" + estUtilise +
                '}';
    }
}
