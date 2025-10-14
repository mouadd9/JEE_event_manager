package com.projet.jee.servlet;

import com.projet.jee.model.RoleUpgradeRequest;
import com.projet.jee.model.Utilisateur;
import com.projet.jee.service.AuthenticationService;
import com.projet.jee.service.UtilisateurService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servlet for admin dashboard.
 * Handles role upgrade requests management.
 */
public class AdminDashboardServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AdminDashboardServlet.class);
    private AuthenticationService authService;
    private UtilisateurService utilisateurService;

    @Override
    public void init() throws ServletException {
        authService = new AuthenticationService();
        utilisateurService = new UtilisateurService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Verify admin access
        if (!authService.isAdmin(request.getSession(false))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès non autorisé");
            return;
        }

        // Get pending role upgrade requests
        List<RoleUpgradeRequest> pendingRequests = utilisateurService.getPendingRequests();
        request.setAttribute("pendingRequests", pendingRequests);

        // Create a map of participant IDs to emails
        Map<Long, String> participantEmails = new HashMap<>();
        for (RoleUpgradeRequest req : pendingRequests) {
            Optional<Utilisateur> user = utilisateurService.getUserById(req.getParticipantId());
            if (user.isPresent()) {
                participantEmails.put(req.getParticipantId(), user.get().getEmail());
            }
        }
        request.setAttribute("participantEmails", participantEmails);

        // Forward to dashboard JSP
        request.getRequestDispatcher("/jsp/admin/dashboard-simple.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Verify admin access
        if (!authService.isAdmin(request.getSession(false))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès non autorisé");
            return;
        }

        String action = request.getParameter("action");
        String requestIdStr = request.getParameter("requestId");
        String comment = request.getParameter("comment");

        if (requestIdStr == null || requestIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        try {
            Long requestId = Long.parseLong(requestIdStr);
            Long adminId = authService.getCurrentUserId(request.getSession());

            if ("approve".equals(action)) {
                utilisateurService.approveRoleUpgrade(requestId, adminId, comment);
                request.getSession().setAttribute("successMessage", "Demande approuvée avec succès");
            } else if ("reject".equals(action)) {
                if (comment == null || comment.trim().isEmpty()) {
                    request.getSession().setAttribute("errorMessage", "Un commentaire est requis pour refuser une demande");
                } else {
                    utilisateurService.rejectRoleUpgrade(requestId, adminId, comment);
                    request.getSession().setAttribute("successMessage", "Demande refusée");
                }
            }

        } catch (NumberFormatException e) {
            logger.error("Invalid request ID format", e);
            request.getSession().setAttribute("errorMessage", "ID de demande invalide");
        } catch (Exception e) {
            logger.error("Erreur lors du traitement de la demande", e);
            request.getSession().setAttribute("errorMessage", e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
    }
}
