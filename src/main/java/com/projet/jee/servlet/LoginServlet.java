package com.projet.jee.servlet;

import com.projet.jee.model.Utilisateur;
import com.projet.jee.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Servlet for user authentication (login).
 */
public class LoginServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private AuthenticationService authService;

    @Override
    public void init() throws ServletException {
        authService = new AuthenticationService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // If already logged in, redirect to profile
        if (authService.isAuthenticated(request.getSession(false))) {
            response.sendRedirect(request.getContextPath() + "/profile");
            return;
        }

        // Show login form
        request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String remember = request.getParameter("remember");

        try {
            Optional<Utilisateur> userOpt = authService.authenticate(email, password);

            if (userOpt.isPresent()) {
                Utilisateur user = userOpt.get();
                authService.createUserSession(request, user);

                // Set remember me cookie if requested
                if ("on".equals(remember)) {
                    // TODO: Implement remember me functionality
                }

                // Redirect based on user type
                String redirectUrl = request.getContextPath();
                if ("administrateur".equals(user.getUserType())) {
                    redirectUrl += "/admin/dashboard"; // Admin dashboard (teammate's module)
                } else if ("organisateur".equals(user.getUserType())) {
                    redirectUrl += "/organizer/dashboard"; // Organizer dashboard (teammate's module)
                } else {
                    redirectUrl += "/profile"; // Participant profile
                }

                response.sendRedirect(redirectUrl);

            } else {
                request.setAttribute("error", "Email ou mot de passe incorrect");
                request.setAttribute("email", email);
                request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            }

        } catch (Exception e) {
            logger.error("Erreur lors de la connexion", e);
            request.setAttribute("error", "Une erreur est survenue lors de la connexion");
            request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
        }
    }
}
