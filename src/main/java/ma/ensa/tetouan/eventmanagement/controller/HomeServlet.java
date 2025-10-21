package ma.ensa.tetouan.eventmanagement.controller;

import ma.ensa.tetouan.eventmanagement.model.Evenement;
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

/**
 * Servlet pour la page d'accueil publique.
 *
 * @author ENSA Tétouan
 */
@WebServlet(name = "HomeServlet", urlPatterns = {"", "/", "/home"})
public class HomeServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(HomeServlet.class);
    private EvenementService evenementService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.evenementService = new EvenementServiceImpl();
        logger.info("HomeServlet initialisé");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        logger.debug("Affichage de la page d'accueil");

        try {
            // Récupérer les événements les plus populaires (limité à 6)
            List<Evenement> popularEvents = evenementService.getMostPopularEvents(6);
            request.setAttribute("popularEvents", popularEvents);

            // Récupérer les événements à venir (limité à 6)
            List<Evenement> upcomingEvents = evenementService.getUpcomingEvents(0, 6);
            request.setAttribute("upcomingEvents", upcomingEvents);

            // Compter le nombre total d'événements publiés
            long totalEvents = evenementService.getTotalPublishedEvents();
            request.setAttribute("totalEvents", totalEvents);

            logger.debug("Page d'accueil chargée: {} événements populaires, {} événements à venir",
                        popularEvents.size(), upcomingEvents.size());

            // Forward vers la page d'accueil
            ServletUtil.forward(request, response, "/WEB-INF/views/home.jsp");

        } catch (Exception e) {
            logger.error("Erreur lors du chargement de la page d'accueil", e);
            request.setAttribute("errorMessage",
                "Erreur lors du chargement de la page d'accueil: " + e.getMessage());
            ServletUtil.forward(request, response, "/WEB-INF/views/error.jsp");
        }
    }
}
