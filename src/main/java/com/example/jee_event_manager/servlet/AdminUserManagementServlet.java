package com.example.jee_event_manager.servlet;

import com.example.jee_event_manager.model.UserType;
import com.example.jee_event_manager.model.Utilisateur;
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

@WebServlet(name = "AdminUserManagementServlet", urlPatterns = {"/admin/users"})
public class AdminUserManagementServlet extends HttpServlet {
    
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
            // Get current admin's user ID from session
            HttpSession session = request.getSession();
            Long currentUserId = (Long) session.getAttribute("userId");
            
            // Get filter parameters
            String userTypeParam = request.getParameter("userType");
            String verifiedParam = request.getParameter("verified");
            String suspendedParam = request.getParameter("suspended");
            
            UserType userType = userTypeParam != null && !userTypeParam.isEmpty() 
                ? UserType.valueOf(userTypeParam) : null;
            Boolean isVerified = verifiedParam != null && !verifiedParam.isEmpty() 
                ? Boolean.parseBoolean(verifiedParam) : null;
            Boolean isSuspended = suspendedParam != null && !suspendedParam.isEmpty() 
                ? Boolean.parseBoolean(suspendedParam) : null;
            
            // Get users and filter out current admin
            List<Utilisateur> users = adminService.getAllUsers(userType, isVerified, isSuspended);
            if (currentUserId != null) {
                users = users.stream()
                    .filter(u -> !u.getId().equals(currentUserId))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            List<Utilisateur> pendingOrganisateurs = adminService.getPendingOrganisateurs();
            // Also filter current admin from pending list
            if (currentUserId != null) {
                pendingOrganisateurs = pendingOrganisateurs.stream()
                    .filter(u -> !u.getId().equals(currentUserId))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            request.setAttribute("users", users);
            request.setAttribute("pendingOrganisateurs", pendingOrganisateurs);
            request.setAttribute("selectedUserType", userTypeParam);
            request.setAttribute("selectedVerified", verifiedParam);
            request.setAttribute("selectedSuspended", suspendedParam);
            
            request.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors du chargement des utilisateurs");
            request.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(request, response);
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
        String userIdStr = request.getParameter("userId");
        
        // Debug logging
        System.out.println("=== AdminUserManagementServlet POST ===");
        System.out.println("Action: " + action);
        System.out.println("UserId: " + userIdStr);
        System.out.println("All parameters:");
        request.getParameterMap().forEach((key, values) -> 
            System.out.println("  " + key + " = " + String.join(", ", values))
        );
        
        Map<String, Object> result = new HashMap<>();
        
        if (action == null || userIdStr == null) {
            result.put("success", false);
            result.put("message", "Paramètres manquants (action=" + action + ", userId=" + userIdStr + ")");
            System.out.println("ERROR: Missing parameters!");
            response.getWriter().write(gson.toJson(result));
            return;
        }
        
        try {
            Long userId = Long.parseLong(userIdStr);
            
            // Prevent admin from performing actions on themselves
            HttpSession session = request.getSession();
            Long currentUserId = (Long) session.getAttribute("userId");
            if (currentUserId != null && currentUserId.equals(userId)) {
                result.put("success", false);
                result.put("message", "Vous ne pouvez pas effectuer cette action sur votre propre compte");
                response.getWriter().write(gson.toJson(result));
                return;
            }
            
            switch (action) {
                case "verify":
                    adminService.verifyOrganisateur(userId);
                    result.put("success", true);
                    result.put("message", "Organisateur approuvé avec succès");
                    break;
                    
                case "suspend":
                    String reason = request.getParameter("reason");
                    if (reason == null || reason.trim().isEmpty()) {
                        reason = "Violation des conditions d'utilisation";
                    }
                    adminService.suspendUser(userId, reason);
                    result.put("success", true);
                    result.put("message", "Utilisateur suspendu avec succès");
                    break;
                    
                case "activate":
                    adminService.activateUser(userId);
                    result.put("success", true);
                    result.put("message", "Utilisateur activé avec succès");
                    break;
                    
                case "delete":
                    adminService.deleteUser(userId);
                    result.put("success", true);
                    result.put("message", "Utilisateur supprimé avec succès");
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
