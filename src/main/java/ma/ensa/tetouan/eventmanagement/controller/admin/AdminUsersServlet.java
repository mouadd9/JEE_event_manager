package ma.ensa.tetouan.eventmanagement.controller.admin;

import ma.ensa.tetouan.eventmanagement.dao.OrganisateurDAO;
import ma.ensa.tetouan.eventmanagement.dao.OrganisateurDAOImpl;
import ma.ensa.tetouan.eventmanagement.dao.UserDAO;
import ma.ensa.tetouan.eventmanagement.dao.UserDAOImpl;
import ma.ensa.tetouan.eventmanagement.model.Organisateur;
import ma.ensa.tetouan.eventmanagement.model.StatutUtilisateur;
import ma.ensa.tetouan.eventmanagement.model.User;
import ma.ensa.tetouan.eventmanagement.service.EmailService;
import ma.ensa.tetouan.eventmanagement.service.EmailServiceImpl;
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
 * Servlet for admin user management
 */
@WebServlet(name = "AdminUsersServlet", urlPatterns = "/admin/users")
public class AdminUsersServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminUsersServlet.class);
    private UserDAO userDAO;
    private OrganisateurDAO organisateurDAO;
    private EmailService emailService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userDAO = new UserDAOImpl();
        this.organisateurDAO = new OrganisateurDAOImpl();
        this.emailService = new EmailServiceImpl();
        logger.info("AdminUsersServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userType = request.getParameter("type");
        String status = request.getParameter("status");

        logger.debug("Loading users list - type: {}, status: {}", userType, status);

        try {
            List<User> users;

            // Filter by status
            if ("pending".equals(status)) {
                users = (List<User>) (List<?>) organisateurDAO.findPendingApproval();
            } else {
                // Get all users
                users = userDAO.findAll();
            }

            request.setAttribute("users", users);
            request.setAttribute("selectedType", userType);
            request.setAttribute("selectedStatus", status);

            ServletUtil.forward(request, response, "/WEB-INF/views/admin/users.jsp");

        } catch (Exception e) {
            logger.error("Error loading users list", e);
            request.setAttribute("errorMessage", "Erreur lors du chargement des utilisateurs");
            ServletUtil.forward(request, response, "/WEB-INF/views/error.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        Long userId = ServletUtil.getLongParameter(request, "userId", null);
        HttpSession session = request.getSession();

        if (userId == null) {
            ServletUtil.setErrorMessage(session, "ID utilisateur manquant");
            ServletUtil.redirect(response, request.getContextPath() + "/admin/users");
            return;
        }

        logger.info("Admin action: {} for user ID: {}", action, userId);

        try {
            switch (action) {
                case "approve":
                    approveOrganizer(userId, session);
                    break;

                case "reject":
                    rejectOrganizer(userId, session);
                    break;

                case "suspend":
                    suspendUser(userId, session);
                    break;

                case "activate":
                    activateUser(userId, session);
                    break;

                default:
                    ServletUtil.setErrorMessage(session, "Action inconnue");
            }

            ServletUtil.redirect(response, request.getContextPath() + "/admin/users");

        } catch (Exception e) {
            logger.error("Error performing admin action: " + action, e);
            ServletUtil.setErrorMessage(session, "Erreur lors de l'action: " + e.getMessage());
            ServletUtil.redirect(response, request.getContextPath() + "/admin/users");
        }
    }

    private void approveOrganizer(Long userId, HttpSession session) {
        Organisateur organisateur = organisateurDAO.findById(userId).orElse(null);
        if (organisateur == null) {
            throw new IllegalArgumentException("Organisateur non trouvé");
        }

        // Approve the organizer
        organisateurDAO.approveOrganisateur(userId);

        // TODO: Send approval email notification
        logger.info("TODO: Send approval email to: {}", organisateur.getEmail());

        ServletUtil.setSuccessMessage(session,
            "Organisateur " + organisateur.getNom() + " approuvé avec succès. Il peut maintenant se connecter.");
        logger.info("Organizer approved: {} ({})", organisateur.getNom(), organisateur.getEmail());
    }

    private void rejectOrganizer(Long userId, HttpSession session) {
        Organisateur organisateur = organisateurDAO.findById(userId).orElse(null);
        if (organisateur == null) {
            throw new IllegalArgumentException("Organisateur non trouvé");
        }

        String email = organisateur.getEmail();
        String nom = organisateur.getNom();

        // Reject (delete) the organizer
        organisateurDAO.rejectOrganisateur(userId);

        // TODO: Send rejection email notification
        logger.info("TODO: Send rejection email to: {}", email);

        ServletUtil.setWarningMessage(session,
            "Organisateur " + nom + " rejeté et compte supprimé");
        logger.info("Organizer rejected: {} ({})", nom, email);
    }

    private void suspendUser(Long userId, HttpSession session) {
        User user = userDAO.findById(userId).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("Utilisateur non trouvé");
        }

        userDAO.changeStatut(userId, StatutUtilisateur.SUSPENDU);

        ServletUtil.setWarningMessage(session,
            "Utilisateur " + user.getNom() + " suspendu");
        logger.info("User suspended: {} ({})", user.getNom(), user.getEmail());
    }

    private void activateUser(Long userId, HttpSession session) {
        User user = userDAO.findById(userId).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("Utilisateur non trouvé");
        }

        userDAO.changeStatut(userId, StatutUtilisateur.ACTIF);

        ServletUtil.setSuccessMessage(session,
            "Utilisateur " + user.getNom() + " activé");
        logger.info("User activated: {} ({})", user.getNom(), user.getEmail());
    }
}
