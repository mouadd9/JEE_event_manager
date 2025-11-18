package com.example.jee_event_manager.servlet;

import com.example.jee_event_manager.dto.EvenementDetailDTO;
import com.example.jee_event_manager.dto.InscriptionDTO;
import com.example.jee_event_manager.model.*;
import com.example.jee_event_manager.service.*;
import com.example.jee_event_manager.util.DTOMapper;
import com.example.jee_event_manager.util.GsonUtil;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servlet pour le dashboard du participant
 */
@WebServlet(name = "ParticipantDashboardServlet", urlPatterns = {"/participant/dashboard"})
public class ParticipantDashboardServlet extends HttpServlet {
    
    @Inject
    private EvenementService evenementService;
    
    @Inject
    private InscriptionService inscriptionService;
    
    @Inject
    private EvaluationService evaluationService;
    
    @Inject
    private CommentaireService commentaireService;
    
    @Inject
    private UtilisateurService utilisateurService;
    
    /**
     * GET /participant/dashboard - Récupérer toutes les données du dashboard
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Vérifier l'authentification
            Long participantId = getParticipantIdFromSession(request);
            if (participantId == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
            
            // Construire les données du dashboard
            Map<String, Object> dashboardData = new HashMap<>();
            
            // 1. Informations du participant
            Optional<Utilisateur> utilisateurOpt = utilisateurService.findById(participantId);
            Participant participant = null;
            if (utilisateurOpt.isPresent() && utilisateurOpt.get() instanceof Participant) {
                participant = (Participant) utilisateurOpt.get();
            }
            if (participant != null) {
                Map<String, Object> participantInfo = new HashMap<>();
                participantInfo.put("id", participant.getId());
                participantInfo.put("nom", participant.getNom());
                participantInfo.put("email", participant.getEmail());
                dashboardData.put("participant", participantInfo);
            }
            
            // 2. Événements disponibles (publiés, futurs)
            System.out.println("=== DEBUG ParticipantDashboardServlet ===");
            System.out.println("Participant ID: " + participantId);
            
            List<Evenement> allPublishedEvents = evenementService.getEvenementsPublies(null, null, null, null);
            System.out.println("Total published events found: " + (allPublishedEvents != null ? allPublishedEvents.size() : "null"));
            
            if (allPublishedEvents != null && !allPublishedEvents.isEmpty()) {
                System.out.println("First event: " + allPublishedEvents.get(0).getTitre() + " - Date: " + allPublishedEvents.get(0).getDateDebut());
            }
            
            List<Evenement> evenementsDisponibles = allPublishedEvents
                .stream()
                .filter(e -> e.getDateDebut().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
            
            System.out.println("Future events after filtering: " + evenementsDisponibles.size());
            
            List<EvenementDetailDTO> evenementsDTO = evenementsDisponibles.stream()
                .map(evt -> enrichirEvenementDTO(evt, participantId))
                .collect(Collectors.toList());
            
            System.out.println("DTOs created: " + evenementsDTO.size());
            
            dashboardData.put("evenementsDisponibles", evenementsDTO);
            
            // 3. Inscriptions du participant
            List<Inscription> inscriptions = inscriptionService.getInscriptionsParticipant(participantId);
            
            // Séparer par statut
            List<InscriptionDTO> inscriptionsActives = inscriptions.stream()
                .filter(i -> i.getStatut() == StatutInscription.ACCEPTEE || i.getStatut() == StatutInscription.EN_ATTENTE)
                .filter(i -> i.getEvenement().getDateDebut().isAfter(LocalDateTime.now()))
                .map(DTOMapper::toInscriptionDTO)
                .peek(dto -> {
                    // Enrichir avec le nombre d'inscrits et la capacité disponible
                    if (dto.getEvenementId() != null) {
                        dto.setNombreInscrits(inscriptionService.countInscritsEvenement(dto.getEvenementId()).intValue());
                        dto.setCapaciteDisponible(inscriptionService.getCapaciteDisponible(dto.getEvenementId()).intValue());
                    }
                })
                .collect(Collectors.toList());
            
            // Événements passés ou dans la fenêtre de 7 jours post-événement
            LocalDateTime maintenant = LocalDateTime.now();
            List<InscriptionDTO> inscriptionsPassees = inscriptions.stream()
                .filter(i -> i.getStatut() == StatutInscription.ACCEPTEE)
                .filter(i -> {
                    LocalDateTime dateFin = i.getEvenement().getDateFin();
                    LocalDateTime limiteSept = dateFin.plusDays(7);
                    // Inclure les événements passés et ceux dans la fenêtre de 7 jours
                    return dateFin.isBefore(maintenant) && maintenant.isBefore(limiteSept);
                })
                .map(DTOMapper::toInscriptionDTO)
                .peek(dto -> {
                    // Enrichir avec le nombre d'inscrits et la capacité disponible
                    if (dto.getEvenementId() != null) {
                        dto.setNombreInscrits(inscriptionService.countInscritsEvenement(dto.getEvenementId()).intValue());
                        dto.setCapaciteDisponible(inscriptionService.getCapaciteDisponible(dto.getEvenementId()).intValue());
                    }
                })
                .collect(Collectors.toList());
            
            List<InscriptionDTO> inscriptionsAnnulees = inscriptions.stream()
                .filter(i -> i.getStatut() == StatutInscription.ANNULEE)
                .map(DTOMapper::toInscriptionDTO)
                .collect(Collectors.toList());
            
            Map<String, Object> inscriptionsData = new HashMap<>();
            inscriptionsData.put("actives", inscriptionsActives);
            inscriptionsData.put("passees", inscriptionsPassees);
            inscriptionsData.put("annulees", inscriptionsAnnulees);
            inscriptionsData.put("total", inscriptions.size());
            
            dashboardData.put("inscriptions", inscriptionsData);
            
            // 4. Statistiques
            Map<String, Object> statistiques = new HashMap<>();
            statistiques.put("nombreInscriptionsActives", inscriptionsActives.size());
            statistiques.put("nombreEvenementsParticipes", inscriptionsPassees.size());
            statistiques.put("nombreCommentaires", commentaireService.getCommentairesParticipant(participantId).size());
            statistiques.put("nombreEvaluations", evaluationService.getEvaluationsParticipant(participantId).size());
            
            dashboardData.put("statistiques", statistiques);
            
            // Mettre les données dans la requête pour la JSP
            request.setAttribute("participant", participant);
            request.setAttribute("evenementsDisponibles", evenementsDTO);
            request.setAttribute("inscriptionsActives", inscriptionsActives);
            request.setAttribute("inscriptionsPassees", inscriptionsPassees);
            request.setAttribute("inscriptionsAnnulees", inscriptionsAnnulees);
            request.setAttribute("statistiques", statistiques);
            
            // Sérialiser en JSON pour le JavaScript
            request.setAttribute("evenementsJSON", GsonUtil.toJson(evenementsDTO));
            request.setAttribute("statistiquesJSON", GsonUtil.toJson(statistiques));
            
            // Sérialiser toutes les inscriptions pour le calendrier
            List<InscriptionDTO> toutesInscriptions = inscriptions.stream()
                .map(DTOMapper::toInscriptionDTO)
                .collect(Collectors.toList());
            request.setAttribute("inscriptionsJSON", GsonUtil.toJson(toutesInscriptions));
            
            // Forward vers la JSP
            request.getRequestDispatcher("/participant-dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors du chargement du dashboard: " + e.getMessage());
            request.getRequestDispatcher("/participant-dashboard.jsp").forward(request, response);
        }
    }
    
    /**
     * Enrichir un EvenementDetailDTO avec les informations spécifiques au participant
     */
    private EvenementDetailDTO enrichirEvenementDTO(Evenement evenement, Long participantId) {
        EvenementDetailDTO dto = DTOMapper.toEvenementDetailDTO(evenement);
        
        // Ajouter les statistiques
        dto.setNoteMoyenne(evaluationService.getMoyenneEvenement(evenement.getId()));
        dto.setNombreEvaluations(evaluationService.countEvaluationsEvenement(evenement.getId()));
        dto.setNombreInscrits(inscriptionService.countInscritsEvenement(evenement.getId()));
        dto.setCapaciteDisponible(inscriptionService.getCapaciteDisponible(evenement.getId()));
        dto.setNombreCommentaires(commentaireService.countByEvenement(evenement.getId()));
        
        // Ajouter le statut d'inscription du participant
        Optional<StatutInscription> statutInscription = inscriptionService.getStatutInscription(participantId, evenement.getId());
        statutInscription.ifPresent(dto::setStatutInscription);
        
        // Ajouter l'évaluation du participant s'il a déjà évalué
        Optional<Evaluation> evaluation = evaluationService.getEvaluationParticipant(participantId, evenement.getId());
        evaluation.ifPresent(e -> dto.setEvaluationParticipant(e.getNote()));
        
        return dto;
    }
    
    /**
     * Récupérer l'ID du participant depuis la session
     */
    private Long getParticipantIdFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
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
        
        return null;
    }
}
