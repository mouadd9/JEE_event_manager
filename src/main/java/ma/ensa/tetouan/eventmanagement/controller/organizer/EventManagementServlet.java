package ma.ensa.tetouan.eventmanagement.controller.organizer;

import ma.ensa.tetouan.eventmanagement.model.Evenement;
import ma.ensa.tetouan.eventmanagement.model.StatutEvenement;
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
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servlet pour gérer la liste des événements d'un organisateur.
 *
 * @author ENSA Tétouan
 */
@WebServlet(name = "EventManagementServlet", urlPatterns = "/events/manage")
public class EventManagementServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(EventManagementServlet.class);
    private EvenementService evenementService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.evenementService = new EvenementServiceImpl();
        logger.info("EventManagementServlet initialisé");
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
        logger.debug("Affichage de la liste des événements pour l'organisateur: {} (ID: {})",
                    user.getNom(), user.getId());

        try {

            // Récupérer tous les événements de l'organisateur
            List<Evenement> events = evenementService.getEvenementsByOrganisateur(user.getId());

            // Récupérer les filtres
            String statusFilter = ServletUtil.getStringParameter(request, "statut", "");
            String keyword = ServletUtil.getStringParameter(request, "keyword", "").trim().toLowerCase();

            // Filtrer par statut si nécessaire
            if (statusFilter != null && !statusFilter.isEmpty()) {
                try {
                    StatutEvenement statut = StatutEvenement.valueOf(statusFilter);
                    events = events.stream()
                        .filter(e -> e.getStatut() == statut)
                        .collect(Collectors.toList());
                    logger.debug("Filtre de statut appliqué: {} - {} événements trouvés",
                                statusFilter, events.size());
                } catch (IllegalArgumentException e) {
                    logger.warn("Statut invalide: {}", statusFilter);
                    ServletUtil.setWarningMessage(request.getSession(), "Statut invalide: " + statusFilter);
                }
            }


            // Filtrer par mot-clé (titre)
            if (keyword != null && !keyword.isEmpty()) {
                events = events.stream()
                    .filter(e -> e.getTitre() != null && e.getTitre().toLowerCase().contains(keyword))
                    .collect(Collectors.toList());
                logger.debug("Filtre de recherche appliqué: '{}' - {} événements trouvés", keyword, events.size());
            }

            // Trier les événements selon l'option choisie
            String sortBy = ServletUtil.getStringParameter(request, "sortBy", "dateDebut");
            switch (sortBy) {
                case "dateDebut":
                    events = events.stream()
                        .sorted((e1, e2) -> e1.getDateDebut().compareTo(e2.getDateDebut()))
                        .collect(Collectors.toList());
                    break;
                case "dateCreation":
                    events = events.stream()
                        .sorted((e1, e2) -> e1.getDateCreation().compareTo(e2.getDateCreation()))
                        .collect(Collectors.toList());
                    break;
                case "titre":
                    events = events.stream()
                        .sorted((e1, e2) -> {
                            if (e1.getTitre() == null) return 1;
                            if (e2.getTitre() == null) return -1;
                            return e1.getTitre().compareToIgnoreCase(e2.getTitre());
                        })
                        .collect(Collectors.toList());
                    break;
                case "nombreInscriptions":
                    events = events.stream()
                        .sorted((e1, e2) -> Integer.compare(e2.getNombreInscriptions(), e1.getNombreInscriptions()))
                        .collect(Collectors.toList());
                    break;
                default:
                    // Par défaut, trier par date de début
                    events = events.stream()
                        .sorted((e1, e2) -> e1.getDateDebut().compareTo(e2.getDateDebut()))
                        .collect(Collectors.toList());
            }
            request.setAttribute("sortBy", sortBy);

            // Calculer les statistiques
            long brouillonCount = events.stream().filter(e -> e.getStatut() == StatutEvenement.BROUILLON).count();
            long publieCount = events.stream().filter(e -> e.getStatut() == StatutEvenement.PUBLIE).count();
            long annuleCount = events.stream().filter(e -> e.getStatut() == StatutEvenement.ANNULE).count();
            long termineCount = events.stream().filter(e -> e.getStatut() == StatutEvenement.TERMINE).count();


            // Mettre les données dans la requête
            request.setAttribute("evenements", events);
            request.setAttribute("statusFilter", statusFilter);
            request.setAttribute("keyword", keyword);
            request.setAttribute("brouillonCount", brouillonCount);
            request.setAttribute("publieCount", publieCount);
            request.setAttribute("annuleCount", annuleCount);
            request.setAttribute("termineCount", termineCount);

            logger.debug("Liste des événements chargée: {} événements au total", events.size());

            // Forward vers la JSP
            ServletUtil.forward(request, response, "/WEB-INF/views/organizer/event-list.jsp");

        } catch (Exception e) {
            logger.error("Erreur lors du chargement de la liste des événements", e);
            ServletUtil.setErrorMessage(request.getSession(),
                "Erreur lors du chargement des événements: " + e.getMessage());
            ServletUtil.redirect(response, request.getContextPath() + "/dashboard/organizer");
        }
    }
}
