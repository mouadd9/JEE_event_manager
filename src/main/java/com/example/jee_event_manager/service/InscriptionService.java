package com.example.jee_event_manager.service;

import com.example.jee_event_manager.DAO.InscriptionDAO;
import com.example.jee_event_manager.model.Evenement;
import com.example.jee_event_manager.model.Inscription;
import com.example.jee_event_manager.model.observer.InscriptionNotifier;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class InscriptionService {
    
    @Inject
    private InscriptionDAO inscriptionDAO;
    
    @Inject
    private EvenementService evenementService;
    
    @Inject
    private InscriptionNotifier notifier;
    
    public Inscription findById(Long id) {
        return inscriptionDAO.findById(id);
    }
    
    public List<Inscription> findByParticipantId(Long participantId) {
        return inscriptionDAO.findByParticipantId(participantId);
    }
    
    public Inscription register(Inscription inscription) {
        // Vérification de la capacité de l'événement
        if (!checkCapaciteEvenement(inscription.getEvenement())) {
            throw new IllegalStateException("Capacité maximale atteinte pour cet événement");
        }
        
        // Vérification des conflits d'horaire
        if (hasConflitHoraire(inscription)) {
            throw new IllegalStateException("Conflit d'horaire avec une autre inscription");
        }
        
        // Validation et enregistrement
        if (inscription.validate()) {
            inscription.setDateInscription(LocalDateTime.now());
            inscriptionDAO.save(inscription);
            notifier.notifyInscription(inscription);
            return inscription;
        }
        throw new IllegalArgumentException("Inscription invalide");
    }
    
    public void delete(Long id) {
        Inscription inscription = findById(id);
        if (inscription != null) {
            inscriptionDAO.delete(inscription);
        }
    }
    
    private boolean checkCapaciteEvenement(Evenement evenement) {
        // Implémentez la logique de vérification de capacité
        // Par exemple, vérifier le nombre d'inscriptions actuelles vs capacité max
        return true;
    }
    
    private boolean hasConflitHoraire(Inscription nouvelleInscription) {
        // Implémentez la logique de détection de conflit
        // Par exemple, vérifier si le participant a déjà une inscription dans la même plage horaire
        return false;
    }
}
