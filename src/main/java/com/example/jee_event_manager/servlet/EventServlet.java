package com.example.jee_event_manager.servlet;

import com.example.jee_event_manager.service.EventService;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;

@WebServlet()
public class EventServlet {

    @Inject
    private EventService eventService;

}
