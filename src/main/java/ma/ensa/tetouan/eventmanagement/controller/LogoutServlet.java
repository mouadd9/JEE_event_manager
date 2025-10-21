package ma.ensa.tetouan.eventmanagement.controller;

import ma.ensa.tetouan.eventmanagement.model.User;
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

/**
 * Servlet pour gérer la déconnexion des utilisateurs.
 *
 * @author ENSA Tétouan
 */
@WebServlet(name = "LogoutServlet", urlPatterns = "/logout")
public class LogoutServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(LogoutServlet.class);

    @Override
    public void init() throws ServletException {
        super.init();
        logger.info("LogoutServlet initialisé");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session != null) {
            User user = ServletUtil.getLoggedUser(request);
            String userName = (user != null) ? user.getNom() : "Utilisateur";

            logger.info("Déconnexion de l'utilisateur: {} (ID: {})",
                       userName, user != null ? user.getId() : "inconnu");

            // Invalider la session
            session.invalidate();

            logger.debug("Session invalidée pour: {}", userName);
        }

        // Créer une nouvelle session pour le message de succès
        HttpSession newSession = request.getSession(true);
        ServletUtil.setSuccessMessage(newSession, "Vous avez été déconnecté avec succès. À bientôt !");

        // Rediriger vers la page de login
        ServletUtil.redirect(response, request.getContextPath() + "/login");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Supporter aussi POST pour la déconnexion
        doGet(request, response);
    }
}
