package com.example.jee_event_manager.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "participants")
public class Participant extends User {

    // A Participant has a list of registrations (inscriptions).
    // The "participant" field in the Inscription class maps this.
    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL)
    private List<Inscription> inscriptions;

    // A Participant can submit many Comments
    @OneToMany(mappedBy = "participant")
    private List<Comment> comments;

    @OneToMany(mappedBy = "participant")
    private List<Review> reviews;
}
