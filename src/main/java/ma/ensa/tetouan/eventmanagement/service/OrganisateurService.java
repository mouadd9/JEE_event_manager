package ma.ensa.tetouan.eventmanagement.service;

import ma.ensa.tetouan.eventmanagement.model.Evenement;
import ma.ensa.tetouan.eventmanagement.model.Organisateur;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Interface du service de gestion des organisateurs.
 * Étend les fonctionnalités de UserService pour les organisateurs.
 *
 * @author ENSA Tétouan
 */
public interface OrganisateurService {

    /**
     * Récupère un organisateur avec ses événements (eager loading).
     *
     * @param organisateurId L'ID de l'organisateur
     * @return Optional contenant l'organisateur avec ses événements si trouvé
     */
    Optional<Organisateur> getOrganisateurWithEvents(Long organisateurId);

    /**
     * Met à jour les informations d'organisation d'un organisateur.
     *
     * @param organisateurId L'ID de l'organisateur
     * @param organisation Le nom de l'organisation
     * @param telephone Le numéro de téléphone
     * @return L'organisateur mis à jour
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si l'organisateur n'existe pas
     * @throws ma.ensa.tetouan.eventmanagement.exception.BusinessException Si validation échoue
     */
    Organisateur updateOrganisation(Long organisateurId, String organisation, String telephone);

    /**
     * Récupère les organisateurs les plus actifs.
     *
     * @param limit Le nombre maximum de résultats
     * @return La liste des organisateurs les plus actifs
     */
    List<Organisateur> getTopOrganisateurs(int limit);

    /**
     * Récupère tous les événements d'un organisateur.
     *
     * @param organisateurId L'ID de l'organisateur
     * @return La liste des événements
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si l'organisateur n'existe pas
     */
    List<Evenement> getEventsByOrganisateur(Long organisateurId);

    /**
     * Récupère les statistiques d'un organisateur.
     *
     * @param organisateurId L'ID de l'organisateur
     * @return Map contenant les statistiques
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si l'organisateur n'existe pas
     */
    Map<String, Object> getOrganisateurStats(Long organisateurId);

    /**
     * Récupère un organisateur par son ID.
     *
     * @param organisateurId L'ID de l'organisateur
     * @return L'organisateur
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si l'organisateur n'existe pas
     */
    Organisateur getOrganisateurById(Long organisateurId);

    /**
     * Récupère tous les organisateurs avec pagination.
     *
     * @param page Le numéro de la page
     * @param pageSize La taille de la page
     * @return La liste des organisateurs
     */
    List<Organisateur> getAllOrganisateurs(int page, int pageSize);

    /**
     * Compte le nombre total d'organisateurs.
     *
     * @return Le nombre d'organisateurs
     */
    long getTotalOrganisateurs();
}
