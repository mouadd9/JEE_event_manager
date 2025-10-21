package ma.ensa.tetouan.eventmanagement.service;

import ma.ensa.tetouan.eventmanagement.model.Evenement;
import ma.ensa.tetouan.eventmanagement.model.Inscription;
import ma.ensa.tetouan.eventmanagement.model.Participant;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Interface du service de gestion des participants.
 * Étend les fonctionnalités de UserService pour les participants.
 *
 * @author ENSA Tétouan
 */
public interface ParticipantService {

    /**
     * Récupère un participant avec ses inscriptions (eager loading).
     *
     * @param participantId L'ID du participant
     * @return Optional contenant le participant avec ses inscriptions si trouvé
     */
    Optional<Participant> getParticipantWithInscriptions(Long participantId);

    /**
     * Met à jour les préférences d'un participant.
     *
     * @param participantId L'ID du participant
     * @param preferences Les préférences du participant
     * @return Le participant mis à jour
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si le participant n'existe pas
     */
    Participant updatePreferences(Long participantId, String preferences);

    /**
     * Récupère les événements à venir auxquels le participant est inscrit (ACCEPTEE).
     *
     * @param participantId L'ID du participant
     * @return La liste des événements à venir
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si le participant n'existe pas
     */
    List<Evenement> getMyUpcomingEvents(Long participantId);

    /**
     * Récupère les événements passés auxquels le participant a assisté (ACCEPTEE).
     *
     * @param participantId L'ID du participant
     * @return La liste des événements passés
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si le participant n'existe pas
     */
    List<Evenement> getMyPastEvents(Long participantId);

    /**
     * Récupère toutes les inscriptions d'un participant.
     *
     * @param participantId L'ID du participant
     * @return La liste des inscriptions
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si le participant n'existe pas
     */
    List<Inscription> getMyInscriptions(Long participantId);

    /**
     * Récupère les statistiques d'un participant.
     *
     * @param participantId L'ID du participant
     * @return Map contenant les statistiques
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si le participant n'existe pas
     */
    Map<String, Object> getParticipantStats(Long participantId);

    /**
     * Récupère un participant par son ID.
     *
     * @param participantId L'ID du participant
     * @return Le participant
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si le participant n'existe pas
     */
    Participant getParticipantById(Long participantId);

    /**
     * Récupère tous les participants avec pagination.
     *
     * @param page Le numéro de la page
     * @param pageSize La taille de la page
     * @return La liste des participants
     */
    List<Participant> getAllParticipants(int page, int pageSize);

    /**
     * Compte le nombre total de participants.
     *
     * @return Le nombre de participants
     */
    long getTotalParticipants();
}
