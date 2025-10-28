package com.example.jee_event_manager.servlet;

import com.example.jee_event_manager.scheduler.EventReminderScheduler;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet de test pour déclencher manuellement l'envoi des rappels d'événements
 * Utile pour les tests et le débogage
 */
@WebServlet(name = "TestReminderServlet", urlPatterns = {"/test-reminders"})
public class TestReminderServlet extends HttpServlet {
    
    private EventReminderScheduler reminderScheduler;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            // Créer une instance du scheduler pour le test
            reminderScheduler = new EventReminderScheduler();
            
            // Initialiser manuellement l'EntityManagerFactory pour le test
            reminderScheduler.emf = jakarta.persistence.Persistence.createEntityManagerFactory("default");
            
            // Déclencher la vérification des rappels
            reminderScheduler.sendEventReminders();
            
            // Fermer l'EntityManagerFactory après le test
            if (reminderScheduler.emf != null && reminderScheduler.emf.isOpen()) {
                reminderScheduler.emf.close();
            }
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Test des rappels d'événements</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 40px; }");
            out.println(".container { max-width: 800px; margin: 0 auto; }");
            out.println(".success { color: green; background: #d4edda; padding: 15px; border-radius: 5px; margin: 20px 0; }");
            out.println(".info { color: #0c5460; background: #d1ecf1; padding: 15px; border-radius: 5px; margin: 20px 0; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='container'>");
            out.println("<h1>🧪 Test des rappels d'événements</h1>");
            
            // Déclencher la vérification des rappels
            out.println("<div class='info'>");
            out.println("<h3>Déclenchement de la vérification des rappels...</h3>");
            out.println("</div>");
            
            reminderScheduler.triggerReminderCheck();
            
            out.println("<div class='success'>");
            out.println("<h3>✅ Vérification terminée</h3>");
            out.println("<p>La vérification des rappels d'événements a été exécutée avec succès.</p>");
            out.println("<p>Consultez les logs du serveur pour voir les détails de l'exécution.</p>");
            out.println("</div>");
            
            out.println("<div class='info'>");
            out.println("<h3>ℹ️ Informations</h3>");
            out.println("<ul>");
            out.println("<li>Cette fonctionnalité vérifie les événements qui commencent dans 24 heures (±1h)</li>");
            out.println("<li>Seuls les événements avec le statut 'PUBLIE' sont considérés</li>");
            out.println("<li>Seuls les participants avec des inscriptions 'ACCEPTEE' reçoivent des rappels</li>");
            out.println("<li>Le scheduler s'exécute automatiquement toutes les heures</li>");
            out.println("</ul>");
            out.println("</div>");
            
            out.println("<p><a href='/jee-event-manager/catalogue'>← Retour au catalogue</a></p>");
            
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
            
        } catch (Exception e) {
            out.println("<div style='color: red; background: #f8d7da; padding: 15px; border-radius: 5px;'>");
            out.println("<h3>❌ Erreur</h3>");
            out.println("<p>Une erreur s'est produite lors de l'exécution du test :</p>");
            out.println("<pre>" + e.getMessage() + "</pre>");
            out.println("</div>");
            e.printStackTrace();
        } finally {
            out.close();
        }
    }
}
