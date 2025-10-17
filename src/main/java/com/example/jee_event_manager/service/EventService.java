package com.example.jee_event_manager.service;

import com.example.jee_event_manager.dto.EventDto;

import java.util.List;
import java.util.Optional;

public interface EventService {
    EventDto createEvent(EventDto event, Long organizerId);
    Optional<EventDto> updateEvent(EventDto event);
    Optional<EventDto> publishEvent(Long eventId);
    Optional<EventDto> cancelEvent(Long eventId);
    List<EventDto> getEventsByOrganizer(Long organizerId);
    Optional<EventDto> getEventById(Long eventId);
}
