package com.example.jee_event_manager.service;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class EmailService {

    // Resend configuration
    private static final String RESEND_API_KEY = System.getenv("RESEND_API_KEY");
    private static final String FROM_EMAIL = "noreply@ufess.codes"; // Your verified domain
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
     * Send contact form email to contact@ufess.codes
     */
    public void sendContactEmail(String senderName, String senderEmail, String subject, String message) {
        String toEmail = "contact@ufess.codes";
        String emailSubject = "Contact Form: " + subject;
        String body = buildContactEmailBody(senderName, senderEmail, message);
        sendEmail(toEmail, emailSubject, body);
    }

    /**
     * Core email sending method using Resend API
     */
    private void sendEmail(String toEmail, String subject, String htmlBody) {
        HttpURLConnection connection = null;
        try {
            System.out.println("=== EmailService.sendEmail called ===");
            System.out.println("To: " + toEmail);
            System.out.println("Subject: " + subject);

            // Get API key from environment variable
            String apiKey = RESEND_API_KEY;
            if (apiKey == null || apiKey.isEmpty()) {
                System.err.println("ERROR: RESEND_API_KEY environment variable is not set!");
                throw new RuntimeException("RESEND_API_KEY environment variable is not set");
            }

            System.out.println("API Key found: " + apiKey.substring(0, Math.min(10, apiKey.length())) + "...");

            // Create connection to Resend API
            URL url = new URL("https://api.resend.com/emails");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            // Build JSON payload - escape special characters
            String escapedHtmlBody = htmlBody
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");

            String jsonPayload = String.format(
                "{\"from\": \"%s <%s>\", \"to\": [\"%s\"], \"subject\": \"%s\", \"html\": \"%s\"}",
                FROM_NAME, FROM_EMAIL, toEmail, subject, escapedHtmlBody
            );

            // Send request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Check response
            int responseCode = connection.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                System.out.println("Email sent successfully to: " + toEmail);
                System.out.println("Resend Response Code: " + responseCode);
            } else {
                // Read error response
                String errorResponse = "";
                try (java.io.InputStream errorStream = connection.getErrorStream()) {
                    if (errorStream != null) {
                        errorResponse = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                    }
                }
                System.err.println("Resend returned error code: " + responseCode);
                System.err.println("Response body: " + errorResponse);
                throw new RuntimeException("Failed to send email. Status: " + responseCode + ", Error: " + errorResponse);
            }

        } catch (Exception e) {
            System.err.println("Failed to send email to: " + toEmail);
            e.printStackTrace();
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
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
     * Build contact form email HTML body
     */
    private String buildContactEmailBody(String senderName, String senderEmail, String message) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #6366f1 0%%, #14b8a6 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .info-box { background: white; border-left: 4px solid #6366f1; padding: 15px; margin: 20px 0; }
                    .message-box { background: white; border: 1px solid #e2e8f0; padding: 20px; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; margin-top: 20px; font-size: 12px; color: #999; }
                    .label { font-weight: 600; color: #6366f1; margin-bottom: 5px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Nouveau Message de Contact</h1>
                    </div>
                    <div class="content">
                        <p>Vous avez reçu un nouveau message via le formulaire de contact EventHub.</p>

                        <div class="info-box">
                            <div class="label">De:</div>
                            <p>%s (%s)</p>
                        </div>

                        <div class="message-box">
                            <div class="label">Message:</div>
                            <p>%s</p>
                        </div>

                        <p style="margin-top: 30px; font-size: 14px; color: #666;">
                            Pour répondre à ce message, veuillez envoyer un email directement à <strong>%s</strong>
                        </p>
                    </div>
                    <div class="footer">
                        <p>EventHub - Système de messagerie automatique</p>
                    </div>
                </div>
            </body>
            </html>
            """, senderName, senderEmail, message.replace("\n", "<br>"), senderEmail);
    }
}
