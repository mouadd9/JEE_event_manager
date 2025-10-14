package com.projet.jee.servlet;

import com.projet.jee.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet for organizer dashboard.
 * Handles event management for organizers.
 */
public class OrganizerDashboardServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(OrganizerDashboardServlet.class);
    private AuthenticationService authService;

    @Override
    public void init() throws ServletException {
        authService = new AuthenticationService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Verify organizer access
        if (!authService.isOrganizer(request.getSession(false))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès non autorisé");
            return;
        }

        // Forward to dashboard JSP
        request.getRequestDispatcher("/jsp/organizer/dashboard.jsp").forward(request, response);
    }
}
