package com.example.jee_event_manager.service;

import com.example.jee_event_manager.DAO.EventRepository;
import jakarta.ejb.Stateless;

@Stateless
public class EventService {
    private EventRepository eventRepository;
}

