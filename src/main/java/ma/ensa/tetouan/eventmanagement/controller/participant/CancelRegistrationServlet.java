package ma.ensa.tetouan.eventmanagement.controller.participant;

import ma.ensa.tetouan.eventmanagement.exception.BusinessException;
import ma.ensa.tetouan.eventmanagement.exception.InvalidEventStateException;
import ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException;
import ma.ensa.tetouan.eventmanagement.model.User;
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

/**
 * Servlet pour gérer l'annulation d'une inscription par un participant.
 * Libère automatiquement une place et promeut le premier de la liste d'attente.
 *
 * @author ENSA Tétouan
 */
@WebServlet(name = "CancelRegistrationServlet", urlPatterns = "/inscriptions/cancel")
public class CancelRegistrationServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(CancelRegistrationServlet.class);
    private InscriptionService inscriptionService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.inscriptionService = new InscriptionServiceImpl();
        logger.info("CancelRegistrationServlet initialisé");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        // Vérifier l'authentification
        if (!ServletUtil.requireLogin(request, response)) {
            return;
        }

        // Vérifier le rôle participant
        if (!ServletUtil.requireRole(request, response, "PARTICIPANT")) {
            return;
        }

        User currentUser = ServletUtil.getLoggedUser(request);
        Long participantId = currentUser.getId();

        Long inscriptionId = ServletUtil.getLongParameter(request, "id", null);

        if (inscriptionId == null) {
            logger.warn("ID d'inscription manquant pour l'annulation");
            ServletUtil.setErrorMessage(session, "ID d'inscription manquant");
            ServletUtil.redirect(response, request.getContextPath() + "/events/my-events");
            return;
        }

        logger.info("Tentative d'annulation de l'inscription {} par le participant {}",
                   inscriptionId, participantId);

        try {
            // Annuler l'inscription (libère la place et promeut automatiquement de la liste d'attente)
            inscriptionService.cancelInscription(inscriptionId, participantId);

            ServletUtil.setSuccessMessage(session,
                "Inscription annulée avec succès. Si une place était confirmée, " +
                "elle a été libérée et le premier participant en attente a été notifié.");

            logger.info("Inscription {} annulée avec succès par participant {}",
                       inscriptionId, participantId);

        } catch (ResourceNotFoundException e) {
            logger.warn("Inscription non trouvée: {}", inscriptionId);
            ServletUtil.setErrorMessage(session, "Inscription non trouvée");

        } catch (InvalidEventStateException e) {
            logger.warn("État d'inscription invalide pour annulation: {}", e.getMessage());
            ServletUtil.setErrorMessage(session, e.getMessage());

        } catch (BusinessException e) {
            logger.warn("Erreur métier lors de l'annulation: {}", e.getMessage());
            ServletUtil.setErrorMessage(session, e.getMessage());

        } catch (Exception e) {
            logger.error("Erreur inattendue lors de l'annulation de l'inscription", e);
            ServletUtil.setErrorMessage(session,
                "Erreur lors de l'annulation: " + e.getMessage());
        }

        // Déterminer la redirection
        String redirectTo = ServletUtil.getStringParameter(request, "redirect", "my-events");
        Long eventId = ServletUtil.getLongParameter(request, "eventId", null);

        if ("details".equals(redirectTo) && eventId != null) {
            ServletUtil.redirect(response,
                request.getContextPath() + "/events/details?id=" + eventId);
        } else {
            ServletUtil.redirect(response, request.getContextPath() + "/events/my-events");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Rediriger GET vers my-events (cette action nécessite POST)
        ServletUtil.setWarningMessage(request.getSession(),
            "L'annulation d'inscription nécessite une requête POST");
        ServletUtil.redirect(response, request.getContextPath() + "/events/my-events");
    }
}
