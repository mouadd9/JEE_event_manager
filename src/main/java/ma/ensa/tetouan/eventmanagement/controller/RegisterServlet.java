package ma.ensa.tetouan.eventmanagement.controller;

import ma.ensa.tetouan.eventmanagement.exception.BusinessException;
import ma.ensa.tetouan.eventmanagement.model.Organisateur;
import ma.ensa.tetouan.eventmanagement.model.Participant;
import ma.ensa.tetouan.eventmanagement.model.User;
import ma.ensa.tetouan.eventmanagement.service.EmailService;
import ma.ensa.tetouan.eventmanagement.service.EmailServiceImpl;
import ma.ensa.tetouan.eventmanagement.service.UserService;
import ma.ensa.tetouan.eventmanagement.service.UserServiceImpl;
import ma.ensa.tetouan.eventmanagement.util.ServletUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Servlet pour gérer l'inscription des nouveaux utilisateurs.
 *
 * @author ENSA Tétouan
 */
@WebServlet(name = "RegisterServlet", urlPatterns = "/register")
public class RegisterServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(RegisterServlet.class);
    private UserService userService;
    private EmailService emailService;

    @Override
    public void init() throws ServletException {
        userService = new UserServiceImpl();
        emailService = new EmailServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        logger.debug("Affichage de la page d'inscription");

        // Si déjà connecté, rediriger vers le dashboard
        if (ServletUtil.isLoggedIn(request)) {
            User user = ServletUtil.getLoggedUser(request);
            String dashboardUrl = ServletUtil.getDashboardUrl(user, request.getContextPath());
            ServletUtil.redirect(response, dashboardUrl);
            return;
        }

        // Afficher la page d'inscription
        ServletUtil.forward(request, response, "/WEB-INF/views/auth/register.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Récupérer les paramètres du formulaire
        String nom = ServletUtil.getStringParameter(request, "nom", null);
        String email = ServletUtil.getStringParameter(request, "email", null);
        String password = ServletUtil.getStringParameter(request, "password", null);
        String confirmPassword = ServletUtil.getStringParameter(request, "confirmPassword", null);
        String userType = ServletUtil.getStringParameter(request, "userType", "PARTICIPANT");

        // Paramètres spécifiques pour organisateur
        String organisation = ServletUtil.getStringParameter(request, "organisation", null);
        String telephone = ServletUtil.getStringParameter(request, "telephone", null);

        // Paramètres spécifiques pour participant
        String preferences = ServletUtil.getStringParameter(request, "preferences", null);

        logger.info("Tentative d'inscription: {} - Type: {}", email, userType);

        // Validation des paramètres obligatoires
        if (nom == null || nom.isEmpty() ||
            email == null || email.isEmpty() ||
            password == null || password.isEmpty() ||
            confirmPassword == null || confirmPassword.isEmpty()) {

            logger.warn("Tentative d'inscription avec des paramètres manquants");
            request.setAttribute("errorMessage", "Tous les champs obligatoires doivent être remplis");
            repopulateForm(request, nom, email, userType, organisation, telephone, preferences);
            ServletUtil.forward(request, response, "/WEB-INF/views/auth/register.jsp");
            return;
        }

        // Validation de la correspondance des mots de passe
        if (!password.equals(confirmPassword)) {
            logger.warn("Les mots de passe ne correspondent pas pour: {}", email);
            request.setAttribute("errorMessage", "Les mots de passe ne correspondent pas");
            repopulateForm(request, nom, email, userType, organisation, telephone, preferences);
            ServletUtil.forward(request, response, "/WEB-INF/views/auth/register.jsp");
            return;
        }

        try {
            // Créer l'objet utilisateur approprié selon le type
            User user;

            if ("ORGANISATEUR".equalsIgnoreCase(userType)) {
                // Validation spécifique pour organisateur
                if (organisation == null || organisation.isEmpty()) {
                    throw new BusinessException("Le nom de l'organisation est obligatoire pour un organisateur");
                }

                Organisateur organisateur = new Organisateur();
                organisateur.setNom(nom);
                organisateur.setEmail(email);
                organisateur.setMotDePasse(password);
                organisateur.setOrganisation(organisation);
                organisateur.setTelephone(telephone);

                user = organisateur;

            } else { // PARTICIPANT
                Participant participant = new Participant();
                participant.setNom(nom);
                participant.setEmail(email);
                participant.setMotDePasse(password);
                participant.setPreferences(preferences);

                user = participant;
            }

            // Generate verification code and set expiry
            String verificationCode = emailService.generateVerificationCode();
            user.setVerificationCode(verificationCode);
            user.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(15));
            user.setEmailVerified(false);

            // Save user (account inactive until email verified)
            User registeredUser = userService.register(user);

            logger.info("User registered, verification email pending for: {} (ID: {}, Type: {})",
                       email, registeredUser.getId(), userType);

            // Send verification email
            boolean emailSent = emailService.sendVerificationEmail(email, nom, verificationCode);
            
            if (!emailSent) {
                logger.warn("Failed to send verification email to: {}", email);
            }

            // Store email in session for verification page
            HttpSession session = request.getSession(true);
            session.setAttribute("pendingVerificationEmail", email);
            session.setAttribute("pendingVerificationName", nom);
            
            ServletUtil.setSuccessMessage(session,
                "Un code de vérification a été envoyé à votre adresse email. Veuillez vérifier votre boîte de réception.");

            // Redirect to verification page
            ServletUtil.redirect(response, request.getContextPath() + "/verify-email");

        } catch (BusinessException e) {
            logger.warn("Échec d'inscription pour: {} - Raison: {}", email, e.getMessage());
            request.setAttribute("errorMessage", e.getMessage());
            repopulateForm(request, nom, email, userType, organisation, telephone, preferences);
            ServletUtil.forward(request, response, "/WEB-INF/views/auth/register.jsp");

        } catch (Exception e) {
            logger.error("Erreur inattendue lors de l'inscription", e);
            request.setAttribute("errorMessage",
                "Une erreur inattendue s'est produite. Veuillez réessayer.");
            repopulateForm(request, nom, email, userType, organisation, telephone, preferences);
            ServletUtil.forward(request, response, "/WEB-INF/views/auth/register.jsp");
        }
    }

    /**
     * Repopule le formulaire avec les données soumises en cas d'erreur.
     */
    private void repopulateForm(HttpServletRequest request, String nom, String email, String userType,
                                 String organisation, String telephone, String preferences) {
        request.setAttribute("nom", nom);
        request.setAttribute("email", email);
        request.setAttribute("userType", userType);
        request.setAttribute("organisation", organisation);
        request.setAttribute("telephone", telephone);
        request.setAttribute("preferences", preferences);
    }
}
