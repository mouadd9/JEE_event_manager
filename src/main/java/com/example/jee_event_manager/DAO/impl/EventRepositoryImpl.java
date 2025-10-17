package com.example.jee_event_manager.DAO.impl;


import com.example.jee_event_manager.DAO.EventRepository;
import com.example.jee_event_manager.model.Event;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@Stateless
public class EventRepositoryImpl implements EventRepository {

    // the container will instantiate an entity manager and will link it to a datasource and then inject it here.
    @PersistenceContext // (unitName = "bookstorePU")
    private EntityManager em;

    @Override
    public Event save(Event event) {
        em.persist(event);
        return event;
    }

    @Override
    public Optional<Event> findById(Long id) {
        Event event = em.find(Event.class, id);
        return Optional.ofNullable(event);
    }

    @Override
    public List<Event> findAll() {
        return em.createQuery("SELECT e FROM Event e", Event.class)
                .getResultList();
        // SELECT e FROM Event e
        // SELECT id, title, author, isbn, price, published_date, in_stock
        // FROM book
    }

    @Override
    public Event update(Event event) {
        return em.merge(event);
    }

    @Override
    public void delete(Long id) {
        Event event = em.find(Event.class, id);
        if (event != null) {
            em.remove(event);
        }
    }
}
