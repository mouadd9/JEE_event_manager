package ma.ensa.tetouan.eventmanagement.dao;

import ma.ensa.tetouan.eventmanagement.model.StatutUtilisateur;
import ma.ensa.tetouan.eventmanagement.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Interface DAO pour l'entité User et ses sous-classes.
 *
 * @author ENSA Tétouan
 */
public interface UserDAO extends GenericDAO<User, Long> {

    /**
     * Recherche un utilisateur par son email.
     *
     * @param email L'email de l'utilisateur
     * @return Optional contenant l'utilisateur si trouvé
     */
    Optional<User> findByEmail(String email);

    /**
     * Recherche tous les utilisateurs par statut.
     *
     * @param statut Le statut des utilisateurs
     * @return Liste des utilisateurs avec le statut donné
     */
    List<User> findByStatut(StatutUtilisateur statut);

    /**
     * Authentifie un utilisateur avec son email et mot de passe.
     *
     * @param email L'email de l'utilisateur
     * @param motDePasse Le mot de passe (en clair)
     * @return Optional contenant l'utilisateur si l'authentification réussit
     */
    Optional<User> authenticate(String email, String motDePasse);

    /**
     * Vérifie si un email existe déjà dans la base de données.
     *
     * @param email L'email à vérifier
     * @return true si l'email existe, false sinon
     */
    boolean existsByEmail(String email);

    /**
     * Recherche tous les utilisateurs par rôle (type).
     *
     * @param role Le rôle (ORGANISATEUR, PARTICIPANT, ADMINISTRATEUR)
     * @return Liste des utilisateurs avec le rôle donné
     */
    List<User> findByRole(String role);

    /**
     * Met à jour la dernière connexion d'un utilisateur.
     *
     * @param userId L'ID de l'utilisateur
     */
    void updateLastLogin(Long userId);

    /**
     * Change le statut d'un utilisateur.
     *
     * @param userId L'ID de l'utilisateur
     * @param statut Le nouveau statut
     */
    void changeStatut(Long userId, StatutUtilisateur statut);

    /**
     * Compte le nombre d'utilisateurs par statut.
     *
     * @param statut Le statut à compter
     * @return Le nombre d'utilisateurs
     */
    long countByStatut(StatutUtilisateur statut);
}
