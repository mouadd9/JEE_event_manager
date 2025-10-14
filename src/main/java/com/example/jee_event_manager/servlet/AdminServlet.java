package com.example.jee_event_manager.servlet;

import com.example.jee_event_manager.service.AdminService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "AdminServlet", urlPatterns = "/api/admin/*")
public class AdminServlet extends HttpServlet {

    private EntityManagerFactory emf;
    private AdminService adminService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.emf = Persistence.createEntityManagerFactory("default");
        EntityManager em = emf.createEntityManager();
        this.adminService = new AdminService(em);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo() == null ? "" : req.getPathInfo();
        try {
            var em = emf.createEntityManager();
            em.getTransaction().begin();
            switch (path) {
                case "/users/suspend":
                    handleSuspendUser(req, resp);
                    break;
                case "/users/activate":
                    handleActivateUser(req, resp);
                    break;
                case "/organizers/validate":
                    handleValidateOrganizer(req, resp);
                    break;
                case "/events/approve":
                    handleApproveEvent(req, resp);
                    break;
                case "/events/reject":
                    handleRejectEvent(req, resp);
                    break;
                default:
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown endpoint");
            }
            em.getTransaction().commit();
            em.close();
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private void handleSuspendUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long userId = parseId(req.getParameter("userId"));
        var user = adminService.suspendUser(userId);
        writeSimpleJson(resp, user == null ? "not_found" : "ok");
    }

    private void handleActivateUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long userId = parseId(req.getParameter("userId"));
        var user = adminService.activateUser(userId);
        writeSimpleJson(resp, user == null ? "not_found" : "ok");
    }

    private void handleValidateOrganizer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long userId = parseId(req.getParameter("userId"));
        var user = adminService.validateOrganizer(userId);
        writeSimpleJson(resp, user == null ? "not_found" : "ok");
    }

    private void handleApproveEvent(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long eventId = parseId(req.getParameter("eventId"));
        var event = adminService.approveEvent(eventId);
        writeSimpleJson(resp, event == null ? "not_found" : "ok");
    }

    private void handleRejectEvent(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long eventId = parseId(req.getParameter("eventId"));
        var event = adminService.rejectEvent(eventId);
        writeSimpleJson(resp, event == null ? "not_found" : "ok");
    }

    private Long parseId(String id) {
        if (id == null) throw new IllegalArgumentException("Missing id parameter");
        return Long.parseLong(id);
    }

    private void writeSimpleJson(HttpServletResponse resp, String status) throws IOException {
        resp.setContentType("application/json");
        resp.getWriter().write("{\"status\":\"" + status + "\"}");
    }

    @Override
    public void destroy() {
        if (emf != null) {
            emf.close();
        }
        super.destroy();
    }
}


