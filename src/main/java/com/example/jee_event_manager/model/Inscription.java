package com.example.jee_event_manager.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "inscriptions")
public class Inscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participantId")
    private Participant participant;

    // an inscription is only done ton one event
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eventId")
    private Event event;

    @Column(nullable = false)
    private LocalDateTime dateInscription = LocalDateTime.now();

    //@Column(nullable = false)
    //private String statut = "EN_ATTENTE";
}
