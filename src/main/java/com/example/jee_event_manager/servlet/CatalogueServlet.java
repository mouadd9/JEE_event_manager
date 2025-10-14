package com.example.jee_event_manager.servlet;

import com.example.jee_event_manager.model.Categorie;
import com.example.jee_event_manager.model.Evenement;
import com.example.jee_event_manager.service.CategorieService;
import com.example.jee_event_manager.service.EvenementService;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet(name = "CatalogueServlet", urlPatterns = {"/catalogue"}, loadOnStartup = 1)
public class CatalogueServlet extends HttpServlet {
    
    @Inject
    private EvenementService evenementService;
    
    @Inject
    private CategorieService categorieService;

    @Override
    public void init() throws ServletException {
        super.init();
        // Si l'injection ne fonctionne pas, initialiser manuellement
        if (evenementService == null) {
            evenementService = new EvenementService();
        }
        if (categorieService == null) {
            categorieService = new CategorieService();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Stub d'authentification
        HttpSession session = request.getSession(true);
        if (session.getAttribute("userId") == null) {
            // Simulation d'un utilisateur connecté (ID = 1 pour les tests)
            session.setAttribute("userId", 1L);
        }
        
        // Récupération des paramètres de filtrage
        String dateStr = request.getParameter("date");
        String lieu = request.getParameter("lieu");
        String categorieId = request.getParameter("categorie");
        
        // Conversion de la date si elle est fournie
        LocalDate date = null;
        if (dateStr != null && !dateStr.trim().isEmpty()) {
            try {
                date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
                // Conserver la valeur pour le réaffichage dans le formulaire
                request.setAttribute("selectedDate", dateStr);
            } catch (Exception e) {
                request.setAttribute("error", "Format de date invalide. Utilisez le format yyyy-MM-dd");
            }
        }
        
        // Conserver les valeurs des autres champs pour le réaffichage
        if (lieu != null && !lieu.trim().isEmpty()) {
            request.setAttribute("selectedLieu", lieu.trim());
        }
        
        if (categorieId != null && !categorieId.trim().isEmpty()) {
            request.setAttribute("selectedCategorie", categorieId.trim());
        }
        
        try {
            // Récupération des catégories pour le menu déroulant
            List<Categorie> categories = categorieService.findAll();
            request.setAttribute("categories", categories);
            
            // Récupération des événements avec les filtres
            List<Evenement> evenements = evenementService.getEvenementsPublies(date, lieu, categorieId);
            request.setAttribute("evenements", evenements);
            
            // Transférer à la JSP
            request.getRequestDispatcher("/catalogue.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("error", "Une erreur est survenue lors de la récupération des événements: " + e.getMessage());
            request.getRequestDispatcher("/catalogue.jsp").forward(request, response);
            System.err.println("=== ERREUR dans CatalogueServlet:");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Rediriger vers GET pour éviter les problèmes de rechargement de formulaire
        doGet(request, response);
    }
}
