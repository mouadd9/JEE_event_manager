package com.example.jee_event_manager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Creates foreign key column FK
    @ManyToOne(fetch = FetchType.LAZY) // this configures Hibernate not to fetch the user related to the event.
    @JoinColumn(name="organizerId") // name of the foreign key
    private Organizer organizer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventPublicationStatus publicationStatus = EventPublicationStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventModerationStatus moderationStatus = EventModerationStatus.PENDING_REVIEW;
}
