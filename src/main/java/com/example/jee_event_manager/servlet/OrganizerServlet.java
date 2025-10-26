package com.example.jee_event_manager.servlet;

import com.example.jee_event_manager.DAO.OrganisateurRepository;
import com.example.jee_event_manager.config.qualifiers.OrganisateurQualifier;
import com.example.jee_event_manager.dto.EvenementDTO;
import com.example.jee_event_manager.model.StatutEvenement;
import com.example.jee_event_manager.model.Commentaire;
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
// import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.BufferedReader; // Add this import at the top of your file
import java.io.InputStreamReader; // Add this import
import java.nio.charset.StandardCharsets; // Add this import

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

    @Inject
    @OrganisateurQualifier
    private OrganisateurRepository organisateurRepository;

    /**
     * Get the current logged-in organizer ID from session
     */
    private Long getCurrentOrganizerId(HttpServletRequest request) {
        Long organizerId = (Long) request.getSession().getAttribute("organisateurId");
        if (organizerId == null) {
            throw new EntityNotFoundException("Aucun organisateur connecté. Veuillez vous connecter.");
        }
        return organizerId;
    }

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
// doPost handles ACTIONS (Create, Update, Delete operations)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String action = null;
        String eventIdParam = null;
        boolean isMultipart = request.getContentType() != null && request.getContentType().startsWith("multipart/form-data");

        if (isMultipart) {
            // Read from parts for multipart forms (create/update)
            action = getStringValueFromPart(request, "action");
            eventIdParam = getStringValueFromPart(request, "id"); // For update
        } else {
            // Read from parameters for regular forms (publish/cancel/delete)
            action = request.getParameter("action");
            eventIdParam = request.getParameter("eventId");
            if (eventIdParam == null) {
                eventIdParam = request.getParameter("id"); // Fallback
            }
        }

        if (action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No action specified.");
            return;
        }

        System.out.println("DEBUG: doPost received action = " + action);

        addOrganizerToRequest(request);
        Long eventId = null;

        try {

            if (action.equals("create") || action.equals("update")) {
                if (action.equals("create")) {
                    EvenementDTO newEvent = handleCreate(request); // Pass request only
                    eventId = newEvent.getId();
                    // Redirect to dashboard after creating
                    response.sendRedirect(request.getContextPath() + "/organizer/dashboard");
                    return;
                } else {
                    // We already read eventIdParam for multipart update
                    EvenementDTO updatedEvent = handleUpdate(request, Long.parseLong(eventIdParam));
                    eventId = updatedEvent.getId();
                    // Redirect to event detail page after updating
                    response.sendRedirect(request.getContextPath() + "/organizer/events/detail?id=" + eventId);
                    return;
                }
            }

            // For non-create/update actions, we MUST have an eventId
            if (eventIdParam == null || eventIdParam.trim().isEmpty()){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No eventId specified for this action.");
                return;
            }
            eventId = Long.parseLong(eventIdParam);

            switch (action) {
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
        } catch (IllegalArgumentException e) {
            System.out.println("DEBUG: IllegalArgumentException: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            System.out.println("DEBUG: Unexpected error: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur interne: " + e.getMessage());
        }
    }

    /**
     * Add current organizer to request attributes
     */
    private void addOrganizerToRequest(HttpServletRequest request) {
        try {
            Long organizerId = getCurrentOrganizerId(request);
            Organisateur organisateur = organisateurRepository.findOrganisateurById(organizerId)
                    .orElseThrow(() -> new EntityNotFoundException("Organisateur avec ID " + organizerId + " introuvable"));
            request.setAttribute("organizer", organisateur);
        } catch (Exception e) {
            request.setAttribute("organizerName", "Erreur");
            throw e;
        }
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long organizerId = getCurrentOrganizerId(request);
        List<EvenementDTO> eventList = evenementService.getEventsByOrganizer(organizerId);
        request.setAttribute("events", eventList);
        request.getRequestDispatcher("/WEB-INF/views/organizer/dashboard.jsp").forward(request, response);
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

        // Get comments for this event
        List<Commentaire> comments = evenementService.getCommentsByEventId(eventId);

        // Put the event data and comments into the request
        request.setAttribute("event", event);
        request.setAttribute("comments", comments);
        request.getRequestDispatcher("/WEB-INF/views/organizer/detail.jsp").forward(request, response);
    }

    private EvenementDTO handleCreate(HttpServletRequest request) throws IOException, ServletException {
        // HttpSession session = request.getSession(); // extracts user session from the server using token in the request.
        // Organisateur organisateur = (Organisateur) session.getAttribute("loggedInUser");
        EvenementDTO dto = new EvenementDTO();

        // Use the helper method for all text fields
        dto.setTitre(getStringValueFromPart(request, "titre"));
        dto.setDescription(getStringValueFromPart(request, "description"));
        dto.setLieu(getStringValueFromPart(request, "lieu"));

        // Parse dates with error handling
        String dateDebutStr = getStringValueFromPart(request, "dateDebut");
        String dateFinStr = getStringValueFromPart(request, "dateFin");
        System.out.println("DEBUG: dateDebut = '" + dateDebutStr + "'");
        System.out.println("DEBUG: dateFin = '" + dateFinStr + "'");

        if (dateDebutStr == null || dateDebutStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Date de début est requise");
        }
        if (dateFinStr == null || dateFinStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Date de fin est requise");
        }

        try {
            dto.setDateDebut(LocalDateTime.parse(dateDebutStr));
            dto.setDateFin(LocalDateTime.parse(dateFinStr));
        } catch (Exception e) {
            throw new IllegalArgumentException("Format de date invalide: " + e.getMessage());
        }

        // Parse coordinates with error handling
        String latStr = getStringValueFromPart(request, "latitude");
        String lonStr = getStringValueFromPart(request, "longitude");
        System.out.println("DEBUG: latitude = '" + latStr + "'");
        System.out.println("DEBUG: longitude = '" + lonStr + "'");

        if (latStr == null || latStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Latitude est requise");
        }
        if (lonStr == null || lonStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Longitude est requise");
        }

        try {
            dto.setLatitude(parseDouble(latStr));
            dto.setLongitude(parseDouble(lonStr));
        } catch (Exception e) {
            throw new IllegalArgumentException("Format de coordonnées invalide: " + e.getMessage());
        }

        // Handle capacity
        String capaciteStr = getStringValueFromPart(request, "capacite");
        if (capaciteStr != null && !capaciteStr.trim().isEmpty()) {
            dto.setCapacite(Integer.parseInt(capaciteStr));
        } else {
            dto.setCapacite(100); // Default capacity
        }

        dto.setStatut(StatutEvenement.PUBLIE);

        // Handle image upload (this part was already correct)
        try {
            Part imagePart = request.getPart("eventImage");
            if (imagePart != null && imagePart.getSize() > 0) {
                String imageUrl = fileUploadService.saveEventImage(imagePart, getServletContext());
                dto.setImageUrl(imageUrl);
            }
        } catch (Exception e) {
            // Log error but don't fail the event creation
            System.err.println("Error uploading image: " + e.getMessage());
        }

        Long organizerId = getCurrentOrganizerId(request);
        return evenementService.createEvent(dto, organizerId);
    }

    private EvenementDTO handleUpdate(HttpServletRequest request, Long eventId) throws IOException, ServletException {
        // Long eventId = Long.parseLong(request.getParameter("id")); // This is now passed from doPost
        EvenementDTO dto = evenementService.getEventById(eventId);

        // Use the helper method for all text fields
        dto.setTitre(getStringValueFromPart(request, "titre"));
        dto.setDescription(getStringValueFromPart(request, "description"));
        dto.setLieu(getStringValueFromPart(request, "lieu"));

        String dateDebutStr = getStringValueFromPart(request, "dateDebut");
        String dateFinStr = getStringValueFromPart(request, "dateFin");
        String latStr = getStringValueFromPart(request, "latitude");
        String lonStr = getStringValueFromPart(request, "longitude");
        String capaciteStr = getStringValueFromPart(request, "capacite");

        // Note: Add null/empty checks here just like in handleCreate for robustness
        // For brevity, assuming valid data is passed from edit form

        dto.setDateDebut(LocalDateTime.parse(dateDebutStr));
        dto.setDateFin(LocalDateTime.parse(dateFinStr));
        dto.setLatitude(parseDouble(latStr));
        dto.setLongitude(parseDouble(lonStr));

        // Handle capacity
        if (capaciteStr != null && !capaciteStr.trim().isEmpty()) {
            dto.setCapacite(Integer.parseInt(capaciteStr));
        }

        // Handle image upload (this part was already correct)
        try {
            Part imagePart = request.getPart("eventImage");
            if (imagePart != null && imagePart.getSize() > 0) {
                // Delete old image if exists
                if (dto.getImageUrl() != null) {
                    fileUploadService.deleteEventImage(dto.getImageUrl());
                }
                // Save new image
                String imageUrl = fileUploadService.saveEventImage(imagePart, getServletContext());
                dto.setImageUrl(imageUrl);
            }
        } catch (Exception e) {
            // Log error but don't fail the event update
            System.err.println("Error uploading image: " + e.getMessage());
        }

        return evenementService.updateEvent(dto);
    }
    private String getStringValueFromPart(HttpServletRequest request, String partName) throws IOException, ServletException {
        Part part = request.getPart(partName);
        if (part == null) {
            System.out.println("DEBUG: Part '" + partName + "' is null.");
            return null;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(part.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder value = new StringBuilder();
            char[] buffer = new char[1024];
            int bytesRead;
            while ((bytesRead = reader.read(buffer)) != -1) {
                value.append(buffer, 0, bytesRead);
            }
            return value.toString();
        } catch (Exception e) {
            System.err.println("Error reading part " + partName + ": " + e.getMessage());
            return null;
        }
    }
}
