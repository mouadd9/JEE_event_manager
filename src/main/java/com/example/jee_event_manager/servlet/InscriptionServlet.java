package com.example.jee_event_manager.servlet;

import com.example.jee_event_manager.model.Evenement;
import com.example.jee_event_manager.model.Inscription;
import com.example.jee_event_manager.service.EvenementService;
import com.example.jee_event_manager.service.InscriptionService;
import com.example.jee_event_manager.service.facade.InscriptionFacade;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "InscriptionServlet", urlPatterns = {"/events/*"}, loadOnStartup = 1)
public class InscriptionServlet extends HttpServlet {
    
    @Inject
    private InscriptionFacade inscriptionFacade;
    
    @Inject
    private EvenementService evenementService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        // Fallback si l'injection ne fonctionne pas
        if (inscriptionFacade == null) {
            inscriptionFacade = new InscriptionFacade();
        }
        if (evenementService == null) {
            evenementService = new EvenementService();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Vérifier que l'URL cible bien /events/{id}/register
        String uri = request.getRequestURI();
        if (uri == null || !uri.contains("/register")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // Stub d'authentification
        HttpSession session = request.getSession(true);
        if (session.getAttribute("userId") == null) {
            // Simulation d'un utilisateur connecté (ID = 1 pour les tests)
            session.setAttribute("userId", 1L);
            session.setAttribute("userName", "Utilisateur Test");
            session.setAttribute("userEmail", "test@example.com");
        }
        
        // Extraire l'ID de l'événement depuis l'URL
        Long evenementId = extractEventIdFromUrl(request.getRequestURI());
        
        if (evenementId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID d'événement invalide");
            return;
        }
        
        try {
            // Récupérer les détails de l'événement
            Evenement evenement = evenementService.findById(evenementId);
            
            if (evenement == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Événement introuvable");
                return;
            }
            
            // Vérifier la disponibilité
            int placesDisponibles = inscriptionFacade.getPlacesDisponibles(evenementId);
            
            // Passer les données à la JSP
            request.setAttribute("evenement", evenement);
            request.setAttribute("placesDisponibles", placesDisponibles);
            request.setAttribute("userId", session.getAttribute("userId"));
            request.setAttribute("userName", session.getAttribute("userName"));
            request.setAttribute("userEmail", session.getAttribute("userEmail"));
            
            // Transférer à la nouvelle page d'inscription
            request.getRequestDispatcher("/inscription-form.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("error", "Erreur lors du chargement de l'événement");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Vérifier que l'URL cible bien /events/{id}/register
        String uri = request.getRequestURI();
        if (uri == null || !uri.contains("/register")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // Stub d'authentification
        HttpSession session = request.getSession(true);
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null) {
            // Hardcode pour les tests
            userId = 1L;
            session.setAttribute("userId", userId);
            session.setAttribute("userName", "Utilisateur Test");
            session.setAttribute("userEmail", "test@example.com");
        }
        
        // Extraire l'ID de l'événement
        Long evenementId = extractEventIdFromUrl(request.getRequestURI());
        
        if (evenementId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID d'événement invalide");
            return;
        }
        
        // Récupérer l'étape actuelle
        String step = request.getParameter("step");
        
        try {
            Evenement evenement = evenementService.findById(evenementId);
            int placesDisponibles = inscriptionFacade.getPlacesDisponibles(evenementId);
            
            request.setAttribute("evenement", evenement);
            request.setAttribute("placesDisponibles", placesDisponibles);
            
            if ("tickets".equals(step)) {
                // Étape 1: Sélection des billets
                handleTicketSelection(request, response, session);
                
            } else if ("info".equals(step)) {
                // Étape 2: Informations du participant
                handleAttendeeInfo(request, response, session);
                
            } else if ("confirm".equals(step)) {
                // Étape 3: Confirmation finale
                handleConfirmation(request, response, session, userId, evenementId);
                
            } else {
                // Méthode par défaut (ancienne logique)
                handleDirectRegistration(request, response, userId, evenementId);
            }
            
        } catch (IllegalStateException e) {
            request.setAttribute("error", e.getMessage());
            doGet(request, response);
            
        } catch (Exception e) {
            request.setAttribute("error", "Une erreur est survenue lors de l'inscription");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleTicketSelection(HttpServletRequest request, HttpServletResponse response, HttpSession session) 
            throws ServletException, IOException {
        // Récupérer les quantités
        int qtyStandard = getIntParameter(request, "qty-standard", 0);
        int qtyVip = getIntParameter(request, "qty-vip", 0);
        int qtyPremium = getIntParameter(request, "qty-premium", 0);
        
        int totalQty = qtyStandard + qtyVip + qtyPremium;
        
        if (totalQty == 0) {
            request.setAttribute("error", "Veuillez sélectionner au moins un billet");
            request.getRequestDispatcher("/inscription-form.jsp").forward(request, response);
            return;
        }
        
        // Déterminer le type de billet principal
        String ticketType = "STANDARD";
        int quantity = qtyStandard;
        
        if (qtyVip > 0) {
            ticketType = "VIP";
            quantity = qtyVip;
        } else if (qtyPremium > 0) {
            ticketType = "PREMIUM";
            quantity = qtyPremium;
        }
        
        // Stocker en session
        session.setAttribute("selectedTicketType", ticketType);
        session.setAttribute("selectedQuantity", quantity);
        
        // Passer à l'étape suivante
        request.setAttribute("step", "info");
        request.setAttribute("selectedTicketType", ticketType);
        request.setAttribute("selectedQuantity", quantity);
        request.getRequestDispatcher("/inscription-form.jsp").forward(request, response);
    }
    
    private void handleAttendeeInfo(HttpServletRequest request, HttpServletResponse response, HttpSession session) 
            throws ServletException, IOException {
        // Récupérer les informations
        String nom = request.getParameter("nom");
        String email = request.getParameter("email");
        String telephone = request.getParameter("telephone");
        
        // Stocker en session
        session.setAttribute("attendeeName", nom);
        session.setAttribute("attendeeEmail", email);
        session.setAttribute("attendeePhone", telephone);
        
        // Passer à l'étape paiement
        request.setAttribute("step", "payment");
        request.setAttribute("selectedTicketType", session.getAttribute("selectedTicketType"));
        request.setAttribute("selectedQuantity", session.getAttribute("selectedQuantity"));
        request.getRequestDispatcher("/inscription-form.jsp").forward(request, response);
    }
    
    private void handleConfirmation(HttpServletRequest request, HttpServletResponse response, 
                                   HttpSession session, Long userId, Long evenementId) 
            throws ServletException, IOException {
        // Récupérer les données de la session
        String typeBillet = (String) session.getAttribute("selectedTicketType");
        Integer quantite = (Integer) session.getAttribute("selectedQuantity");
        
        if (typeBillet == null || quantite == null) {
            request.setAttribute("error", "Session expirée. Veuillez recommencer.");
            doGet(request, response);
            return;
        }
        
        // Créer l'inscription
        Inscription inscription = inscriptionFacade.registerParticipant(
            userId, 
            evenementId, 
            typeBillet, 
            quantite
        );
        
        // Nettoyer la session
        session.removeAttribute("selectedTicketType");
        session.removeAttribute("selectedQuantity");
        session.removeAttribute("attendeeName");
        session.removeAttribute("attendeeEmail");
        session.removeAttribute("attendeePhone");
        
        // Rediriger vers la confirmation
        response.sendRedirect(request.getContextPath() + "/inscription-confirmation?id=" + inscription.getId());
    }
    
    private void handleDirectRegistration(HttpServletRequest request, HttpServletResponse response, 
                                         Long userId, Long evenementId) 
            throws ServletException, IOException {
        // Ancienne méthode pour compatibilité
        String typeBillet = request.getParameter("typeBillet");
        String quantiteStr = request.getParameter("quantite");
        
        int quantite = 1;
        if (quantiteStr != null && !quantiteStr.isEmpty()) {
            quantite = Integer.parseInt(quantiteStr);
        }
        
        if (quantite < 1 || quantite > 10) {
            request.setAttribute("error", "La quantité doit être entre 1 et 10");
            doGet(request, response);
            return;
        }
        
        Inscription inscription = inscriptionFacade.registerParticipant(
            userId, 
            evenementId, 
            typeBillet, 
            quantite
        );
        
        response.sendRedirect(request.getContextPath() + "/inscription-confirmation?id=" + inscription.getId());
    }
    
    private int getIntParameter(HttpServletRequest request, String name, int defaultValue) {
        String value = request.getParameter(name);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Extrait l'ID de l'événement depuis l'URL
     * Format attendu: /events/{id}/register
     */
    private Long extractEventIdFromUrl(String uri) {
        try {
            String[] parts = uri.split("/");
            for (int i = 0; i < parts.length; i++) {
                if ("events".equals(parts[i]) && i + 1 < parts.length) {
                    return Long.parseLong(parts[i + 1]);
                }
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return null;
    }
}
