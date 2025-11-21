package com.example.jee_event_manager.servlet;

import com.example.jee_event_manager.service.EmailService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet for handling contact form submissions
 * Sends emails to contact@ufess.codes
 */
@WebServlet("/contact")
public class ContactServlet extends HttpServlet {

    @Inject
    private EmailService emailService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get form parameters
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");

        // Validate inputs
        if (name == null || name.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            subject == null || subject.trim().isEmpty() ||
            message == null || message.trim().isEmpty()) {

            response.sendRedirect(request.getContextPath() + "/catalogue?error=missing_fields#contact");
            return;
        }

        try {
            // Send email via EmailService
            emailService.sendContactEmail(name.trim(), email.trim(), subject.trim(), message.trim());

            // Redirect with success message
            response.sendRedirect(request.getContextPath() + "/catalogue?success=true#contact");

        } catch (Exception e) {
            System.err.println("Error sending contact email: " + e.getMessage());
            e.printStackTrace();

            // Redirect with error message
            response.sendRedirect(request.getContextPath() + "/catalogue?error=email_failed#contact");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect GET requests to catalogue page
        response.sendRedirect(request.getContextPath() + "/catalogue");
    }
}
