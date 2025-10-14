package com.projet.jee.servlet;

import com.projet.jee.model.RoleUpgradeRequest;
import com.projet.jee.service.AuthenticationService;
import com.projet.jee.service.UtilisateurService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet for handling role upgrade requests (Participant -> Organisateur).
 */
public class RoleUpgradeServlet extends HttpServlet {
    private UtilisateurService utilisateurService;
    private AuthenticationService authService;

    @Override
    public void init() throws ServletException {
        utilisateurService = new UtilisateurService();
        authService = new AuthenticationService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check if user is participant
        if (!authService.isParticipant(request.getSession())) {
            response.sendRedirect(request.getContextPath() + "/profile");
            return;
        }

        request.getRequestDispatcher("/jsp/request-organizer.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long userId = authService.getCurrentUserId(request.getSession());
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String nomOrganisation = request.getParameter("nomOrganisation");
        String description = request.getParameter("description");
        String siteWeb = request.getParameter("siteWeb");
        String numSiret = request.getParameter("numSiret");

        try {
            RoleUpgradeRequest upgradeRequest = utilisateurService.requestRoleUpgrade(
                    userId, nomOrganisation, description, siteWeb, numSiret
            );

            request.setAttribute("success", "Votre demande a été soumise avec succès. " +
                    "Un administrateur l'examinera prochainement.");
            request.getRequestDispatcher("/jsp/request-organizer.jsp").forward(request, response);

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("nomOrganisation", nomOrganisation);
            request.setAttribute("description", description);
            request.setAttribute("siteWeb", siteWeb);
            request.setAttribute("numSiret", numSiret);
            request.getRequestDispatcher("/jsp/request-organizer.jsp").forward(request, response);
        }
    }
}
