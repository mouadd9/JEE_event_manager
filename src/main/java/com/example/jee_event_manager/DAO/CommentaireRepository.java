package com.example.jee_event_manager.DAO;

import com.example.jee_event_manager.model.Commentaire;

import java.util.List;
import java.util.Optional;

public interface CommentaireRepository {
    
    // Basic CRUD operations
    List<Commentaire> findAll();
    Optional<Commentaire> findById(Integer id);
    Commentaire save(Commentaire commentaire);
    void delete(Integer id);
    Commentaire update(Commentaire commentaire);
    
    // Event-related queries
    List<Commentaire> findByEvenement(Integer evenementId);
    List<Commentaire> findByEvenementOrderByDate(Integer evenementId);
    
    // Participant-related queries
    List<Commentaire> findByParticipant(Long participantId);
    
    // Combined queries
    List<Commentaire> findByParticipantAndEvenement(Long participantId, Integer evenementId);
}
