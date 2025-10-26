package com.example.jee_event_manager.servlet;

import com.example.jee_event_manager.model.Organisateur;
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

@WebServlet(name = "OrganizateurProfilServlet", urlPatterns = {
    "/organizer/profil",
    "/organizer/mot-de-passe"
})
public class OrganizateurProfilServlet extends HttpServlet {
    
    @Inject
    private UtilisateurService utilisateurService;
    
    private final Gson gson = GsonUtil.getGson();
    
    /**
     * GET /organizer/profil - Récupérer les informations du profil
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // Vérifier l'authentification
            Long organisateurId = getOrganisateurIdFromSession(request);
            if (organisateurId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(gson.toJson(JsonResponse.error("Vous devez être connecté")));
                return;
            }
            
            // Récupérer l'organisateur
            Optional<Utilisateur> utilisateurOpt = utilisateurService.findById(organisateurId);
            Organisateur organisateur = null;
            if (utilisateurOpt.isPresent() && utilisateurOpt.get() instanceof Organisateur) {
                organisateur = (Organisateur) utilisateurOpt.get();
            }
            if (organisateur == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(JsonResponse.error("Organisateur introuvable")));
                return;
            }
            
            // Créer le DTO du profil (sans mot de passe)
            Map<String, Object> profil = new HashMap<>();
            profil.put("id", organisateur.getId());
            profil.put("nom", organisateur.getNom());
            profil.put("email", organisateur.getEmail());
            profil.put("userType", organisateur.getUserType());
            profil.put("entreprise", organisateur.getEntreprise());
            profil.put("siret", organisateur.getSiret());
            profil.put("siteWeb", organisateur.getSiteWeb());
            profil.put("description", organisateur.getDescription());
            profil.put("createdAt", organisateur.getCreatedAt());
            profil.put("updatedAt", organisateur.getUpdatedAt());
            
            response.getWriter().write(gson.toJson(JsonResponse.success("Profil récupéré avec succès", profil)));
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(JsonResponse.error("Erreur serveur: " + e.getMessage())));
        }
    }
    
    /**
     * PUT /organizer/profil - Modifier les informations du profil
     * PUT /organizer/mot-de-passe - Changer le mot de passe
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
            Long organisateurId = getOrganisateurIdFromSession(request);
            if (organisateurId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(gson.toJson(JsonResponse.error("Vous devez être connecté")));
                return;
            }
            
            // Lire le corps de la requête
            String requestBody = request.getReader().lines().collect(Collectors.joining());
            @SuppressWarnings("unchecked")
            Map<String, String> updates = gson.fromJson(requestBody, Map.class);
            
            // Récupérer l'organisateur
            Optional<Utilisateur> utilisateurOpt = utilisateurService.findById(organisateurId);
            Organisateur organisateur = null;
            if (utilisateurOpt.isPresent() && utilisateurOpt.get() instanceof Organisateur) {
                organisateur = (Organisateur) utilisateurOpt.get();
            }
            if (organisateur == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(JsonResponse.error("Organisateur introuvable")));
                return;
            }
            
            // Mettre à jour les champs autorisés
            boolean updated = false;
            
            if (updates.containsKey("nom") && updates.get("nom") != null && !updates.get("nom").trim().isEmpty()) {
                organisateur.setNom(updates.get("nom").trim());
                updated = true;
            }
            
            if (updates.containsKey("email") && updates.get("email") != null && !updates.get("email").trim().isEmpty()) {
                String newEmail = updates.get("email").trim();
                
                // Vérifier que l'email n'est pas déjà utilisé
                if (!newEmail.equals(organisateur.getEmail()) && utilisateurService.emailExists(newEmail)) {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    response.getWriter().write(gson.toJson(JsonResponse.error("Cet email est déjà utilisé")));
                    return;
                }
                
                organisateur.setEmail(newEmail);
                updated = true;
            }
            
            // Champs spécifiques à l'organisateur
            if (updates.containsKey("entreprise")) {
                organisateur.setEntreprise(updates.get("entreprise"));
                updated = true;
            }
            
            if (updates.containsKey("siret")) {
                organisateur.setSiret(updates.get("siret"));
                updated = true;
            }
            
            if (updates.containsKey("siteWeb")) {
                organisateur.setSiteWeb(updates.get("siteWeb"));
                updated = true;
            }
            
            if (updates.containsKey("description")) {
                organisateur.setDescription(updates.get("description"));
                updated = true;
            }
            
            if (!updated) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(JsonResponse.error("Aucune modification fournie")));
                return;
            }
            
            // Sauvegarder les modifications
            utilisateurService.update(organisateur);
            
            response.getWriter().write(gson.toJson(JsonResponse.success("Profil mis à jour avec succès", null)));
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(JsonResponse.error("Erreur serveur: " + e.getMessage())));
        }
    }
    
    /**
     * Gérer le changement de mot de passe
     */
    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        try {
            // Vérifier l'authentification
            Long organisateurId = getOrganisateurIdFromSession(request);
            if (organisateurId == null) {
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
            
            // Valider les données
            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(JsonResponse.error("Le mot de passe actuel est requis")));
                return;
            }
            
            if (newPassword == null || newPassword.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(JsonResponse.error("Le nouveau mot de passe est requis")));
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(JsonResponse.error("Les mots de passe ne correspondent pas")));
                return;
            }
            
            if (newPassword.length() < 8) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(JsonResponse.error("Le nouveau mot de passe doit contenir au moins 8 caractères")));
                return;
            }
            
            // Récupérer l'organisateur
            Optional<Utilisateur> utilisateurOpt = utilisateurService.findById(organisateurId);
            if (!utilisateurOpt.isPresent()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(JsonResponse.error("Organisateur introuvable")));
                return;
            }
            
            Utilisateur organisateur = utilisateurOpt.get();
            
            // Changer le mot de passe
            try {
                utilisateurService.changePassword(organisateur.getId(), currentPassword, newPassword);
                response.getWriter().write(gson.toJson(JsonResponse.success("Mot de passe modifié avec succès", null)));
            } catch (IllegalArgumentException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(JsonResponse.error("Mot de passe actuel incorrect")));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(JsonResponse.error("Erreur serveur: " + e.getMessage())));
        }
    }
    
    /**
     * Récupérer l'ID de l'organisateur depuis la session
     */
    private Long getOrganisateurIdFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        
        return (Long) session.getAttribute("organisateurId");
    }
}
