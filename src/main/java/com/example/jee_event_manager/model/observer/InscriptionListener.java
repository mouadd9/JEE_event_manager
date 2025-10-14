package com.example.jee_event_manager.model.observer;

import com.example.jee_event_manager.model.Inscription;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class InscriptionListener implements InscriptionObserver {
    
    private static final Logger LOG = Logger.getLogger(InscriptionListener.class.getName());
    
    @Override
    public void onInscriptionCreated(@Observes Inscription inscription) {
        LOG.log(Level.INFO, "Nouvelle inscription enregistrée - ID: {0}, Événement: {1}, Participant: {2}",
                new Object[]{inscription.getId(), 
                           inscription.getEvenement().getTitre(), 
                           inscription.getParticipant().getNom()});
        
        // Ici, vous pouvez ajouter la logique d'envoi d'email, notification, etc.
    }
}
