package com.projet.jee.servlet;

import com.projet.jee.model.Participant;
import com.projet.jee.service.AuthenticationService;
import com.projet.jee.service.UtilisateurService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet for user registration.
 * Handles participant account creation.
 */
public class RegisterServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(RegisterServlet.class);
    private UtilisateurService utilisateurService;
    private AuthenticationService authService;

    @Override
    public void init() throws ServletException {
        utilisateurService = new UtilisateurService();
        authService = new AuthenticationService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Show registration form
        request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        try {
            // Validate passwords match
            if (!password.equals(confirmPassword)) {
                request.setAttribute("error", "Les mots de passe ne correspondent pas");
                request.setAttribute("nom", nom);
                request.setAttribute("prenom", prenom);
                request.setAttribute("email", email);
                request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
                return;
            }

            // Register user
            Participant participant = utilisateurService.registerParticipant(nom, prenom, email, password);

            // Auto-login after registration - create new session
            authService.createUserSession(request, participant);

            // Redirect to profile or home
            response.sendRedirect(request.getContextPath() + "/profile");

        } catch (IllegalArgumentException e) {
            logger.warn("Échec d'enregistrement: {}", e.getMessage());
            request.setAttribute("error", e.getMessage());
            request.setAttribute("nom", nom);
            request.setAttribute("prenom", prenom);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Erreur lors de l'enregistrement", e);
            request.setAttribute("error", "Une erreur est survenue lors de la création du compte");
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
        }
    }
}
