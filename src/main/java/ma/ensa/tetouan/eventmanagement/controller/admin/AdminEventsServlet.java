package ma.ensa.tetouan.eventmanagement.controller.admin;

import ma.ensa.tetouan.eventmanagement.dao.EvenementDAO;
import ma.ensa.tetouan.eventmanagement.dao.EvenementDAOImpl;
import ma.ensa.tetouan.eventmanagement.model.Evenement;
import ma.ensa.tetouan.eventmanagement.model.StatutEvenement;
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
import java.util.List;

/**
 * Servlet for admin event moderation
 */
@WebServlet(name = "AdminEventsServlet", urlPatterns = "/admin/events")
public class AdminEventsServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminEventsServlet.class);
    private EvenementDAO evenementDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.evenementDAO = new EvenementDAOImpl();
        logger.info("AdminEventsServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String statusFilter = request.getParameter("status");

        logger.debug("Loading events list - status filter: {}", statusFilter);

        try {
            List<Evenement> events;

            if (statusFilter != null && !statusFilter.isEmpty()) {
                StatutEvenement statut = StatutEvenement.valueOf(statusFilter.toUpperCase());
                events = evenementDAO.findByStatut(statut);
            } else {
                // Get all events
                events = evenementDAO.findAll();
            }

            request.setAttribute("events", events);
            request.setAttribute("selectedStatus", statusFilter);

            ServletUtil.forward(request, response, "/WEB-INF/views/admin/events.jsp");

        } catch (Exception e) {
            logger.error("Error loading events list", e);
            request.setAttribute("errorMessage", "Erreur lors du chargement des événements");
            ServletUtil.forward(request, response, "/WEB-INF/views/error.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        Long eventId = ServletUtil.getLongParameter(request, "eventId", null);
        HttpSession session = request.getSession();

        if (eventId == null) {
            ServletUtil.setErrorMessage(session, "ID événement manquant");
            ServletUtil.redirect(response, request.getContextPath() + "/admin/events");
            return;
        }

        logger.info("Admin action: {} for event ID: {}", action, eventId);

        try {
            Evenement event = evenementDAO.findById(eventId).orElse(null);
            if (event == null) {
                throw new IllegalArgumentException("Événement non trouvé");
            }

            switch (action) {
                case "delete":
                    deleteEvent(event, session);
                    break;

                case "unpublish":
                    unpublishEvent(event, session);
                    break;

                case "publish":
                    publishEvent(event, session);
                    break;

                default:
                    ServletUtil.setErrorMessage(session, "Action inconnue");
            }

            ServletUtil.redirect(response, request.getContextPath() + "/admin/events");

        } catch (Exception e) {
            logger.error("Error performing admin action: " + action, e);
            ServletUtil.setErrorMessage(session, "Erreur lors de l'action: " + e.getMessage());
            ServletUtil.redirect(response, request.getContextPath() + "/admin/events");
        }
    }

    private void deleteEvent(Evenement event, HttpSession session) {
        String eventTitle = event.getTitre();
        evenementDAO.delete(event);

        ServletUtil.setWarningMessage(session,
            "Événement \"" + eventTitle + "\" supprimé");
        logger.info("Event deleted: {}", eventTitle);
    }

    private void unpublishEvent(Evenement event, HttpSession session) {
        event.setStatut(StatutEvenement.BROUILLON);
        evenementDAO.update(event);

        ServletUtil.setWarningMessage(session,
            "Événement \"" + event.getTitre() + "\" dépublié");
        logger.info("Event unpublished: {}", event.getTitre());
    }

    private void publishEvent(Evenement event, HttpSession session) {
        event.setStatut(StatutEvenement.PUBLIE);
        evenementDAO.update(event);

        ServletUtil.setSuccessMessage(session,
            "Événement \"" + event.getTitre() + "\" publié");
        logger.info("Event published: {}", event.getTitre());
    }
}
