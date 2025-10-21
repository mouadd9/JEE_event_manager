package ma.ensa.tetouan.eventmanagement.controller.participant;

import ma.ensa.tetouan.eventmanagement.exception.BusinessException;
import ma.ensa.tetouan.eventmanagement.exception.DuplicateRegistrationException;
import ma.ensa.tetouan.eventmanagement.exception.InvalidEventStateException;
import ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException;
import ma.ensa.tetouan.eventmanagement.model.Evenement;
import ma.ensa.tetouan.eventmanagement.model.Inscription;
import ma.ensa.tetouan.eventmanagement.model.StatutInscription;
import ma.ensa.tetouan.eventmanagement.model.User;
import ma.ensa.tetouan.eventmanagement.service.EmailService;
import ma.ensa.tetouan.eventmanagement.service.EmailServiceImpl;
import ma.ensa.tetouan.eventmanagement.service.EvenementService;
import ma.ensa.tetouan.eventmanagement.service.EvenementServiceImpl;
import ma.ensa.tetouan.eventmanagement.service.InscriptionService;
import ma.ensa.tetouan.eventmanagement.service.InscriptionServiceImpl;
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
import java.time.format.DateTimeFormatter;

/**
 * Servlet pour gérer l'inscription d'un participant à un événement.
 * Gère automatiquement la capacité et la liste d'attente.
 *
 * @author ENSA Tétouan
 */
@WebServlet(name = "RegisterToEventServlet", urlPatterns = "/events/register")
public class RegisterToEventServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(RegisterToEventServlet.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy à HH:mm");
    
    private InscriptionService inscriptionService;
    private EvenementService evenementService;
    private EmailService emailService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.inscriptionService = new InscriptionServiceImpl();
        this.evenementService = new EvenementServiceImpl();
        this.emailService = new EmailServiceImpl();
        logger.info("RegisterToEventServlet initialisé");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        // Vérifier que l'utilisateur est connecté
        if (!ServletUtil.requireLogin(request, response)) {
            return;
        }

        // Vérifier le rôle participant
        User currentUser = ServletUtil.getLoggedUser(request);
        if (!"PARTICIPANT".equals(currentUser.getRole())) {
            logger.warn("Tentative d'inscription par un non-participant: {} ({})",
                       currentUser.getNom(), currentUser.getRole());
            ServletUtil.setErrorMessage(session,
                "Seuls les participants peuvent s'inscrire aux événements");
            ServletUtil.redirect(response, request.getContextPath() + "/login");
            return;
        }

        Long participantId = currentUser.getId();
        Long eventId = ServletUtil.getLongParameter(request, "eventId", null);

        if (eventId == null) {
            logger.warn("ID d'événement manquant pour l'inscription");
            ServletUtil.setErrorMessage(session, "ID d'événement manquant");
            ServletUtil.redirect(response, request.getContextPath() + "/events/browse");
            return;
        }

        logger.info("Tentative d'inscription du participant {} à l'événement {}",
                   participantId, eventId);

        try {
            // S'inscrire à l'événement (gère automatiquement capacité et liste d'attente)
            Inscription inscription = inscriptionService.registerToEvent(participantId, eventId);

            // Get event details for email
            Evenement event = evenementService.getEvenementById(eventId);
            String eventDate = event.getDateDebut() != null ? 
                event.getDateDebut().format(DATE_FORMATTER) : "Date à confirmer";
            String eventLocation = event.getLieu() != null ? event.getLieu() : "Lieu à confirmer";

            // Send email notification
            boolean emailSent = emailService.sendEventRegistrationEmail(
                currentUser.getEmail(),
                currentUser.getNom(),
                event.getTitre(),
                eventDate,
                eventLocation
            );

            if (!emailSent) {
                logger.warn("Failed to send registration email to: {}", currentUser.getEmail());
            }

            // Message différent selon le statut
            if (inscription.getStatut() == StatutInscription.ACCEPTEE) {
                ServletUtil.setSuccessMessage(session,
                    "Inscription confirmée ! Vous êtes inscrit à cet événement. " +
                    "Vous recevrez un email de confirmation.");
                logger.info("Inscription ACCEPTEE pour participant {} à événement {}",
                           participantId, eventId);

            } else if (inscription.getStatut() == StatutInscription.EN_ATTENTE) {
                ServletUtil.setInfoMessage(session,
                    "Événement complet. Vous avez été ajouté à la liste d'attente. " +
                    "Vous serez notifié si une place se libère.");
                logger.info("Inscription EN_ATTENTE pour participant {} à événement {}",
                           participantId, eventId);
            }

            // Rediriger vers les détails de l'événement
            ServletUtil.redirect(response,
                request.getContextPath() + "/events/details?id=" + eventId);

        } catch (DuplicateRegistrationException e) {
            logger.warn("Tentative d'inscription en double: participant={}, événement={}",
                       participantId, eventId);
            ServletUtil.setWarningMessage(session,
                "Vous êtes déjà inscrit à cet événement");
            ServletUtil.redirect(response,
                request.getContextPath() + "/events/details?id=" + eventId);

        } catch (InvalidEventStateException e) {
            logger.warn("État d'événement invalide pour inscription: {}", e.getMessage());
            ServletUtil.setErrorMessage(session, e.getMessage());
            ServletUtil.redirect(response,
                request.getContextPath() + "/events/details?id=" + eventId);

        } catch (ResourceNotFoundException e) {
            logger.warn("Ressource non trouvée lors de l'inscription: {}", e.getMessage());
            ServletUtil.setErrorMessage(session, "Événement ou participant non trouvé");
            ServletUtil.redirect(response, request.getContextPath() + "/events/browse");

        } catch (BusinessException e) {
            logger.warn("Erreur métier lors de l'inscription: {}", e.getMessage());
            ServletUtil.setErrorMessage(session, e.getMessage());
            ServletUtil.redirect(response,
                request.getContextPath() + "/events/details?id=" + eventId);

        } catch (Exception e) {
            logger.error("Erreur inattendue lors de l'inscription à l'événement", e);
            ServletUtil.setErrorMessage(session,
                "Erreur lors de l'inscription: " + e.getMessage());
            ServletUtil.redirect(response,
                request.getContextPath() + "/events/details?id=" + eventId);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Rediriger GET vers browse (cette action nécessite POST)
        ServletUtil.setWarningMessage(request.getSession(),
            "L'inscription nécessite une requête POST");
        ServletUtil.redirect(response, request.getContextPath() + "/events/browse");
    }
}
