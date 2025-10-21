package ma.ensa.tetouan.eventmanagement.controller.admin;

import ma.ensa.tetouan.eventmanagement.dao.EvenementDAO;
import ma.ensa.tetouan.eventmanagement.dao.EvenementDAOImpl;
import ma.ensa.tetouan.eventmanagement.dao.OrganisateurDAO;
import ma.ensa.tetouan.eventmanagement.dao.OrganisateurDAOImpl;
import ma.ensa.tetouan.eventmanagement.dao.UserDAO;
import ma.ensa.tetouan.eventmanagement.dao.UserDAOImpl;
import ma.ensa.tetouan.eventmanagement.model.Evenement;
import ma.ensa.tetouan.eventmanagement.model.Organisateur;
import ma.ensa.tetouan.eventmanagement.model.StatutEvenement;
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
 * Servlet for admin dashboard showing platform statistics
 */
@WebServlet(name = "AdminDashboardServlet", urlPatterns = "/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminDashboardServlet.class);
    private UserDAO userDAO;
    private OrganisateurDAO organisateurDAO;
    private EvenementDAO evenementDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userDAO = new UserDAOImpl();
        this.organisateurDAO = new OrganisateurDAOImpl();
        this.evenementDAO = new EvenementDAOImpl();
        logger.info("AdminDashboardServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        logger.debug("Loading admin dashboard");

        try {
            // Total users
            long totalUsers = userDAO.count();
            request.setAttribute("totalUsers", totalUsers);

            // Pending organizers (not approved)
            List<Organisateur> pendingOrganizers = organisateurDAO.findPendingApproval();
            request.setAttribute("pendingOrganizersCount", pendingOrganizers.size());
            request.setAttribute("pendingOrganizers", pendingOrganizers);

            // Total events by status
            long totalEvents = evenementDAO.count();
            long publishedEvents = evenementDAO.countByStatut(StatutEvenement.PUBLIE);
            long draftEvents = evenementDAO.countByStatut(StatutEvenement.BROUILLON);
            
            request.setAttribute("totalEvents", totalEvents);
            request.setAttribute("publishedEvents", publishedEvents);
            request.setAttribute("draftEvents", draftEvents);

            // Recent events (last 5)
            List<Evenement> recentEvents = evenementDAO.findAll(0, 5);
            request.setAttribute("recentEvents", recentEvents);

            logger.debug("Admin dashboard loaded: {} users, {} pending organizers, {} events",
                    totalUsers, pendingOrganizers.size(), totalEvents);

            ServletUtil.forward(request, response, "/WEB-INF/views/admin/dashboard.jsp");

        } catch (Exception e) {
            logger.error("Error loading admin dashboard", e);
            request.setAttribute("errorMessage", "Erreur lors du chargement du tableau de bord");
            ServletUtil.forward(request, response, "/WEB-INF/views/error.jsp");
        }
    }
}
