package com.example.jee_event_manager.servlet;

import com.example.jee_event_manager.model.Utilisateur;
import com.example.jee_event_manager.model.UserType;
import com.example.jee_event_manager.service.UtilisateurService;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

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
        
        // Afficher la page d'inscription
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Récupérer les paramètres du formulaire
            String nom = request.getParameter("nom");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String userTypeStr = request.getParameter("userType");

            // Validation des données
            if (nom == null || nom.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                userTypeStr == null || userTypeStr.trim().isEmpty()) {
                
                request.setAttribute("error", "Tous les champs sont obligatoires");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            // Validation du mot de passe
            if (password.length() < 8) {
                request.setAttribute("error", "Le mot de passe doit contenir au moins 8 caractères");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            // Convertir le type d'utilisateur
            UserType userType;
            try {
                userType = UserType.valueOf(userTypeStr);
            } catch (IllegalArgumentException e) {
                request.setAttribute("error", "Type d'utilisateur invalide");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            // Vérifier si l'email existe déjà
            if (!utilisateurService.isEmailAvailable(email)) {
                request.setAttribute("error", "Un compte avec cet email existe déjà");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            // Créer l'utilisateur
            Utilisateur utilisateur = utilisateurService.createUser(nom, email, password, userType);

            // Créer une session pour l'utilisateur
            HttpSession session = request.getSession(true);
            session.setAttribute("user", utilisateur);
            session.setAttribute("userId", utilisateur.getId());
            session.setAttribute("userName", utilisateur.getNom());
            session.setAttribute("userEmail", utilisateur.getEmail());
            session.setAttribute("userType", utilisateur.getUserType().toString());

            // Rediriger selon le type d'utilisateur
            if (userType == UserType.PARTICIPANT) {
                response.sendRedirect(request.getContextPath() + "/catalogue");
            } else {
                // Pour les organisateurs, rediriger vers une page de création d'événement
                response.sendRedirect(request.getContextPath() + "/catalogue");
            }

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Une erreur s'est produite lors de la création du compte. Veuillez réessayer.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}
