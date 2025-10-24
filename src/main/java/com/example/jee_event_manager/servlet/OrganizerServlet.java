package com.example.jee_event_manager.servlet;

import com.example.jee_event_manager.DAO.OrganisateurRepository;
import com.example.jee_event_manager.dto.EvenementDTO;
import com.example.jee_event_manager.enums.StatutEvenement;
import com.example.jee_event_manager.model.Organisateur;
import com.example.jee_event_manager.service.EvenementService;
import com.example.jee_event_manager.service.FileUploadService;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Double.parseDouble;

@WebServlet("/organizer/*")
@MultipartConfig(
    maxFileSize = 5242880,      // 5MB
    maxRequestSize = 10485760   // 10MB
)
public class OrganizerServlet extends HttpServlet { // the HttpServlet abstract class defines how to intercept http requests and construct http responses.
    // in our case the EventServlet will be registered as a servlet class and it will intercept http requests with a specific path "/organizer/events"
    // this servlet will return names of jsp web pages and the tomcat container will return the correct JSP page in the http response.

    @Inject
    private EvenementService evenementService;
    
    @Inject
    private FileUploadService fileUploadService;

    // !!!!!!! temporary we will use sessions in the future
    @Inject
    private OrganisateurRepository organisateurRepository;
    private static final Long CURRENT_ORGANIZER_ID = 1L;
    // !!!!!!! temporary we will use sessions in the future

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            pathInfo = "/dashboard";
        }

        // !!!!!!! temporary we will use sessions in the future
        addOrganizerToRequest(request);
        // !!!!!!! temporary we will use sessions in the future

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
                case "/events/detail":
                    showEventDetail(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Page not found");
            }
        } catch (EntityNotFoundException e) {
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

        addOrganizerToRequest(request);
        Long eventId = null;

        try {

            if (action.equals("create") || action.equals("update")) {
                if (action.equals("create")) {
                    EvenementDTO newEvent = handleCreate(request); // Pass request only
                    eventId = newEvent.getId();
                } else {
                    EvenementDTO updatedEvent = handleUpdate(request); // Pass request only
                    eventId = updatedEvent.getId();
                }
                response.sendRedirect(request.getContextPath() + "/organizer/events/detail?id=" + eventId);
                return;
            }

            // we extract event ID for operations that require event id
            String eventIdParam = request.getParameter("eventId");
            if (eventIdParam == null) {
                eventIdParam = request.getParameter("id"); // Check 'id' as fallback
                if (eventIdParam == null){
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No eventId specified for this action.");
                    return;
                }
            }
            eventId = Long.parseLong(eventIdParam);

            switch (action) {
                case "publish":
                    evenementService.publishEvent(eventId);
                    break;
                case "unpublish":
                    evenementService.unpublishEvent(eventId);
                    break;
                case "cancel":
                    evenementService.cancelEvent(eventId);
                    break;
                case "delete":
                    // evenementService.deleteEvent(eventId); // Commented out as per plan
                    response.sendRedirect(request.getContextPath() + "/organizer/dashboard");
                    return;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action.");
                    return;
            }
            // Redirect back to the detail page
            response.sendRedirect(request.getContextPath() + "/organizer/events/detail?id=" + eventId);
        } catch (EntityNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    // !!!!!!! temporary we will use sessions in the future
    private void addOrganizerToRequest(HttpServletRequest request) {
        try {
            Organisateur organisateur = organisateurRepository.findOrganisateurById(CURRENT_ORGANIZER_ID)
                    .orElseThrow(() -> new EntityNotFoundException("Organisateur not found"));
            request.setAttribute("organizer", organisateur);
        } catch (Exception e) {
            // Handle error, maybe set a default name
            request.setAttribute("organizerName", "Erreur");
        }
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //HttpSession session = request.getSession(); // extracts user session from the server using token in the request.
        //Organisateur organisateur = (Organisateur) session.getAttribute("loggedInUser");
        List<EvenementDTO> eventList = evenementService.getEventsByOrganizer(CURRENT_ORGANIZER_ID);
        request.setAttribute("events", eventList); // here we pass data to our request
        request.getRequestDispatcher("/WEB-INF/views/organizer/dashboard.jsp").forward(request, response); // and then we forward the request to the JSP page.
    }

    private void showCreateEventForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/organizer/eventForm.jsp").forward(request, response);
    }

    private void showEditEventForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long eventId = Long.parseLong(request.getParameter("id")); // here we extract the id of the event
        EvenementDTO event = evenementService.getEventById(eventId); // here we extract the event
        request.setAttribute("event", event);
        request.getRequestDispatcher("/WEB-INF/views/organizer/editEventForm.jsp").forward(request, response);
    }

    private void showEventDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long eventId = Long.parseLong(request.getParameter("id"));
        EvenementDTO event = evenementService.getEventById(eventId);

        // Put the event data into the request
        request.setAttribute("event", event);
        request.getRequestDispatcher("/WEB-INF/views/organizer/detail.jsp").forward(request, response);
    }

    private EvenementDTO handleCreate(HttpServletRequest request) throws IOException {
       // HttpSession session = request.getSession(); // extracts user session from the server using token in the request.
       // Organisateur organisateur = (Organisateur) session.getAttribute("loggedInUser");
        EvenementDTO dto = new EvenementDTO();
        dto.setTitre(request.getParameter("titre"));
        dto.setDescription(request.getParameter("description"));
        dto.setLieu(request.getParameter("lieu"));
        dto.setDateDebut(LocalDateTime.parse(request.getParameter("dateDebut")));
        dto.setDateFin(LocalDateTime.parse(request.getParameter("dateFin")));
        dto.setLatitude(parseDouble(request.getParameter("latitude")));
        dto.setLongitude(parseDouble(request.getParameter("longitude")));
        
        // Handle capacity
        String capaciteStr = request.getParameter("capacite");
        if (capaciteStr != null && !capaciteStr.trim().isEmpty()) {
            dto.setCapacite(Integer.parseInt(capaciteStr));
        } else {
            dto.setCapacite(100); // Default capacity
        }
        
        dto.setStatut(StatutEvenement.BROUILLON);
        
        // Handle image upload
        try {
            Part imagePart = request.getPart("eventImage");
            if (imagePart != null && imagePart.getSize() > 0) {
                String imageUrl = fileUploadService.saveEventImage(imagePart);
                dto.setImageUrl(imageUrl);
            }
        } catch (Exception e) {
            // Log error but don't fail the event creation
            System.err.println("Error uploading image: " + e.getMessage());
        }
        
       return evenementService.createEvent(dto, CURRENT_ORGANIZER_ID); // creates a new event
    }

    private EvenementDTO handleUpdate(HttpServletRequest request) throws IOException {
        Long eventId = Long.parseLong(request.getParameter("id"));
        EvenementDTO dto = evenementService.getEventById(eventId);
        dto.setTitre(request.getParameter("titre"));
        dto.setDescription(request.getParameter("description"));
        dto.setLieu(request.getParameter("lieu"));
        dto.setDateDebut(LocalDateTime.parse(request.getParameter("dateDebut")));
        dto.setDateFin(LocalDateTime.parse(request.getParameter("dateFin")));
        dto.setLatitude(parseDouble(request.getParameter("latitude")));
        dto.setLongitude(parseDouble(request.getParameter("longitude")));
        
        // Handle capacity
        String capaciteStr = request.getParameter("capacite");
        if (capaciteStr != null && !capaciteStr.trim().isEmpty()) {
            dto.setCapacite(Integer.parseInt(capaciteStr));
        }
        
        // Handle image upload
        try {
            Part imagePart = request.getPart("eventImage");
            if (imagePart != null && imagePart.getSize() > 0) {
                // Delete old image if exists
                if (dto.getImageUrl() != null) {
                    fileUploadService.deleteEventImage(dto.getImageUrl());
                }
                // Save new image
                String imageUrl = fileUploadService.saveEventImage(imagePart);
                dto.setImageUrl(imageUrl);
            }
        } catch (Exception e) {
            // Log error but don't fail the event update
            System.err.println("Error uploading image: " + e.getMessage());
        }
        
        return evenementService.updateEvent(dto);
    }
}
