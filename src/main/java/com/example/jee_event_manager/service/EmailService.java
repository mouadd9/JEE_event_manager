package com.example.jee_event_manager.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

@ApplicationScoped
public class EmailService {
    
    // Email configuration from environment variables
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_USERNAME = System.getenv().getOrDefault("EMAIL_USERNAME", "youssef2003plus@gmail.com");
    private static final String EMAIL_PASSWORD = System.getenv().getOrDefault("EMAIL_PASSWORD", "wyanhkxrkdqpacuu");
    private static final String FROM_EMAIL = System.getenv().getOrDefault("EMAIL_FROM", "youssef2003plus@gmail.com");
    private static final String FROM_NAME = "Event Manager";
    
    /**
     * Send verification code email
     */
    public void sendVerificationEmail(String toEmail, String code) {
        String subject = "Vérification de votre compte Event Manager";
        String body = buildVerificationEmailBody(code);
        sendEmail(toEmail, subject, body);
    }
    
    /**
     * Send password reset email with temporary password
     */
    public void sendPasswordResetEmail(String toEmail, String temporaryPassword) {
        String subject = "Réinitialisation de votre mot de passe";
        String body = buildPasswordResetEmailBody(temporaryPassword);
        sendEmail(toEmail, subject, body);
    }
    
    /**
     * Core email sending method
     */
    private void sendEmail(String toEmail, String subject, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);
        
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
            }
        });
        
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=utf-8");
            
            Transport.send(message);
            System.out.println("Email sent successfully to: " + toEmail);
            
        } catch (Exception e) {
            System.err.println("Failed to send email to: " + toEmail);
            e.printStackTrace();
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
    
    /**
     * Build verification email HTML body
     */
    private String buildVerificationEmailBody(String code) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .code-box { background: white; border: 2px dashed #667eea; padding: 20px; text-align: center; font-size: 32px; font-weight: bold; color: #667eea; letter-spacing: 5px; margin: 20px 0; border-radius: 5px; }
                    .footer { text-align: center; margin-top: 20px; font-size: 12px; color: #999; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Vérification de compte</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour,</p>
                        <p>Merci de vous être inscrit sur Event Manager ! Pour finaliser votre inscription, veuillez utiliser le code de vérification ci-dessous :</p>
                        <div class="code-box">%s</div>
                        <p>Ce code est valide pendant <strong>15 minutes</strong>.</p>
                        <p>Si vous n'avez pas créé de compte, vous pouvez ignorer cet email.</p>
                        <p>Cordialement,<br>L'équipe Event Manager</p>
                    </div>
                    <div class="footer">
                        <p>Ceci est un email automatique, merci de ne pas y répondre.</p>
                    </div>
                </div>
            </body>
            </html>
            """, code);
    }
    
    /**
     * Build password reset email HTML body
     */
    private String buildPasswordResetEmailBody(String temporaryPassword) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #f093fb 0%%, #f5576c 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .password-box { background: white; border: 2px solid #f5576c; padding: 20px; text-align: center; font-size: 24px; font-weight: bold; color: #f5576c; margin: 20px 0; border-radius: 5px; }
                    .warning { background: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; }
                    .footer { text-align: center; margin-top: 20px; font-size: 12px; color: #999; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Réinitialisation du mot de passe</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour,</p>
                        <p>Vous avez demandé la réinitialisation de votre mot de passe. Voici votre mot de passe temporaire :</p>
                        <div class="password-box">%s</div>
                        <div class="warning">
                            <strong>⚠️ Important :</strong> Pour votre sécurité, veuillez modifier ce mot de passe temporaire dès votre première connexion dans votre profil.
                        </div>
                        <p>Si vous n'avez pas demandé de réinitialisation, veuillez contacter immédiatement notre support.</p>
                        <p>Cordialement,<br>L'équipe Event Manager</p>
                    </div>
                    <div class="footer">
                        <p>Ceci est un email automatique, merci de ne pas y répondre.</p>
                    </div>
                </div>
            </body>
            </html>
            """, temporaryPassword);
    }
}
