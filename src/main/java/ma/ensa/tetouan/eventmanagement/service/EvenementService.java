package ma.ensa.tetouan.eventmanagement.service;

import ma.ensa.tetouan.eventmanagement.model.Evenement;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface du service de gestion des événements.
 *
 * @author ENSA Tétouan
 */
public interface EvenementService {

    /**
     * Crée un nouvel événement en mode brouillon.
     *
     * @param evenement L'événement à créer
     * @param organisateurId L'ID de l'organisateur
     * @return L'événement créé
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si l'organisateur n'existe pas
     * @throws ma.ensa.tetouan.eventmanagement.exception.BusinessException Si la validation échoue
     */
    Evenement createEvenement(Evenement evenement, Long organisateurId);

    /**
     * Met à jour un événement existant.
     *
     * @param evenementId L'ID de l'événement
     * @param updatedData Les données mises à jour
     * @param organisateurId L'ID de l'organisateur (pour vérification)
     * @return L'événement mis à jour
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si l'événement n'existe pas
     * @throws ma.ensa.tetouan.eventmanagement.exception.BusinessException Si pas propriétaire ou validation échoue
     * @throws ma.ensa.tetouan.eventmanagement.exception.InvalidEventStateException Si l'événement est terminé/annulé
     */
    Evenement updateEvenement(Long evenementId, Evenement updatedData, Long organisateurId);

    /**
     * Supprime un événement (seulement si en mode brouillon).
     *
     * @param evenementId L'ID de l'événement
     * @param organisateurId L'ID de l'organisateur
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si l'événement n'existe pas
     * @throws ma.ensa.tetouan.eventmanagement.exception.BusinessException Si pas propriétaire
     * @throws ma.ensa.tetouan.eventmanagement.exception.InvalidEventStateException Si déjà publié
     */
    void deleteEvenement(Long evenementId, Long organisateurId);

    /**
     * Publie un événement (BROUILLON → PUBLIE).
     *
     * @param evenementId L'ID de l'événement
     * @param organisateurId L'ID de l'organisateur
     * @return L'événement publié
     * @throws ma.ensa.tetouan.eventmanagement.exception.InvalidEventStateException Si pas en mode brouillon
     */
    Evenement publishEvenement(Long evenementId, Long organisateurId);

    /**
     * Annule un événement (PUBLIE → ANNULE).
     *
     * @param evenementId L'ID de l'événement
     * @param organisateurId L'ID de l'organisateur
     * @param raison La raison de l'annulation
     * @return L'événement annulé
     * @throws ma.ensa.tetouan.eventmanagement.exception.InvalidEventStateException Si pas publié
     */
    Evenement annulerEvenement(Long evenementId, Long organisateurId, String raison);

    /**
     * Termine un événement automatiquement (PUBLIE → TERMINE).
     *
     * @param evenementId L'ID de l'événement
     * @return L'événement terminé
     */
    Evenement terminerEvenement(Long evenementId);

    /**
     * Récupère un événement par son ID.
     *
     * @param evenementId L'ID de l'événement
     * @return L'événement
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si non trouvé
     */
    Evenement getEvenementById(Long evenementId);

    /**
     * Récupère tous les événements publiés avec pagination.
     *
     * @param page Le numéro de la page
     * @param pageSize La taille de la page
     * @return La liste des événements publiés
     */
    List<Evenement> getAllPublishedEvents(int page, int pageSize);

    /**
     * Recherche des événements selon des critères.
     *
     * @param keyword Mot-clé (titre/description)
     * @param startDate Date de début (optionnel)
     * @param endDate Date de fin (optionnel)
     * @param categorieId ID de la catégorie (optionnel)
     * @return La liste des événements correspondants
     */
    List<Evenement> searchEvenements(String keyword, LocalDate startDate, LocalDate endDate, Long categorieId);

    /**
     * Récupère les événements à venir.
     *
     * @param page Le numéro de la page
     * @param pageSize La taille de la page
     * @return La liste des événements à venir
     */
    List<Evenement> getUpcomingEvents(int page, int pageSize);

    /**
     * Récupère les événements d'un organisateur.
     *
     * @param organisateurId L'ID de l'organisateur
     * @return La liste des événements
     */
    List<Evenement> getEvenementsByOrganisateur(Long organisateurId);

    /**
     * Récupère les événements d'une catégorie.
     *
     * @param categorieId L'ID de la catégorie
     * @return La liste des événements
     */
    List<Evenement> getEvenementsByCategorie(Long categorieId);

    /**
     * Récupère les événements les plus populaires.
     *
     * @param limit Le nombre maximum de résultats
     * @return La liste des événements populaires
     */
    List<Evenement> getMostPopularEvents(int limit);

    /**
     * Incrémente le nombre de vues d'un événement.
     *
     * @param evenementId L'ID de l'événement
     */
    void incrementViews(Long evenementId);

    /**
     * Compte le nombre total d'événements publiés.
     *
     * @return Le nombre d'événements
     */
    long getTotalPublishedEvents();
}
