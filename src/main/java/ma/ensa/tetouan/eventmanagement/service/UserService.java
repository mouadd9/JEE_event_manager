package ma.ensa.tetouan.eventmanagement.service;

import ma.ensa.tetouan.eventmanagement.model.StatutUtilisateur;
import ma.ensa.tetouan.eventmanagement.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Interface du service de gestion des utilisateurs.
 *
 * @author ENSA Tétouan
 */
public interface UserService {

    /**
     * Enregistre un nouvel utilisateur dans le système.
     *
     * @param user L'utilisateur à enregistrer
     * @return L'utilisateur enregistré avec son ID
     * @throws ma.ensa.tetouan.eventmanagement.exception.BusinessException Si l'email existe déjà ou validation échoue
     */
    User register(User user);

    /**
     * Authentifie un utilisateur avec son email et mot de passe.
     *
     * @param email L'email de l'utilisateur
     * @param password Le mot de passe en clair
     * @return L'utilisateur authentifié
     * @throws ma.ensa.tetouan.eventmanagement.exception.AuthenticationException Si l'authentification échoue
     */
    User authenticate(String email, String password);

    /**
     * Met à jour le profil d'un utilisateur.
     *
     * @param userId L'ID de l'utilisateur
     * @param updatedData Les données mises à jour
     * @return L'utilisateur mis à jour
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si l'utilisateur n'existe pas
     */
    User updateProfile(Long userId, User updatedData);

    /**
     * Change le mot de passe d'un utilisateur.
     *
     * @param userId L'ID de l'utilisateur
     * @param oldPassword L'ancien mot de passe
     * @param newPassword Le nouveau mot de passe
     * @throws ma.ensa.tetouan.eventmanagement.exception.AuthenticationException Si l'ancien mot de passe est incorrect
     * @throws ma.ensa.tetouan.eventmanagement.exception.BusinessException Si le nouveau mot de passe est faible
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * Désactive le compte d'un utilisateur.
     *
     * @param userId L'ID de l'utilisateur
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si l'utilisateur n'existe pas
     */
    void deactivateAccount(Long userId);

    /**
     * Récupère tous les utilisateurs avec pagination.
     *
     * @param page Le numéro de la page
     * @param pageSize La taille de la page
     * @return La liste des utilisateurs
     */
    List<User> getAllUsers(int page, int pageSize);

    /**
     * Récupère un utilisateur par son ID.
     *
     * @param userId L'ID de l'utilisateur
     * @return L'utilisateur
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si l'utilisateur n'existe pas
     */
    User getUserById(Long userId);

    /**
     * Récupère un utilisateur par son email.
     *
     * @param email L'email de l'utilisateur
     * @return Optional contenant l'utilisateur si trouvé
     */
    Optional<User> getUserByEmail(String email);

    /**
     * Compte le nombre total d'utilisateurs.
     *
     * @return Le nombre d'utilisateurs
     */
    long getTotalUsers();

    /**
     * Récupère les utilisateurs par statut.
     *
     * @param statut Le statut
     * @return La liste des utilisateurs
     */
    List<User> getUsersByStatut(StatutUtilisateur statut);

    /**
     * Change le statut d'un utilisateur.
     *
     * @param userId L'ID de l'utilisateur
     * @param statut Le nouveau statut
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si l'utilisateur n'existe pas
     */
    void changeStatut(Long userId, StatutUtilisateur statut);

    /**
     * Finds a user by email address.
     *
     * @param email The email address
     * @return The user if found, null otherwise
     */
    User findByEmail(String email);

    /**
     * Updates a user entity.
     *
     * @param user The user to update
     * @return The updated user
     * @throws ma.ensa.tetouan.eventmanagement.exception.BusinessException If update fails
     */
    User update(User user);
}
