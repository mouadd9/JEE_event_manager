package com.example.jee_event_manager.DAO;

import com.example.jee_event_manager.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository {
        public List<Event> findAll();
        public Optional<Event> findById(Long id); // this will return an optional
        public Event save(Event event);
        public void delete(Long id);
        public Event update(Event event);
        public List<Event> findByOrganizerId(Long id);
}
