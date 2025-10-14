package com.projet.jee.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * RoleUpgradeRequest entity - tracks requests from participants to become organizers.
 *
 * Business Rules:
 * - Participants can request to upgrade to Organisateur role
 * - Requests must be approved by an Administrator
 * - Only one pending request per participant at a time
 * - Approved requests result in user role upgrade
 */
@Entity
@Table(name = "role_upgrade_request",
        indexes = {
                @Index(name = "idx_role_request_participant", columnList = "id_participant"),
                @Index(name = "idx_role_request_status", columnList = "statut"),
                @Index(name = "idx_role_request_date", columnList = "date_demande")
        })
public class RoleUpgradeRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum StatutDemande {
        EN_ATTENTE,
        APPROUVEE,
        REFUSEE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_demande")
    private Long id;

    @NotNull(message = "Le participant est obligatoire")
    @Column(name = "id_participant", nullable = false)
    private Long participantId;

    @NotBlank(message = "Le nom de l'organisation est obligatoire")
    @Size(min = 2, max = 200, message = "Le nom de l'organisation doit contenir entre 2 et 200 caractères")
    @Column(name = "nom_organisation", nullable = false, length = 200)
    private String nomOrganisation;

    @NotBlank(message = "La description est obligatoire")
    @Size(min = 50, max = 2000, message = "La description doit contenir entre 50 et 2000 caractères")
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "site_web", length = 255)
    private String siteWeb;

    @Column(name = "num_siret", length = 14)
    private String numSiret;

    @Column(name = "document_justificatif")
    private String documentJustificatif;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutDemande statut = StatutDemande.EN_ATTENTE;

    @Column(name = "date_demande", nullable = false, updatable = false)
    private LocalDateTime dateDemande;

    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;

    @Column(name = "id_admin_traitant")
    private Long adminTraitantId;

    @Column(name = "commentaire_admin", columnDefinition = "TEXT")
    private String commentaireAdmin;

    // Constructors
    public RoleUpgradeRequest() {
    }

    public RoleUpgradeRequest(Long participantId, String nomOrganisation, String description) {
        this.participantId = participantId;
        this.nomOrganisation = nomOrganisation;
        this.description = description;
    }

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        dateDemande = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    public String getNomOrganisation() {
        return nomOrganisation;
    }

    public void setNomOrganisation(String nomOrganisation) {
        this.nomOrganisation = nomOrganisation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSiteWeb() {
        return siteWeb;
    }

    public void setSiteWeb(String siteWeb) {
        this.siteWeb = siteWeb;
    }

    public String getNumSiret() {
        return numSiret;
    }

    public void setNumSiret(String numSiret) {
        this.numSiret = numSiret;
    }

    public String getDocumentJustificatif() {
        return documentJustificatif;
    }

    public void setDocumentJustificatif(String documentJustificatif) {
        this.documentJustificatif = documentJustificatif;
    }

    public StatutDemande getStatut() {
        return statut;
    }

    public void setStatut(StatutDemande statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(LocalDateTime dateDemande) {
        this.dateDemande = dateDemande;
    }

    public LocalDateTime getDateTraitement() {
        return dateTraitement;
    }

    public void setDateTraitement(LocalDateTime dateTraitement) {
        this.dateTraitement = dateTraitement;
    }

    public Long getAdminTraitantId() {
        return adminTraitantId;
    }

    public void setAdminTraitantId(Long adminTraitantId) {
        this.adminTraitantId = adminTraitantId;
    }

    public String getCommentaireAdmin() {
        return commentaireAdmin;
    }

    public void setCommentaireAdmin(String commentaireAdmin) {
        this.commentaireAdmin = commentaireAdmin;
    }

    // Business methods
    public void approuver(Long adminId, String commentaire) {
        this.statut = StatutDemande.APPROUVEE;
        this.adminTraitantId = adminId;
        this.commentaireAdmin = commentaire;
        this.dateTraitement = LocalDateTime.now();
    }

    public void refuser(Long adminId, String commentaire) {
        this.statut = StatutDemande.REFUSEE;
        this.adminTraitantId = adminId;
        this.commentaireAdmin = commentaire;
        this.dateTraitement = LocalDateTime.now();
    }

    public boolean isPending() {
        return statut == StatutDemande.EN_ATTENTE;
    }

    public boolean isApproved() {
        return statut == StatutDemande.APPROUVEE;
    }

    public boolean isRejected() {
        return statut == StatutDemande.REFUSEE;
    }

    @Override
    public String toString() {
        return "RoleUpgradeRequest{" +
                "id=" + id +
                ", participantId=" + participantId +
                ", nomOrganisation='" + nomOrganisation + '\'' +
                ", statut=" + statut +
                ", dateDemande=" + dateDemande +
                '}';
    }
}
