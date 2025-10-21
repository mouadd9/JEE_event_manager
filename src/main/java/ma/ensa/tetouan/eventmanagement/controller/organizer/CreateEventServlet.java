package ma.ensa.tetouan.eventmanagement.controller.organizer;

import ma.ensa.tetouan.eventmanagement.exception.BusinessException;
import ma.ensa.tetouan.eventmanagement.model.Categorie;
import ma.ensa.tetouan.eventmanagement.model.Evenement;
import ma.ensa.tetouan.eventmanagement.model.User;
import ma.ensa.tetouan.eventmanagement.service.CategorieService;
import ma.ensa.tetouan.eventmanagement.service.CategorieServiceImpl;
import ma.ensa.tetouan.eventmanagement.service.EvenementService;
import ma.ensa.tetouan.eventmanagement.service.EvenementServiceImpl;
import ma.ensa.tetouan.eventmanagement.util.ServletUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Servlet pour créer un nouvel événement.
 *
 * @author ENSA Tétouan
 */
@WebServlet(name = "CreateEventServlet", urlPatterns = "/events/create")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10,       // 10MB
    maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class CreateEventServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(CreateEventServlet.class);
    private EvenementService evenementService;
    private CategorieService categorieService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.evenementService = new EvenementServiceImpl();
        this.categorieService = new CategorieServiceImpl();
        logger.info("CreateEventServlet initialisé");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Vérifier l'authentification et le rôle
        if (!ServletUtil.requireLogin(request, response)) {
            return;
        }

        if (!ServletUtil.requireRole(request, response, "ORGANISATEUR")) {
            return;
        }

        logger.debug("Affichage du formulaire de création d'événement");

        try {
            // Charger toutes les catégories pour le formulaire
            List<Categorie> categories = categorieService.getAllCategories();
            request.setAttribute("categories", categories);

            // Forward vers le formulaire
            ServletUtil.forward(request, response, "/WEB-INF/views/organizer/event-form.jsp");

        } catch (Exception e) {
            logger.error("Erreur lors du chargement du formulaire de création", e);
            ServletUtil.setErrorMessage(request.getSession(),
                "Erreur lors du chargement du formulaire: " + e.getMessage());
            ServletUtil.redirect(response, request.getContextPath() + "/events/manage");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Vérifier l'authentification et le rôle
        if (!ServletUtil.requireLogin(request, response)) {
            return;
        }

        if (!ServletUtil.requireRole(request, response, "ORGANISATEUR")) {
            return;
        }

        User user = ServletUtil.getLoggedUser(request);
        HttpSession session = request.getSession();

        // Récupérer les paramètres du formulaire
        String titre = ServletUtil.getStringParameter(request, "titre", null);
        String description = ServletUtil.getStringParameter(request, "description", null);
        String lieu = ServletUtil.getStringParameter(request, "lieu", null);
        String adresseComplete = ServletUtil.getStringParameter(request, "adresseComplete", null);
        Integer capacite = ServletUtil.getIntParameter(request, "capacite", 0);
        String imageUrl = ServletUtil.getStringParameter(request, "imageUrl", null);
        String dateDebutStr = request.getParameter("dateDebut");
        String dateFinStr = request.getParameter("dateFin");
        String[] categoryIds = request.getParameterValues("categoriesIds");
        String gratuitStr = request.getParameter("gratuit");
        String prixStr = request.getParameter("prix");
        String statut = ServletUtil.getStringParameter(request, "statut", "BROUILLON");
        String latitudeStr = request.getParameter("latitude");
        String longitudeStr = request.getParameter("longitude");

        logger.info("Tentative de création d'événement: {} par l'organisateur: {}",
                   titre, user.getId());
        
        // Debug: Log tous les paramètres reçus
        logger.info("Paramètres reçus - titre: {}, description: {}, lieu: {}, adresseComplete: {}, capacite: {}, dateDebut: {}, dateFin: {}, categories: {}, gratuit: {}, prix: {}, statut: {}",
                    titre, description, lieu, adresseComplete, capacite, dateDebutStr, dateFinStr, 
                    categoryIds != null ? categoryIds.length : 0, gratuitStr, prixStr, statut);

        // Validation des champs obligatoires
        List<String> missingFields = new ArrayList<>();
        
        if (titre == null || titre.isEmpty()) {
            missingFields.add("Titre");
        }
        if (description == null || description.isEmpty()) {
            missingFields.add("Description");
        }
        if (lieu == null || lieu.isEmpty()) {
            missingFields.add("Lieu");
        }
        if (adresseComplete == null || adresseComplete.isEmpty()) {
            missingFields.add("Adresse complète");
        }
        if (capacite <= 0) {
            missingFields.add("Capacité");
        }
        if (dateDebutStr == null) {
            missingFields.add("Date de début");
        }
        if (dateFinStr == null) {
            missingFields.add("Date de fin");
        }
        if (categoryIds == null || categoryIds.length == 0) {
            missingFields.add("Catégories (au moins une)");
        }
        
        if (!missingFields.isEmpty()) {
            String errorMessage = "Les champs suivants sont obligatoires : " + String.join(", ", missingFields);
            logger.warn("Champs obligatoires manquants: {}", missingFields);
            ServletUtil.setErrorMessage(session, errorMessage);
            repopulateForm(request, titre, description, lieu, adresseComplete, capacite, imageUrl,
                          dateDebutStr, dateFinStr, categoryIds, gratuitStr, prixStr, statut,
                          latitudeStr, longitudeStr);
            doGet(request, response);
            return;
        }

        try {
            // Parser les dates
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime dateDebut = LocalDateTime.parse(dateDebutStr, formatter);
            LocalDateTime dateFin = LocalDateTime.parse(dateFinStr, formatter);

            // Valider que la date de fin est après la date de début
            if (dateFin.isBefore(dateDebut) || dateFin.isEqual(dateDebut)) {
                throw new BusinessException("La date de fin doit être après la date de début");
            }

            // Récupérer les catégories sélectionnées
            Set<Categorie> categories = new HashSet<>();
            if (categoryIds != null && categoryIds.length > 0) {
                for (String idStr : categoryIds) {
                    try {
                        Long id = Long.parseLong(idStr);
                        Categorie categorie = categorieService.getCategorieById(id);
                        categories.add(categorie);
                    } catch (NumberFormatException e) {
                        logger.warn("ID de catégorie invalide: {}", idStr);
                    }
                }
            }

            if (categories.isEmpty()) {
                throw new BusinessException("Veuillez sélectionner au moins une catégorie");
            }

            // Parser prix et gratuit
            boolean gratuit = "true".equals(gratuitStr);
            Double prix = 0.0;
            if (!gratuit && prixStr != null && !prixStr.isEmpty()) {
                try {
                    prix = Double.parseDouble(prixStr);
                } catch (NumberFormatException e) {
                    throw new BusinessException("Le prix doit être un nombre valide");
                }
            }

            // Parser latitude et longitude (optionnels)
            Double latitude = null;
            Double longitude = null;
            if (latitudeStr != null && !latitudeStr.isEmpty()) {
                try {
                    latitude = Double.parseDouble(latitudeStr);
                } catch (NumberFormatException e) {
                    logger.warn("Latitude invalide: {}", latitudeStr);
                }
            }
            if (longitudeStr != null && !longitudeStr.isEmpty()) {
                try {
                    longitude = Double.parseDouble(longitudeStr);
                } catch (NumberFormatException e) {
                    logger.warn("Longitude invalide: {}", longitudeStr);
                }
            }

            // Créer l'objet Evenement
            Evenement evenement = new Evenement();
            evenement.setTitre(titre);
            evenement.setDescription(description);
            evenement.setLieu(lieu);
            evenement.setAdresseComplete(adresseComplete);
            evenement.setCapacite(capacite);
            evenement.setPlacesDisponibles(capacite);
            evenement.setDateDebut(dateDebut);
            evenement.setDateFin(dateFin);
            evenement.setImageUrl(imageUrl);
            evenement.setCategories(new ArrayList<>(categories));
            evenement.setGratuit(gratuit);
            evenement.setPrix(prix);
            evenement.setStatut(ma.ensa.tetouan.eventmanagement.model.StatutEvenement.valueOf(statut));
            evenement.setLatitude(latitude);
            evenement.setLongitude(longitude);

            // Créer l'événement via le service
            Evenement createdEvent = evenementService.createEvenement(evenement, user.getId());

            logger.info("Événement créé avec succès: ID={}, Titre={}",
                       createdEvent.getId(), createdEvent.getTitre());

            ServletUtil.setSuccessMessage(session,
                "Événement '" + createdEvent.getTitre() + "' créé avec succès en mode brouillon !");

            // Rediriger vers le dashboard
            ServletUtil.redirect(response, request.getContextPath() + "/organizer/dashboard");

        } catch (BusinessException e) {
            logger.warn("Erreur de validation lors de la création de l'événement: {}", e.getMessage());
            ServletUtil.setErrorMessage(session, e.getMessage());
            repopulateForm(request, titre, description, lieu, adresseComplete, capacite, imageUrl,
                          dateDebutStr, dateFinStr, categoryIds, gratuitStr, prixStr, statut,
                          latitudeStr, longitudeStr);
            doGet(request, response);

        } catch (Exception e) {
            logger.error("Erreur inattendue lors de la création de l'événement", e);
            ServletUtil.setErrorMessage(session,
                "Erreur lors de la création de l'événement: " + e.getMessage());
            repopulateForm(request, titre, description, lieu, adresseComplete, capacite, imageUrl,
                          dateDebutStr, dateFinStr, categoryIds, gratuitStr, prixStr, statut,
                          latitudeStr, longitudeStr);
            doGet(request, response);
        }
    }

    /**
     * Repopule le formulaire avec les données soumises en cas d'erreur.
     */
    private void repopulateForm(HttpServletRequest request, String titre, String description,
                                 String lieu, String adresseComplete, Integer capacite, String imageUrl,
                                 String dateDebut, String dateFin, String[] categoryIds,
                                 String gratuit, String prix, String statut,
                                 String latitude, String longitude) {
        // Créer un objet Evenement temporaire avec les valeurs soumises
        Evenement evenement = new Evenement();
        evenement.setTitre(titre);
        evenement.setDescription(description);
        evenement.setLieu(lieu);
        evenement.setAdresseComplete(adresseComplete);
        evenement.setCapacite(capacite);
        evenement.setImageUrl(imageUrl);
        
        // Parser et setter les valeurs optionnelles
        if (gratuit != null) {
            evenement.setGratuit("true".equals(gratuit));
        }
        if (prix != null && !prix.isEmpty()) {
            try {
                evenement.setPrix(Double.parseDouble(prix));
            } catch (NumberFormatException e) {
                // Ignorer les erreurs de parsing lors de la repopulation
            }
        }
        if (statut != null && !statut.isEmpty()) {
            try {
                evenement.setStatut(ma.ensa.tetouan.eventmanagement.model.StatutEvenement.valueOf(statut));
            } catch (IllegalArgumentException e) {
                evenement.setStatut(ma.ensa.tetouan.eventmanagement.model.StatutEvenement.BROUILLON);
            }
        }
        if (latitude != null && !latitude.isEmpty()) {
            try {
                evenement.setLatitude(Double.parseDouble(latitude));
            } catch (NumberFormatException e) {
                // Ignorer les erreurs de parsing lors de la repopulation
            }
        }
        if (longitude != null && !longitude.isEmpty()) {
            try {
                evenement.setLongitude(Double.parseDouble(longitude));
            } catch (NumberFormatException e) {
                // Ignorer les erreurs de parsing lors de la repopulation
            }
        }
        
        request.setAttribute("evenement", evenement);
        request.setAttribute("dateDebut", dateDebut);
        request.setAttribute("dateFin", dateFin);
        request.setAttribute("selectedCategories", categoryIds);
    }
}
