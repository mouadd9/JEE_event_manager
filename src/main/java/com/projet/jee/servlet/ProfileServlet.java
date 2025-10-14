package com.projet.jee.servlet;

import com.projet.jee.model.Utilisateur;
import com.projet.jee.service.AuthenticationService;
import com.projet.jee.service.UtilisateurService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Servlet for user profile management.
 */
public class ProfileServlet extends HttpServlet {
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
        Long userId = authService.getCurrentUserId(request.getSession());
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Optional<Utilisateur> userOpt = utilisateurService.getUserById(userId);
        if (userOpt.isPresent()) {
            request.setAttribute("user", userOpt.get());
            request.getRequestDispatcher("/jsp/profile.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long userId = authService.getCurrentUserId(request.getSession());
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String telephone = request.getParameter("telephone");
        String adresse = request.getParameter("adresse");
        String ville = request.getParameter("ville");
        String codePostal = request.getParameter("codePostal");
        String pays = request.getParameter("pays");

        try {
            Utilisateur updated = utilisateurService.updateProfile(
                    userId, nom, prenom, telephone, adresse, ville, codePostal, pays
            );

            // Refresh session
            authService.refreshUserSession(request.getSession());

            request.setAttribute("user", updated);
            request.setAttribute("success", "Profil mis à jour avec succès");
            request.getRequestDispatcher("/jsp/profile.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            Optional<Utilisateur> userOpt = utilisateurService.getUserById(userId);
            userOpt.ifPresent(user -> request.setAttribute("user", user));
            request.getRequestDispatcher("/jsp/profile.jsp").forward(request, response);
        }
    }
}
