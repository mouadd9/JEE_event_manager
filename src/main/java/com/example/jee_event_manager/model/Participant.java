package com.example.jee_event_manager.model;

import jakarta.persistence.*;

@Entity
@Table(name = "participant")
@PrimaryKeyJoinColumn(name = "utilisateur_id")
public class Participant extends Utilisateur {
    
    public Participant() {
        this.setUserType(UserType.PARTICIPANT);
    }
    
    @Override
    public boolean validate() {
        return super.validate();
    }
}
