package ma.ensa.tetouan.eventmanagement.controller.participant;

import ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException;
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
import java.util.Optional;

/**
 * Servlet pour afficher les détails complets d'un événement.
 *
 * @author ENSA Tétouan
 */
@WebServlet(name = "EventDetailsServlet", urlPatterns = "/events/details")
public class EventDetailsServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(EventDetailsServlet.class);
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
        logger.info("EventDetailsServlet initialisé");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long eventId = ServletUtil.getLongParameter(request, "id", null);

        if (eventId == null) {
            logger.warn("ID d'événement manquant");
            ServletUtil.setErrorMessage(request.getSession(), "ID d'événement manquant");
            ServletUtil.redirect(response, request.getContextPath() + "/events/browse");
            return;
        }

        logger.debug("Affichage des détails de l'événement: {}", eventId);

        try {
            // Récupérer l'événement
            Evenement event = evenementService.getEvenementById(eventId);

            // Incrémenter le nombre de vues
            evenementService.incrementViews(eventId);
            logger.debug("Vues incrémentées pour l'événement {}", eventId);

            // Récupérer les commentaires et évaluations
            List<Commentaire> comments = commentaireService.getCommentairesByEvenement(eventId);
            List<Evaluation> evaluations = evaluationService.getEvaluationsByEvenement(eventId);
            Double averageRating = evaluationService.getAverageRating(eventId);

            // Vérifier le statut d'inscription du participant
            User currentUser = ServletUtil.getLoggedUser(request);
            boolean canRegister = false;
            boolean alreadyRegistered = false;
            Inscription inscription = null;
            boolean canEvaluate = false;
            Evaluation userEvaluation = null;

            if (currentUser != null && "PARTICIPANT".equals(currentUser.getRole())) {
                Long participantId = currentUser.getId();

                // Vérifier si peut s'inscrire
                canRegister = inscriptionService.canRegister(participantId, eventId);

                // Vérifier si déjà inscrit
                List<Inscription> participantInscriptions =
                    inscriptionService.getInscriptionsByParticipant(participantId);

                Optional<Inscription> inscriptionOpt = participantInscriptions.stream()
                    .filter(i -> i.getEvenement().getId().equals(eventId))
                    .findFirst();

                if (inscriptionOpt.isPresent()) {
                    inscription = inscriptionOpt.get();
                    alreadyRegistered = true;
                    logger.debug("Participant {} déjà inscrit avec statut: {}",
                                participantId, inscription.getStatut());
                }

                // Vérifier si peut évaluer (événement terminé et participation confirmée)
                canEvaluate = evaluationService.canEvaluate(participantId, eventId);

                // Récupérer l'évaluation du participant si elle existe
                Optional<Evaluation> evaluationOpt =
                    evaluationService.getEvaluationByParticipantAndEvent(participantId, eventId);
                userEvaluation = evaluationOpt.orElse(null);
            }

            // Calculer les places disponibles
            int availableSeats = inscriptionService.getAvailableSeats(eventId);
            boolean isFull = inscriptionService.isEventFull(eventId);

            // Obtenir les statistiques d'inscription
            long totalInscriptions = inscriptionService.getTotalInscriptions(eventId);
            long acceptedInscriptions = inscriptionService.getAcceptedInscriptionsCount(eventId);

            // Définir tous les attributs
            request.setAttribute("event", event);
            request.setAttribute("comments", comments);
            request.setAttribute("evaluations", evaluations);
            request.setAttribute("averageRating", averageRating);
            request.setAttribute("canRegister", canRegister);
            request.setAttribute("alreadyRegistered", alreadyRegistered);
            request.setAttribute("inscription", inscription);
            request.setAttribute("canEvaluate", canEvaluate);
            request.setAttribute("userEvaluation", userEvaluation);
            request.setAttribute("availableSeats", availableSeats);
            request.setAttribute("isFull", isFull);
            request.setAttribute("totalInscriptions", totalInscriptions);
            request.setAttribute("acceptedInscriptions", acceptedInscriptions);

            logger.debug("Détails de l'événement {} chargés: {} commentaires, {} évaluations",
                        eventId, comments.size(), evaluations.size());

            // Forward vers la JSP
            ServletUtil.forward(request, response, "/WEB-INF/views/participant/event-details.jsp");

        } catch (ResourceNotFoundException e) {
            logger.warn("Événement non trouvé: {}", eventId);
            ServletUtil.setErrorMessage(request.getSession(), "Événement non trouvé");
            ServletUtil.redirect(response, request.getContextPath() + "/events/browse");

        } catch (Exception e) {
            logger.error("Erreur lors du chargement des détails de l'événement", e);
            ServletUtil.setErrorMessage(request.getSession(),
                "Erreur lors du chargement de l'événement: " + e.getMessage());
            ServletUtil.redirect(response, request.getContextPath() + "/events/browse");
        }
    }
}
