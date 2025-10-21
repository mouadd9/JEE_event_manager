package ma.ensa.tetouan.eventmanagement.controller.organizer;

import ma.ensa.tetouan.eventmanagement.model.*;
import ma.ensa.tetouan.eventmanagement.service.*;
import ma.ensa.tetouan.eventmanagement.util.ServletUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet pour afficher les détails d'un événement pour l'organisateur.
 *
 * @author ENSA Tétouan
 */
@WebServlet(name = "ViewEventServlet", urlPatterns = "/organizer/events/view")
public class ViewEventServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ViewEventServlet.class);
    private EvenementService evenementService;
    private InscriptionService inscriptionService;
    private CommentaireService commentaireService;
    private EvaluationService evaluationService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.evenementService = new EvenementServiceImpl();
        this.inscriptionService = new InscriptionServiceImpl();
        this.commentaireService = new CommentaireServiceImpl();
        this.evaluationService = new EvaluationServiceImpl();
        logger.info("ViewEventServlet initialisé");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Vérifier l'authentification
        if (!ServletUtil.requireLogin(request, response)) {
            return;
        }

        // Vérifier le rôle organisateur
        if (!ServletUtil.requireRole(request, response, "ORGANISATEUR")) {
            return;
        }

        User user = ServletUtil.getLoggedUser(request);
        Long eventId = ServletUtil.getLongParameter(request, "id", null);

        if (eventId == null) {
            logger.warn("ID d'événement manquant");
            ServletUtil.setErrorMessage(request.getSession(), "ID d'événement manquant");
            ServletUtil.redirect(response, request.getContextPath() + "/events/manage");
            return;
        }

        logger.debug("Affichage des détails de l'événement {} pour l'organisateur {}", eventId, user.getId());

        try {
            // Récupérer l'événement
            Evenement event = evenementService.getEvenementById(eventId);

            // Vérifier que l'organisateur est le propriétaire de l'événement
            if (!event.getOrganisateur().getId().equals(user.getId())) {
                logger.warn("L'organisateur {} n'est pas autorisé à voir l'événement {}", 
                           user.getId(), eventId);
                ServletUtil.setErrorMessage(request.getSession(), 
                    "Vous n'êtes pas autorisé à voir cet événement");
                ServletUtil.redirect(response, request.getContextPath() + "/events/manage");
                return;
            }

            // Récupérer les inscriptions
            List<Inscription> inscriptions = inscriptionService.getInscriptionsByEvenement(eventId);

            // Récupérer les commentaires et évaluations
            List<Commentaire> commentaires = commentaireService.getCommentairesByEvenement(eventId);
            List<Evaluation> evaluations = evaluationService.getEvaluationsByEvenement(eventId);
            Double averageRating = evaluationService.getAverageRating(eventId);

            // Calculer les statistiques
            long acceptedCount = inscriptions.stream()
                .filter(i -> i.getStatut() == StatutInscription.ACCEPTEE)
                .count();
            long pendingCount = inscriptions.stream()
                .filter(i -> i.getStatut() == StatutInscription.EN_ATTENTE)
                .count();
            long refusedCount = inscriptions.stream()
                .filter(i -> i.getStatut() == StatutInscription.REFUSEE)
                .count();
            long cancelledCount = inscriptions.stream()
                .filter(i -> i.getStatut() == StatutInscription.ANNULEE)
                .count();

            // Mettre les données dans la requête
            request.setAttribute("evenement", event);
            request.setAttribute("inscriptions", inscriptions);
            request.setAttribute("commentaires", commentaires);
            request.setAttribute("evaluations", evaluations);
            request.setAttribute("averageRating", averageRating != null ? averageRating : 0.0);
            request.setAttribute("acceptedCount", acceptedCount);
            request.setAttribute("pendingCount", pendingCount);
            request.setAttribute("refusedCount", refusedCount);
            request.setAttribute("cancelledCount", cancelledCount);

            logger.debug("Détails de l'événement {} chargés: {} inscriptions, {} commentaires", 
                        eventId, inscriptions.size(), commentaires.size());

            // Forward vers la JSP
            ServletUtil.forward(request, response, "/WEB-INF/views/organizer/event-details.jsp");

        } catch (Exception e) {
            logger.error("Erreur lors du chargement des détails de l'événement", e);
            ServletUtil.setErrorMessage(request.getSession(),
                "Erreur lors du chargement de l'événement: " + e.getMessage());
            ServletUtil.redirect(response, request.getContextPath() + "/events/manage");
        }
    }
}
