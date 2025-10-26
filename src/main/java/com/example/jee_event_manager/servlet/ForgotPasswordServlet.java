package com.example.jee_event_manager.servlet;

import com.example.jee_event_manager.model.Utilisateur;
import com.example.jee_event_manager.service.EmailService;
import com.example.jee_event_manager.service.UtilisateurService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Optional;

@WebServlet(name = "ForgotPasswordServlet", urlPatterns = {"/forgot-password"})
public class ForgotPasswordServlet extends HttpServlet {
    
    @Inject
    private UtilisateurService utilisateurService;
    
    @Inject
    private EmailService emailService;
    
    private static final SecureRandom random = new SecureRandom();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/forgot-password.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Veuillez entrer votre adresse email");
            request.getRequestDispatcher("/forgot-password.jsp").forward(request, response);
            return;
        }
        
        try {
            // Check if user exists
            Optional<Utilisateur> utilisateurOpt = utilisateurService.findByEmail(email);
            
            if (utilisateurOpt.isEmpty()) {
                // Don't reveal if email exists or not (security)
                request.setAttribute("success", "Si un compte existe avec cet email, vous recevrez un mot de passe temporaire.");
                request.getRequestDispatcher("/forgot-password.jsp").forward(request, response);
                return;
            }
            
            Utilisateur utilisateur = utilisateurOpt.get();
            
            // Generate temporary password
            String temporaryPassword = generateTemporaryPassword();
            
            // Update user password
            utilisateurService.changePassword(utilisateur.getId(), temporaryPassword);
            
            // Send email with temporary password
            emailService.sendPasswordResetEmail(email, temporaryPassword);
            
            System.out.println("=== PASSWORD RESET ===");
            System.out.println("Email: " + email);
            System.out.println("Temporary Password: " + temporaryPassword);
            
            request.setAttribute("success", "Un mot de passe temporaire a été envoyé à votre adresse email.");
            request.getRequestDispatcher("/forgot-password.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Une erreur s'est produite. Veuillez réessayer.");
            request.getRequestDispatcher("/forgot-password.jsp").forward(request, response);
        }
    }
    
    /**
     * Generate a secure random temporary password
     */
    private String generateTemporaryPassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789!@#$%";
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return password.toString();
    }
}
