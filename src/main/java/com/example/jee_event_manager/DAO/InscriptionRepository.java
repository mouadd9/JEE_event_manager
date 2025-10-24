package com.example.jee_event_manager.DAO;

import com.example.jee_event_manager.model.Inscription;
import com.example.jee_event_manager.model.StatutInscription;

import java.util.List;
import java.util.Optional;

public interface InscriptionRepository {
    
    // Basic CRUD operations
    List<Inscription> findAll();
    Optional<Inscription> findById(Integer id);
    Inscription save(Inscription inscription);
    void delete(Integer id);
    Inscription update(Inscription inscription);
    
    // Participant-related queries
    List<Inscription> findByParticipant(Long participantId);
    List<Inscription> findByParticipantAndStatut(Long participantId, StatutInscription statut);
    Optional<Inscription> findByParticipantAndEvenement(Long participantId, Integer evenementId);
    boolean isParticipantInscrit(Long participantId, Integer evenementId);
    boolean isOwner(Integer inscriptionId, Long participantId);
    
    // Event-related queries
    List<Inscription> findByEvenement(Integer evenementId);
    List<Inscription> findByEvenementAndStatut(Integer evenementId, StatutInscription statut);
    Long countByEvenement(Integer evenementId);
    Long countPlacesReservees(Integer evenementId);
    
    // Status management
    void cancelInscription(Integer inscriptionId);
    Optional<StatutInscription> getStatutInscription(Long participantId, Integer evenementId);
}
