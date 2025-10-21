package ma.ensa.tetouan.eventmanagement.controller.organizer;

import ma.ensa.tetouan.eventmanagement.model.Evenement;
import ma.ensa.tetouan.eventmanagement.model.Organisateur;
import ma.ensa.tetouan.eventmanagement.model.User;
import ma.ensa.tetouan.eventmanagement.service.EvenementService;
import ma.ensa.tetouan.eventmanagement.service.EvenementServiceImpl;
import ma.ensa.tetouan.eventmanagement.service.OrganisateurService;
import ma.ensa.tetouan.eventmanagement.service.OrganisateurServiceImpl;
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

/**
 * Servlet pour le tableau de bord de l'organisateur.
 *
 * @author ENSA Tétouan
 */
@WebServlet(name = "OrganizerDashboardServlet", urlPatterns = "/organizer/dashboard")
public class OrganizerDashboardServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(OrganizerDashboardServlet.class);
    private OrganisateurService organisateurService;
    private EvenementService evenementService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.organisateurService = new OrganisateurServiceImpl();
        this.evenementService = new EvenementServiceImpl();
        logger.info("OrganizerDashboardServlet initialisé");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Vérifier l'authentification
        if (!ServletUtil.requireLogin(request, response)) {
            return;
        }

        // Vérifier le rôle
        if (!ServletUtil.requireRole(request, response, "ORGANISATEUR")) {
            return;
        }

        User user = ServletUtil.getLoggedUser(request);
        logger.debug("Affichage du dashboard pour l'organisateur: {} (ID: {})", user.getNom(), user.getId());

        try {
            // Récupérer l'organisateur complet
            Organisateur organisateur = organisateurService.getOrganisateurById(user.getId());
            if (organisateur != null) {
                request.setAttribute("organisateur", organisateur);
            }

            // Récupérer les statistiques de l'organisateur
            Map<String, Object> stats = organisateurService.getOrganisateurStats(user.getId());
            if (stats != null) {
                request.setAttribute("stats", stats);
                logger.debug("Statistiques chargées pour l'organisateur: {} événements",
                            stats.get("totalEvents"));
            }

            // Récupérer les événements récents
            List<Evenement> recentEvents = evenementService.getEvenementsByOrganisateur(user.getId());
            if (recentEvents != null) {
                // Limiter aux 5 derniers
                if (recentEvents.size() > 5) {
                    recentEvents = recentEvents.subList(0, 5);
                }
                request.setAttribute("recentEvents", recentEvents);
            }

            // Forward vers la JSP du dashboard
            ServletUtil.forward(request, response, "/WEB-INF/views/organizer/dashboard.jsp");

        } catch (Exception e) {
            logger.error("Erreur lors du chargement du dashboard organisateur", e);
            ServletUtil.setErrorMessage(request.getSession(),
                "Erreur lors du chargement du dashboard: " + e.getMessage());
            ServletUtil.redirect(response, request.getContextPath() + "/");
        }
    }
}
