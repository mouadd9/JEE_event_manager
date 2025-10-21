package ma.ensa.tetouan.eventmanagement.service;

import ma.ensa.tetouan.eventmanagement.dao.UserDAO;
import ma.ensa.tetouan.eventmanagement.dao.UserDAOImpl;
import ma.ensa.tetouan.eventmanagement.exception.AuthenticationException;
import ma.ensa.tetouan.eventmanagement.exception.BusinessException;
import ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException;
import ma.ensa.tetouan.eventmanagement.model.StatutUtilisateur;
import ma.ensa.tetouan.eventmanagement.model.User;
import ma.ensa.tetouan.eventmanagement.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Implémentation du service de gestion des utilisateurs.
 *
 * @author ENSA Tétouan
 */
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private final UserDAO userDAO;

    /**
     * Constructeur avec injection du DAO
     */
    public UserServiceImpl() {
        this.userDAO = new UserDAOImpl();
    }

    /**
     * Constructeur pour les tests (injection de dépendance)
     */
    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public User register(User user) {
        logger.info("Tentative d'enregistrement d'un nouvel utilisateur: {}", user.getEmail());

        // 1. Validation des données
        validateUserForRegistration(user);

        // 2. Vérifier l'unicité de l'email
        if (userDAO.existsByEmail(user.getEmail())) {
            logger.warn("Tentative d'enregistrement avec un email existant: {}", user.getEmail());
            throw new BusinessException("Un utilisateur avec cet email existe déjà");
        }

        // 3. Valider la force du mot de passe
        if (!PasswordUtil.isStrongPassword(user.getMotDePasse())) {
            throw new BusinessException("Le mot de passe doit contenir au moins 8 caractères, " +
                "incluant majuscules, minuscules, chiffres et caractères spéciaux");
        }

        // 4. Hacher le mot de passe
        String hashedPassword = PasswordUtil.hashPassword(user.getMotDePasse());
        user.setMotDePasse(hashedPassword);

        // 5. Définir les valeurs par défaut
        user.setStatut(StatutUtilisateur.ACTIF);
        user.setDateInscription(LocalDateTime.now());

        // 6. Sauvegarder l'utilisateur
        try {
            User savedUser = userDAO.save(user);
            logger.info("Utilisateur enregistré avec succès: ID={}, Email={}",
                       savedUser.getId(), savedUser.getEmail());
            return savedUser;
        } catch (Exception e) {
            logger.error("Erreur lors de l'enregistrement de l'utilisateur", e);
            throw new BusinessException("Erreur lors de l'enregistrement de l'utilisateur", e);
        }
    }

    @Override
    public User authenticate(String email, String password) {
        logger.debug("Tentative d'authentification pour: {}", email);

        // 1. Validation des paramètres
        if (email == null || email.trim().isEmpty()) {
            throw new AuthenticationException("L'email est obligatoire");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new AuthenticationException("Le mot de passe est obligatoire");
        }

        // 2. Rechercher l'utilisateur par email
        Optional<User> userOpt = userDAO.findByEmail(email);
        if (!userOpt.isPresent()) {
            logger.warn("Tentative d'authentification avec un email inexistant: {}", email);
            throw new AuthenticationException("Email ou mot de passe incorrect");
        }

        User user = userOpt.get();

        // 3. Vérifier le statut de l'utilisateur
        if (user.getStatut() != StatutUtilisateur.ACTIF) {
            logger.warn("Tentative d'authentification avec un compte {} pour: {}",
                       user.getStatut(), email);
            throw new AuthenticationException("Votre compte est " + user.getStatut().getLibelle().toLowerCase());
        }

        // 4. Vérifier le mot de passe
        if (!PasswordUtil.verifyPassword(password, user.getMotDePasse())) {
            logger.warn("Tentative d'authentification avec un mot de passe incorrect pour: {}", email);
            throw new AuthenticationException("Email ou mot de passe incorrect");
        }

        // 5. Mettre à jour la dernière connexion
        try {
            userDAO.updateLastLogin(user.getId());
        } catch (Exception e) {
            logger.warn("Erreur lors de la mise à jour de la dernière connexion", e);
            // Ne pas échouer l'authentification pour cette erreur
        }

        logger.info("Authentification réussie pour: {} (ID={})", email, user.getId());
        return user;
    }

    @Override
    public User updateProfile(Long userId, User updatedData) {
        logger.info("Mise à jour du profil utilisateur ID: {}", userId);

        // 1. Vérifier que l'utilisateur existe
        User existingUser = getUserById(userId);

        // 2. Valider les nouvelles données
        if (updatedData == null) {
            throw new IllegalArgumentException("Les données de mise à jour ne peuvent pas être null");
        }

        // 3. Mettre à jour les champs autorisés
        if (updatedData.getNom() != null && !updatedData.getNom().trim().isEmpty()) {
            existingUser.setNom(updatedData.getNom());
        }

        if (updatedData.getEmail() != null && !updatedData.getEmail().equals(existingUser.getEmail())) {
            // Vérifier l'unicité du nouvel email
            if (userDAO.existsByEmail(updatedData.getEmail())) {
                throw new BusinessException("Cet email est déjà utilisé par un autre utilisateur");
            }
            validateEmail(updatedData.getEmail());
            existingUser.setEmail(updatedData.getEmail());
        }

        if (updatedData.getPhotoProfil() != null) {
            existingUser.setPhotoProfil(updatedData.getPhotoProfil());
        }

        // 4. Mettre à jour les champs spécifiques selon le type d'utilisateur
        if (updatedData instanceof ma.ensa.tetouan.eventmanagement.model.Organisateur && 
            existingUser instanceof ma.ensa.tetouan.eventmanagement.model.Organisateur) {
            
            ma.ensa.tetouan.eventmanagement.model.Organisateur updatedOrg = 
                (ma.ensa.tetouan.eventmanagement.model.Organisateur) updatedData;
            ma.ensa.tetouan.eventmanagement.model.Organisateur existingOrg = 
                (ma.ensa.tetouan.eventmanagement.model.Organisateur) existingUser;
            
            if (updatedOrg.getOrganisation() != null) {
                existingOrg.setOrganisation(updatedOrg.getOrganisation());
            }
            if (updatedOrg.getTelephone() != null) {
                existingOrg.setTelephone(updatedOrg.getTelephone());
            }
            if (updatedOrg.getSiteWeb() != null) {
                existingOrg.setSiteWeb(updatedOrg.getSiteWeb());
            }
            if (updatedOrg.getAdresse() != null) {
                existingOrg.setAdresse(updatedOrg.getAdresse());
            }
            if (updatedOrg.getDescription() != null) {
                existingOrg.setDescription(updatedOrg.getDescription());
            }
        } else if (updatedData instanceof ma.ensa.tetouan.eventmanagement.model.Participant && 
                   existingUser instanceof ma.ensa.tetouan.eventmanagement.model.Participant) {
            
            ma.ensa.tetouan.eventmanagement.model.Participant updatedPart = 
                (ma.ensa.tetouan.eventmanagement.model.Participant) updatedData;
            ma.ensa.tetouan.eventmanagement.model.Participant existingPart = 
                (ma.ensa.tetouan.eventmanagement.model.Participant) existingUser;
            
            if (updatedPart.getTelephone() != null) {
                existingPart.setTelephone(updatedPart.getTelephone());
            }
            if (updatedPart.getVille() != null) {
                existingPart.setVille(updatedPart.getVille());
            }
            if (updatedPart.getDateNaissance() != null) {
                existingPart.setDateNaissance(updatedPart.getDateNaissance());
            }
        }

        // 5. Sauvegarder les modifications
        try {
            User updated = userDAO.update(existingUser);
            logger.info("Profil utilisateur mis à jour avec succès: ID={}", userId);
            return updated;
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du profil", e);
            throw new BusinessException("Erreur lors de la mise à jour du profil", e);
        }
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        logger.info("Tentative de changement de mot de passe pour l'utilisateur ID: {}", userId);

        // 1. Récupérer l'utilisateur
        User user = getUserById(userId);

        // 2. Vérifier l'ancien mot de passe
        if (!PasswordUtil.verifyPassword(oldPassword, user.getMotDePasse())) {
            logger.warn("Ancien mot de passe incorrect pour l'utilisateur ID: {}", userId);
            throw new AuthenticationException("L'ancien mot de passe est incorrect");
        }

        // 3. Valider le nouveau mot de passe
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new BusinessException("Le nouveau mot de passe est obligatoire");
        }

        if (!PasswordUtil.isStrongPassword(newPassword)) {
            throw new BusinessException("Le nouveau mot de passe doit contenir au moins 8 caractères, " +
                "incluant majuscules, minuscules, chiffres et caractères spéciaux");
        }

        // 4. Hacher et sauvegarder le nouveau mot de passe
        String hashedPassword = PasswordUtil.hashPassword(newPassword);
        user.setMotDePasse(hashedPassword);

        try {
            userDAO.update(user);
            logger.info("Mot de passe changé avec succès pour l'utilisateur ID: {}", userId);
        } catch (Exception e) {
            logger.error("Erreur lors du changement de mot de passe", e);
            throw new BusinessException("Erreur lors du changement de mot de passe", e);
        }
    }

    @Override
    public void deactivateAccount(Long userId) {
        logger.info("Désactivation du compte utilisateur ID: {}", userId);

        // 1. Vérifier que l'utilisateur existe
        User user = getUserById(userId);

        // 2. Changer le statut
        user.setStatut(StatutUtilisateur.INACTIF);

        try {
            userDAO.update(user);
            logger.info("Compte utilisateur désactivé avec succès: ID={}", userId);
        } catch (Exception e) {
            logger.error("Erreur lors de la désactivation du compte", e);
            throw new BusinessException("Erreur lors de la désactivation du compte", e);
        }
    }

    @Override
    public List<User> getAllUsers(int page, int pageSize) {
        logger.debug("Récupération de tous les utilisateurs - page: {}, taille: {}", page, pageSize);

        try {
            return userDAO.findAll(page, pageSize);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des utilisateurs", e);
            throw new BusinessException("Erreur lors de la récupération des utilisateurs", e);
        }
    }

    @Override
    public User getUserById(Long userId) {
        logger.debug("Récupération de l'utilisateur ID: {}", userId);

        if (userId == null) {
            throw new IllegalArgumentException("L'ID utilisateur ne peut pas être null");
        }

        Optional<User> userOpt = userDAO.findById(userId);
        if (!userOpt.isPresent()) {
            logger.warn("Utilisateur non trouvé: ID={}", userId);
            throw new ResourceNotFoundException("Utilisateur", userId);
        }

        return userOpt.get();
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        logger.debug("Récupération de l'utilisateur par email: {}", email);

        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }

        try {
            return userDAO.findByEmail(email);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de l'utilisateur par email", e);
            throw new BusinessException("Erreur lors de la récupération de l'utilisateur", e);
        }
    }

    @Override
    public long getTotalUsers() {
        try {
            return userDAO.count();
        } catch (Exception e) {
            logger.error("Erreur lors du comptage des utilisateurs", e);
            throw new BusinessException("Erreur lors du comptage des utilisateurs", e);
        }
    }

    @Override
    public List<User> getUsersByStatut(StatutUtilisateur statut) {
        logger.debug("Récupération des utilisateurs par statut: {}", statut);

        if (statut == null) {
            throw new IllegalArgumentException("Le statut ne peut pas être null");
        }

        try {
            return userDAO.findByStatut(statut);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des utilisateurs par statut", e);
            throw new BusinessException("Erreur lors de la récupération des utilisateurs", e);
        }
    }

    @Override
    public void changeStatut(Long userId, StatutUtilisateur statut) {
        logger.info("Changement de statut pour l'utilisateur ID: {} vers {}", userId, statut);

        if (statut == null) {
            throw new IllegalArgumentException("Le statut ne peut pas être null");
        }

        // Vérifier que l'utilisateur existe
        getUserById(userId);

        try {
            userDAO.changeStatut(userId, statut);
            logger.info("Statut changé avec succès pour l'utilisateur ID: {}", userId);
        } catch (Exception e) {
            logger.error("Erreur lors du changement de statut", e);
            throw new BusinessException("Erreur lors du changement de statut", e);
        }
    }

    /**
     * Valide les données d'un utilisateur pour l'enregistrement.
     *
     * @param user L'utilisateur à valider
     * @throws BusinessException Si la validation échoue
     */
    private void validateUserForRegistration(User user) {
        if (user == null) {
            throw new IllegalArgumentException("L'utilisateur ne peut pas être null");
        }

        // Valider le nom
        if (user.getNom() == null || user.getNom().trim().isEmpty()) {
            throw new BusinessException("Le nom est obligatoire");
        }
        if (user.getNom().length() < 2 || user.getNom().length() > 100) {
            throw new BusinessException("Le nom doit contenir entre 2 et 100 caractères");
        }

        // Valider l'email
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new BusinessException("L'email est obligatoire");
        }
        validateEmail(user.getEmail());

        // Valider le mot de passe
        if (user.getMotDePasse() == null || user.getMotDePasse().trim().isEmpty()) {
            throw new BusinessException("Le mot de passe est obligatoire");
        }
    }

    /**
     * Valide le format d'un email.
     *
     * @param email L'email à valider
     * @throws BusinessException Si le format est invalide
     */
    private void validateEmail(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessException("Le format de l'email est invalide");
        }
    }

    @Override
    public User findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        Optional<User> user = getUserByEmail(email);
        return user.orElse(null);
    }

    @Override
    public User update(User user) {
        if (user == null || user.getId() == null) {
            throw new BusinessException("L'utilisateur ou son ID ne peut pas être null");
        }

        try {
            return userDAO.update(user);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de l'utilisateur: " + user.getEmail(), e);
            throw new BusinessException("Erreur lors de la mise à jour de l'utilisateur", e);
        }
    }
}

