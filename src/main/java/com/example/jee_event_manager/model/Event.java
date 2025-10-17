package com.example.jee_event_manager.model;

import com.example.jee_event_manager.enums.EventStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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

    @Column(nullable = false)
    private String titre;

    @Column
    private String description;

    @Column(nullable = false)
    private LocalDateTime dateDebut;

    @Column(nullable = false)
    private LocalDateTime dateFin;

    @Column(name = "statut", nullable = false)
    @Enumerated(EnumType.STRING) // This is the key!
    private EventStatus statut = EventStatus.BROUILLON; // Default status

    @Column
    private String lieu;
    // Creates foreign key column FK
    @ManyToOne(fetch = FetchType.LAZY) // this configures Hibernate not to fetch the user related to the event.
    @JoinColumn(name="organizer_id") // name of the foreign key
    private Organizer organizer;

    @OneToMany(mappedBy = "event")
    private List<Review> reviews;

    @OneToMany(mappedBy = "event")
    private List<Comment> comments;


}
