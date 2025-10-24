package com.example.jee_event_manager.DAO;

import com.example.jee_event_manager.model.Event;
import com.example.jee_event_manager.model.Organizer;

import java.util.Optional;

public interface OrganizerRepository {
    public Optional<Organizer> findById(Long id); // this will return an optional
}
