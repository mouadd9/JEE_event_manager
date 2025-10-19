package com.example.jee_event_manager.servlet;

import com.example.jee_event_manager.model.Participant;
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
import java.util.stream.Collectors;

@WebServlet(name = "ParticipantProfilServlet", urlPatterns = {
    "/participant/profil",
    "/participant/mot-de-passe"
})
public class ParticipantProfilServlet extends HttpServlet {
    
    @Inject
    private UtilisateurService utilisateurService;
    
    private final Gson gson = GsonUtil.getGson();
    
    /**
     * GET /participant/profil - Récupérer les informations du profil
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // Vérifier l'authentification
            Long participantId = getParticipantIdFromSession(request);
            if (participantId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(gson.toJson(JsonResponse.error("Vous devez être connecté")));
                return;
            }
            
            // Récupérer le participant
            Participant participant = (Participant) utilisateurService.findById(participantId);
            if (participant == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(JsonResponse.error("Participant introuvable")));
                return;
            }
            
            // Créer le DTO du profil (sans mot de passe)
            Map<String, Object> profil = new HashMap<>();
            profil.put("id", participant.getId());
            profil.put("nom", participant.getNom());
            profil.put("email", participant.getEmail());
            profil.put("userType", participant.getUserType());
            profil.put("createdAt", participant.getCreatedAt());
            profil.put("updatedAt", participant.getUpdatedAt());
            
            response.getWriter().write(gson.toJson(JsonResponse.success("Profil récupéré avec succès", profil)));
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(JsonResponse.error("Erreur serveur: " + e.getMessage())));
        }
    }
    
    /**
     * PUT /participant/profil - Modifier les informations du profil
     * PUT /participant/mot-de-passe - Changer le mot de passe
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
            Long participantId = getParticipantIdFromSession(request);
            if (participantId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(gson.toJson(JsonResponse.error("Vous devez être connecté")));
                return;
            }
            
            // Lire le corps de la requête
            String requestBody = request.getReader().lines().collect(Collectors.joining());
            @SuppressWarnings("unchecked")
            Map<String, String> updates = gson.fromJson(requestBody, Map.class);
            
            // Récupérer le participant
            Participant participant = (Participant) utilisateurService.findById(participantId);
            if (participant == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(JsonResponse.error("Participant introuvable")));
                return;
            }
            
            // Mettre à jour les champs autorisés
            boolean updated = false;
            
            if (updates.containsKey("nom") && updates.get("nom") != null && !updates.get("nom").trim().isEmpty()) {
                participant.setNom(updates.get("nom").trim());
                updated = true;
            }
            
            if (updates.containsKey("email") && updates.get("email") != null && !updates.get("email").trim().isEmpty()) {
                String newEmail = updates.get("email").trim();
                
                // Vérifier que l'email n'est pas déjà utilisé
                if (!newEmail.equals(participant.getEmail()) && utilisateurService.emailExists(newEmail)) {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    response.getWriter().write(gson.toJson(JsonResponse.error("Cet email est déjà utilisé")));
                    return;
                }
                
                participant.setEmail(newEmail);
                updated = true;
            }
            
            if (!updated) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(JsonResponse.error("Aucune modification fournie")));
                return;
            }
            
            // Sauvegarder les modifications
            utilisateurService.update(participant);
            
            // Retourner le profil mis à jour
            Map<String, Object> profil = new HashMap<>();
            profil.put("id", participant.getId());
            profil.put("nom", participant.getNom());
            profil.put("email", participant.getEmail());
            profil.put("updatedAt", participant.getUpdatedAt());
            
            response.getWriter().write(gson.toJson(JsonResponse.success("Profil mis à jour avec succès", profil)));
            
        } catch (Exception e) {
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
            Long participantId = getParticipantIdFromSession(request);
            if (participantId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(gson.toJson(JsonResponse.error("Vous devez être connecté")));
                return;
            }
            
            // Lire le corps de la requête
            String requestBody = request.getReader().lines().collect(Collectors.joining());
            @SuppressWarnings("unchecked")
            Map<String, String> passwordData = gson.fromJson(requestBody, Map.class);
            
            String ancienMotDePasse = passwordData.get("ancienMotDePasse");
            String nouveauMotDePasse = passwordData.get("nouveauMotDePasse");
            String confirmation = passwordData.get("confirmation");
            
            // Validations
            if (ancienMotDePasse == null || ancienMotDePasse.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(JsonResponse.error("L'ancien mot de passe est requis")));
                return;
            }
            
            if (nouveauMotDePasse == null || nouveauMotDePasse.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(JsonResponse.error("Le nouveau mot de passe est requis")));
                return;
            }
            
            if (nouveauMotDePasse.length() < 6) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(JsonResponse.error("Le mot de passe doit contenir au moins 6 caractères")));
                return;
            }
            
            if (!nouveauMotDePasse.equals(confirmation)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(JsonResponse.error("Les mots de passe ne correspondent pas")));
                return;
            }
            
            // Récupérer le participant
            Participant participant = (Participant) utilisateurService.findById(participantId);
            if (participant == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(JsonResponse.error("Participant introuvable")));
                return;
            }
            
            // Vérifier l'ancien mot de passe
            if (!utilisateurService.verifyPassword(ancienMotDePasse, participant.getMotDePasseHash())) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(gson.toJson(JsonResponse.error("Ancien mot de passe incorrect")));
                return;
            }
            
            // Changer le mot de passe
            utilisateurService.changePassword(participantId, nouveauMotDePasse);
            
            response.getWriter().write(gson.toJson(JsonResponse.success("Mot de passe modifié avec succès")));
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(JsonResponse.error("Erreur serveur: " + e.getMessage())));
        }
    }
    
    /**
     * Récupérer l'ID du participant depuis la session
     */
    private Long getParticipantIdFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        
        Object participantId = session.getAttribute("participantId");
        if (participantId instanceof Long) {
            return (Long) participantId;
        }
        
        Object userId = session.getAttribute("userId");
        if (userId instanceof Long) {
            return (Long) userId;
        }
        
        return null;
    }
}
