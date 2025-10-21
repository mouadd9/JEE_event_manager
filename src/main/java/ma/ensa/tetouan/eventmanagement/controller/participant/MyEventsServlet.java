package ma.ensa.tetouan.eventmanagement.controller.participant;

import ma.ensa.tetouan.eventmanagement.model.Evenement;
import ma.ensa.tetouan.eventmanagement.model.Inscription;
import ma.ensa.tetouan.eventmanagement.model.User;
import ma.ensa.tetouan.eventmanagement.service.InscriptionService;
import ma.ensa.tetouan.eventmanagement.service.InscriptionServiceImpl;
import ma.ensa.tetouan.eventmanagement.service.ParticipantService;
import ma.ensa.tetouan.eventmanagement.service.ParticipantServiceImpl;
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
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servlet pour afficher les événements d'un participant (à venir et passés).
 *
 * @author ENSA Tétouan
 */
@WebServlet(name = "MyEventsServlet", urlPatterns = "/events/my-events")
public class MyEventsServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(MyEventsServlet.class);
    private ParticipantService participantService;
    private InscriptionService inscriptionService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.participantService = new ParticipantServiceImpl();
        this.inscriptionService = new InscriptionServiceImpl();
        logger.info("MyEventsServlet initialisé");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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

        logger.debug("Affichage des événements du participant: {} (ID: {})",
                    currentUser.getNom(), participantId);

        try {
            // Récupérer les événements à venir
            List<Evenement> upcomingEvents = participantService.getMyUpcomingEvents(participantId);

            // Récupérer les événements passés
            List<Evenement> pastEvents = participantService.getMyPastEvents(participantId);

            // Récupérer toutes les inscriptions pour afficher les statuts
            List<Inscription> allInscriptions = inscriptionService.getInscriptionsByParticipant(participantId);

            // Créer une map pour accéder rapidement aux inscriptions par ID d'événement
            Map<Long, Inscription> inscriptionMap = allInscriptions.stream()
                .collect(Collectors.toMap(
                    i -> i.getEvenement().getId(),
                    i -> i,
                    (existing, replacement) -> existing // En cas de doublons, garder le premier
                ));

            // Calculer quelques statistiques rapides
            long totalInscriptions = allInscriptions.size();
            long acceptedCount = allInscriptions.stream()
                .filter(i -> i.getStatut() == ma.ensa.tetouan.eventmanagement.model.StatutInscription.ACCEPTEE)
                .count();
            long waitingCount = allInscriptions.stream()
                .filter(i -> i.getStatut() == ma.ensa.tetouan.eventmanagement.model.StatutInscription.EN_ATTENTE)
                .count();

            // Définir les attributs
            request.setAttribute("upcomingEvents", upcomingEvents);
            request.setAttribute("pastEvents", pastEvents);
            request.setAttribute("inscriptions", allInscriptions);
            request.setAttribute("inscriptionMap", inscriptionMap);
            request.setAttribute("totalInscriptions", totalInscriptions);
            request.setAttribute("acceptedCount", acceptedCount);
            request.setAttribute("waitingCount", waitingCount);

            logger.debug("Événements chargés pour participant {}: {} à venir, {} passés",
                        participantId, upcomingEvents.size(), pastEvents.size());

            // Forward vers la JSP
            ServletUtil.forward(request, response, "/WEB-INF/views/participant/my-events.jsp");

        } catch (Exception e) {
            logger.error("Erreur lors du chargement des événements du participant", e);
            ServletUtil.setErrorMessage(request.getSession(),
                "Erreur lors du chargement de vos événements: " + e.getMessage());
            ServletUtil.redirect(response, request.getContextPath() + "/dashboard/participant");
        }
    }
}
