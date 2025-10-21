package ma.ensa.tetouan.eventmanagement.controller;

import ma.ensa.tetouan.eventmanagement.exception.AuthenticationException;
import ma.ensa.tetouan.eventmanagement.model.Organisateur;
import ma.ensa.tetouan.eventmanagement.model.User;
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

/**
 * Servlet pour gérer l'authentification des utilisateurs.
 *
 * @author ENSA Tétouan
 */
@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userService = new UserServiceImpl();
        logger.info("LoginServlet initialisé");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        logger.debug("Affichage de la page de login");

        // Si déjà connecté, rediriger vers le dashboard approprié
        if (ServletUtil.isLoggedIn(request)) {
            User user = ServletUtil.getLoggedUser(request);
            String dashboardUrl = ServletUtil.getDashboardUrl(user, request.getContextPath());
            ServletUtil.redirect(response, dashboardUrl);
            return;
        }

        // Afficher la page de login
        ServletUtil.forward(request, response, "/WEB-INF/views/auth/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Récupérer les paramètres du formulaire
        String email = ServletUtil.getStringParameter(request, "email", null);
        String password = ServletUtil.getStringParameter(request, "password", null);
        String rememberMe = request.getParameter("rememberMe");

        logger.info("Tentative de connexion pour: {}", email);

        // Validation des paramètres
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            logger.warn("Tentative de connexion avec des paramètres manquants");
            request.setAttribute("errorMessage", "L'email et le mot de passe sont obligatoires");
            ServletUtil.forward(request, response, "/WEB-INF/views/auth/login.jsp");
            return;
        }

        try {
            // Authentifier l'utilisateur
            User user = userService.authenticate(email, password);

            // Check if email is verified
            if (!user.isEmailVerified()) {
                logger.warn("Tentative de connexion avec email non vérifié: {}", email);
                HttpSession session = request.getSession(true);
                session.setAttribute("pendingVerificationEmail", email);
                session.setAttribute("pendingVerificationName", user.getNom());
                ServletUtil.setErrorMessage(session, 
                    "Votre email n'a pas encore été vérifié. Veuillez vérifier votre boîte de réception.");
                ServletUtil.redirect(response, request.getContextPath() + "/verify-email");
                return;
            }

            // Check if organizer is approved by admin
            if (user instanceof Organisateur) {
                Organisateur organisateur = (Organisateur) user;
                if (!organisateur.isApproved()) {
                    logger.warn("Tentative de connexion d'un organisateur non approuvé: {}", email);
                    request.setAttribute("errorMessage", 
                        "Votre compte organisateur est en attente d'approbation par un administrateur. " +
                        "Vous recevrez un email une fois votre compte approuvé.");
                    request.setAttribute("email", email);
                    ServletUtil.forward(request, response, "/WEB-INF/views/auth/login.jsp");
                    return;
                }
            }

            // Créer une session et stocker l'utilisateur
            HttpSession session = request.getSession(true);
            session.setAttribute(ServletUtil.ATTR_CURRENT_USER, user);

            // Gérer "Se souvenir de moi"
            if ("on".equals(rememberMe)) {
                session.setMaxInactiveInterval(7 * 24 * 60 * 60); // 7 jours
                logger.debug("Session prolongée à 7 jours pour: {}", email);
            }

            logger.info("Connexion réussie pour: {} ({})", email, user.getRole());

            // Message de succès
            ServletUtil.setSuccessMessage(session, "Bienvenue, " + user.getNom() + " !");

            // Récupérer l'URL de redirection après login, ou utiliser le dashboard par défaut
            String redirectUrl = ServletUtil.getAndClearRedirectAfterLogin(session);
            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                logger.debug("Redirection vers l'URL sauvegardée: {}", redirectUrl);
                ServletUtil.redirect(response, redirectUrl);
            } else {
                String dashboardUrl = ServletUtil.getDashboardUrl(user, request.getContextPath());
                logger.debug("Redirection vers le dashboard: {}", dashboardUrl);
                ServletUtil.redirect(response, dashboardUrl);
            }

        } catch (AuthenticationException e) {
            logger.warn("Échec d'authentification pour: {} - Raison: {}", email, e.getMessage());
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("email", email); // Repeupler le champ email
            ServletUtil.forward(request, response, "/WEB-INF/views/auth/login.jsp");

        } catch (Exception e) {
            logger.error("Erreur inattendue lors de l'authentification", e);
            request.setAttribute("errorMessage", "Une erreur inattendue s'est produite. Veuillez réessayer.");
            ServletUtil.forward(request, response, "/WEB-INF/views/auth/login.jsp");
        }
    }
}
