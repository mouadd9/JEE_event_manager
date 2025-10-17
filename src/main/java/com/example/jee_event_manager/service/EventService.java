package com.example.jee_event_manager.service;

import com.example.jee_event_manager.dto.EventDto;

import java.util.List;

public interface EventService {
    EventDto createEvent(EventDto event, Long organizerId);
    EventDto updateEvent(EventDto event);
    void publishEvent(Long eventId);
    void cancelEvent(Long eventId);
    List<EventDto> getEventsByOrganizer(Long organizerId);
    EventDto getEventById(Long eventId);
}
