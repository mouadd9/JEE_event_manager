package com.example.jee_event_manager.DAO;

import com.example.jee_event_manager.model.Evaluation;

import java.util.List;
import java.util.Optional;

public interface EvaluationRepository {
    
    // Basic CRUD operations
    List<Evaluation> findAll();
    Optional<Evaluation> findById(Integer id);
    Evaluation save(Evaluation evaluation);
    void delete(Integer id);
    Evaluation update(Evaluation evaluation);
    
    // Event-related queries
    List<Evaluation> findByEvenement(Integer evenementId);
    List<Evaluation> findByEvenementOrderByDate(Integer evenementId);
    Optional<Evaluation> findByParticipantAndEvenement(Long participantId, Integer evenementId);
    
    // Participant-related queries
    List<Evaluation> findByParticipant(Long participantId);
    
    // Statistics queries
    Double getMoyenneNoteByEvenement(Integer evenementId);
    Long countByEvenement(Integer evenementId);
    Long countByNote(Integer evenementId, Integer note);
}
