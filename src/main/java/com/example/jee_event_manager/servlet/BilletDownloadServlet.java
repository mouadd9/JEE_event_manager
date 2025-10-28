package com.example.jee_event_manager.servlet;

import com.example.jee_event_manager.model.Billet;
import com.example.jee_event_manager.service.BilletService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Servlet pour le téléchargement des billets PDF
 */
@WebServlet(name = "BilletDownloadServlet", urlPatterns = {"/billet/download"})
public class BilletDownloadServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(BilletDownloadServlet.class.getName());
    
    @Inject
    private BilletService billetService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        // Injection manuelle si CDI ne fonctionne pas
        if (billetService == null) {
            throw new ServletException("BilletService n'a pas pu être injecté");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Récupérer l'ID du billet depuis les paramètres
            String billetIdParam = request.getParameter("id");
            String numeroBilletParam = request.getParameter("numero");
            
            if (billetIdParam == null && numeroBilletParam == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID ou numéro de billet requis");
                return;
            }
            
            Optional<Billet> billetOpt = Optional.empty();
            
            // Rechercher par ID ou par numéro
            if (billetIdParam != null) {
                try {
                    Long billetId = Long.parseLong(billetIdParam);
                    billetOpt = billetService.findById(billetId);
                } catch (NumberFormatException e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de billet invalide");
                    return;
                }
            } else if (numeroBilletParam != null) {
                billetOpt = billetService.findByNumeroBillet(numeroBilletParam);
            }
            
            if (billetOpt.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Billet non trouvé");
                return;
            }
            
            Billet billet = billetOpt.get();
            
            // Vérifier que le billet est valide
            if (!billet.isValide()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Ce billet n'est plus valide");
                return;
            }
            
            // Récupérer le contenu PDF
            byte[] pdfContent = billetService.getPdfContent(billet.getId());
            
            // Configurer la réponse
            response.setContentType("application/pdf");
            response.setContentLength(pdfContent.length);
            response.setHeader("Content-Disposition", 
                "attachment; filename=\"" + billet.getNomFichier() + "\"");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            
            // Écrire le contenu PDF
            response.getOutputStream().write(pdfContent);
            response.getOutputStream().flush();
            
            logger.info("Billet téléchargé: " + billet.getNumeroBillet());
            
        } catch (Exception e) {
            logger.severe("Erreur lors du téléchargement du billet: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Erreur lors du téléchargement du billet");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}
