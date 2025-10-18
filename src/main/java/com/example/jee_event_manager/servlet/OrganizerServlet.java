package com.example.jee_event_manager.servlet;

import com.example.jee_event_manager.dto.EventDto;
import com.example.jee_event_manager.model.Organizer;
import com.example.jee_event_manager.service.EventService;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/organizer/events")
public class OrganizerServlet extends HttpServlet { // the HttpServlet abstract class defines how to intercept http requests and construct http responses.
    // in our case the EventServlet will be registered as a servlet class and it will intercept http requests with a specific path "/organizer/events"
    // this servlet will return names of jsp web pages and the tomcat container will return the correct JSP page in the http response.

    @Inject
    private EventService eventService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            pathInfo = "/dashboard";
        }

        try {
            switch (pathInfo) {
                case "/dashboard":
                    showDashboard(request, response);
                    break;
                case "/events/new":
                    showCreateEventForm(request, response);
                    break;
                case "/events/edit":
                    showEditEventForm(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Page not found");
            }
        } catch (EntityNotFoundException e) {
            // Handle cases where an event ID doesn't exist
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }

    // doPost handles ACTIONS (Create, Update, Delete operations)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No action specified.");
            return;
        }

        try {
            switch (action) {
                case "create":
                    handleCreate(request, response);
                    break;
                case "update":
                    handleUpdate(request, response);
                    break;
                case "publish":
                    handlePublish(request, response);
                    break;
                case "cancel":
                    handleCancel(request, response);
                    break;
                case "delete":
                    handleDelete(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action.");
            }
        } catch (EntityNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            // Handle other potential errors (e.g., bad date format)
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(); // extracts user session from the server using token in the request.
        Organizer organizer = (Organizer) session.getAttribute("loggedInUser");
        List<EventDto> eventList = eventService.getEventsByOrganizer(organizer.getId());
        request.setAttribute("events", eventList); // here we pass data to our request
        request.getRequestDispatcher("/WEB-INF/views/organizer/dashboard.jsp").forward(request, response); // and then we forward the request to the JSP page.
    }

    private void showCreateEventForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/organizer/createEvent.jsp").forward(request, response);
    }

    private void showEditEventForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long eventId = Long.parseLong(request.getParameter("id")); // here we extract the id of the event
        EventDto event = eventService.getEventById(eventId); // here we extract the event
        request.setAttribute("event", event);
        request.getRequestDispatcher("/WEB-INF/views/organizer/eventForm.jsp").forward(request, response);
    }

    private void handleCreate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(); // extracts user session from the server using token in the request.
        Organizer organizer = (Organizer) session.getAttribute("loggedInUser");

        // we should manually construct the event DTO
        EventDto dto = new EventDto();
        dto.setTitre(request.getParameter("titre"));
        dto.setDescription(request.getParameter("description"));
        dto.setLieu(request.getParameter("lieu"));
        dto.setDateDebut(LocalDateTime.parse(request.getParameter("dateDebut")));
        dto.setDateFin(LocalDateTime.parse(request.getParameter("dateFin")));

        eventService.createEvent(dto, organizer.getId()); // creates a new event

        response.sendRedirect(request.getContextPath() + "/organizer/dashboard"); // we then redirect to the dashboard
    }

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long eventId = Long.parseLong(request.getParameter("id"));

        EventDto dto = eventService.getEventById(eventId);
        dto.setTitre(request.getParameter("titre"));
        dto.setDescription(request.getParameter("description"));
        dto.setLieu(request.getParameter("lieu"));
        dto.setDateDebut(LocalDateTime.parse(request.getParameter("dateDebut")));
        dto.setDateFin(LocalDateTime.parse(request.getParameter("dateFin")));

        eventService.updateEvent(dto);
        response.sendRedirect(request.getContextPath() + "/organizer/dashboard");
    }

    private void handlePublish(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long eventId = Long.parseLong(request.getParameter("eventId"));
        eventService.publishEvent(eventId);
        response.sendRedirect(request.getContextPath() + "/organizer/dashboard");
    }

    private void handleCancel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long eventId = Long.parseLong(request.getParameter("eventId"));
        eventService.cancelEvent(eventId);
        response.sendRedirect(request.getContextPath() + "/organizer/dashboard");
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long eventId = Long.parseLong(request.getParameter("eventId"));
        eventService.deleteEvent(eventId);
        response.sendRedirect(request.getContextPath() + "/organizer/dashboard");
    }

}
