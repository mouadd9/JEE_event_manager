package com.projet.jee.service;

import com.projet.jee.dao.*;
import com.projet.jee.model.*;
import com.projet.jee.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Service for user management operations.
 * Handles registration, profile updates, role upgrades, and password management.
 */
public class UtilisateurService {
    private static final Logger logger = LoggerFactory.getLogger(UtilisateurService.class);
    private final UtilisateurDAO utilisateurDAO;
    private final RoleUpgradeRequestDAO requestDAO;
    private final PasswordResetTokenDAO tokenDAO;

    public UtilisateurService() {
        this.utilisateurDAO = new UtilisateurDAO();
        this.requestDAO = new RoleUpgradeRequestDAO();
        this.tokenDAO = new PasswordResetTokenDAO();
    }

    /**
     * Register a new participant
     */
    public Participant registerParticipant(String nom, String prenom, String email, String password) {
        // Validate inputs
        if (!ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Email invalide");
        }
        if (!PasswordUtil.isPasswordStrong(password)) {
            throw new IllegalArgumentException(PasswordUtil.getPasswordStrengthMessage(password));
        }
        if (!ValidationUtil.isValidName(nom) || !ValidationUtil.isValidName(prenom)) {
            throw new IllegalArgumentException("Nom ou prénom invalide");
        }

        // Check if email already exists
        if (utilisateurDAO.emailExists(email)) {
            throw new IllegalArgumentException("Un compte existe déjà avec cet email");
        }

        // Create participant
        Participant participant = new Participant();
        participant.setNom(ValidationUtil.sanitizeInput(nom));
        participant.setPrenom(ValidationUtil.sanitizeInput(prenom));
        participant.setEmail(email.toLowerCase().trim());
        participant.setMotDePasse(PasswordUtil.hashPassword(password));
        participant.setEstActif(true);
        participant.setNotificationsActivees(true);

        try {
            Participant saved = (Participant) utilisateurDAO.save(participant);
            logger.info("Nouveau participant enregistré: {}", email);

            // Send welcome email (non-blocking, failure won't prevent registration)
            try {
                EmailService.sendWelcomeEmail(email, participant.getNomComplet());
            } catch (Exception emailEx) {
                logger.warn("Impossible d'envoyer l'email de bienvenue: {}", emailEx.getMessage());
            }

            return saved;
        } catch (Exception e) {
            logger.error("Erreur lors de l'enregistrement du participant", e);
            throw new RuntimeException("Erreur lors de la création du compte", e);
        }
    }

    /**
     * Update user profile
     */
    public Utilisateur updateProfile(Long userId, String nom, String prenom, String telephone,
                                     String adresse, String ville, String codePostal, String pays) {
        Optional<Utilisateur> userOpt = utilisateurDAO.findById(userId);
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("Utilisateur non trouvé");
        }

        Utilisateur user = userOpt.get();

        if (nom != null && ValidationUtil.isValidName(nom)) {
            user.setNom(ValidationUtil.sanitizeInput(nom));
        }
        if (prenom != null && ValidationUtil.isValidName(prenom)) {
            user.setPrenom(ValidationUtil.sanitizeInput(prenom));
        }
        if (telephone != null && ValidationUtil.isValidPhone(telephone)) {
            user.setTelephone(ValidationUtil.sanitizeInput(telephone));
        }
        if (adresse != null) {
            user.setAdresse(ValidationUtil.sanitizeInput(adresse));
        }
        if (ville != null) {
            user.setVille(ValidationUtil.sanitizeInput(ville));
        }
        if (codePostal != null && ValidationUtil.isValidPostalCode(codePostal)) {
            user.setCodePostal(codePostal.trim());
        }
        if (pays != null) {
            user.setPays(ValidationUtil.sanitizeInput(pays));
        }

