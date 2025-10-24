package com.example.jee_event_manager.DAO;

import com.example.jee_event_manager.model.Participant;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends UtilisateurRepository {
    
    // Participant-specific queries
    List<Participant> findAllParticipants();
    Optional<Participant> findParticipantById(Long id);
    Participant saveParticipant(Participant participant);
    List<Participant> findByPreferences(String preferences);
    List<Participant> findByTelephone(String telephone);
}
