package com.example.jee_event_manager.scheduler;

import com.example.jee_event_manager.model.Evenement;
import com.example.jee_event_manager.model.Inscription;
import com.example.jee_event_manager.model.Participant;
import com.example.jee_event_manager.service.EmailService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

/**
 * Scheduler pour l'envoi automatique d'emails de rappel aux participants
 * 24 heures avant le début de chaque événement
 * Compatible avec Jakarta EE Web Profile
 */
@WebListener
public class EventReminderScheduler implements ServletContextListener {
    
    private static final Logger logger = Logger.getLogger(EventReminderScheduler.class.getName());
    private Timer timer;
    public EntityManagerFactory emf; // Public pour les tests
    
    /**
     * Initialisation du scheduler au démarrage de l'application
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Initialisation du scheduler de rappels d'événements");
        
        try {
            // Initialiser l'EntityManagerFactory
            emf = Persistence.createEntityManagerFactory("default");
            
            timer = new Timer("EventReminderScheduler", true);
            
            // Programmer la tâche pour s'exécuter toutes les heures
            // Délai initial de 1 minute pour permettre au contexte de se stabiliser
            timer.scheduleAtFixedRate(new ReminderTask(), 60000, 3600000); // 1 min initial, puis toutes les heures
            
            logger.info("Scheduler de rappels d'événements démarré");
        } catch (Exception e) {
            logger.severe("Erreur lors de l'initialisation du scheduler: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Arrêt du scheduler à la fermeture de l'application
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Arrêt du scheduler de rappels d'événements");
        
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
        
        logger.info("Scheduler de rappels d'événements arrêté");
    }
    
    /**
     * Tâche interne pour l'envoi des rappels
     */
    private class ReminderTask extends TimerTask {
        @Override
        public void run() {
            sendEventReminders();
        }
    }
    
    /**
     * Méthode principale pour l'envoi des rappels
     */
    public void sendEventReminders() {
        logger.info("Début de la vérification des rappels d'événements");
        
        EntityManager em = null;
        try {
            if (emf == null) {
                logger.warning("EntityManagerFactory non initialisé, impossible d'envoyer les rappels");
                return;
            }
            
            em = emf.createEntityManager();
            
            // Calculer la date/heure de rappel (24h avant maintenant)
            LocalDateTime reminderTime = LocalDateTime.now().plusHours(24);
            
            // Trouver les événements qui commencent dans 24h (±1h de tolérance)
            List<Evenement> eventsToRemind = findEventsStartingIn24Hours(em, reminderTime);
            
            logger.info(String.format("Trouvé %d événement(s) nécessitant un rappel", eventsToRemind.size()));
            
            for (Evenement event : eventsToRemind) {
                sendRemindersForEvent(em, event);
            }
            
            logger.info("Fin de la vérification des rappels d'événements");
            
        } catch (Exception e) {
            logger.severe("Erreur lors de l'envoi des rappels d'événements: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    
    /**
     * Trouve les événements qui commencent dans 24 heures (±1h de tolérance)
     */
    private List<Evenement> findEventsStartingIn24Hours(EntityManager em, LocalDateTime reminderTime) {
        LocalDateTime startWindow = reminderTime.minusHours(1);
        LocalDateTime endWindow = reminderTime.plusHours(1);
        
        String jpql = """
            SELECT e FROM Evenement e 
            WHERE e.dateDebut BETWEEN :startWindow AND :endWindow
            AND e.statut = 'PUBLIE'
            ORDER BY e.dateDebut
            """;
        
        TypedQuery<Evenement> query = em.createQuery(jpql, Evenement.class);
        query.setParameter("startWindow", startWindow);
        query.setParameter("endWindow", endWindow);
        
        return query.getResultList();
    }
    
    /**
     * Envoie les rappels pour un événement spécifique
     */
    private void sendRemindersForEvent(EntityManager em, Evenement event) {
        logger.info(String.format("Envoi des rappels pour l'événement: %s (ID: %d)", 
                event.getTitre(), event.getId()));
        
        try {
            // Récupérer toutes les inscriptions pour cet événement
            String jpql = "SELECT i FROM Inscription i WHERE i.evenement.id = :eventId";
            TypedQuery<Inscription> query = em.createQuery(jpql, Inscription.class);
            query.setParameter("eventId", event.getId());
            List<Inscription> inscriptions = query.getResultList();
            
            int remindersSent = 0;
            int remindersFailed = 0;
            
            // Créer une instance du service email
            EmailService emailService = new EmailService();
            
            for (Inscription inscription : inscriptions) {
                // Vérifier que l'inscription est acceptée
                if (inscription.getStatut().name().equals("ACCEPTEE")) {
                    Participant participant = inscription.getParticipant();
                    
                    try {
                        // Envoyer l'email de rappel
                        emailService.sendEventReminderEmail(
                            participant.getEmail(),
                            event,
                            participant.getNom()
                        );
                        
                        remindersSent++;
                        logger.info(String.format("Rappel envoyé à %s (%s)", 
                                participant.getNom(), participant.getEmail()));
                        
                    } catch (Exception e) {
                        remindersFailed++;
                        logger.warning(String.format("Échec de l'envoi du rappel à %s (%s): %s", 
                                participant.getNom(), participant.getEmail(), e.getMessage()));
                    }
                }
            }
            
            logger.info(String.format("Résumé pour l'événement '%s': %d rappels envoyés, %d échecs", 
                    event.getTitre(), remindersSent, remindersFailed));
            
        } catch (Exception e) {
            logger.severe(String.format("Erreur lors de l'envoi des rappels pour l'événement %s: %s", 
                    event.getTitre(), e.getMessage()));
            e.printStackTrace();
        }
    }
    
    /**
     * Méthode de test pour déclencher manuellement l'envoi de rappels
     * (utile pour les tests et le débogage)
     */
    public void triggerReminderCheck() {
        logger.info("Déclenchement manuel de la vérification des rappels");
        sendEventReminders();
    }
}
