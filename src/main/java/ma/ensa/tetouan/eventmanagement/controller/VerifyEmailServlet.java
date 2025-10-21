package ma.ensa.tetouan.eventmanagement.controller;

import ma.ensa.tetouan.eventmanagement.exception.BusinessException;
import ma.ensa.tetouan.eventmanagement.model.User;
import ma.ensa.tetouan.eventmanagement.service.EmailService;
import ma.ensa.tetouan.eventmanagement.service.EmailServiceImpl;
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
import java.time.LocalDateTime;

/**
 * Servlet for handling email verification
 */
@WebServlet("/verify-email")
public class VerifyEmailServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(VerifyEmailServlet.class);
    private UserService userService;
    private EmailService emailService;

    @Override
    public void init() throws ServletException {
        userService = new UserServiceImpl();
        emailService = new EmailServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        
        // Check if there's a pending verification
        if (session == null || session.getAttribute("pendingVerificationEmail") == null) {
            ServletUtil.setErrorMessage(request.getSession(true), 
                "Aucune vérification en attente. Veuillez vous inscrire d'abord.");
            ServletUtil.redirect(response, request.getContextPath() + "/register");
            return;
        }

        String email = (String) session.getAttribute("pendingVerificationEmail");
        String name = (String) session.getAttribute("pendingVerificationName");
        
        request.setAttribute("email", email);
        request.setAttribute("name", name);
        
        ServletUtil.forward(request, response, "/WEB-INF/views/auth/verify-email.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        
        // Check if there's a pending verification
        if (session == null || session.getAttribute("pendingVerificationEmail") == null) {
            ServletUtil.setErrorMessage(request.getSession(true), 
                "Session expirée. Veuillez vous inscrire à nouveau.");
            ServletUtil.redirect(response, request.getContextPath() + "/register");
            return;
        }

        String email = (String) session.getAttribute("pendingVerificationEmail");
        String action = request.getParameter("action");
        
        // Handle resend code
        if ("resend".equals(action)) {
            handleResendCode(request, response, email, session);
            return;
        }

        // Handle verification
        String code = request.getParameter("code");
        
        if (code == null || code.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Veuillez entrer le code de vérification.");
            request.setAttribute("email", email);
            request.setAttribute("name", session.getAttribute("pendingVerificationName"));
            ServletUtil.forward(request, response, "/WEB-INF/views/auth/verify-email.jsp");
            return;
        }

        try {
            // Find user by email
            User user = userService.findByEmail(email);
            
            if (user == null) {
                throw new BusinessException("Utilisateur non trouvé.");
            }

            // Check if already verified
            if (user.isEmailVerified()) {
                ServletUtil.setInfoMessage(session, "Email déjà vérifié. Vous pouvez vous connecter.");
                ServletUtil.redirect(response, request.getContextPath() + "/login");
                return;
            }

            // Validate verification code
            if (user.getVerificationCode() == null || !user.getVerificationCode().equals(code.trim())) {
                throw new BusinessException("Code de vérification invalide.");
            }

            // Check if code is expired
            if (user.getVerificationCodeExpiry() == null || 
                LocalDateTime.now().isAfter(user.getVerificationCodeExpiry())) {
                throw new BusinessException("Le code de vérification a expiré. Demandez un nouveau code.");
            }

            // Verify email
            user.setEmailVerified(true);
            user.setVerificationCode(null);
            user.setVerificationCodeExpiry(null);
            userService.update(user);

            logger.info("Email verified successfully for: {}", email);

            // Clear session attributes
            session.removeAttribute("pendingVerificationEmail");
            session.removeAttribute("pendingVerificationName");

            // Success message and redirect to login
            ServletUtil.setSuccessMessage(session, 
                "Votre email a été vérifié avec succès ! Vous pouvez maintenant vous connecter.");
            ServletUtil.redirect(response, request.getContextPath() + "/login");

        } catch (BusinessException e) {
            logger.warn("Email verification failed for: {} - Reason: {}", email, e.getMessage());
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("email", email);
            request.setAttribute("name", session.getAttribute("pendingVerificationName"));
            ServletUtil.forward(request, response, "/WEB-INF/views/auth/verify-email.jsp");

        } catch (Exception e) {
            logger.error("Unexpected error during email verification for: " + email, e);
            request.setAttribute("errorMessage", "Une erreur est survenue lors de la vérification.");
            request.setAttribute("email", email);
            request.setAttribute("name", session.getAttribute("pendingVerificationName"));
            ServletUtil.forward(request, response, "/WEB-INF/views/auth/verify-email.jsp");
        }
    }

    /**
     * Handle resend verification code
     */
    private void handleResendCode(HttpServletRequest request, HttpServletResponse response,
                                   String email, HttpSession session) 
            throws ServletException, IOException {
        try {
            // Find user by email
            User user = userService.findByEmail(email);
            
            if (user == null) {
                throw new BusinessException("Utilisateur non trouvé.");
            }

            // Check if already verified
            if (user.isEmailVerified()) {
                ServletUtil.setInfoMessage(session, "Email déjà vérifié. Vous pouvez vous connecter.");
                ServletUtil.redirect(response, request.getContextPath() + "/login");
                return;
            }

            // Generate new verification code
            String newCode = emailService.generateVerificationCode();
            user.setVerificationCode(newCode);
            user.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(15));
            userService.update(user);

            // Send new verification email
            boolean emailSent = emailService.sendVerificationEmail(email, user.getNom(), newCode);
            
            if (emailSent) {
                logger.info("Verification code resent to: {}", email);
                ServletUtil.setSuccessMessage(session, "Un nouveau code a été envoyé à votre email.");
            } else {
                logger.warn("Failed to resend verification email to: {}", email);
                ServletUtil.setErrorMessage(session, "Erreur lors de l'envoi de l'email. Réessayez plus tard.");
            }

            ServletUtil.redirect(response, request.getContextPath() + "/verify-email");

        } catch (BusinessException e) {
            logger.warn("Failed to resend verification code for: {} - Reason: {}", email, e.getMessage());
            ServletUtil.setErrorMessage(session, e.getMessage());
            ServletUtil.redirect(response, request.getContextPath() + "/verify-email");

        } catch (Exception e) {
            logger.error("Unexpected error while resending code for: " + email, e);
            ServletUtil.setErrorMessage(session, "Une erreur est survenue. Réessayez plus tard.");
            ServletUtil.redirect(response, request.getContextPath() + "/verify-email");
        }
    }
}
