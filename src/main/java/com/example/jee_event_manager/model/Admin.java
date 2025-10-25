package com.example.jee_event_manager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "admin")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Admin extends Utilisateur {
    
    @Column(name = "role")
    private String role = "SUPER_ADMIN"; // SUPER_ADMIN, MODERATOR
    
    @Column(name = "permissions")
    private String permissions; // JSON or comma-separated list
    
    public Admin(String nom, String email, String motDePasseHash) {
        super();
        setNom(nom);
        setEmail(email);
        setMotDePasseHash(motDePasseHash);
        setUserType(UserType.ADMIN);
        setIsActive(true);
        setIsVerified(true);
    }
}
