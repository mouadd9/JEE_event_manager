package com.example.jee_event_manager.service.impl;

import com.example.jee_event_manager.DAO.EventRepository;
import com.example.jee_event_manager.DAO.OrganizerRepository;
import com.example.jee_event_manager.dto.EventDto;
import com.example.jee_event_manager.enums.EventStatus;
import com.example.jee_event_manager.mappers.EventMapper;
import com.example.jee_event_manager.model.Event;
import com.example.jee_event_manager.model.Organizer;
import com.example.jee_event_manager.service.EventService;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
public class EventServiceImp implements EventService {

    @Inject
    private EventRepository eventRepository;

    @Inject
    private OrganizerRepository organizerRepository;

    @Override
    public EventDto createEvent(EventDto dto, Long organizerId) {

        Organizer organizer = organizerRepository.findById(organizerId)
                .orElseThrow(() -> new EntityNotFoundException("Org with ID " + organizerId + " not found."));

        Event event = EventMapper.toEntity(dto);
        event.setOrganizer(organizer);
        Event saved = eventRepository.save(event);
        return EventMapper.toDto(saved);
    }

    @Override
    public EventDto updateEvent(EventDto dto) {
        // we get the existing event
        Event existing = eventRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Event with ID " + dto.getId() + " not found."));
        // we update its fields, without changing the id
        EventMapper.updateEntityFromDto(existing, dto);
        // we then use the entity manager's merge() to update the existing entity.
        Event updated = eventRepository.update(existing);
        // and we return the updated one
        return EventMapper.toDto(updated);
    }

    @Override
    public void publishEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with ID " + eventId + " not found."));
        event.setStatut(EventStatus.PUBLIE);
        eventRepository.update(event);
    }

    @Override
    public void unpublishEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with ID " + eventId + " not found."));
        event.setStatut(EventStatus.BROUILLON);
        eventRepository.update(event);
    }

    @Override
    public void cancelEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with ID " + eventId + " not found."));
        event.setStatut(EventStatus.ANNULE);
        eventRepository.update(event);
    }

    @Override
    public List<EventDto> getEventsByOrganizer(Long organizerId) {
        List<EventDto> dtos = this.eventRepository.findByOrganizerId(organizerId).stream()
                .map(event -> EventMapper.toDto(event))
                .toList();
        return dtos;
    }

    @Override
    public EventDto getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with ID " + eventId + " not found."));
        return EventMapper.toDto(event);
    }

    @Override
    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with ID " + eventId + " not found."));

        eventRepository.delete(event.getId());
    }
}

