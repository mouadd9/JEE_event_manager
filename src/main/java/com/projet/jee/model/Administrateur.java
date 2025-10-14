package com.projet.jee.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Administrateur entity - users with full system privileges.
 *
 * Administrators are created directly by the system or other admins.
 * They have the highest level of access and can:
 * - Approve/reject Organisateur upgrade requests
 * - Manage all users (activate/deactivate accounts)
 * - Moderate content (comments, events)
 * - Access system analytics
 * - Manage categories and system configuration
 */
@Entity
@Table(name = "administrateur")
@DiscriminatorValue("administrateur")
@PrimaryKeyJoinColumn(name = "id_utilisateur")
public class Administrateur extends Utilisateur {

    private static final long serialVersionUID = 1L;

    @Column(name = "role_admin", length = 50)
    private String roleAdmin; // e.g., "SUPER_ADMIN", "MODERATOR", "SUPPORT"

    @Column(name = "permissions", columnDefinition = "TEXT")
    private String permissions; // Comma-separated list of permissions

    @Column(name = "derniere_connexion")
    private LocalDateTime derniereConnexion;

    @Column(name = "nombre_actions_effectuees")
    private Integer nombreActionsEffectuees = 0;

    // Constructors
    public Administrateur() {
        super();
    }

    public Administrateur(String nom, String prenom, String email, String motDePasse) {
        super(nom, prenom, email, motDePasse);
        this.roleAdmin = "ADMIN";
    }

    // Getters and Setters
    public String getRoleAdmin() {
        return roleAdmin;
    }

    public void setRoleAdmin(String roleAdmin) {
        this.roleAdmin = roleAdmin;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public LocalDateTime getDerniereConnexion() {
        return derniereConnexion;
    }

    public void setDerniereConnexion(LocalDateTime derniereConnexion) {
        this.derniereConnexion = derniereConnexion;
    }

    public Integer getNombreActionsEffectuees() {
        return nombreActionsEffectuees;
    }

    public void setNombreActionsEffectuees(Integer nombreActionsEffectuees) {
        this.nombreActionsEffectuees = nombreActionsEffectuees;
    }

    // Business methods
    public void incrementActions() {
        this.nombreActionsEffectuees++;
    }

    public void updateLastLogin() {
        this.derniereConnexion = LocalDateTime.now();
    }

    public boolean hasPermission(String permission) {
        if (permissions == null) {
            return false;
        }
        return permissions.contains(permission);
    }

    @Override
    public String toString() {
        return "Administrateur{" +
                "id=" + getId() +
                ", nom='" + getNom() + '\'' +
                ", prenom='" + getPrenom() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", roleAdmin='" + roleAdmin + '\'' +
                '}';
    }
}
