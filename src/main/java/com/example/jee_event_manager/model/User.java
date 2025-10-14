package com.example.jee_event_manager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

 

// this class will be a JPA entity representing the user table
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    // we need an auto incremented PRIMARY KEY id field.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Adds AUTO_INCREMENT
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.PARTICIPANT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserAccountStatus accountStatus = UserAccountStatus.ACTIVE;

    // Relevant only when role == ORGANIZER, but kept here for simplicity
    @Column(nullable = false)
    private boolean organizerValidated = false;
}
