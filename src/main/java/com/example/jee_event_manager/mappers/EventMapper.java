package com.example.jee_event_manager.mappers;

import com.example.jee_event_manager.dto.EventDto;
import com.example.jee_event_manager.model.Event;
import com.example.jee_event_manager.model.User;

public class EventMapper {
    public static EventDto toDto(Event event) {
        if (event == null) {
            return null;
        }

        EventDto dto = new EventDto();
        dto.setId(event.getId());
        dto.setTitre(event.getTitre());
        dto.setDescription(event.getDescription());
        dto.setDateDebut(event.getDateDebut());
        dto.setDateFin(event.getDateFin());
        dto.setLieu(event.getLieu());
        dto.setStatut(event.getStatut()); // This now works with the enum

        if (event.getOrganizer() != null) {
            User organizerUser = event.getOrganizer();
            String firstName = organizerUser.getFirstName() != null ? organizerUser.getFirstName() : "";
            String lastName = organizerUser.getLastName() != null ? organizerUser.getLastName() : "";
            dto.setOrganizerName((firstName + " " + lastName).trim());
        } else {
            dto.setOrganizerName("N/A");
        }

        return dto;
    }

    public static Event toEntity(EventDto dto) {
        if (dto == null) {
            return null;
        }

        Event event = new Event();
        event.setId(dto.getId());
        event.setTitre(dto.getTitre());
        event.setDescription(dto.getDescription());
        event.setDateDebut(dto.getDateDebut());
        event.setDateFin(dto.getDateFin());
        event.setLieu(dto.getLieu());
        event.setStatut(dto.getStatut());
        // the 'organizer' is set by the service layer.

        return event;
    }

    public static void updateEntityFromDto(Event event, EventDto dto) {
        if (dto == null || event == null) {
            return;
        }

        // Only update fields if the new value from the DTO is not null
        if (dto.getTitre() != null) {
            event.setTitre(dto.getTitre());
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getDateDebut() != null) {
            event.setDateDebut(dto.getDateDebut());
        }
        if (dto.getDateFin() != null) {
            event.setDateFin(dto.getDateFin());
        }
        if (dto.getLieu() != null) {
            event.setLieu(dto.getLieu());
        }
        if (dto.getStatut() != null) {
            event.setStatut(dto.getStatut());
        }
    }
}
