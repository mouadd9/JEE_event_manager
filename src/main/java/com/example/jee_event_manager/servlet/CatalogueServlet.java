package com.example.jee_event_manager.servlet;

import com.example.jee_event_manager.dto.EvenementDetailDTO;
import com.example.jee_event_manager.model.Categorie;
import com.example.jee_event_manager.model.Evenement;
import com.example.jee_event_manager.model.Evaluation;
import com.example.jee_event_manager.model.StatutInscription;
import com.example.jee_event_manager.service.CategorieService;
import com.example.jee_event_manager.service.CommentaireService;
import com.example.jee_event_manager.service.EvaluationService;
import com.example.jee_event_manager.service.EvenementService;
import com.example.jee_event_manager.service.InscriptionService;
import com.example.jee_event_manager.util.DTOMapper;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet(name = "CatalogueServlet", urlPatterns = {"/catalogue"}, loadOnStartup = 1)
public class CatalogueServlet extends HttpServlet {
    
    @Inject
    private EvenementService evenementService;
    
    @Inject
    private CategorieService categorieService;
    
    @Inject
    private InscriptionService inscriptionService;
    
    @Inject
    private EvaluationService evaluationService;
    
    @Inject
    private CommentaireService commentaireService;

    @Override
    public void init() throws ServletException {
        super.init();
        // Si l'injection ne fonctionne pas, initialiser manuellement
        if (evenementService == null) {
            evenementService = new EvenementService();
        }
        if (categorieService == null) {
            categorieService = new CategorieService();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Stub d'authentification
        HttpSession session = request.getSession(true);
        if (session.getAttribute("userId") == null) {
            // Simulation d'un utilisateur connecté (ID = 1 pour les tests)
            session.setAttribute("userId", 1L);
        }
        
        // Récupération des paramètres de filtrage
        String dateStr = request.getParameter("date");
        String lieu = request.getParameter("lieu");
        String categorieId = request.getParameter("categorie");
        String searchQuery = request.getParameter("search");
        
        // Conversion de la date si elle est fournie
        LocalDate date = null;
        if (dateStr != null && !dateStr.trim().isEmpty()) {
            try {
                date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
                // Conserver la valeur pour le réaffichage dans le formulaire
                request.setAttribute("selectedDate", dateStr);
            } catch (Exception e) {
                request.setAttribute("error", "Format de date invalide. Utilisez le format yyyy-MM-dd");
            }
        }
        
        // Conserver les valeurs des autres champs pour le réaffichage
        if (lieu != null && !lieu.trim().isEmpty()) {
            request.setAttribute("selectedLieu", lieu.trim());
        }
        
        if (categorieId != null && !categorieId.trim().isEmpty()) {
            request.setAttribute("selectedCategorie", categorieId.trim());
        }
        
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            request.setAttribute("selectedSearch", searchQuery.trim());
        }
        
        try {
            // Récupérer l'ID du participant si connecté
            Long participantId = getParticipantIdFromSession(request);
            
            // Récupération des catégories pour le menu déroulant
            List<Categorie> categories = categorieService.findAll();
            request.setAttribute("categories", categories);
            
            // Récupération des événements avec les filtres
            List<Evenement> evenements = evenementService.getEvenementsPublies(date, lieu, categorieId, searchQuery);
            System.out.println("=== DEBUG CatalogueServlet ===");
            System.out.println("Nombre d'événements récupérés: " + (evenements != null ? evenements.size() : "null"));
            System.out.println("Filtres: date=" + date + ", lieu=" + lieu + ", categorieId=" + categorieId + ", search=" + searchQuery);
            
            // Enrichir les événements avec statistiques et statut inscription
            List<EvenementDetailDTO> evenementsDTO = evenements.stream()
                .map(evt -> enrichirEvenementDTO(evt, participantId))
                .collect(java.util.stream.Collectors.toList());
            
            System.out.println("Nombre de DTOs créés: " + evenementsDTO.size());
            if (!evenementsDTO.isEmpty()) {
                System.out.println("Premier événement: " + evenementsDTO.get(0).getTitre());
            }
            
            request.setAttribute("evenements", evenementsDTO);
            request.setAttribute("isParticipantConnecte", participantId != null);
            
            // Transférer à la JSP
            request.getRequestDispatcher("/catalogue.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("error", "Une erreur est survenue lors de la récupération des événements: " + e.getMessage());
            request.getRequestDispatcher("/catalogue.jsp").forward(request, response);
            System.err.println("=== ERREUR dans CatalogueServlet:");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Rediriger vers GET pour éviter les problèmes de rechargement de formulaire
        doGet(request, response);
    }
    
    /**
     * Enrichir un événement avec les statistiques et le statut d'inscription
     */
    private EvenementDetailDTO enrichirEvenementDTO(Evenement evenement, Long participantId) {
        EvenementDetailDTO dto = DTOMapper.toEvenementDetailDTO(evenement);
        
        // Ajouter les statistiques
        dto.setNoteMoyenne(evaluationService.getMoyenneEvenement(evenement.getId()));
        dto.setNombreEvaluations(evaluationService.countEvaluationsEvenement(evenement.getId()));
        dto.setNombreInscrits(inscriptionService.countInscritsEvenement(evenement.getId()));
        dto.setCapaciteDisponible(inscriptionService.getCapaciteDisponible(evenement.getId()));
        dto.setNombreCommentaires(commentaireService.countCommentairesEvenement(evenement.getId()));
        
        // Si un participant est connecté, ajouter son statut d'inscription
        if (participantId != null) {
            java.util.Optional<StatutInscription> statutInscription = 
                inscriptionService.getStatutInscription(participantId, evenement.getId());
            statutInscription.ifPresent(dto::setStatutInscription);
            
            // Ajouter l'évaluation du participant s'il a déjà évalué
            java.util.Optional<Evaluation> evaluation = 
                evaluationService.getEvaluationParticipant(participantId, evenement.getId());
            evaluation.ifPresent(e -> dto.setEvaluationParticipant(e.getNote()));
        }
        
        return dto;
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
