package com.example.jee_event_manager.servlet;

import com.example.jee_event_manager.model.Admin;
import com.example.jee_event_manager.model.Utilisateur;
import com.example.jee_event_manager.service.UtilisateurService;
import com.example.jee_event_manager.util.JsonResponse;
import com.google.gson.Gson;
import com.example.jee_event_manager.util.GsonUtil;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet(name = "AdminProfilServlet", urlPatterns = {
    "/admin/profil",
    "/admin/mot-de-passe"
})
public class AdminProfilServlet extends HttpServlet {
    
    @Inject
    private UtilisateurService utilisateurService;
    
    private final Gson gson = GsonUtil.getGson();
    
    /**
     * GET /admin/profil - Afficher la page profil ou récupérer les informations (API)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if user is admin
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Si c'est une requête API (Accept: application/json), retourner JSON
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            handleApiGet(request, response);
        } else {
            // Sinon, afficher la page JSP
            request.getRequestDispatcher("/WEB-INF/views/admin/profil.jsp").forward(request, response);
        }
    }
    
    /**
     * Gérer les requêtes API GET
     */
    private void handleApiGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // Vérifier l'authentification
            Long adminId = getAdminIdFromSession(request);
            if (adminId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(gson.toJson(JsonResponse.error("Vous devez être connecté")));
                return;
            }
            
            // Récupérer l'admin
            Optional<Utilisateur> utilisateurOpt = utilisateurService.findById(adminId);
            Admin admin = null;
            if (utilisateurOpt.isPresent() && utilisateurOpt.get() instanceof Admin) {
                admin = (Admin) utilisateurOpt.get();
            }
            if (admin == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(JsonResponse.error("Administrateur introuvable")));
                return;
            }
            
            // Créer le DTO du profil (sans mot de passe)
            Map<String, Object> profil = new HashMap<>();
            profil.put("id", admin.getId());
            profil.put("nom", admin.getNom());
            profil.put("email", admin.getEmail());
            profil.put("userType", admin.getUserType());
            profil.put("role", admin.getRole());
            profil.put("permissions", admin.getPermissions());
            profil.put("createdAt", admin.getCreatedAt());
            profil.put("updatedAt", admin.getUpdatedAt());
            
            response.getWriter().write(gson.toJson(JsonResponse.success("Profil récupéré avec succès", profil)));
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(JsonResponse.error("Erreur serveur: " + e.getMessage())));
        }
    }
    
    /**
     * Vérifier si l'utilisateur est admin
     */
    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        
        String userType = (String) session.getAttribute("userType");
        return "ADMIN".equals(userType);
    }
    
    /**
     * PUT /admin/profil - Modifier les informations du profil
     * PUT /admin/mot-de-passe - Changer le mot de passe
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String uri = request.getRequestURI();
        
        if (uri.contains("/mot-de-passe")) {
            handleChangePassword(request, response);
        } else {
            handleUpdateProfile(request, response);
        }
    }
    
    /**
     * Gérer la modification du profil
     */
    private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        try {
            // Vérifier l'authentification
            Long adminId = getAdminIdFromSession(request);
            if (adminId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(gson.toJson(JsonResponse.error("Vous devez être connecté")));
                return;
            }
            
            // Lire le corps de la requête
            String requestBody = request.getReader().lines().collect(Collectors.joining());
            @SuppressWarnings("unchecked")
            Map<String, String> updates = gson.fromJson(requestBody, Map.class);
            
            // Récupérer l'admin
            Optional<Utilisateur> utilisateurOpt = utilisateurService.findById(adminId);
            Admin admin = null;
            if (utilisateurOpt.isPresent() && utilisateurOpt.get() instanceof Admin) {
                admin = (Admin) utilisateurOpt.get();
            }
            if (admin == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(JsonResponse.error("Administrateur introuvable")));
                return;
            }
            
            // Mettre à jour les champs autorisés
            boolean updated = false;
            
            if (updates.containsKey("nom") && updates.get("nom") != null && !updates.get("nom").trim().isEmpty()) {
                admin.setNom(updates.get("nom").trim());
                updated = true;
            }
            
            if (updates.containsKey("email") && updates.get("email") != null && !updates.get("email").trim().isEmpty()) {
                String newEmail = updates.get("email").trim();
                
                // Vérifier que l'email n'est pas déjà utilisé
                if (!newEmail.equals(admin.getEmail()) && utilisateurService.emailExists(newEmail)) {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    response.getWriter().write(gson.toJson(JsonResponse.error("Cet email est déjà utilisé")));
                    return;
                }
                
                admin.setEmail(newEmail);
                updated = true;
            }
            
            if (!updated) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(JsonResponse.error("Aucune modification à effectuer")));
                return;
            }
            
            // Sauvegarder les modifications
            utilisateurService.update(admin);
            
            // Mettre à jour la session
            HttpSession session = request.getSession();
            session.setAttribute("user", admin);
            session.setAttribute("userName", admin.getNom());
            session.setAttribute("userEmail", admin.getEmail());
            
            response.getWriter().write(gson.toJson(JsonResponse.success("Profil mis à jour avec succès", null)));
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(JsonResponse.error("Erreur lors de la mise à jour: " + e.getMessage())));
        }
    }
    
    /**
     * Gérer le changement de mot de passe
     */
    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        try {
            // Vérifier l'authentification
            Long adminId = getAdminIdFromSession(request);
            if (adminId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(gson.toJson(JsonResponse.error("Vous devez être connecté")));
                return;
            }
            
            // Lire le corps de la requête
            String requestBody = request.getReader().lines().collect(Collectors.joining());
            @SuppressWarnings("unchecked")
            Map<String, String> passwords = gson.fromJson(requestBody, Map.class);
            
            String currentPassword = passwords.get("currentPassword");
            String newPassword = passwords.get("newPassword");
            String confirmPassword = passwords.get("confirmPassword");
            
            // Validation
            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(JsonResponse.error("Mot de passe actuel requis")));
                return;
            }
            
            if (newPassword == null || newPassword.length() < 8) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(JsonResponse.error("Le nouveau mot de passe doit contenir au moins 8 caractères")));
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(JsonResponse.error("Les mots de passe ne correspondent pas")));
                return;
            }
            
            // Récupérer l'admin
            Optional<Utilisateur> utilisateurOpt = utilisateurService.findById(adminId);
            if (!utilisateurOpt.isPresent()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(JsonResponse.error("Administrateur introuvable")));
                return;
            }
            
            Utilisateur admin = utilisateurOpt.get();
            
            // Changer le mot de passe
            try {
                utilisateurService.changePassword(admin.getId(), currentPassword, newPassword);
                response.getWriter().write(gson.toJson(JsonResponse.success("Mot de passe modifié avec succès", null)));
            } catch (IllegalArgumentException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(JsonResponse.error(e.getMessage())));
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(JsonResponse.error("Erreur lors du changement de mot de passe: " + e.getMessage())));
        }
    }
    
    /**
     * Récupérer l'ID de l'admin depuis la session
     */
    private Long getAdminIdFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object adminId = session.getAttribute("adminId");
            if (adminId instanceof Long) {
                return (Long) adminId;
            } else if (adminId instanceof Integer) {
                return ((Integer) adminId).longValue();
            }
        }
        return null;
    }
}
