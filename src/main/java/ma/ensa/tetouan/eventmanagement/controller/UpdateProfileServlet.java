package ma.ensa.tetouan.eventmanagement.controller;

import ma.ensa.tetouan.eventmanagement.model.Organisateur;
import ma.ensa.tetouan.eventmanagement.model.Participant;
import ma.ensa.tetouan.eventmanagement.model.User;
import ma.ensa.tetouan.eventmanagement.service.UserService;
import ma.ensa.tetouan.eventmanagement.service.UserServiceImpl;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Servlet pour mettre à jour le profil utilisateur.
 *
 * @author ENSA Tétouan
 */
@WebServlet(name = "UpdateProfileServlet", urlPatterns = "/profile/update")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10,       // 10MB
    maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class UpdateProfileServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(UpdateProfileServlet.class);
    private static final String UPLOAD_DIR = "uploads/profiles";
    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userService = new UserServiceImpl();
        logger.info("UpdateProfileServlet initialisé");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("currentUser");
        logger.debug("Mise à jour du profil pour l'utilisateur ID: {}", user.getId());

        Map<String, String> errors = new HashMap<>();

        try {
            // Récupérer les paramètres communs
            String nom = request.getParameter("nom");
            String email = request.getParameter("email");

            // Validation
            if (nom == null || nom.trim().isEmpty()) {
                errors.put("nom", "Le nom est obligatoire");
            }
            if (email == null || email.trim().isEmpty()) {
                errors.put("email", "L'email est obligatoire");
            } else if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                errors.put("email", "Format d'email invalide");
            }

            // Gérer l'upload de la photo de profil
            String photoUrl = null;
            Part photoPart = request.getPart("photoProfil");
            if (photoPart != null && photoPart.getSize() > 0) {
                photoUrl = uploadProfilePhoto(photoPart, request);
                if (photoUrl == null) {
                    errors.put("photoProfil", "Erreur lors de l'upload de la photo");
                }
            }

            if (!errors.isEmpty()) {
                request.setAttribute("errors", errors);
                request.setAttribute("user", user);
                ServletUtil.forward(request, response, "/profile");
                return;
            }

            // Mettre à jour selon le type d'utilisateur
            if (user instanceof Organisateur) {
                updateOrganisateurProfile(request, (Organisateur) user, nom, email, photoUrl);
            } else if (user instanceof Participant) {
                updateParticipantProfile(request, (Participant) user, nom, email, photoUrl);
            } else {
                // Utilisateur générique
                user.setNom(nom);
                user.setEmail(email);
            }

            // Sauvegarder les modifications
            User updatedUser = userService.updateProfile(user.getId(), user);
            
            // Mettre à jour la session
            session.setAttribute("currentUser", updatedUser);

            logger.info("Profil mis à jour avec succès pour l'utilisateur ID: {}", user.getId());
            ServletUtil.setSuccessMessage(session, "Profil mis à jour avec succès");
            response.sendRedirect(request.getContextPath() + "/profile");

        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du profil", e);
            request.setAttribute("errors", errors);
            request.setAttribute("errorMessage", "Erreur lors de la mise à jour: " + e.getMessage());
            ServletUtil.forward(request, response, "/profile");
        }
    }

    private void updateOrganisateurProfile(HttpServletRequest request, Organisateur organisateur,
                                          String nom, String email, String photoUrl) {
        organisateur.setNom(nom);
        organisateur.setEmail(email);
        
        if (photoUrl != null) {
            organisateur.setPhotoProfil(photoUrl);
        }

        // Informations spécifiques à l'organisateur
        String organisation = request.getParameter("organisation");
        String telephone = request.getParameter("telephone");
        String siteWeb = request.getParameter("siteWeb");
        String adresse = request.getParameter("adresse");
        String description = request.getParameter("description");

        if (organisation != null && !organisation.trim().isEmpty()) {
            organisateur.setOrganisation(organisation);
        }
        if (telephone != null && !telephone.trim().isEmpty()) {
            organisateur.setTelephone(telephone);
        }
        if (siteWeb != null && !siteWeb.trim().isEmpty()) {
            organisateur.setSiteWeb(siteWeb);
        }
        if (adresse != null && !adresse.trim().isEmpty()) {
            organisateur.setAdresse(adresse);
        }
        if (description != null && !description.trim().isEmpty()) {
            organisateur.setDescription(description);
        }

        logger.debug("Profil organisateur mis à jour: {}", organisateur.getEmail());
    }

    private void updateParticipantProfile(HttpServletRequest request, Participant participant,
                                         String nom, String email, String photoUrl) {
        participant.setNom(nom);
        participant.setEmail(email);
        
        if (photoUrl != null) {
            participant.setPhotoProfil(photoUrl);
        }

        // Informations spécifiques au participant
        String telephone = request.getParameter("telephone");
        String dateNaissance = request.getParameter("dateNaissance");
        String ville = request.getParameter("ville");

        if (telephone != null && !telephone.trim().isEmpty()) {
            participant.setTelephone(telephone);
        }
        if (ville != null && !ville.trim().isEmpty()) {
            participant.setVille(ville);
        }

        logger.debug("Profil participant mis à jour: {}", participant.getEmail());
    }

    private String uploadProfilePhoto(Part filePart, HttpServletRequest request) {
        try {
            // Get filename from content-disposition header (Servlet 3.0 compatible)
            String fileName = getFileName(filePart);
            if (fileName == null || fileName.isEmpty()) {
                logger.warn("Nom de fichier vide");
                return null;
            }
            
            String fileExtension = fileName.substring(fileName.lastIndexOf("."));
            
            // Vérifier le type de fichier
            String contentType = filePart.getContentType();
            if (!contentType.startsWith("image/")) {
                logger.warn("Type de fichier invalide: {}", contentType);
                return null;
            }

            // Générer un nom unique
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            // Créer le répertoire s'il n'existe pas
            String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Sauvegarder le fichier
            String filePath = uploadPath + File.separator + uniqueFileName;
            filePart.write(filePath);

            logger.info("Photo de profil uploadée: {}", uniqueFileName);
            
            // Retourner l'URL relative
            return request.getContextPath() + "/" + UPLOAD_DIR + "/" + uniqueFileName;

        } catch (Exception e) {
            logger.error("Erreur lors de l'upload de la photo de profil", e);
            return null;
        }
    }
    
    /**
     * Extract filename from content-disposition header of file part (Servlet 3.0 compatible).
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
