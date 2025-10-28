package com.example.jee_event_manager.servlet;

import com.example.jee_event_manager.model.Billet;
import com.example.jee_event_manager.model.Inscription;
import com.example.jee_event_manager.service.BilletService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

/**
 * Servlet pour afficher les billets d'un participant
 */
@WebServlet(name = "MesBilletsServlet", urlPatterns = {"/mes-billets"})
public class MesBilletsServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(MesBilletsServlet.class.getName());
    
    @Inject
    private BilletService billetService;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        
        try {
            // Récupérer l'ID du participant depuis la session ou les paramètres
            String participantIdParam = request.getParameter("participantId");
            
            if (participantIdParam == null) {
                // En production, récupérer depuis la session
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID participant requis");
                return;
            }
            
            Long participantId = Long.parseLong(participantIdParam);
            
            // Récupérer les billets du participant
            List<Billet> billets = billetService.getBilletsByParticipant(participantId);
            
            // Générer la page HTML
            generateBilletsPage(response, billets);
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID participant invalide");
        } catch (Exception e) {
            logger.severe("Erreur lors de l'affichage des billets: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Erreur lors de l'affichage des billets");
        }
    }
    
    private void generateBilletsPage(HttpServletResponse response, List<Billet> billets) 
            throws IOException {
        
        response.getWriter().println("<!DOCTYPE html>");
        response.getWriter().println("<html lang='fr'>");
        response.getWriter().println("<head>");
        response.getWriter().println("<meta charset='UTF-8'>");
        response.getWriter().println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        response.getWriter().println("<title>Mes Billets - EventHub</title>");
        response.getWriter().println("<style>");
        response.getWriter().println("""
            body { 
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
                margin: 0; 
                padding: 20px; 
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                min-height: 100vh;
            }
            .container { 
                max-width: 1200px; 
                margin: 0 auto; 
                background: white; 
                border-radius: 15px; 
                box-shadow: 0 10px 30px rgba(0,0,0,0.2);
                overflow: hidden;
            }
            .header { 
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); 
                color: white; 
                padding: 30px; 
                text-align: center; 
            }
            .header h1 { margin: 0; font-size: 2.5em; }
            .content { padding: 30px; }
            .ticket-grid { 
                display: grid; 
                grid-template-columns: repeat(auto-fill, minmax(350px, 1fr)); 
                gap: 20px; 
                margin-top: 20px; 
            }
            .ticket-card { 
                border: 2px solid #e0e0e0; 
                border-radius: 10px; 
                padding: 20px; 
                background: #f9f9f9;
                transition: transform 0.3s ease, box-shadow 0.3s ease;
            }
            .ticket-card:hover { 
                transform: translateY(-5px); 
                box-shadow: 0 5px 15px rgba(0,0,0,0.1); 
            }
            .ticket-header { 
                display: flex; 
                justify-content: space-between; 
                align-items: center; 
                margin-bottom: 15px; 
            }
            .ticket-number { 
                font-weight: bold; 
                color: #667eea; 
                font-size: 1.1em; 
            }
            .ticket-type { 
                padding: 5px 10px; 
                border-radius: 15px; 
                font-size: 0.9em; 
                font-weight: bold; 
            }
            .type-standard { background: #e3f2fd; color: #1976d2; }
            .type-vip { background: #fff3e0; color: #f57c00; }
            .type-premium { background: #f3e5f5; color: #7b1fa2; }
            .event-info { margin-bottom: 15px; }
            .event-title { 
                font-size: 1.3em; 
                font-weight: bold; 
                color: #333; 
                margin-bottom: 10px; 
            }
            .event-details { 
                color: #666; 
                line-height: 1.6; 
            }
            .ticket-actions { 
                display: flex; 
                gap: 10px; 
                margin-top: 15px; 
            }
            .btn { 
                padding: 10px 20px; 
                border: none; 
                border-radius: 5px; 
                cursor: pointer; 
                text-decoration: none; 
                display: inline-block; 
                text-align: center; 
                font-weight: bold; 
                transition: background-color 0.3s ease; 
            }
            .btn-primary { 
                background: #667eea; 
                color: white; 
            }
            .btn-primary:hover { background: #5a6fd8; }
            .btn-success { 
                background: #4caf50; 
                color: white; 
            }
            .btn-success:hover { background: #45a049; }
            .btn-danger { 
                background: #f44336; 
                color: white; 
            }
            .btn-danger:hover { background: #da190b; }
            .status-badge { 
                padding: 5px 10px; 
                border-radius: 15px; 
                font-size: 0.8em; 
                font-weight: bold; 
                text-transform: uppercase; 
            }
            .status-valide { background: #c8e6c9; color: #2e7d32; }
            .status-utilise { background: #ffcdd2; color: #c62828; }
            .status-annule { background: #f5f5f5; color: #616161; }
            .no-tickets { 
                text-align: center; 
                padding: 50px; 
                color: #666; 
                font-size: 1.2em; 
            }
            .back-link { 
                display: inline-block; 
                margin-bottom: 20px; 
                color: #667eea; 
                text-decoration: none; 
                font-weight: bold; 
            }
            .back-link:hover { text-decoration: underline; }
            """);
        response.getWriter().println("</style>");
        response.getWriter().println("</head>");
        response.getWriter().println("<body>");
        
        response.getWriter().println("<div class='container'>");
        response.getWriter().println("<div class='header'>");
        response.getWriter().println("<h1>🎫 Mes Billets</h1>");
        response.getWriter().println("<p>Gérez et téléchargez vos billets d'événements</p>");
        response.getWriter().println("</div>");
        
        response.getWriter().println("<div class='content'>");
        response.getWriter().println("<a href='/jee-event-manager/catalogue' class='back-link'>← Retour au catalogue</a>");
        
        if (billets.isEmpty()) {
            response.getWriter().println("<div class='no-tickets'>");
            response.getWriter().println("<h3>Aucun billet trouvé</h3>");
            response.getWriter().println("<p>Vous n'avez pas encore de billets. Inscrivez-vous à un événement pour en obtenir !</p>");
            response.getWriter().println("</div>");
        } else {
            response.getWriter().println("<div class='ticket-grid'>");
            
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            
            for (Billet billet : billets) {
                Inscription inscription = billet.getInscription();
                String eventTitre = inscription.getEvenement().getTitre();
                String eventDate = inscription.getEvenement().getDateDebut().format(dateFormatter);
                String eventTime = inscription.getEvenement().getDateDebut().format(timeFormatter);
                String eventLieu = inscription.getEvenement().getLieu();
                
                response.getWriter().println("<div class='ticket-card'>");
                
                // En-tête du billet
                response.getWriter().println("<div class='ticket-header'>");
                response.getWriter().println("<div class='ticket-number'>" + billet.getNumeroBillet() + "</div>");
                response.getWriter().println("<div class='ticket-type type-" + billet.getTypeBillet().toLowerCase() + "'>" + 
                    billet.getTypeBillet() + "</div>");
                response.getWriter().println("</div>");
                
                // Informations de l'événement
                response.getWriter().println("<div class='event-info'>");
                response.getWriter().println("<div class='event-title'>" + eventTitre + "</div>");
                response.getWriter().println("<div class='event-details'>");
                response.getWriter().println("<strong>📅 Date:</strong> " + eventDate + "<br>");
                response.getWriter().println("<strong>🕐 Heure:</strong> " + eventTime + "<br>");
                response.getWriter().println("<strong>📍 Lieu:</strong> " + eventLieu + "<br>");
                response.getWriter().println("<strong>👤 Participant:</strong> " + inscription.getParticipant().getNom());
                response.getWriter().println("</div>");
                response.getWriter().println("</div>");
                
                // Statut et actions
                response.getWriter().println("<div class='status-badge status-" + billet.getStatut().name().toLowerCase() + "'>" + 
                    billet.getStatut().name() + "</div>");
                
                response.getWriter().println("<div class='ticket-actions'>");
                
                if (billet.isValide()) {
                    response.getWriter().println("<a href='/jee-event-manager/billet/download?id=" + billet.getId() + 
                        "' class='btn btn-primary'>📥 Télécharger PDF</a>");
                }
                
                if (billet.getStatut().name().equals("VALIDE")) {
                    response.getWriter().println("<a href='/jee-event-manager/billet/marquer-utilise?id=" + billet.getId() + 
                        "' class='btn btn-success' onclick='return confirm(\"Marquer ce billet comme utilisé ?\")'>✅ Marquer utilisé</a>");
                }
                
                response.getWriter().println("</div>");
                response.getWriter().println("</div>");
            }
            
            response.getWriter().println("</div>");
        }
        
        response.getWriter().println("</div>");
        response.getWriter().println("</div>");
        response.getWriter().println("</body>");
        response.getWriter().println("</html>");
    }
}
