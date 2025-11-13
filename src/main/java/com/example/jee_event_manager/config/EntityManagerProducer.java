package com.example.jee_event_manager.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class EntityManagerProducer {

    private EntityManagerFactory emf;

    @PostConstruct
    public void init() {
        System.out.println("=== Initializing EntityManagerFactory ===");
        try {
            // Read database config from environment variables (for deployment)
            Map<String, String> properties = new HashMap<>();
            
            String dbUrl = System.getenv("DB_URL");
            String dbUser = System.getenv("DB_USER");
            String dbPassword = System.getenv("DB_PASSWORD");
            
            if (dbUrl != null && !dbUrl.isEmpty()) {
                properties.put("jakarta.persistence.jdbc.url", dbUrl);
                System.out.println("Using DB_URL from environment: " + dbUrl);
            }
            if (dbUser != null && !dbUser.isEmpty()) {
                properties.put("jakarta.persistence.jdbc.user", dbUser);
                System.out.println("Using DB_USER from environment: " + dbUser);
            }
            if (dbPassword != null) {
                properties.put("jakarta.persistence.jdbc.password", dbPassword);
                System.out.println("Using DB_PASSWORD from environment");
            }
            
            // Create EMF with overridden properties
            emf = properties.isEmpty() 
                ? Persistence.createEntityManagerFactory("default")
                : Persistence.createEntityManagerFactory("default", properties);
                
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