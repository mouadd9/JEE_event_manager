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
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // each event is associated to one or many comments
    // a participant can submit one or many comments

    @ManyToOne(fetch = FetchType.LAZY) // a comment is submitted by one Participant.
    @JoinColumn(name = "participant_id")
    private Participant participant;

    @ManyToOne(fetch = FetchType.LAZY) // a comment
    @JoinColumn(name = "comment_id")
    private Event event;

}
