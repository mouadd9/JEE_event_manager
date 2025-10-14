package com.projet.jee.servlet;

import com.projet.jee.service.UtilisateurService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet for handling password reset with token.
 */
public class ResetPasswordServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ResetPasswordServlet.class);
    private UtilisateurService utilisateurService;

    @Override
    public void init() throws ServletException {
        utilisateurService = new UtilisateurService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");

        if (token == null || token.trim().isEmpty()) {
            request.setAttribute("error", "Lien de réinitialisation invalide");
            request.getRequestDispatcher("/jsp/reset-password.jsp").forward(request, response);
            return;
        }

        // Pass token to JSP for form submission
        request.setAttribute("token", token);
        request.getRequestDispatcher("/jsp/reset-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // Validate inputs
        if (token == null || token.trim().isEmpty()) {
            request.setAttribute("error", "Lien de réinitialisation invalide");
            request.getRequestDispatcher("/jsp/reset-password.jsp").forward(request, response);
            return;
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            request.setAttribute("error", "Veuillez entrer un nouveau mot de passe");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/jsp/reset-password.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Les mots de passe ne correspondent pas");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/jsp/reset-password.jsp").forward(request, response);
            return;
        }

        try {
            boolean success = utilisateurService.resetPassword(token, newPassword);

            if (success) {
                request.setAttribute("success",
                    "Votre mot de passe a été réinitialisé avec succès. Vous pouvez maintenant vous connecter.");
                request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            } else {
                request.setAttribute("error",
                    "Le lien de réinitialisation est invalide ou a expiré. Veuillez faire une nouvelle demande.");
                request.getRequestDispatcher("/jsp/forgot-password.jsp").forward(request, response);
            }

        } catch (IllegalArgumentException e) {
            logger.warn("Échec de réinitialisation: {}", e.getMessage());
            request.setAttribute("error", e.getMessage());
            request.setAttribute("token", token);
            request.getRequestDispatcher("/jsp/reset-password.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Erreur lors de la réinitialisation du mot de passe", e);
            request.setAttribute("error", "Une erreur est survenue. Veuillez réessayer.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/jsp/reset-password.jsp").forward(request, response);
        }
    }
}
