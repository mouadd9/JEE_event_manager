package com.example.jee_event_manager.servlet;

import com.example.jee_event_manager.dto.CommentaireDTO;
import com.example.jee_event_manager.dto.CommentaireRequest;
import com.example.jee_event_manager.model.Commentaire;
import com.example.jee_event_manager.service.CommentaireService;
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
 * Servlet pour gérer les commentaires sur les événements
 * URLs: /api/commentaires?evenementId=X ou /api/commentaires/{id}
 */
@WebServlet(name = "CommentaireServlet", urlPatterns = {"/api/commentaires", "/api/commentaires/*"})
public class CommentaireServlet extends HttpServlet {
    
    @Inject
    private CommentaireService commentaireService;
    
    @Inject
    private Validator validator;
    
    private final Gson gson = GsonUtil.getGson();
    
    /**
     * GET /api/commentaires?evenementId={id} - Liste des commentaires d'un événement
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
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
            
            // Récupérer les commentaires
            List<Commentaire> commentaires = commentaireService.getCommentairesEvenement(evenementId);
            
            // Convertir en DTOs
            List<CommentaireDTO> dtos = commentaires.stream()
                .map(DTOMapper::toCommentaireDTO)
                .collect(Collectors.toList());
            
            JsonResponse jsonResponse = JsonResponse.success("Commentaires récupérés avec succès", dtos);
            jsonResponse.addMetadata("total", dtos.size());
            
            response.getWriter().write(gson.toJson(jsonResponse));
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(JsonResponse.error("Erreur serveur: " + e.getMessage())));
        }
    }
    
    /**
     * POST /api/commentaires?evenementId={id} - Ajouter un commentaire
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
                response.getWriter().write(gson.toJson(JsonResponse.error("Vous devez être connecté pour commenter")));
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
            CommentaireRequest commentaireRequest = gson.fromJson(requestBody, CommentaireRequest.class);
            
            // Forcer l'ID de l'événement depuis le paramètre
            if (commentaireRequest == null) {
                commentaireRequest = new CommentaireRequest();
            }
            commentaireRequest.setEvenementId(evenementId);
            
            // Valider la requête
            Set<ConstraintViolation<CommentaireRequest>> violations = validator.validate(commentaireRequest);
            if (!violations.isEmpty()) {
                String errors = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(JsonResponse.error("Validation échouée: " + errors)));
                return;
            }
            
            // Ajouter le commentaire
            Commentaire commentaire = commentaireService.ajouterCommentaire(
                participantId,
                evenementId,
                commentaireRequest.getTexte()
            );
            
            // Convertir en DTO
            CommentaireDTO dto = DTOMapper.toCommentaireDTO(commentaire);
            
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(gson.toJson(JsonResponse.success("Commentaire ajouté avec succès", dto)));
            
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
     * DELETE /commentaires/{id} - Supprimer un commentaire
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
            
            // Extraire l'ID du commentaire du path
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(JsonResponse.error("ID de commentaire manquant")));
                return;
            }
            
            Long commentaireId = Long.parseLong(pathInfo.substring(1));
            
            // Supprimer le commentaire
            commentaireService.supprimerCommentaire(commentaireId, participantId);
            
            response.getWriter().write(gson.toJson(JsonResponse.success("Commentaire supprimé avec succès")));
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(JsonResponse.error("ID de commentaire invalide")));
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
     * Format: /evenements/{id}/commentaires
     */
    private Long extractEvenementId(HttpServletRequest request) {
        String pathInfo = request.getRequestURI();
        String[] parts = pathInfo.split("/");
        
        // Chercher "evenements" suivi d'un nombre
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
     * Récupérer l'ID du participant (hardcoded for testing)
     */
    private Long getParticipantIdFromSession(HttpServletRequest request) {
        // Hardcoded participant ID for testing
        return 2L;
        
        /* TODO: Restore session-based authentication
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
        */
    }
}
