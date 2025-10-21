package ma.ensa.tetouan.eventmanagement.controller;

import ma.ensa.tetouan.eventmanagement.model.Organisateur;
import ma.ensa.tetouan.eventmanagement.model.Participant;
import ma.ensa.tetouan.eventmanagement.model.User;
import ma.ensa.tetouan.eventmanagement.service.EvenementService;
import ma.ensa.tetouan.eventmanagement.service.EvenementServiceImpl;
import ma.ensa.tetouan.eventmanagement.service.InscriptionService;
import ma.ensa.tetouan.eventmanagement.service.InscriptionServiceImpl;
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
import java.util.List;
import java.util.Map;

/**
 * Servlet pour gérer le profil utilisateur unifié.
 * Supporte les organisateurs, participants et administrateurs.
 *
 * @author ENSA Tétouan
 */
@WebServlet(name = "ProfileServlet", urlPatterns = "/profile")
public class ProfileServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ProfileServlet.class);
    private EvenementService evenementService;
    private InscriptionService inscriptionService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.evenementService = new EvenementServiceImpl();
        this.inscriptionService = new InscriptionServiceImpl();
        logger.info("ProfileServlet initialisé");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            logger.debug("Utilisateur non connecté, redirection vers login");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("currentUser");
        logger.debug("Affichage du profil pour l'utilisateur ID: {}", user.getId());

        try {
            // Récupérer les statistiques selon le type d'utilisateur
            Map<String, Object> stats = new HashMap<>();
            String viewPath;

            if (user instanceof Organisateur) {
                Organisateur organisateur = (Organisateur) user;
                
                // Statistiques pour organisateur
                List<ma.ensa.tetouan.eventmanagement.model.Evenement> events = 
                    evenementService.getEvenementsByOrganisateur(organisateur.getId());
                long totalEvents = events.size();
                
                // Calculer le total des participants
                long totalParticipants = events.stream()
                    .mapToLong(e -> e.getNombreInscriptions() != null ? e.getNombreInscriptions() : 0)
                    .sum();
                
                stats.put("totalEvents", totalEvents);
                stats.put("totalParticipants", totalParticipants);
                
                request.setAttribute("organisateur", organisateur);
                request.setAttribute("stats", stats);
                
                viewPath = "/WEB-INF/views/organizer/profile.jsp";
                logger.debug("Profil organisateur - Événements: {}, Participants: {}", totalEvents, totalParticipants);
                
            } else if (user instanceof Participant) {
                Participant participant = (Participant) user;
                
                // Statistiques pour participant
                List<ma.ensa.tetouan.eventmanagement.model.Inscription> inscriptions = 
                    inscriptionService.getInscriptionsByParticipant(participant.getId());
                long totalInscriptions = inscriptions.size();
                
                stats.put("totalInscriptions", totalInscriptions);
                
                request.setAttribute("participant", participant);
                request.setAttribute("stats", stats);
                
                viewPath = "/WEB-INF/views/participant/profile.jsp";
                logger.debug("Profil participant - Inscriptions: {}", totalInscriptions);
                
            } else {
                // Administrateur ou autre type
                request.setAttribute("user", user);
                viewPath = "/WEB-INF/views/profile.jsp";
                logger.debug("Profil utilisateur générique");
            }

            ServletUtil.forward(request, response, viewPath);

        } catch (Exception e) {
            logger.error("Erreur lors du chargement du profil", e);
            request.setAttribute("errorMessage", "Erreur lors du chargement du profil: " + e.getMessage());
            ServletUtil.forward(request, response, "/WEB-INF/views/error.jsp");
        }
    }
}
