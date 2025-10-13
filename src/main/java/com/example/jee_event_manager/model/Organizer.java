package com.example.jee_event_manager.model;

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
@Table(name = "organizers")
public class Organizer extends User{

    // An Organizer has a list of events they have created.
    // The "organisateur" field in the Evenement class maps this.
    @OneToMany(mappedBy = "organizer")
    private List<Event> events;
}
