package com.example.jee_event_manager.servlet;

import com.example.jee_event_manager.service.BilletService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Servlet pour marquer un billet comme utilisé
 */
@WebServlet(name = "MarquerBilletUtiliseServlet", urlPatterns = {"/billet/marquer-utilise"})
public class MarquerBilletUtiliseServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(MarquerBilletUtiliseServlet.class.getName());
    
    @Inject
    private BilletService billetService;
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Récupérer l'ID du billet
            String billetIdParam = request.getParameter("id");
            
            if (billetIdParam == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de billet requis");
                return;
            }
            
            Long billetId = Long.parseLong(billetIdParam);
            
            // Marquer le billet comme utilisé
            billetService.marquerBilletUtilise(billetId);
            
            logger.info("Billet marqué comme utilisé: " + billetId);
            
            // Rediriger vers la page des billets avec un message de succès
            String participantId = request.getParameter("participantId");
            if (participantId != null) {
                response.sendRedirect("/jee-event-manager/mes-billets?participantId=" + participantId + "&success=utilise");
            } else {
                response.sendRedirect("/jee-event-manager/mes-billets?success=utilise");
            }
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de billet invalide");
        } catch (Exception e) {
            logger.severe("Erreur lors du marquage du billet comme utilisé: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Erreur lors du marquage du billet");
        }
    }
}
