package com.example.jee_event_manager.DAO;

import com.example.jee_event_manager.model.Evaluation;

import java.util.List;
import java.util.Optional;

public interface EvaluationRepository {
    
    // Basic CRUD operations
    List<Evaluation> findAll();
    Optional<Evaluation> findById(Long id);
    Evaluation save(Evaluation evaluation);
    void delete(Long id);
    Evaluation update(Evaluation evaluation);
    
    // Event-related queries
    List<Evaluation> findByEvenement(Long evenementId);
    List<Evaluation> findByEvenementOrderByDate(Long evenementId);
    Optional<Evaluation> findByParticipantAndEvenement(Long participantId, Long evenementId);
    
    // Participant-related queries
    List<Evaluation> findByParticipant(Long participantId);
    
    // Statistics queries
    Double getMoyenneNoteByEvenement(Long evenementId);
    Long countByEvenement(Long evenementId);
    Long countByNote(Long evenementId, Long note);
}
