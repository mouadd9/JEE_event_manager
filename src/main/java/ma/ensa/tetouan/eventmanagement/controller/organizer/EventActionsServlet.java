package ma.ensa.tetouan.eventmanagement.controller.organizer;

import ma.ensa.tetouan.eventmanagement.exception.BusinessException;
import ma.ensa.tetouan.eventmanagement.exception.InvalidEventStateException;
import ma.ensa.tetouan.eventmanagement.model.Evenement;
import ma.ensa.tetouan.eventmanagement.model.User;
import ma.ensa.tetouan.eventmanagement.service.EvenementService;
import ma.ensa.tetouan.eventmanagement.service.EvenementServiceImpl;
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
 * Servlet pour gérer les actions sur les événements (publier, annuler, supprimer).
 *
 * @author ENSA Tétouan
 */
@WebServlet(name = "EventActionsServlet", urlPatterns = {
    "/events/publish",
    "/events/cancel",
    "/events/delete"
})
public class EventActionsServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(EventActionsServlet.class);
    private EvenementService evenementService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.evenementService = new EvenementServiceImpl();
        logger.info("EventActionsServlet initialisé");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Vérifier l'authentification et le rôle
        if (!ServletUtil.requireLogin(request, response)) {
            return;
        }

        if (!ServletUtil.requireRole(request, response, "ORGANISATEUR")) {
            return;
        }

        User user = ServletUtil.getLoggedUser(request);
        HttpSession session = request.getSession();
        String action = getActionFromPath(request.getServletPath());

        Long eventId = ServletUtil.getLongParameter(request, "id", null);

        if (eventId == null) {
            logger.warn("ID d'événement manquant pour l'action: {}", action);
            ServletUtil.setErrorMessage(session, "ID d'événement manquant");
            ServletUtil.redirect(response, request.getContextPath() + "/events/manage");
            return;
        }

        logger.info("Action '{}' demandée pour l'événement {} par l'organisateur {}",
                   action, eventId, user.getId());

        try {
            switch (action) {
                case "publish":
                    handlePublish(eventId, user.getId(), session);
                    break;

                case "cancel":
                    handleCancel(eventId, user.getId(), request, session);
                    break;

                case "delete":
                    handleDelete(eventId, user.getId(), session);
                    break;

                default:
                    logger.warn("Action invalide: {}", action);
                    ServletUtil.setErrorMessage(session, "Action invalide");
            }

        } catch (InvalidEventStateException e) {
            logger.warn("Erreur d'état pour l'action {}: {}", action, e.getMessage());
            ServletUtil.setErrorMessage(session, e.getMessage());

        } catch (BusinessException e) {
            logger.warn("Erreur métier pour l'action {}: {}", action, e.getMessage());
            ServletUtil.setErrorMessage(session, e.getMessage());

        } catch (Exception e) {
            logger.error("Erreur inattendue lors de l'action " + action, e);
            ServletUtil.setErrorMessage(session,
                "Erreur lors de l'exécution de l'action: " + e.getMessage());
        }

        // Rediriger vers la liste des événements
        ServletUtil.redirect(response, request.getContextPath() + "/events/manage");
    }

    /**
     * Gère la publication d'un événement.
     */
    private void handlePublish(Long eventId, Long organizerId, HttpSession session) {
        logger.debug("Publication de l'événement: {}", eventId);

        Evenement published = evenementService.publishEvenement(eventId, organizerId);

        ServletUtil.setSuccessMessage(session,
            "L'événement '" + published.getTitre() + "' a été publié avec succès ! " +
            "Il est maintenant visible par tous les participants.");

        logger.info("Événement publié avec succès: ID={}, Titre={}",
                   published.getId(), published.getTitre());
    }

    /**
     * Gère l'annulation d'un événement.
     */
    private void handleCancel(Long eventId, Long organizerId, HttpServletRequest request,
                               HttpSession session) {
        logger.debug("Annulation de l'événement: {}", eventId);

        // Récupérer la raison de l'annulation (optionnel)
        String raison = ServletUtil.getStringParameter(request, "raison",
            "Annulation par l'organisateur");

        Evenement cancelled = evenementService.annulerEvenement(eventId, organizerId, raison);

        ServletUtil.setSuccessMessage(session,
            "L'événement '" + cancelled.getTitre() + "' a été annulé. " +
            "Les participants inscrits seront notifiés.");

        logger.info("Événement annulé avec succès: ID={}, Titre={}, Raison={}",
                   cancelled.getId(), cancelled.getTitre(), raison);
    }

    /**
     * Gère la suppression d'un événement.
     */
    private void handleDelete(Long eventId, Long organizerId, HttpSession session) {
        logger.debug("Suppression de l'événement: {}", eventId);

        // Récupérer le titre avant suppression pour le message
        Evenement evenement = evenementService.getEvenementById(eventId);
        String titre = evenement.getTitre();

        evenementService.deleteEvenement(eventId, organizerId);

        ServletUtil.setSuccessMessage(session,
            "L'événement '" + titre + "' a été supprimé définitivement.");

        logger.info("Événement supprimé avec succès: ID={}, Titre={}", eventId, titre);
    }

    /**
     * Extrait l'action du chemin de la servlet.
     */
    private String getActionFromPath(String servletPath) {
        if (servletPath.contains("/publish")) {
            return "publish";
        } else if (servletPath.contains("/cancel")) {
            return "cancel";
        } else if (servletPath.contains("/delete")) {
            return "delete";
        }
        return "unknown";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Rediriger GET vers POST (pour éviter les erreurs)
        ServletUtil.setWarningMessage(request.getSession(),
            "Cette action nécessite une requête POST");
        ServletUtil.redirect(response, request.getContextPath() + "/events/manage");
    }
}
