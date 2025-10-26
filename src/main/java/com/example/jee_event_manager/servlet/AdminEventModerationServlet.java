package com.example.jee_event_manager.servlet;

import com.example.jee_event_manager.model.Evenement;
import com.example.jee_event_manager.model.StatutEvenement;
import com.example.jee_event_manager.service.AdminService;
import com.google.gson.Gson;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AdminEventModerationServlet", urlPatterns = {"/admin/events"})
public class AdminEventModerationServlet extends HttpServlet {
    
    @Inject
    private AdminService adminService;
    
    private final Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if user is admin
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            // Get filter parameter
            String statutParam = request.getParameter("statut");
            StatutEvenement statut = null;
            
            if (statutParam != null && !statutParam.isEmpty()) {
                try {
                    statut = StatutEvenement.valueOf(statutParam);
                } catch (IllegalArgumentException e) {
                    // Invalid status, ignore
                }
            }
            
            // Get events
            List<Evenement> events = adminService.getAllEvents(statut);
            
            request.setAttribute("events", events);
            request.setAttribute("selectedStatut", statutParam);
            
            request.getRequestDispatcher("/WEB-INF/views/admin/events.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors du chargement des événements");
            request.getRequestDispatcher("/WEB-INF/views/admin/events.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Set request encoding BEFORE reading any parameters
        request.setCharacterEncoding("UTF-8");
        
        // Check if user is admin
        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String action = request.getParameter("action");
        String eventIdStr = request.getParameter("eventId");
        
        // Debug logging
        System.out.println("=== AdminEventModerationServlet POST ===");
        System.out.println("Action: " + action);
        System.out.println("EventId: " + eventIdStr);
        
        Map<String, Object> result = new HashMap<>();
        
        if (action == null || eventIdStr == null) {
            result.put("success", false);
            result.put("message", "Paramètres manquants (action=" + action + ", eventId=" + eventIdStr + ")");
            System.out.println("ERROR: Missing parameters!");
            response.getWriter().write(gson.toJson(result));
            return;
        }
        
        try {
            Long eventId = Long.parseLong(eventIdStr);
            
            switch (action) {
                case "hide":
                    adminService.hideEvent(eventId);
                    result.put("success", true);
                    result.put("message", "Événement masqué avec succès");
                    break;
                    
                case "unhide":
                case "publish":
                    adminService.unhideEvent(eventId);
                    result.put("success", true);
                    result.put("message", "Événement publié avec succès");
                    break;
                    
                case "delete":
                    adminService.deleteEvent(eventId);
                    result.put("success", true);
                    result.put("message", "Événement supprimé avec succès");
                    break;
                    
                default:
                    result.put("success", false);
                    result.put("message", "Action non reconnue");
            }
            
            response.getWriter().write(gson.toJson(result));
            
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Erreur: " + e.getMessage());
            response.getWriter().write(gson.toJson(result));
        }
    }
    
    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        
        String userType = (String) session.getAttribute("userType");
        return "ADMIN".equals(userType);
    }
}
