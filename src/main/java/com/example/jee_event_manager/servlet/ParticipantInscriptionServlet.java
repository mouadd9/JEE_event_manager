package com.example.jee_event_manager.servlet;

import com.example.jee_event_manager.dto.InscriptionDTO;
import com.example.jee_event_manager.dto.InscriptionRequest;
import com.example.jee_event_manager.model.Inscription;
import com.example.jee_event_manager.model.StatutInscription;
import com.example.jee_event_manager.service.InscriptionService;
import com.example.jee_event_manager.util.DTOMapper;
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
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servlet pour gérer les inscriptions des participants
 */
@WebServlet(name = "ParticipantInscriptionServlet", urlPatterns = {"/participant/inscriptions", "/participant/inscriptions/*"})
public class ParticipantInscriptionServlet extends HttpServlet {
    
    @Inject
    private InscriptionService inscriptionService;
    
    @Inject
    private Validator validator;
    
    private final Gson gson = GsonUtil.getGson();
    
    /**
     * GET /participant/inscriptions - Liste des inscriptions du participant
     * Paramètres optionnels: statut (EN_ATTENTE, ACCEPTEE, REFUSEE, ANNULEE)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // Récupérer le participant de la session
            Long participantId = getParticipantIdFromSession(request);
            if (participantId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(gson.toJson(JsonResponse.error("Vous devez être connecté")));
                return;
            }
            
            // Récupérer le paramètre statut (optionnel)
            String statutParam = request.getParameter("statut");
            
            List<Inscription> inscriptions;
            if (statutParam != null && !statutParam.isEmpty()) {
                try {
                    StatutInscription statut = StatutInscription.valueOf(statutParam.toUpperCase());
                    inscriptions = inscriptionService.getInscriptionsParticipantByStatut(participantId, statut);
                } catch (IllegalArgumentException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(gson.toJson(JsonResponse.error("Statut invalide")));
                    return;
                }
            } else {
                inscriptions = inscriptionService.getInscriptionsParticipant(participantId);
            }
            
            // Convertir en DTOs
            List<InscriptionDTO> dtos = inscriptions.stream()
                .map(DTOMapper::toInscriptionDTO)
                .collect(Collectors.toList());
            
            JsonResponse jsonResponse = JsonResponse.success("Inscriptions récupérées avec succès", dtos);
            jsonResponse.addMetadata("total", dtos.size());
            
            response.getWriter().write(gson.toJson(jsonResponse));
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(JsonResponse.error("Erreur serveur: " + e.getMessage())));
        }
    }
    
    /**
     * POST /participant/inscriptions - Créer une nouvelle inscription
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // Récupérer le participant de la session
            Long participantId = getParticipantIdFromSession(request);
            if (participantId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(gson.toJson(JsonResponse.error("Vous devez être connecté")));
                return;
            }
            
            // Lire le corps de la requête
            String requestBody = request.getReader().lines().collect(Collectors.joining());
            InscriptionRequest inscriptionRequest = gson.fromJson(requestBody, InscriptionRequest.class);
            
            // Valider la requête
            Set<ConstraintViolation<InscriptionRequest>> violations = validator.validate(inscriptionRequest);
            if (!violations.isEmpty()) {
                String errors = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(JsonResponse.error("Validation échouée: " + errors)));
                return;
            }
            
            // Créer l'inscription
            Inscription inscription = inscriptionService.inscrireParticipant(
                participantId,
                inscriptionRequest.getEvenementId(),
                inscriptionRequest.getTypeBillet(),
                inscriptionRequest.getQuantite()
            );
            
            // Convertir en DTO
            InscriptionDTO dto = DTOMapper.toInscriptionDTO(inscription);
            
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(gson.toJson(JsonResponse.success("Inscription créée avec succès", dto)));
            
        } catch (IllegalStateException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write(gson.toJson(JsonResponse.error(e.getMessage())));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(JsonResponse.error(e.getMessage())));
        } catch (Exception e) {
            e.printStackTrace(); // Log complet de l'erreur
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(JsonResponse.error("Erreur serveur: " + e.getMessage())));
        }
    }
    
    /**
     * DELETE /participant/inscriptions/{id} - Annuler une inscription
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // Récupérer le participant de la session
            Long participantId = getParticipantIdFromSession(request);
            if (participantId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(gson.toJson(JsonResponse.error("Vous devez être connecté")));
                return;
            }
            
            // Extraire l'ID de l'inscription du path
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(JsonResponse.error("ID d'inscription manquant")));
                return;
            }
            
            Long inscriptionId = Long.parseLong(pathInfo.substring(1));
            
            // Annuler l'inscription
            inscriptionService.annulerInscription(inscriptionId, participantId);
            
            response.getWriter().write(gson.toJson(JsonResponse.success("Inscription annulée avec succès")));
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(JsonResponse.error("ID d'inscription invalide")));
        } catch (IllegalStateException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(gson.toJson(JsonResponse.error(e.getMessage())));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(gson.toJson(JsonResponse.error(e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(JsonResponse.error("Erreur serveur: " + e.getMessage())));
        }
    }
    
    /**
     * Récupérer l'ID du participant (hardcoded for testing)
     */
    private Long getParticipantIdFromSession(HttpServletRequest request) {
        // Hardcoded participant ID for testing
        return 2L;
        
        /* TODO: Restore session-based authentication
        HttpSession session = request.getSession(false);
        if (session == null) {
            // Créer une session pour les tests
            session = request.getSession(true);
            session.setAttribute("userId", 1L);
            session.setAttribute("userName", "Utilisateur Test");
            session.setAttribute("userEmail", "test@example.com");
            return 1L;
        }
        
        // Essayer d'abord avec "userId" (principal)
        Object userId = session.getAttribute("userId");
        if (userId instanceof Long) {
            return (Long) userId;
        }
        
        // Essayer avec "participantId" (fallback)
        Object participantId = session.getAttribute("participantId");
        if (participantId instanceof Long) {
            return (Long) participantId;
        }
        
        // Si aucun ID trouvé, créer une session de test
        session.setAttribute("userId", 1L);
        session.setAttribute("userName", "Utilisateur Test");
        session.setAttribute("userEmail", "test@example.com");
        return 1L;
        */
    }
}
