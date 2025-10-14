package com.example.jee_event_manager.model;

import jakarta.persistence.*;
import java.io.Serializable;

@MappedSuperclass
public abstract class BaseEntity implements Serializable {
    
    public abstract boolean validate();
    
    @PrePersist
    @PreUpdate
    public void save() {
        if (!validate()) {
            throw new IllegalStateException("Validation failed for entity: " + this.getClass().getSimpleName());
        }
    }
}
