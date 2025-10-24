package com.example.jee_event_manager.dto;

import com.example.jee_event_manager.enums.EventStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.sql.Timestamp;

@Data
public class EventDto {
    private Long id;
    private String titre;
    private String description;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String lieu;
    private EventStatus statut;
    private String organizerName;
    private Double latitude;
    private Double longitude;

    // 2. ADD THESE TWO METHODS

    /**
     * Helper method for JSTL <fmt:formatDate> tag.
     * It converts the modern LocalDateTime to a java.util.Date.
     */
    public java.util.Date getDateDebutAsDate() {
        if (this.dateDebut == null) {
            return null;
        }
        // Timestamp is a subclass of java.util.Date, making this a safe conversion
        return Timestamp.valueOf(this.dateDebut);
    }

    /**
     * Helper method for JSTL <fmt:formatDate> tag.
     * It converts the modern LocalDateTime to a java.util.Date.
     */
    public java.util.Date getDateFinAsDate() {
        if (this.dateFin == null) {
            return null;
        }
        return Timestamp.valueOf(this.dateFin);
    }
}
