package com.example.jee_event_manager.DAO;

import com.example.jee_event_manager.model.Commentaire;

import java.util.List;
import java.util.Optional;

public interface CommentaireRepository {
    
    // Basic CRUD operations
    List<Commentaire> findAll();
    Optional<Commentaire> findById(Long id);
    Commentaire save(Commentaire commentaire);
    void delete(Long id);
    Commentaire update(Commentaire commentaire);
    
    // Event-related queries
    List<Commentaire> findByEvenement(Long evenementId);
    List<Commentaire> findByEvenementOrderByDate(Long evenementId);
    Long countByEvenement(Long evenementId);
    
    // Participant-related queries
    List<Commentaire> findByParticipant(Long participantId);
    
    // Combined queries
    List<Commentaire> findByParticipantAndEvenement(Long participantId, Long evenementId);
}
