package com.example.jee_event_manager.servlet;

import com.example.jee_event_manager.dto.EvaluationDTO;
import com.example.jee_event_manager.dto.EvaluationRequest;
import com.example.jee_event_manager.model.Evaluation;
import com.example.jee_event_manager.service.EvaluationService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@WebServlet(name = "EvaluationServlet", urlPatterns = {
    "/api/evaluations",
    "/api/evaluations/*"
})
public class EvaluationServlet extends HttpServlet {
    
    @Inject
    private EvaluationService evaluationService;
    
    @Inject
    private Validator validator;
    
    private final Gson gson = GsonUtil.getGson();
    
    /**
     * GET /evenements/{id}/evaluations - Liste des évaluations
     * GET /evenements/{id}/moyenne - Note moyenne
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            String uri = request.getRequestURI();
            
            // Vérifier si c'est une requête pour la moyenne
            if (uri.contains("/moyenne")) {
                handleGetMoyenne(request, response);
            } else {
                handleGetEvaluations(request, response);
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(JsonResponse.error("Erreur serveur: " + e.getMessage())));
        }
    }
    
    /**
     * Gérer GET /api/evaluations?evenementId={id}
     */
    private void handleGetEvaluations(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        String evenementIdParam = request.getParameter("evenementId");
        if (evenementIdParam == null || evenementIdParam.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(JsonResponse.error("Paramètre evenementId manquant")));
            return;
        }
        
        Long evenementId;
        try {
            evenementId = Long.parseLong(evenementIdParam);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(JsonResponse.error("ID d'événement invalide")));
            return;
        }
        
        // Récupérer les évaluations
        List<Evaluation> evaluations = evaluationService.getEvaluationsEvenement(evenementId);
        
        // Convertir en DTOs
        List<EvaluationDTO> dtos = evaluations.stream()
            .map(DTOMapper::toEvaluationDTO)
            .collect(Collectors.toList());
        
        // Ajouter des métadonnées
        Double moyenne = evaluationService.getMoyenneEvenement(evenementId);
        Long count = evaluationService.countEvaluationsEvenement(evenementId);
        
        JsonResponse jsonResponse = JsonResponse.success("Évaluations récupérées avec succès", dtos);
        jsonResponse.addMetadata("total", count);
        jsonResponse.addMetadata("moyenne", moyenne);
        
        response.getWriter().write(gson.toJson(jsonResponse));
    }
    
    /**
     * Gérer GET /evenements/{id}/moyenne
     */
    private void handleGetMoyenne(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        Long evenementId = extractEvenementId(request);
        if (evenementId == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(JsonResponse.error("ID d'événement manquant ou invalide")));
            return;
        }
        
        Double moyenne = evaluationService.getMoyenneEvenement(evenementId);
        Long count = evaluationService.countEvaluationsEvenement(evenementId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("moyenne", moyenne);
        data.put("nombreEvaluations", count);
        data.put("moyenneFormatee", String.format("%.1f/5", moyenne));
        
        response.getWriter().write(gson.toJson(JsonResponse.success("Note moyenne récupérée", data)));
    }
    
    /**
     * POST /evenements/{id}/evaluations - Ajouter ou modifier une évaluation
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // Vérifier l'authentification
            Long participantId = getParticipantIdFromSession(request);
            if (participantId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(gson.toJson(JsonResponse.error("Vous devez être connecté pour évaluer")));
                return;
            }
            
            // Extraire l'ID de l'événement du paramètre
            String evenementIdParam = request.getParameter("evenementId");
            if (evenementIdParam == null || evenementIdParam.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(JsonResponse.error("Paramètre evenementId manquant")));
                return;
            }
            
            Long evenementId;
            try {
                evenementId = Long.parseLong(evenementIdParam);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(JsonResponse.error("ID d'événement invalide")));
                return;
            }
            
            // Lire le corps de la requête
            String requestBody = request.getReader().lines().collect(Collectors.joining());
            EvaluationRequest evaluationRequest = gson.fromJson(requestBody, EvaluationRequest.class);
            
            // Forcer l'ID de l'événement depuis le paramètre
            if (evaluationRequest == null) {
                evaluationRequest = new EvaluationRequest();
            }
            evaluationRequest.setEvenementId(evenementId);
            
            // Valider la requête
            Set<ConstraintViolation<EvaluationRequest>> violations = validator.validate(evaluationRequest);
            if (!violations.isEmpty()) {
                String errors = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(JsonResponse.error("Validation échouée: " + errors)));
                return;
            }
            
            // Ajouter ou modifier l'évaluation
            Evaluation evaluation = evaluationService.ajouterOuModifierEvaluation(
                participantId,
                evenementId,
                evaluationRequest.getNote(),
                evaluationRequest.getTexte()
            );
            
            // Convertir en DTO
            EvaluationDTO dto = DTOMapper.toEvaluationDTO(evaluation);
            
            // Récupérer la nouvelle moyenne
            Double nouvelleMoyenne = evaluationService.getMoyenneEvenement(evenementId);
            
            JsonResponse jsonResponse = JsonResponse.success("Évaluation enregistrée avec succès", dto);
            jsonResponse.addMetadata("nouvelleMoyenne", nouvelleMoyenne);
            
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(gson.toJson(jsonResponse));
            
        } catch (IllegalStateException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(gson.toJson(JsonResponse.error(e.getMessage())));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(JsonResponse.error(e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(JsonResponse.error("Erreur serveur: " + e.getMessage())));
        }
    }
    
    /**
     * DELETE /evaluations/{id} - Supprimer une évaluation
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
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
            
            // Extraire l'ID de l'évaluation
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(JsonResponse.error("ID d'évaluation manquant")));
                return;
            }
            
            Long evaluationId = Long.parseLong(pathInfo.substring(1));
            
            // Supprimer l'évaluation
            evaluationService.supprimerEvaluation(evaluationId, participantId);
            
            response.getWriter().write(gson.toJson(JsonResponse.success("Évaluation supprimée avec succès")));
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(JsonResponse.error("ID d'évaluation invalide")));
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
     * Extraire l'ID de l'événement du path
     */
    private Long extractEvenementId(HttpServletRequest request) {
        String pathInfo = request.getRequestURI();
        String[] parts = pathInfo.split("/");
        
        for (int i = 0; i < parts.length - 1; i++) {
            if ("evenements".equals(parts[i])) {
                try {
                    return Long.parseLong(parts[i + 1]);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
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
