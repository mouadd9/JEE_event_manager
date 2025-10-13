package com.example.jee_event_manager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
}
