package ma.ensa.tetouan.eventmanagement.service;

/**
 * Service pour l'envoi d'emails.
 *
 * @author ENSA Tétouan
 */
public interface EmailService {

    /**
     * Envoie un email de vérification avec un code à 6 chiffres.
     *
     * @param toEmail Email du destinataire
     * @param userName Nom de l'utilisateur
     * @param verificationCode Code de vérification
     * @return true si l'email a été envoyé avec succès
     */
    boolean sendVerificationEmail(String toEmail, String userName, String verificationCode);

    /**
     * Envoie un email de confirmation d'inscription à un événement.
     *
     * @param toEmail Email du participant
     * @param participantName Nom du participant
     * @param eventTitle Titre de l'événement
     * @param eventDate Date de l'événement
     * @param eventLocation Lieu de l'événement
     * @return true si l'email a été envoyé avec succès
     */
    boolean sendEventRegistrationEmail(String toEmail, String participantName, String eventTitle, 
                                       String eventDate, String eventLocation);

    /**
     * Envoie un email de confirmation d'acceptation d'inscription.
     *
     * @param toEmail Email du participant
     * @param participantName Nom du participant
     * @param eventTitle Titre de l'événement
     * @return true si l'email a été envoyé avec succès
     */
    boolean sendRegistrationAcceptedEmail(String toEmail, String participantName, String eventTitle);

    /**
     * Génère un code de vérification aléatoire à 6 chiffres.
     *
     * @return Code de vérification
     */
    String generateVerificationCode();
}
