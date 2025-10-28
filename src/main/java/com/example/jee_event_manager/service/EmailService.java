package com.example.jee_event_manager.service;

import com.example.jee_event_manager.model.Evenement;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.time.format.DateTimeFormatter;
import java.util.Properties;

@ApplicationScoped
public class EmailService {
    
    // Email configuration
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_USERNAME = "youssef2003plus@gmail.com";
    private static final String EMAIL_PASSWORD = "wyanhkxrkdqpacuu"; // Gmail App Password (spaces removed)
    private static final String FROM_EMAIL = "youssef2003plus@gmail.com";
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
     * Send event reminder email to participant
     */
    public void sendEventReminderEmail(String toEmail, Evenement event, String participantName) {
        String subject = "Rappel: Votre événement " + event.getTitre() + " commence demain !";
        String body = buildEventReminderEmailBody(event, participantName);
        sendEmail(toEmail, subject, body);
    }
    
    /**
     * Send ticket confirmation email to participant
     */
    public void sendTicketConfirmationEmail(String toEmail, String participantName, String eventTitle, String ticketNumber, String ticketType) {
        String subject = "Votre billet pour " + eventTitle;
        String body = buildTicketConfirmationEmailBody(participantName, eventTitle, ticketNumber, ticketType);
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
    
    /**
     * Build event reminder email HTML body
     */
    private String buildEventReminderEmailBody(Evenement event, String participantName) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        String eventDate = event.getDateDebut().format(dateFormatter);
        String eventTime = event.getDateDebut().format(timeFormatter);
        String eventEndTime = event.getDateFin().format(timeFormatter);
        
        // Construire la liste des catégories
        String categories = "";
        if (event.getCategories() != null && !event.getCategories().isEmpty()) {
            categories = event.getCategories().stream()
                .map(cat -> cat.getNom())
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        }
        
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .event-card { background: white; border: 1px solid #ddd; border-radius: 8px; padding: 20px; margin: 20px 0; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                    .event-title { font-size: 24px; font-weight: bold; color: #667eea; margin-bottom: 15px; }
                    .event-details { margin: 10px 0; }
                    .event-details strong { color: #555; }
                    .reminder-badge { background: #ff6b6b; color: white; padding: 8px 16px; border-radius: 20px; font-size: 14px; font-weight: bold; display: inline-block; margin-bottom: 20px; }
                    .footer { text-align: center; margin-top: 20px; font-size: 12px; color: #999; }
                    .cta-button { background: #667eea; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; display: inline-block; margin: 20px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>📅 Rappel d'événement</h1>
                        <p>Votre événement commence demain !</p>
                    </div>
                    <div class="content">
                        <div class="reminder-badge">⏰ Rappel 24h</div>
                        
                        <p>Bonjour <strong>%s</strong>,</p>
                        
                        <p>Nous vous rappelons que vous êtes inscrit(e) à l'événement suivant qui commence <strong>demain</strong> :</p>
                        
                        <div class="event-card">
                            <div class="event-title">%s</div>
                            
                            <div class="event-details">
                                <strong>📅 Date :</strong> %s<br>
                                <strong>🕐 Heure :</strong> %s - %s<br>
                                <strong>📍 Lieu :</strong> %s<br>
                                <strong>📝 Description :</strong> %s<br>
                                <strong>👥 Capacité :</strong> %d personnes<br>
                                %s
                            </div>
                        </div>
                        
                        <p><strong>N'oubliez pas :</strong></p>
                        <ul>
                            <li>Arrivez quelques minutes en avance</li>
                            <li>Apportez une pièce d'identité si nécessaire</li>
                            <li>Vérifiez les conditions météorologiques si l'événement est en extérieur</li>
                        </ul>
                        
                        <p>Nous avons hâte de vous voir à cet événement !</p>
                        
                        <p>Cordialement,<br>L'équipe Event Manager</p>
                    </div>
                    <div class="footer">
                        <p>Ceci est un email automatique de rappel, merci de ne pas y répondre.</p>
                        <p>Si vous ne pouvez plus participer à cet événement, veuillez vous désinscrire depuis votre tableau de bord.</p>
                    </div>
                </div>
            </body>
            </html>
            """, 
            participantName,
            event.getTitre(),
            eventDate,
            eventTime,
            eventEndTime,
            event.getLieu(),
            event.getDescription() != null ? event.getDescription() : "Aucune description disponible",
            event.getCapacite(),
            !categories.isEmpty() ? "<strong>🏷️ Catégories :</strong> " + categories + "<br>" : ""
        );
    }
    
    /**
     * Build ticket confirmation email HTML body
     */
    private String buildTicketConfirmationEmailBody(String participantName, String eventTitle, String ticketNumber, String ticketType) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .ticket-info { background: white; border: 2px solid #667eea; padding: 20px; margin: 20px 0; border-radius: 8px; }
                    .ticket-number { font-size: 24px; font-weight: bold; color: #667eea; text-align: center; margin: 20px 0; }
                    .ticket-type { padding: 10px 20px; border-radius: 20px; font-weight: bold; text-align: center; margin: 10px 0; }
                    .type-standard { background: #e3f2fd; color: #1976d2; }
                    .type-vip { background: #fff3e0; color: #f57c00; }
                    .type-premium { background: #f3e5f5; color: #7b1fa2; }
                    .footer { text-align: center; margin-top: 20px; font-size: 12px; color: #999; }
                    .cta-button { background: #667eea; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; display: inline-block; margin: 20px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎫 Votre billet est prêt !</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour <strong>%s</strong>,</p>
                        
                        <p>Votre inscription à l'événement <strong>%s</strong> a été confirmée !</p>
                        
                        <div class="ticket-info">
                            <div class="ticket-number">Numéro de billet: %s</div>
                            <div class="ticket-type type-%s">Type: %s</div>
                            <p><strong>Statut:</strong> Valide</p>
                        </div>
                        
                        <p>Vous pouvez télécharger votre billet PDF depuis votre espace participant ou le présenter directement avec ce numéro.</p>
                        
                        <p><strong>Important:</strong> Ce billet est gratuit et non transférable. Présentez-le à l'entrée de l'événement.</p>
                        
                        <p>Cordialement,<br>L'équipe EventHub</p>
                    </div>
                    <div class="footer">
                        <p>Ceci est un email automatique, merci de ne pas y répondre.</p>
                    </div>
                </div>
            </body>
            </html>
            """, 
            participantName, 
            eventTitle, 
            ticketNumber, 
            ticketType.toLowerCase(),
            ticketType
        );
    }
}
