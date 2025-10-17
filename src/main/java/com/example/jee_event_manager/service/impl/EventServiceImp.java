package com.example.jee_event_manager.service.impl;

import com.example.jee_event_manager.DAO.EventRepository;
import com.example.jee_event_manager.dto.EventDto;
import com.example.jee_event_manager.service.EventService;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

@Stateless
public class EventServiceImp implements EventService {

    @Inject
    private EventRepository eventRepository;


    @Override
    public EventDto createEvent(EventDto event, Long organizerId) {
        return null;
    }

    @Override
    public Optional<EventDto> updateEvent(EventDto event) {
        return Optional.empty();
    }

    @Override
    public Optional<EventDto> publishEvent(Long eventId) {
        return Optional.empty();
    }

    @Override
    public Optional<EventDto> cancelEvent(Long eventId) {
        return Optional.empty();
    }

    @Override
    public List<EventDto> getEventsByOrganizer(Long organizerId) {
        return List.of();
    }

    @Override
    public Optional<EventDto> getEventById(Long eventId) {
        return Optional.empty();
    }
}

