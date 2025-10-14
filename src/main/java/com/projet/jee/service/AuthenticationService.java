package com.projet.jee.service;

import com.projet.jee.dao.UtilisateurDAO;
import com.projet.jee.model.Utilisateur;
import com.projet.jee.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

/**
 * Service for authentication operations (login, logout, session management).
 * Handles user authentication and session management.
 */
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final UtilisateurDAO utilisateurDAO;

    // Session attribute keys
    public static final String SESSION_USER = "currentUser";
    public static final String SESSION_USER_ID = "userId";
    public static final String SESSION_USER_TYPE = "userType";
    public static final String SESSION_USER_EMAIL = "userEmail";

    public AuthenticationService() {
        this.utilisateurDAO = new UtilisateurDAO();
    }

    /**
     * Authenticate a user with email and password
     *
     * @param email    user's email
     * @param password plain text password
     * @return Optional containing the authenticated user, or empty if authentication failed
     */
    public Optional<Utilisateur> authenticate(String email, String password) {
        try {
            // Find user by email
            Optional<Utilisateur> userOpt = utilisateurDAO.findByEmail(email);

            if (!userOpt.isPresent()) {
                logger.warn("Échec d'authentification: email non trouvé - {}", email);
                return Optional.empty();
            }

            Utilisateur user = userOpt.get();

            // Check if account is active
            if (!user.getEstActif()) {
                logger.warn("Échec d'authentification: compte désactivé - {}", email);
                return Optional.empty();
            }

            // Verify password
            if (PasswordUtil.verifyPassword(password, user.getMotDePasse())) {
                logger.info("Authentification réussie pour: {}", email);
                return Optional.of(user);
            } else {
                logger.warn("Échec d'authentification: mot de passe incorrect - {}", email);
                return Optional.empty();
            }

        } catch (Exception e) {
            logger.error("Erreur lors de l'authentification de: {}", email, e);
            return Optional.empty();
        }
    }

    /**
     * Create a user session after successful authentication
     *
     * @param request HTTP servlet request
     * @param user    authenticated user
     */
    public void createUserSession(HttpServletRequest request, Utilisateur user) {
        // Get existing session if any
        HttpSession oldSession = request.getSession(false);

        // Invalidate old session to prevent session fixation
        if (oldSession != null) {
            oldSession.invalidate();
        }

        // Create new session
        HttpSession newSession = request.getSession(true);

        // Store user information in session
        newSession.setAttribute(SESSION_USER, user);
        newSession.setAttribute(SESSION_USER_ID, user.getId());
        newSession.setAttribute(SESSION_USER_TYPE, user.getUserType());
        newSession.setAttribute(SESSION_USER_EMAIL, user.getEmail());

        // Set session timeout (30 minutes)
        newSession.setMaxInactiveInterval(30 * 60);

        logger.info("Session créée pour l'utilisateur: {} (ID: {})", user.getEmail(), user.getId());
    }

    /**
     * Logout user and destroy session
     *
     * @param session HTTP session
     */
    public void logout(HttpSession session) {
        if (session != null) {
            String email = (String) session.getAttribute(SESSION_USER_EMAIL);
            session.invalidate();
            logger.info("Utilisateur déconnecté: {}", email);
        }
    }

    /**
     * Check if user is authenticated
     *
     * @param session HTTP session
     * @return true if user is authenticated
     */
    public boolean isAuthenticated(HttpSession session) {
        return session != null && session.getAttribute(SESSION_USER) != null;
    }

    /**
     * Get current user from session
     *
     * @param session HTTP session
     * @return Optional containing current user, or empty if not authenticated
     */
    public Optional<Utilisateur> getCurrentUser(HttpSession session) {
        if (session == null) {
            return Optional.empty();
        }
        Utilisateur user = (Utilisateur) session.getAttribute(SESSION_USER);
        return Optional.ofNullable(user);
    }

    /**
     * Get current user ID from session
     *
     * @param session HTTP session
     * @return user ID or null if not authenticated
     */
    public Long getCurrentUserId(HttpSession session) {
        if (session == null) {
            return null;
        }
        return (Long) session.getAttribute(SESSION_USER_ID);
    }

    /**
     * Check if current user has a specific role
     *
     * @param session  HTTP session
     * @param userType expected user type (participant, organisateur, administrateur)
     * @return true if user has the specified role
     */
    public boolean hasRole(HttpSession session, String userType) {
        if (session == null) {
            return false;
        }
        String currentUserType = (String) session.getAttribute(SESSION_USER_TYPE);
        return userType.equalsIgnoreCase(currentUserType);
    }

    /**
     * Check if current user is an administrator
     *
     * @param session HTTP session
     * @return true if user is an administrator
     */
    public boolean isAdmin(HttpSession session) {
        return hasRole(session, "administrateur");
    }

    /**
     * Check if current user is an organizer
     *
     * @param session HTTP session
     * @return true if user is an organizer
     */
    public boolean isOrganizer(HttpSession session) {
        return hasRole(session, "organisateur");
    }

    /**
     * Check if current user is a participant
     *
     * @param session HTTP session
     * @return true if user is a participant
     */
    public boolean isParticipant(HttpSession session) {
        return hasRole(session, "participant");
    }

    /**
     * Refresh user data in session (call after user updates their profile)
     *
     * @param session HTTP session
     */
    public void refreshUserSession(HttpSession session) {
        Long userId = getCurrentUserId(session);
        if (userId != null) {
            Optional<Utilisateur> userOpt = utilisateurDAO.findById(userId);
            userOpt.ifPresent(user -> {
                session.setAttribute(SESSION_USER, user);
                session.setAttribute(SESSION_USER_TYPE, user.getUserType());
                logger.info("Session rafraîchie pour l'utilisateur ID: {}", userId);
            });
        }
    }
}
