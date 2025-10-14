package com.example.jee_event_manager.servlet;

import com.example.jee_event_manager.model.Inscription;
import com.example.jee_event_manager.service.InscriptionService;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "InscriptionConfirmationServlet", urlPatterns = {"/inscription-confirmation"})
public class InscriptionConfirmationServlet extends HttpServlet {
    
    @Inject
    private InscriptionService inscriptionService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        if (inscriptionService == null) {
            inscriptionService = new InscriptionService();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        
        if (idStr == null || idStr.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID d'inscription manquant");
            return;
        }
        
        try {
            Long inscriptionId = Long.parseLong(idStr);
            Inscription inscription = inscriptionService.findById(inscriptionId);
            
            if (inscription == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Inscription introuvable");
                return;
            }
            
            request.setAttribute("inscription", inscription);
            request.getRequestDispatcher("/inscription-confirmation.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID d'inscription invalide");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            e.printStackTrace();
        }
    }
}
