package com.example.jee_event_manager.model;

import jakarta.persistence.*;

@Entity
@Table(name = "organisateur")
@PrimaryKeyJoinColumn(name = "utilisateur_id")
public class Organisateur extends Utilisateur {
    
    public Organisateur() {
        this.setUserType(UserType.ORGANISATEUR);
    }
    
    @Override
    public boolean validate() {
        return super.validate();
    }
}
