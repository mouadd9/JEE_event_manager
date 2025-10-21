package ma.ensa.tetouan.eventmanagement.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

/**
 * Implémentation du service d'envoi d'emails.
 *
 * @author ENSA Tétouan
 */
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    
    private final Properties emailProperties;
    private final String fromEmail;
    private final String emailPassword;
    private final boolean emailEnabled;

    public EmailServiceImpl() {
        emailProperties = new Properties();
        
        // Load email configuration from application.properties
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                Properties appProps = new Properties();
                appProps.load(input);
                
                fromEmail = appProps.getProperty("email.from");
                emailPassword = appProps.getProperty("email.password");
                emailEnabled = Boolean.parseBoolean(appProps.getProperty("email.enabled", "false"));
                
                // Configure SMTP properties for Gmail
                emailProperties.put("mail.smtp.host", appProps.getProperty("email.smtp.host", "smtp.gmail.com"));
                emailProperties.put("mail.smtp.port", appProps.getProperty("email.smtp.port", "587"));
                emailProperties.put("mail.smtp.auth", "true");
                emailProperties.put("mail.smtp.starttls.enable", "true");
                
                logger.info("Service email initialisé (activé: {})", emailEnabled);
            } else {
                throw new RuntimeException("Fichier application.properties non trouvé");
            }
        } catch (IOException e) {
            logger.error("Erreur lors du chargement de la configuration email", e);
            throw new RuntimeException("Erreur lors du chargement de la configuration email", e);
        }
    }

    @Override
    public boolean sendVerificationEmail(String toEmail, String userName, String verificationCode) {
        if (!emailEnabled) {
            logger.warn("Service email désactivé - email non envoyé à {}", toEmail);
            return false;
        }

        String subject = "Vérification de votre compte - EventManagement";
        String body = buildVerificationEmailBody(userName, verificationCode);

        return sendEmail(toEmail, subject, body);
    }

    @Override
    public boolean sendEventRegistrationEmail(String toEmail, String participantName, String eventTitle,
                                              String eventDate, String eventLocation) {
        if (!emailEnabled) {
            logger.warn("Service email désactivé - email non envoyé à {}", toEmail);
            return false;
        }

        String subject = "Confirmation d'inscription - " + eventTitle;
        String body = buildRegistrationEmailBody(participantName, eventTitle, eventDate, eventLocation);

        return sendEmail(toEmail, subject, body);
    }

    @Override
    public boolean sendRegistrationAcceptedEmail(String toEmail, String participantName, String eventTitle) {
        if (!emailEnabled) {
            logger.warn("Service email désactivé - email non envoyé à {}", toEmail);
            return false;
        }

        String subject = "Inscription acceptée - " + eventTitle;
        String body = buildAcceptanceEmailBody(participantName, eventTitle);

        return sendEmail(toEmail, subject, body);
    }

    @Override
    public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Génère un nombre entre 100000 et 999999
        return String.valueOf(code);
    }

    public boolean sendEmail(String toEmail, String subject, String body) {
        try {
            Session session = Session.getInstance(emailProperties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, emailPassword);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=utf-8");

            Transport.send(message);

            logger.info("Email envoyé avec succès à: {}", toEmail);
            return true;

        } catch (MessagingException e) {
            logger.error("Erreur lors de l'envoi de l'email à {}: {}", toEmail, e.getMessage());
            return false;
        }
    }

    private String buildVerificationEmailBody(String userName, String verificationCode) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <style>" +
                "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }" +
                "        .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }" +
                "        .code { background: #fff; border: 2px dashed #667eea; padding: 20px; text-align: center; font-size: 32px; font-weight: bold; color: #667eea; margin: 20px 0; letter-spacing: 5px; }" +
                "        .footer { text-align: center; margin-top: 20px; color: #999; font-size: 12px; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='container'>" +
                "        <div class='header'>" +
                "            <h1>🎉 Bienvenue sur EventManagement !</h1>" +
                "        </div>" +
                "        <div class='content'>" +
                "            <p>Bonjour <strong>" + userName + "</strong>,</p>" +
                "            <p>Merci de vous être inscrit sur EventManagement. Pour activer votre compte, veuillez utiliser le code de vérification ci-dessous :</p>" +
                "            <div class='code'>" + verificationCode + "</div>" +
                "            <p><strong>Ce code est valable pendant 15 minutes.</strong></p>" +
                "            <p>Si vous n'avez pas créé de compte, veuillez ignorer cet email.</p>" +
                "            <p>Cordialement,<br>L'équipe EventManagement - ENSA Tétouan</p>" +
                "        </div>" +
                "        <div class='footer'>" +
                "            <p>Cet email a été envoyé automatiquement, merci de ne pas y répondre.</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

    private String buildRegistrationEmailBody(String participantName, String eventTitle,
                                              String eventDate, String eventLocation) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <style>" +
                "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }" +
                "        .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }" +
                "        .event-details { background: #fff; border-left: 4px solid #667eea; padding: 15px; margin: 20px 0; }" +
                "        .footer { text-align: center; margin-top: 20px; color: #999; font-size: 12px; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='container'>" +
                "        <div class='header'>" +
                "            <h1>📅 Inscription enregistrée !</h1>" +
                "        </div>" +
                "        <div class='content'>" +
                "            <p>Bonjour <strong>" + participantName + "</strong>,</p>" +
                "            <p>Votre demande d'inscription à l'événement suivant a bien été enregistrée :</p>" +
                "            <div class='event-details'>" +
                "                <h3>" + eventTitle + "</h3>" +
                "                <p><strong>📅 Date :</strong> " + eventDate + "</p>" +
                "                <p><strong>📍 Lieu :</strong> " + eventLocation + "</p>" +
                "            </div>" +
                "            <p><strong>Statut :</strong> En attente de validation par l'organisateur</p>" +
                "            <p>Vous recevrez une notification par email dès que votre inscription sera validée.</p>" +
                "            <p>Cordialement,<br>L'équipe EventManagement - ENSA Tétouan</p>" +
                "        </div>" +
                "        <div class='footer'>" +
                "            <p>Cet email a été envoyé automatiquement, merci de ne pas y répondre.</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

    private String buildAcceptanceEmailBody(String participantName, String eventTitle) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <style>" +
                "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "        .header { background: linear-gradient(135deg, #28a745 0%, #20c997 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }" +
                "        .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }" +
                "        .success { background: #d4edda; border: 1px solid #c3e6cb; padding: 15px; border-radius: 5px; margin: 20px 0; text-align: center; }" +
                "        .footer { text-align: center; margin-top: 20px; color: #999; font-size: 12px; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='container'>" +
                "        <div class='header'>" +
                "            <h1>✅ Inscription confirmée !</h1>" +
                "        </div>" +
                "        <div class='content'>" +
                "            <p>Bonjour <strong>" + participantName + "</strong>,</p>" +
                "            <div class='success'>" +
                "                <h3>🎉 Bonne nouvelle !</h3>" +
                "                <p>Votre inscription à l'événement <strong>" + eventTitle + "</strong> a été acceptée !</p>" +
                "            </div>" +
                "            <p>Vous pouvez désormais consulter tous les détails de l'événement dans votre espace personnel.</p>" +
                "            <p>Nous vous attendons avec impatience !</p>" +
                "            <p>Cordialement,<br>L'équipe EventManagement - ENSA Tétouan</p>" +
                "        </div>" +
                "        <div class='footer'>" +
                "            <p>Cet email a été envoyé automatiquement, merci de ne pas y répondre.</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }
}
