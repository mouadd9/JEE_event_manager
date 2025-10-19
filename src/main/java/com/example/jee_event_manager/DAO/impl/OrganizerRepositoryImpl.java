package com.example.jee_event_manager.DAO.impl;

import com.example.jee_event_manager.DAO.OrganizerRepository;
import com.example.jee_event_manager.model.Organizer;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;

@Stateless
public class OrganizerRepositoryImpl implements OrganizerRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Organizer> findById(Long id) {
        Organizer org = em.find(Organizer.class, id);
        return Optional.ofNullable(org);
    }
}
