package com.example.jee_event_manager.servlet;

import com.example.jee_event_manager.model.Utilisateur;
import com.example.jee_event_manager.model.UserType;
import com.example.jee_event_manager.service.UtilisateurService;
import com.example.jee_event_manager.service.VerificationCodeService;

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
    
    @Inject
    private VerificationCodeService verificationCodeService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Vérifier si l'utilisateur est déjà connecté
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/catalogue");
            return;
        }
        
        // Check if we're at the verification step
        String step = request.getParameter("step");
        if ("verify".equals(step)) {
            request.getRequestDispatcher("/verify-email.jsp").forward(request, response);
        } else {
            // Afficher la page d'inscription
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("send-code".equals(action)) {
            handleSendCode(request, response);
        } else if ("verify-code".equals(action)) {
            handleVerifyCode(request, response);
        } else {
            // Default: send code (Step 1)
            handleSendCode(request, response);
        }
    }
    
    /**
     * Step 1: Validate user data and send verification code
     */
    private void handleSendCode(HttpServletRequest request, HttpServletResponse response)
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

            // Generate and send verification code
            String code = verificationCodeService.generateEmailVerificationCode(email);
            
            System.out.println("=== VERIFICATION CODE SENT ===");
            System.out.println("Email: " + email);
            System.out.println("Code: " + code);

            // Store registration data in session (temporarily)
            HttpSession session = request.getSession(true);
            session.setAttribute("reg_nom", nom);
            session.setAttribute("reg_email", email);
            session.setAttribute("reg_password", password);
            session.setAttribute("reg_userType", userType.toString());
            
            // Set success message
            request.setAttribute("email", email);
            request.setAttribute("success", "Un code de vérification a été envoyé à votre adresse email.");
            
            // Forward to verification page
            request.getRequestDispatcher("/verify-email.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Une erreur s'est produite lors de l'envoi du code. Veuillez réessayer.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
    
    /**
     * Step 2: Verify code and create account
     */
    private void handleVerifyCode(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                response.sendRedirect(request.getContextPath() + "/register");
                return;
            }

            // Get registration data from session
            String nom = (String) session.getAttribute("reg_nom");
            String email = (String) session.getAttribute("reg_email");
            String password = (String) session.getAttribute("reg_password");
            String userTypeStr = (String) session.getAttribute("reg_userType");
            String code = request.getParameter("code");

            if (nom == null || email == null || password == null || userTypeStr == null) {
                request.setAttribute("error", "Session expirée. Veuillez recommencer l'inscription.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            if (code == null || code.trim().isEmpty()) {
                request.setAttribute("error", "Veuillez entrer le code de vérification.");
                request.setAttribute("email", email);
                request.getRequestDispatcher("/verify-email.jsp").forward(request, response);
                return;
            }

            // Verify the code
            boolean isValid = verificationCodeService.verifyEmailCode(email, code.trim());
            
            if (!isValid) {
                request.setAttribute("error", "Code de vérification invalide ou expiré. Veuillez réessayer.");
                request.setAttribute("email", email);
                request.getRequestDispatcher("/verify-email.jsp").forward(request, response);
                return;
            }

            // Code is valid - check if user already exists
            UserType userType = UserType.valueOf(userTypeStr);
            
            // Check if user already exists
            if (utilisateurService.findByEmail(email).isPresent()) {
                // User already exists, just verify them
                Utilisateur existingUser = utilisateurService.findByEmail(email).get();
                existingUser.setIsVerified(true);
                
                // If it's a participant, activate immediately
                if (existingUser.getUserType() == UserType.PARTICIPANT) {
                    existingUser.setIsActive(true);
                }
                
                utilisateurService.update(existingUser);
            } else {
                // Create new user account
                Utilisateur utilisateur = utilisateurService.createUser(nom, email, password, userType);
            }

            // Clean up registration session data
            session.removeAttribute("reg_nom");
            session.removeAttribute("reg_email");
            session.removeAttribute("reg_password");
            session.removeAttribute("reg_userType");

            // Redirect to login page with success message
            session.setAttribute("registrationSuccess", true);
            
            if (userType == UserType.PARTICIPANT) {
                session.setAttribute("successMessage", "Votre compte a été créé avec succès ! Vous pouvez maintenant vous connecter.");
            } else {
                session.setAttribute("successMessage", "Votre compte a été créé avec succès ! Votre compte est en attente d'approbation par un administrateur. Vous recevrez une notification par email une fois votre compte approuvé.");
            }
            
            response.sendRedirect(request.getContextPath() + "/login");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Une erreur s'est produite lors de la vérification. Veuillez réessayer.");
            request.getRequestDispatcher("/verify-email.jsp").forward(request, response);
        }
    }
}
