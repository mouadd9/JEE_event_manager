package ma.ensa.tetouan.eventmanagement.controller.participant;

import ma.ensa.tetouan.eventmanagement.model.Categorie;
import ma.ensa.tetouan.eventmanagement.model.Evenement;
import ma.ensa.tetouan.eventmanagement.service.CategorieService;
import ma.ensa.tetouan.eventmanagement.service.CategorieServiceImpl;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet pour parcourir et rechercher les événements publiés.
 *
 * @author ENSA Tétouan
 */
@WebServlet(name = "BrowseEventsServlet", urlPatterns = "/events/browse")
public class BrowseEventsServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(BrowseEventsServlet.class);
    private EvenementService evenementService;
    private CategorieService categorieService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.evenementService = new EvenementServiceImpl();
        this.categorieService = new CategorieServiceImpl();
        logger.info("BrowseEventsServlet initialisé");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        logger.debug("Affichage de la page de recherche d'événements");

        // Récupérer les paramètres de recherche
        String keyword = ServletUtil.getStringParameter(request, "keyword", "");
        String categorieIdStr = request.getParameter("categorieId");
        String dateDebutStr = request.getParameter("dateDebut");
        String dateFinStr = request.getParameter("dateFin");
        int page = ServletUtil.getIntParameter(request, "page", 0);
        int pageSize = 9; // Grille 3x3

        try {
            // Parser les dates si fournies
            LocalDate dateDebut = null;
            LocalDate dateFin = null;

            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
                try {
                    dateDebut = LocalDate.parse(dateDebutStr);
                } catch (Exception e) {
                    logger.warn("Format de date début invalide: {}", dateDebutStr);
                }
            }

            if (dateFinStr != null && !dateFinStr.isEmpty()) {
                try {
                    dateFin = LocalDate.parse(dateFinStr);
                } catch (Exception e) {
                    logger.warn("Format de date fin invalide: {}", dateFinStr);
                }
            }

            // Parser l'ID de catégorie si fourni
            Long categorieId = null;
            if (categorieIdStr != null && !categorieIdStr.isEmpty()) {
                try {
                    categorieId = Long.parseLong(categorieIdStr);
                } catch (NumberFormatException e) {
                    logger.warn("ID de catégorie invalide: {}", categorieIdStr);
                }
            }

            // Rechercher les événements
            List<Evenement> allEvents;

            if ((keyword != null && !keyword.trim().isEmpty()) ||
                categorieId != null ||
                dateDebut != null ||
                dateFin != null) {

                // Recherche avec critères
                allEvents = evenementService.searchEvenements(
                    keyword.trim().isEmpty() ? null : keyword,
                    dateDebut,
                    dateFin,
                    categorieId
                );
                logger.debug("Recherche avec critères: {} événements trouvés", allEvents.size());

            } else {
                // Récupérer tous les événements publiés
                allEvents = evenementService.getAllPublishedEvents(0, 1000); // Large limit
                logger.debug("Chargement de tous les événements publiés: {}", allEvents.size());
            }

            // Appliquer la pagination
            int totalEvents = allEvents.size();
            int totalPages = (int) Math.ceil((double) totalEvents / pageSize);

            // Vérifier que la page demandée est valide
            if (page < 0) page = 0;
            if (page >= totalPages && totalPages > 0) page = totalPages - 1;

            int fromIndex = page * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, totalEvents);

            List<Evenement> pageEvents = (fromIndex < totalEvents) ?
                allEvents.subList(fromIndex, toIndex) : new ArrayList<>();

            // Charger toutes les catégories pour le filtre
            List<Categorie> categories = categorieService.getAllCategories();

            // Définir les attributs de la requête
            request.setAttribute("events", pageEvents);
            request.setAttribute("categories", categories);
            request.setAttribute("totalEvents", totalEvents);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("currentPage", page);
            request.setAttribute("keyword", keyword);
            request.setAttribute("selectedCategorieId", categorieId);
            request.setAttribute("dateDebut", dateDebutStr);
            request.setAttribute("dateFin", dateFinStr);

            logger.debug("Page {}/{} chargée avec {} événements",
                        page + 1, totalPages, pageEvents.size());

            // Forward vers la JSP
            ServletUtil.forward(request, response, "/WEB-INF/views/participant/browse-events.jsp");

        } catch (Exception e) {
            logger.error("Erreur lors de la recherche d'événements", e);
            ServletUtil.setErrorMessage(request.getSession(),
                "Erreur lors de la recherche d'événements: " + e.getMessage());
            ServletUtil.redirect(response, request.getContextPath() + "/");
        }
    }
}
