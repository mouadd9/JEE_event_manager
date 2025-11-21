package com.example.jee_event_manager.service;

import com.example.jee_event_manager.model.VerificationCode;
import com.example.jee_event_manager.model.VerificationType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@ApplicationScoped
public class VerificationCodeService {
    
    @Inject
    private EntityManager em;
    
    @Inject
    private EmailService emailService;
    
    private static final int CODE_LENGTH = 6;
    private static final int EXPIRATION_MINUTES = 15;
    private static final SecureRandom random = new SecureRandom();
    
    /**
     * Generate and send verification code for email verification
     */
    public String generateEmailVerificationCode(String email) {
        String code = generateCode();
        saveVerificationCode(email, code, VerificationType.EMAIL_VERIFICATION);

        try {
            System.out.println("=== ATTEMPTING TO SEND VERIFICATION EMAIL ===");
            System.out.println("To: " + email);
            System.out.println("Code: " + code);
            emailService.sendVerificationEmail(email, code);
            System.out.println("=== EMAIL SENT SUCCESSFULLY ===");
        } catch (Exception e) {
            System.err.println("=== FAILED TO SEND EMAIL ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send verification email: " + e.getMessage(), e);
        }

        return code;
    }
    
    /**
     * Generate and send verification code for password reset
     */
    public String generatePasswordResetCode(String email) {
        String code = generateCode();
        saveVerificationCode(email, code, VerificationType.PASSWORD_RESET);
        return code;
    }
    
    /**
     * Verify a code for email verification
     */
    public boolean verifyEmailCode(String email, String code) {
        return verifyCode(email, code, VerificationType.EMAIL_VERIFICATION);
    }
    
    /**
     * Verify a code for password reset
     */
    public boolean verifyPasswordResetCode(String email, String code) {
        return verifyCode(email, code, VerificationType.PASSWORD_RESET);
    }
    
    /**
     * Generate a random 6-digit code
     */
    private String generateCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
    
    /**
     * Save verification code to database
     */
    private void saveVerificationCode(String email, String code, VerificationType type) {
        try {
            em.getTransaction().begin();
            
            // Invalidate any existing codes for this email and type
            TypedQuery<VerificationCode> query = em.createQuery(
                "SELECT v FROM VerificationCode v WHERE v.email = :email AND v.type = :type AND v.used = false",
                VerificationCode.class
            );
            query.setParameter("email", email);
            query.setParameter("type", type);
            
            for (VerificationCode existingCode : query.getResultList()) {
                existingCode.setUsed(true);
                em.merge(existingCode);
            }
            
            // Create new verification code
            VerificationCode verificationCode = new VerificationCode();
            verificationCode.setEmail(email);
            verificationCode.setCode(code);
            verificationCode.setType(type);
            verificationCode.setCreatedAt(LocalDateTime.now());
            verificationCode.setExpiresAt(LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES));
            verificationCode.setUsed(false);
            
            em.persist(verificationCode);
            em.getTransaction().commit();
            
            System.out.println("Verification code saved for: " + email + " (Type: " + type + ")");
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Failed to save verification code", e);
        }
    }
    
    /**
     * Verify a code
     */
    private boolean verifyCode(String email, String code, VerificationType type) {
        try {
            TypedQuery<VerificationCode> query = em.createQuery(
                "SELECT v FROM VerificationCode v WHERE v.email = :email AND v.code = :code AND v.type = :type AND v.used = false",
                VerificationCode.class
            );
            query.setParameter("email", email);
            query.setParameter("code", code);
            query.setParameter("type", type);
            
            Optional<VerificationCode> codeOpt = query.getResultStream().findFirst();
            
            if (codeOpt.isEmpty()) {
                System.out.println("Verification code not found for: " + email);
                return false;
            }
            
            VerificationCode verificationCode = codeOpt.get();
            
            if (!verificationCode.isValid()) {
                System.out.println("Verification code expired or already used for: " + email);
                return false;
            }
            
            // Mark code as used
            em.getTransaction().begin();
            verificationCode.setUsed(true);
            em.merge(verificationCode);
            em.getTransaction().commit();
            
            System.out.println("Verification code validated successfully for: " + email);
            return true;
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error verifying code: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Clean up expired codes (call this periodically)
     */
    public void cleanupExpiredCodes() {
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM VerificationCode v WHERE v.expiresAt < :now")
                .setParameter("now", LocalDateTime.now())
                .executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error cleaning up expired codes: " + e.getMessage());
        }
    }
}