        return utilisateurDAO.update(user);
    }

    /**
     * Request upgrade to organizer role
     */
    public RoleUpgradeRequest requestRoleUpgrade(Long participantId, String nomOrganisation,
                                                  String description, String siteWeb, String numSiret) {
        // Check if user exists and is a participant
        Optional<Utilisateur> userOpt = utilisateurDAO.findById(participantId);
        if (!userOpt.isPresent() || !(userOpt.get() instanceof Participant)) {
            throw new IllegalArgumentException("Utilisateur non trouvé ou déjà organisateur");
        }

        // Check if there's already a pending request
        if (requestDAO.hasPendingRequest(participantId)) {
            throw new IllegalArgumentException("Vous avez déjà une demande en attente");
        }

        // Validate inputs
        if (!ValidationUtil.isValidLength(nomOrganisation, 2, 200)) {
            throw new IllegalArgumentException("Nom d'organisation invalide");
        }
        if (!ValidationUtil.isValidLength(description, 50, 2000)) {
            throw new IllegalArgumentException("La description doit contenir entre 50 et 2000 caractères");
        }
        if (siteWeb != null && !siteWeb.trim().isEmpty() && !ValidationUtil.isValidUrl(siteWeb)) {
            throw new IllegalArgumentException("URL du site web invalide");
        }
        if (numSiret != null && !numSiret.trim().isEmpty() && !ValidationUtil.isValidSiret(numSiret)) {
            throw new IllegalArgumentException("Numéro SIRET invalide");
        }

        RoleUpgradeRequest request = new RoleUpgradeRequest();
        request.setParticipantId(participantId);
        request.setNomOrganisation(ValidationUtil.sanitizeInput(nomOrganisation));
        request.setDescription(ValidationUtil.sanitizeInput(description));
        request.setSiteWeb(siteWeb != null && !siteWeb.trim().isEmpty() ? siteWeb.trim() : null);
        request.setNumSiret(numSiret != null && !numSiret.trim().isEmpty() ? numSiret.replaceAll("\\s", "") : null);

        return requestDAO.save(request);
    }

    /**
     * Approve role upgrade request (Admin only)
     */
    public void approveRoleUpgrade(Long requestId, Long adminId, String comment) {
        Optional<RoleUpgradeRequest> reqOpt = requestDAO.findById(requestId);
        if (!reqOpt.isPresent()) {
            throw new IllegalArgumentException("Demande non trouvée");
        }

        RoleUpgradeRequest request = reqOpt.get();
        if (!request.isPending()) {
            throw new IllegalArgumentException("Cette demande a déjà été traitée");
        }

        // Upgrade user to organizer
        Organisateur organisateur = utilisateurDAO.upgradeToOrganisateur(
                request.getParticipantId(),
                request.getNomOrganisation(),
                request.getDescription()
        );

        // Update additional fields
        organisateur.setSiteWeb(request.getSiteWeb());
        organisateur.setNumSiret(request.getNumSiret());
        utilisateurDAO.update(organisateur);

        // Update request status
        request.approuver(adminId, comment);
        requestDAO.update(request);

        // Send notification email (non-blocking)
        try {
            EmailService.sendRoleUpgradeNotification(organisateur.getEmail(), true, comment);
        } catch (Exception e) {
            logger.warn("Impossible d'envoyer l'email de notification: {}", e.getMessage());
        }

        logger.info("Demande d'organisateur approuvée pour l'utilisateur ID: {}", request.getParticipantId());
    }

    /**
     * Reject role upgrade request (Admin only)
     */
    public void rejectRoleUpgrade(Long requestId, Long adminId, String reason) {
        Optional<RoleUpgradeRequest> reqOpt = requestDAO.findById(requestId);
        if (!reqOpt.isPresent()) {
            throw new IllegalArgumentException("Demande non trouvée");
        }

        RoleUpgradeRequest request = reqOpt.get();
        if (!request.isPending()) {
            throw new IllegalArgumentException("Cette demande a déjà été traitée");
        }

        request.refuser(adminId, reason);
        requestDAO.update(request);

        // Get user email for notification (non-blocking)
        Optional<Utilisateur> userOpt = utilisateurDAO.findById(request.getParticipantId());
        userOpt.ifPresent(user -> {
            try {
                EmailService.sendRoleUpgradeNotification(user.getEmail(), false, reason);
            } catch (Exception e) {
                logger.warn("Impossible d'envoyer l'email de notification: {}", e.getMessage());
            }
        });

        logger.info("Demande d'organisateur refusée pour l'utilisateur ID: {}", request.getParticipantId());
    }

    /**
     * Initiate password reset
     */
    public boolean initiatePasswordReset(String email, String baseUrl) {
        Optional<Utilisateur> userOpt = utilisateurDAO.findByEmail(email);
        if (!userOpt.isPresent()) {
            // Don't reveal if email exists or not (security)
            return true;
        }

        Utilisateur user = userOpt.get();

        // Invalidate existing tokens
        tokenDAO.invalidateUserTokens(user.getId());

        // Create new token
        PasswordResetToken token = new PasswordResetToken(user.getId(), user.getEmail());
        tokenDAO.save(token);

        // Send email
        boolean sent = EmailService.sendPasswordResetEmail(
                user.getEmail(),
                token.getToken(),
                baseUrl,
                token.getHeuresRestantes()
        );

        logger.info("Demande de réinitialisation de mot de passe pour: {}", email);
        return sent;
    }

    /**
     * Reset password using token
     */
    public boolean resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> tokenOpt = tokenDAO.findValidToken(token);
        if (!tokenOpt.isPresent()) {
            return false;
        }

        if (!PasswordUtil.isPasswordStrong(newPassword)) {
            throw new IllegalArgumentException(PasswordUtil.getPasswordStrengthMessage(newPassword));
        }

        PasswordResetToken resetToken = tokenOpt.get();
        Optional<Utilisateur> userOpt = utilisateurDAO.findById(resetToken.getUtilisateurId());

        if (!userOpt.isPresent()) {
            return false;
        }

        Utilisateur user = userOpt.get();
        user.setMotDePasse(PasswordUtil.hashPassword(newPassword));
        utilisateurDAO.update(user);

        // Mark token as used
        tokenDAO.markAsUsed(token);

        logger.info("Mot de passe réinitialisé pour l'utilisateur ID: {}", user.getId());
        return true;
    }

    /**
     * Change password (for logged-in users)
     */
    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        Optional<Utilisateur> userOpt = utilisateurDAO.findById(userId);
        if (!userOpt.isPresent()) {
            return false;
        }

        Utilisateur user = userOpt.get();

        // Verify current password
        if (!PasswordUtil.verifyPassword(currentPassword, user.getMotDePasse())) {
            throw new IllegalArgumentException("Mot de passe actuel incorrect");
        }

        // Validate new password
        if (!PasswordUtil.isPasswordStrong(newPassword)) {
            throw new IllegalArgumentException(PasswordUtil.getPasswordStrengthMessage(newPassword));
        }

        // Update password
        user.setMotDePasse(PasswordUtil.hashPassword(newPassword));
        utilisateurDAO.update(user);

        logger.info("Mot de passe changé pour l'utilisateur ID: {}", userId);
        return true;
    }

    // Additional methods
    public List<RoleUpgradeRequest> getPendingRequests() {
        return requestDAO.findPendingRequests();
    }

    public Optional<Utilisateur> getUserById(Long id) {
        return utilisateurDAO.findById(id);
    }

    public Optional<Utilisateur> getUserByEmail(String email) {
        return utilisateurDAO.findByEmail(email);
    }

    public List<Utilisateur> searchUsers(String keyword) {
        return utilisateurDAO.searchUsers(keyword);
    }

    public void activateUser(Long userId) {
        utilisateurDAO.activateUser(userId);
    }

    public void deactivateUser(Long userId) {
        utilisateurDAO.deactivateUser(userId);
    }
}
