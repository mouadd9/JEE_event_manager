package com.example.jee_event_manager.servlet;

import com.example.jee_event_manager.dto.CommentaireDTO;
import com.example.jee_event_manager.dto.EvenementDetailDTO;
import com.example.jee_event_manager.dto.EvaluationDTO;
import com.example.jee_event_manager.model.Commentaire;
import com.example.jee_event_manager.model.Evenement;
import com.example.jee_event_manager.model.Evaluation;
import com.example.jee_event_manager.model.StatutInscription;
import com.example.jee_event_manager.service.CommentaireService;
import com.example.jee_event_manager.service.EvaluationService;
import com.example.jee_event_manager.service.EvenementService;
import com.example.jee_event_manager.service.InscriptionService;
import com.example.jee_event_manager.util.DTOMapper;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "EventDetailServlet", urlPatterns = {"/event-details"})
public class EventDetailServlet extends HttpServlet {
    
    @Inject
    private EvenementService evenementService;
    
    @Inject
    private InscriptionService inscriptionService;
    
    @Inject
    private EvaluationService evaluationService;
    
    @Inject
    private CommentaireService commentaireService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Récupérer l'ID de l'événement depuis les paramètres
        String eventIdStr = request.getParameter("id");
        
        if (eventIdStr == null || eventIdStr.trim().isEmpty()) {
            request.setAttribute("error", "ID d'événement manquant");
            request.getRequestDispatcher("/catalogue.jsp").forward(request, response);
            return;
        }
        
        try {
            Long eventId = Long.parseLong(eventIdStr);
            
            // Récupérer l'événement
            Evenement evenement = evenementService.findById(eventId)
                    .orElseThrow(() -> new RuntimeException("Événement introuvable"));
            
            // Vérifier que l'événement est publié (ou permettre tous les statuts pour l'admin/organisateur)
            // Pour l'instant, on permet tous les statuts pour la page de détails
            
            // Convertir en DTO
            EvenementDetailDTO dto = DTOMapper.toEvenementDetailDTO(evenement);
            
            // Enrichir avec les statistiques
            dto.setNoteMoyenne(evaluationService.getMoyenneEvenement(evenement.getId()));
            dto.setNombreEvaluations(evaluationService.countEvaluationsEvenement(evenement.getId()));
            dto.setNombreInscrits(inscriptionService.countInscritsEvenement(evenement.getId()));
            dto.setCapaciteDisponible(inscriptionService.getCapaciteDisponible(evenement.getId()));
            dto.setNombreCommentaires(commentaireService.countByEvenement(evenement.getId()));
            
            // Si un participant est connecté, ajouter son statut d'inscription
            Long participantId = getParticipantIdFromSession(request);
            if (participantId != null) {
                java.util.Optional<StatutInscription> statutInscription = 
                    inscriptionService.getStatutInscription(participantId, evenement.getId());
                statutInscription.ifPresent(dto::setStatutInscription);
                
                // Ajouter l'évaluation du participant s'il a déjà évalué
                java.util.Optional<Evaluation> evaluation = 
                    evaluationService.getEvaluationParticipant(participantId, evenement.getId());
                evaluation.ifPresent(e -> dto.setEvaluationParticipant(e.getNote()));
            }
            
            // Récupérer les commentaires et évaluations
            List<Commentaire> commentaires = commentaireService.getCommentairesEvenement(evenement.getId());
            List<Evaluation> evaluations = evaluationService.getEvaluationsEvenement(evenement.getId());
            
            // Convertir en DTOs
            List<CommentaireDTO> commentairesDTO = commentaires.stream()
                    .map(DTOMapper::toCommentaireDTO)
                    .collect(Collectors.toList());
            
            List<EvaluationDTO> evaluationsDTO = evaluations.stream()
                    .map(DTOMapper::toEvaluationDTO)
                    .collect(Collectors.toList());
            
            // Ajouter le DTO à la requête
            request.setAttribute("evenement", dto);
            request.setAttribute("commentaires", commentairesDTO);
            request.setAttribute("evaluations", evaluationsDTO);
            request.setAttribute("isParticipantConnecte", participantId != null);
            
            // Transférer à la JSP
            request.getRequestDispatcher("/event-details.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID d'événement invalide");
            request.getRequestDispatcher("/catalogue.jsp").forward(request, response);
        } catch (RuntimeException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/catalogue.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Une erreur est survenue lors du chargement de l'événement");
            request.getRequestDispatcher("/catalogue.jsp").forward(request, response);
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
            // Vérifier que c'est un participant (peut être amélioré)
            return (Long) userId;
        }
        
        return null;
    }
}

