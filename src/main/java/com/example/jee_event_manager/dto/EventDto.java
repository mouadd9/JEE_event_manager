package com.example.jee_event_manager.dto;

import com.example.jee_event_manager.enums.EventStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventDto {
    private Long id;
    private String titre;
    private String description;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String lieu;
    private EventStatus statut; // Use the enum type
    private String organizerName;
}
