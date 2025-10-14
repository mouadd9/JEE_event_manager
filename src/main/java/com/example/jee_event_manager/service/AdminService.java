package com.example.jee_event_manager.service;

import com.example.jee_event_manager.model.*;
import jakarta.persistence.EntityManager;

public class AdminService {

    private final EntityManager entityManager;

    public AdminService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public User suspendUser(Long userId) {
        User user = entityManager.find(User.class, userId);
        if (user == null) return null;
        user.setAccountStatus(UserAccountStatus.SUSPENDED);
        return user;
    }

    public User activateUser(Long userId) {
        User user = entityManager.find(User.class, userId);
        if (user == null) return null;
        user.setAccountStatus(UserAccountStatus.ACTIVE);
        return user;
    }

    public User validateOrganizer(Long userId) {
        User user = entityManager.find(User.class, userId);
        if (user == null) return null;
        user.setOrganizerValidated(true);
        user.setRole(UserRole.ORGANIZER);
        return user;
    }

    public Event approveEvent(Long eventId) {
        Event event = entityManager.find(Event.class, eventId);
        if (event == null) return null;
        event.setModerationStatus(EventModerationStatus.APPROVED);
        event.setPublicationStatus(EventPublicationStatus.PUBLISHED);
        return event;
    }

    public Event rejectEvent(Long eventId) {
        Event event = entityManager.find(Event.class, eventId);
        if (event == null) return null;
        event.setModerationStatus(EventModerationStatus.REJECTED);
        event.setPublicationStatus(EventPublicationStatus.DRAFT);
        return event;
    }
}


