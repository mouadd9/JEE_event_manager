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
 * Servlet for handling forgot password requests.
 */
public class ForgotPasswordServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordServlet.class);
    private UtilisateurService utilisateurService;

    @Override
    public void init() throws ServletException {
        utilisateurService = new UtilisateurService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Show forgot password form
        request.getRequestDispatcher("/jsp/forgot-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");

        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Veuillez entrer votre adresse email");
            request.getRequestDispatcher("/jsp/forgot-password.jsp").forward(request, response);
            return;
        }

        try {
            // Get base URL for reset link
            String baseUrl = request.getScheme() + "://" +
                           request.getServerName() + ":" +
                           request.getServerPort() +
                           request.getContextPath();

            // Initiate password reset
            utilisateurService.initiatePasswordReset(email.trim(), baseUrl);

            // Always show success message (security: don't reveal if email exists)
            request.setAttribute("success",
                "Si un compte existe avec cet email, vous recevrez un lien de réinitialisation dans quelques minutes.");
            request.getRequestDispatcher("/jsp/forgot-password.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Erreur lors de la demande de réinitialisation", e);
            request.setAttribute("error", "Une erreur est survenue. Veuillez réessayer.");
            request.getRequestDispatcher("/jsp/forgot-password.jsp").forward(request, response);
        }
    }
}
