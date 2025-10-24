package com.example.jee_event_manager.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class EntityManagerProducer {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    @Produces
    @ApplicationScoped
    public EntityManager createEntityManager() {
        return em;
    }
}