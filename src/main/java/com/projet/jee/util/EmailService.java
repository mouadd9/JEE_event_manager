package com.projet.jee.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Email service utility for sending emails (password reset, notifications, etc.)
 * Uses JavaMail API with SMTP
 */
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    // Email configuration (should be loaded from environment variables or config file)
    private static final String SMTP_HOST = System.getenv().getOrDefault("SMTP_HOST", "smtp.gmail.com");
    private static final String SMTP_PORT = System.getenv().getOrDefault("SMTP_PORT", "587");
    private static final String SMTP_USERNAME = System.getenv().getOrDefault("EMAIL_USERNAME", "");
    private static final String SMTP_PASSWORD = System.getenv().getOrDefault("EMAIL_PASSWORD", "");
    private static final String FROM_EMAIL = System.getenv().getOrDefault("FROM_EMAIL", SMTP_USERNAME);
    private static final String FROM_NAME = "Event Management Platform";

    /**
     * Send a simple text email
     *
     * @param to      recipient email address
     * @param subject email subject
     * @param body    email body (plain text)
     * @return true if email was sent successfully
     */
    public static boolean sendEmail(String to, String subject, String body) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");

            // Create session with authentication
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
                }
            });

            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            // Send message
            Transport.send(message);
            logger.info("Email envoyé avec succès à: {}", to);
            return true;

        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email à: {}", to, e);
            return false;
        }
    }

    /**
     * Send HTML email
     *
     * @param to       recipient email address
     * @param subject  email subject
     * @param htmlBody email body (HTML)
     * @return true if email was sent successfully
     */
    public static boolean sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(htmlBody, "text/html; charset=utf-8");

            Transport.send(message);
            logger.info("Email HTML envoyé avec succès à: {}", to);
            return true;

        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email HTML à: {}", to, e);
            return false;
        }
    }

    /**
     * Send password reset email
     *
     * @param to             recipient email address
     * @param resetToken     password reset token
     * @param baseUrl        application base URL
     * @param hoursRemaining hours until token expires
     * @return true if email was sent successfully
     */
    public static boolean sendPasswordResetEmail(String to, String resetToken, String baseUrl, long hoursRemaining) {
        String resetLink = baseUrl + "/reset-password?token=" + resetToken;

        String htmlBody = "<!DOCTYPE html>" +
                "<html>" +
                "<head><meta charset='UTF-8'></head>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f9f9f9;'>" +
                "<h2 style='color: #4CAF50;'>Réinitialisation de mot de passe</h2>" +
                "<p>Bonjour,</p>" +
                "<p>Vous avez demandé la réinitialisation de votre mot de passe. Cliquez sur le lien ci-dessous pour créer un nouveau mot de passe :</p>" +
                "<p style='text-align: center; margin: 30px 0;'>" +
                "<a href='" + resetLink + "' style='background-color: #4CAF50; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block;'>Réinitialiser mon mot de passe</a>" +
                "</p>" +
                "<p>Ce lien est valide pendant <strong>" + hoursRemaining + " heures</strong>.</p>" +
                "<p>Si vous n'avez pas demandé cette réinitialisation, vous pouvez ignorer cet email en toute sécurité.</p>" +
                "<hr style='border: none; border-top: 1px solid #ddd; margin: 30px 0;'>" +
                "<p style='color: #666; font-size: 12px;'>Event Management Platform<br>Email automatique - Ne pas répondre</p>" +
                "</div>" +
                "</body>" +
                "</html>";

        return sendHtmlEmail(to, "Réinitialisation de votre mot de passe", htmlBody);
    }

    /**
     * Send welcome email to new users
     *
     * @param to       recipient email address
     * @param userName user's full name
     * @return true if email was sent successfully
     */
    public static boolean sendWelcomeEmail(String to, String userName) {
        String htmlBody = "<!DOCTYPE html>" +
                "<html>" +
                "<head><meta charset='UTF-8'></head>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f9f9f9;'>" +
                "<h2 style='color: #4CAF50;'>Bienvenue sur Event Management Platform !</h2>" +
                "<p>Bonjour " + userName + ",</p>" +
                "<p>Votre compte a été créé avec succès. Vous pouvez maintenant :</p>" +
                "<ul>" +
                "<li>Parcourir et rechercher des événements</li>" +
                "<li>Vous inscrire aux événements qui vous intéressent</li>" +
                "<li>Commenter et évaluer les événements auxquels vous avez participé</li>" +
                "<li>Demander à devenir organisateur pour créer vos propres événements</li>" +
                "</ul>" +
                "<p>Nous sommes ravis de vous compter parmi nous !</p>" +
                "<hr style='border: none; border-top: 1px solid #ddd; margin: 30px 0;'>" +
                "<p style='color: #666; font-size: 12px;'>Event Management Platform<br>Email automatique - Ne pas répondre</p>" +
                "</div>" +
                "</body>" +
                "</html>";

        return sendHtmlEmail(to, "Bienvenue sur Event Management Platform", htmlBody);
    }

    /**
     * Send role upgrade notification email
     *
     * @param to         recipient email address
     * @param isApproved true if request was approved, false if rejected
     * @param comment    admin comment
     * @return true if email was sent successfully
     */
    public static boolean sendRoleUpgradeNotification(String to, boolean isApproved, String comment) {
        String subject;
        String htmlBody;

        if (isApproved) {
            subject = "Demande d'organisateur approuvée";
            htmlBody = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head><meta charset='UTF-8'></head>" +
                    "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f9f9f9;'>" +
                    "<h2 style='color: #4CAF50;'>Félicitations ! Vous êtes maintenant organisateur</h2>" +
                    "<p>Votre demande pour devenir organisateur a été approuvée.</p>" +
                    "<p>Vous pouvez maintenant créer et gérer vos propres événements !</p>" +
                    (comment != null && !comment.isEmpty() ? "<p><strong>Commentaire de l'administrateur:</strong> " + comment + "</p>" : "") +
                    "<p>Connectez-vous pour commencer à organiser vos événements.</p>" +
                    "<hr style='border: none; border-top: 1px solid #ddd; margin: 30px 0;'>" +
                    "<p style='color: #666; font-size: 12px;'>Event Management Platform</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";
        } else {
            subject = "Demande d'organisateur refusée";
            htmlBody = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head><meta charset='UTF-8'></head>" +
                    "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f9f9f9;'>" +
                    "<h2 style='color: #f44336;'>Demande d'organisateur refusée</h2>" +
                    "<p>Nous regrettons de vous informer que votre demande pour devenir organisateur n'a pas été approuvée.</p>" +
                    (comment != null && !comment.isEmpty() ? "<p><strong>Raison:</strong> " + comment + "</p>" : "") +
                    "<p>Vous pouvez soumettre une nouvelle demande ultérieurement.</p>" +
                    "<hr style='border: none; border-top: 1px solid #ddd; margin: 30px 0;'>" +
                    "<p style='color: #666; font-size: 12px;'>Event Management Platform</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";
        }

        return sendHtmlEmail(to, subject, htmlBody);
    }
}
