
package com.example.jee_event_manager.model.observer;

import com.example.jee_event_manager.model.Inscription;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

@ApplicationScoped
public class InscriptionNotifier {
    
    @Inject
    private Event<Inscription> inscriptionEvent;
    
    public void notifyInscription(Inscription inscription) {
        inscriptionEvent.fire(inscription);
    }
}
