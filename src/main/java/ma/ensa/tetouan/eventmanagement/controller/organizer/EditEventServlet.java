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
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servlet pour modifier un événement existant.
 *
 * @author ENSA Tétouan
 */
@WebServlet(name = "EditEventServlet", urlPatterns = "/events/edit")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10,       // 10MB
    maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class EditEventServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(EditEventServlet.class);
    private EvenementService evenementService;
    private CategorieService categorieService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.evenementService = new EvenementServiceImpl();
        this.categorieService = new CategorieServiceImpl();
        logger.info("EditEventServlet initialisé");
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

        User user = ServletUtil.getLoggedUser(request);
        Long eventId = ServletUtil.getLongParameter(request, "id", null);

        if (eventId == null) {
            ServletUtil.setErrorMessage(request.getSession(), "ID d'événement manquant");
            ServletUtil.redirect(response, request.getContextPath() + "/events/manage");
            return;
        }

        logger.debug("Affichage du formulaire d'édition pour l'événement: {}", eventId);

        try {
            // Récupérer l'événement
            Evenement evenement = evenementService.getEvenementById(eventId);

            // Vérifier la propriété
            if (!evenement.getOrganisateur().getId().equals(user.getId())) {
                logger.warn("Tentative d'accès non autorisé à l'événement {} par l'utilisateur {}",
                           eventId, user.getId());
                ServletUtil.setErrorMessage(request.getSession(),
                    "Vous n'êtes pas autorisé à modifier cet événement");
                ServletUtil.redirect(response, request.getContextPath() + "/events/manage");
                return;
            }

            // Charger toutes les catégories
            List<Categorie> allCategories = categorieService.getAllCategories();

            // Préparer les IDs des catégories sélectionnées
            Set<Long> selectedCategoryIds = evenement.getCategories().stream()
                .map(Categorie::getId)
                .collect(Collectors.toSet());

            // Mettre les données dans la requête
            request.setAttribute("evenement", evenement);
            request.setAttribute("categories", allCategories);
            request.setAttribute("selectedCategoryIds", selectedCategoryIds);
            request.setAttribute("editMode", true);

            // Forward vers le formulaire
            ServletUtil.forward(request, response, "/WEB-INF/views/organizer/event-form.jsp");

        } catch (Exception e) {
            logger.error("Erreur lors du chargement de l'événement pour édition", e);
            ServletUtil.setErrorMessage(request.getSession(),
                "Erreur lors du chargement de l'événement: " + e.getMessage());
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
        Long eventId = ServletUtil.getLongParameter(request, "id", null);

        if (eventId == null) {
            ServletUtil.setErrorMessage(session, "ID d'événement manquant");
            ServletUtil.redirect(response, request.getContextPath() + "/events/manage");
            return;
        }

        // Récupérer les paramètres du formulaire
        String titre = ServletUtil.getStringParameter(request, "titre", null);
        String description = ServletUtil.getStringParameter(request, "description", null);
        String lieu = ServletUtil.getStringParameter(request, "lieu", null);
        String adresse = ServletUtil.getStringParameter(request, "adresse", null);
        Integer capacite = ServletUtil.getIntParameter(request, "capacite", 0);
        
        // Get existing event to preserve image URL if no new image uploaded
        Evenement existingEvent = null;
        try {
            existingEvent = evenementService.getEvenementById(eventId);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de l'événement existant", e);
            ServletUtil.setErrorMessage(session, "Événement introuvable");
            ServletUtil.redirect(response, request.getContextPath() + "/events/manage");
            return;
        }
        
        // Handle image upload
        String imageUrl = existingEvent.getImageUrl(); // Keep existing image by default
        try {
            Part imagePart = request.getPart("image");
            if (imagePart != null && imagePart.getSize() > 0) {
                String uploadedImageUrl = uploadEventImage(imagePart, request);
                if (uploadedImageUrl != null) {
                    imageUrl = uploadedImageUrl;
                    logger.info("Nouvelle image d'événement uploadée: {}", imageUrl);
                }
            }
        } catch (Exception e) {
            logger.warn("Erreur lors de l'upload de l'image: {}", e.getMessage());
            // Continue without updating the image
        }
        
        String dateDebutStr = request.getParameter("dateDebut");
        String dateFinStr = request.getParameter("dateFin");
        String[] categoryIds = request.getParameterValues("categoriesIds");
        
        // Nouveaux paramètres
        Double latitude = null;
        Double longitude = null;
        try {
            String latStr = request.getParameter("latitude");
            if (latStr != null && !latStr.isEmpty()) {
                latitude = Double.parseDouble(latStr);
            }
            String lonStr = request.getParameter("longitude");
            if (lonStr != null && !lonStr.isEmpty()) {
                longitude = Double.parseDouble(lonStr);
            }
        } catch (NumberFormatException e) {
            logger.warn("Coordonnées GPS invalides");
        }
        
        Boolean gratuit = "true".equals(request.getParameter("gratuit"));
        Double prix = 0.0;
        if (!gratuit) {
            try {
                String prixStr = request.getParameter("prix");
                if (prixStr != null && !prixStr.isEmpty()) {
                    prix = Double.parseDouble(prixStr);
                }
            } catch (NumberFormatException e) {
                logger.warn("Prix invalide");
            }
        }
        String statutStr = request.getParameter("statut");

        logger.info("Tentative de modification de l'événement: {} par l'organisateur: {}",
                   eventId, user.getId());

        // Validation des champs obligatoires
        if (titre == null || titre.isEmpty() ||
            description == null || description.isEmpty() ||
            lieu == null || lieu.isEmpty() ||
            capacite <= 0 ||
            dateDebutStr == null || dateFinStr == null) {

            logger.warn("Champs obligatoires manquants");
            ServletUtil.setErrorMessage(session, "Tous les champs obligatoires doivent être remplis");
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

            // Créer l'objet avec les données mises à jour
            Evenement updatedData = new Evenement();
            updatedData.setTitre(titre);
            updatedData.setDescription(description);
            updatedData.setLieu(lieu);
            updatedData.setAdresseComplete(adresse);
            updatedData.setCapacite(capacite);
            updatedData.setDateDebut(dateDebut);
            updatedData.setDateFin(dateFin);
            updatedData.setImageUrl(imageUrl);
            updatedData.setCategories(new ArrayList<>(categories));
            updatedData.setLatitude(latitude);
            updatedData.setLongitude(longitude);
            updatedData.setGratuit(gratuit);
            updatedData.setPrix(prix);
            
            // Statut
            if (statutStr != null && !statutStr.isEmpty()) {
                try {
                    updatedData.setStatut(ma.ensa.tetouan.eventmanagement.model.StatutEvenement.valueOf(statutStr));
                } catch (IllegalArgumentException e) {
                    logger.warn("Statut invalide: {}", statutStr);
                }
            }

            // Mettre à jour l'événement via le service
            Evenement updatedEvent = evenementService.updateEvenement(eventId, updatedData, user.getId());

            logger.info("Événement mis à jour avec succès: ID={}, Titre={}",
                       updatedEvent.getId(), updatedEvent.getTitre());

            ServletUtil.setSuccessMessage(session,
                "Événement '" + updatedEvent.getTitre() + "' mis à jour avec succès !");

            // Rediriger vers la liste des événements
            ServletUtil.redirect(response, request.getContextPath() + "/events/manage");

        } catch (BusinessException e) {
            logger.warn("Erreur de validation lors de la mise à jour de l'événement: {}", e.getMessage());
            ServletUtil.setErrorMessage(session, e.getMessage());
            doGet(request, response);

        } catch (Exception e) {
            logger.error("Erreur inattendue lors de la mise à jour de l'événement", e);
            ServletUtil.setErrorMessage(session,
                "Erreur lors de la mise à jour de l'événement: " + e.getMessage());
            doGet(request, response);
        }
    }
    
    /**
     * Upload event image and return the URL
     */
    private String uploadEventImage(Part filePart, HttpServletRequest request) throws IOException {
        if (filePart == null || filePart.getSize() == 0) {
            return null;
        }
        
        // Get filename
        String fileName = getFileName(filePart);
        if (fileName == null || fileName.isEmpty()) {
            logger.warn("Nom de fichier vide");
            return null;
        }
        
        // Validate file type
        String contentType = filePart.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            logger.warn("Type de fichier invalide: {}", contentType);
            return null;
        }
        
        // Generate unique filename
        String fileExtension = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = fileName.substring(dotIndex);
        }
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        
        // Get upload directory - use proper path construction
        String webappPath = request.getServletContext().getRealPath("/");
        String uploadPath = webappPath + File.separator + "uploads" + File.separator + "events";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            logger.info("Répertoire uploads créé: {} (succès: {})", uploadPath, created);
        }
        
        // Save file
        String filePath = uploadPath + File.separator + uniqueFileName;
        filePart.write(filePath);
        
        logger.info("Image d'événement sauvegardée: {}", uniqueFileName);
        
        // Return URL
        return request.getContextPath() + "/uploads/events/" + uniqueFileName;
    }
    
    /**
     * Extract filename from Part (Servlet 3.0 compatible)
     */
    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition == null) {
            return null;
        }
        
        for (String token : contentDisposition.split(";")) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf('=') + 1).trim()
                        .replace("\"", "");
            }
        }
        return null;
    }
}
