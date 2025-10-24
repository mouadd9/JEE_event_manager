package com.example.jee_event_manager.DAO;

import com.example.jee_event_manager.model.Inscription;
import com.example.jee_event_manager.model.StatutInscription;

import java.util.List;
import java.util.Optional;

public interface InscriptionRepository {
    
    // Basic CRUD operations
    List<Inscription> findAll();
    Optional<Inscription> findById(Long id);
    Inscription save(Inscription inscription);
    void delete(Long id);
    Inscription update(Inscription inscription);
    
    // Participant-related queries
    List<Inscription> findByParticipant(Long participantId);
    List<Inscription> findByParticipantAndStatut(Long participantId, StatutInscription statut);
    Optional<Inscription> findByParticipantAndEvenement(Long participantId, Long evenementId);
    boolean isParticipantInscrit(Long participantId, Long evenementId);
    boolean isOwner(Long inscriptionId, Long participantId);
    
    // Event-related queries
    List<Inscription> findByEvenement(Long evenementId);
    List<Inscription> findByEvenementAndStatut(Long evenementId, StatutInscription statut);
    Long countByEvenement(Long evenementId);
    Long countPlacesReservees(Long evenementId);
    
    // Status management
    void cancelInscription(Long inscriptionId);
    Optional<StatutInscription> getStatutInscription(Long participantId, Long evenementId);
}
