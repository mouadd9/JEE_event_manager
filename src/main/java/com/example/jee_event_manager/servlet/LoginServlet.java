package com.example.jee_event_manager.servlet;

import com.example.jee_event_manager.model.Utilisateur;
import com.example.jee_event_manager.service.UtilisateurService;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    @Inject
    private UtilisateurService utilisateurService;

    @Override
    public void init() throws ServletException {
        super.init();
        if (utilisateurService == null) {
            utilisateurService = new UtilisateurService();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Vérifier si l'utilisateur est déjà connecté
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/catalogue");
            return;
        }
        
        // Afficher la page de connexion
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Récupérer les paramètres du formulaire
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            // Validation des données
            if (email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
                
                request.setAttribute("error", "Email et mot de passe sont obligatoires");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }

            // Authentifier l'utilisateur
            Utilisateur utilisateur = utilisateurService.authenticate(email, password);

            if (utilisateur == null) {
                request.setAttribute("error", "Email ou mot de passe incorrect");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }

            // Créer une session pour l'utilisateur
            HttpSession session = request.getSession(true);
            session.setAttribute("user", utilisateur);
            session.setAttribute("userId", utilisateur.getId());
            session.setAttribute("userName", utilisateur.getNom());
            session.setAttribute("userEmail", utilisateur.getEmail());
            session.setAttribute("userType", utilisateur.getUserType().toString());

            // Rediriger vers le catalogue
            response.sendRedirect(request.getContextPath() + "/catalogue");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Une erreur s'est produite lors de la connexion. Veuillez réessayer.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
