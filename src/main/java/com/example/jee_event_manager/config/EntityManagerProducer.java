package com.example.jee_event_manager.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@ApplicationScoped
public class EntityManagerProducer {

    private EntityManagerFactory emf;

    @PostConstruct
    public void init() {
        System.out.println("=== Initializing EntityManagerFactory ===");
        try {
            emf = Persistence.createEntityManagerFactory("default");
            System.out.println("=== EntityManagerFactory created successfully ===");
        } catch (Exception e) {
            System.err.println("=== ERROR creating EntityManagerFactory ===");
            e.printStackTrace();
            throw e;
        }
    }

    @Produces
    @ApplicationScoped
    public EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    @Produces
    public EntityManager createEntityManager(EntityManagerFactory emf) {
        System.out.println("=== Creating EntityManager ===");
        return emf.createEntityManager();
    }

    @PreDestroy
    public void cleanup() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            System.out.println("=== EntityManagerFactory closed ===");
        }
    }
}