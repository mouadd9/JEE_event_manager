package ma.ensa.tetouan.eventmanagement.controller;

import ma.ensa.tetouan.eventmanagement.model.User;
import ma.ensa.tetouan.eventmanagement.service.UserService;
import ma.ensa.tetouan.eventmanagement.service.UserServiceImpl;
import ma.ensa.tetouan.eventmanagement.util.PasswordUtil;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet pour changer le mot de passe utilisateur.
 *
 * @author ENSA Tétouan
 */
@WebServlet(name = "ChangePasswordServlet", urlPatterns = "/profile/change-password")
public class ChangePasswordServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ChangePasswordServlet.class);
    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userService = new UserServiceImpl();
        logger.info("ChangePasswordServlet initialisé");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Afficher la page de changement de mot de passe
        ServletUtil.forward(request, response, "/WEB-INF/views/change-password.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("currentUser");
        logger.debug("Changement de mot de passe pour l'utilisateur ID: {}", user.getId());

        Map<String, String> errors = new HashMap<>();

        try {
            // Récupérer les paramètres
            String currentPassword = request.getParameter("currentPassword");
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");

            // Validation
            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                errors.put("currentPassword", "Le mot de passe actuel est obligatoire");
            }
            if (newPassword == null || newPassword.trim().isEmpty()) {
                errors.put("newPassword", "Le nouveau mot de passe est obligatoire");
            } else if (newPassword.length() < 6) {
                errors.put("newPassword", "Le mot de passe doit contenir au moins 6 caractères");
            }
            if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
                errors.put("confirmPassword", "La confirmation du mot de passe est obligatoire");
            } else if (!newPassword.equals(confirmPassword)) {
                errors.put("confirmPassword", "Les mots de passe ne correspondent pas");
            }

            if (!errors.isEmpty()) {
                request.setAttribute("errors", errors);
                ServletUtil.forward(request, response, "/WEB-INF/views/change-password.jsp");
                return;
            }

            // Vérifier le mot de passe actuel et changer le mot de passe
            userService.changePassword(user.getId(), currentPassword, newPassword);

            // Recharger l'utilisateur depuis la base de données
            User updatedUser = userService.getUserById(user.getId());
            
            // Mettre à jour la session
            session.setAttribute("currentUser", updatedUser);

            logger.info("Mot de passe changé avec succès pour l'utilisateur ID: {}", user.getId());
            ServletUtil.setSuccessMessage(session, "Mot de passe changé avec succès");
            response.sendRedirect(request.getContextPath() + "/profile");

        } catch (Exception e) {
            logger.error("Erreur lors du changement de mot de passe", e);
            request.setAttribute("errors", errors);
            request.setAttribute("errorMessage", "Erreur lors du changement de mot de passe: " + e.getMessage());
            ServletUtil.forward(request, response, "/WEB-INF/views/change-password.jsp");
        }
    }
}
