package ma.ensa.tetouan.eventmanagement.service;

import ma.ensa.tetouan.eventmanagement.model.Inscription;

import java.util.List;

/**
 * Interface du service de gestion des inscriptions.
 *
 * @author ENSA Tétouan
 */
public interface InscriptionService {

    /**
     * Inscrit un participant à un événement avec gestion automatique de la capacité.
     * - Si places disponibles : statut ACCEPTEE
     * - Si complet : statut EN_ATTENTE (liste d'attente)
     *
     * @param participantId L'ID du participant
     * @param evenementId L'ID de l'événement
     * @return L'inscription créée
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si participant ou événement n'existe pas
     * @throws ma.ensa.tetouan.eventmanagement.exception.InvalidEventStateException Si événement pas PUBLIE
     * @throws ma.ensa.tetouan.eventmanagement.exception.DuplicateRegistrationException Si déjà inscrit
     * @throws ma.ensa.tetouan.eventmanagement.exception.BusinessException Si événement passé
     */
    Inscription registerToEvent(Long participantId, Long evenementId);

    /**
     * Accepte une inscription en attente (uniquement par l'organisateur).
     *
     * @param inscriptionId L'ID de l'inscription
     * @param organisateurId L'ID de l'organisateur
     * @return L'inscription acceptée
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si inscription n'existe pas
     * @throws ma.ensa.tetouan.eventmanagement.exception.BusinessException Si pas organisateur ou pas assez de places
     * @throws ma.ensa.tetouan.eventmanagement.exception.InvalidEventStateException Si pas EN_ATTENTE
     */
    Inscription acceptInscription(Long inscriptionId, Long organisateurId);

    /**
     * Refuse une inscription en attente (uniquement par l'organisateur).
     *
     * @param inscriptionId L'ID de l'inscription
     * @param organisateurId L'ID de l'organisateur
     * @return L'inscription refusée
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si inscription n'existe pas
     * @throws ma.ensa.tetouan.eventmanagement.exception.BusinessException Si pas organisateur
     * @throws ma.ensa.tetouan.eventmanagement.exception.InvalidEventStateException Si pas EN_ATTENTE
     */
    Inscription refuseInscription(Long inscriptionId, Long organisateurId);

    /**
     * Annule une inscription (par le participant).
     * Si ACCEPTEE, libère une place et accepte automatiquement le premier en attente.
     *
     * @param inscriptionId L'ID de l'inscription
     * @param participantId L'ID du participant
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si inscription n'existe pas
     * @throws ma.ensa.tetouan.eventmanagement.exception.BusinessException Si pas le participant propriétaire
     * @throws ma.ensa.tetouan.eventmanagement.exception.InvalidEventStateException Si déjà annulée ou refusée
     */
    void cancelInscription(Long inscriptionId, Long participantId);

    /**
     * Accepte automatiquement le premier participant en attente.
     * Appelé après une annulation ou une augmentation de capacité.
     *
     * @param evenementId L'ID de l'événement
     * @return L'inscription acceptée depuis la liste d'attente, ou null si aucune
     */
    Inscription acceptNextInWaitlist(Long evenementId);

    /**
     * Récupère toutes les inscriptions d'un participant.
     *
     * @param participantId L'ID du participant
     * @return La liste des inscriptions
     */
    List<Inscription> getInscriptionsByParticipant(Long participantId);

    /**
     * Récupère toutes les inscriptions d'un événement.
     *
     * @param evenementId L'ID de l'événement
     * @return La liste des inscriptions
     */
    List<Inscription> getInscriptionsByEvenement(Long evenementId);

    /**
     * Récupère les participants en liste d'attente pour un événement.
     *
     * @param evenementId L'ID de l'événement
     * @return La liste des inscriptions EN_ATTENTE triée par date
     */
    List<Inscription> getWaitlistedParticipants(Long evenementId);

    /**
     * Récupère les participants acceptés pour un événement.
     *
     * @param evenementId L'ID de l'événement
     * @return La liste des inscriptions ACCEPTEE
     */
    List<Inscription> getAcceptedParticipants(Long evenementId);

    /**
     * Calcule le nombre de places disponibles pour un événement.
     *
     * @param evenementId L'ID de l'événement
     * @return Le nombre de places disponibles
     */
    int getAvailableSeats(Long evenementId);

    /**
     * Vérifie si un événement est complet (plus de places).
     *
     * @param evenementId L'ID de l'événement
     * @return true si complet, false sinon
     */
    boolean isEventFull(Long evenementId);

    /**
     * Vérifie si un participant peut s'inscrire à un événement.
     * Vérifie : pas déjà inscrit, événement PUBLIE, date non passée.
     *
     * @param participantId L'ID du participant
     * @param evenementId L'ID de l'événement
     * @return true si inscription possible, false sinon
     */
    boolean canRegister(Long participantId, Long evenementId);

    /**
     * Récupère une inscription par son ID.
     *
     * @param inscriptionId L'ID de l'inscription
     * @return L'inscription
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si non trouvée
     */
    Inscription getInscriptionById(Long inscriptionId);

    /**
     * Compte le nombre total d'inscriptions pour un événement.
     *
     * @param evenementId L'ID de l'événement
     * @return Le nombre d'inscriptions
     */
    long getTotalInscriptions(Long evenementId);

    /**
     * Compte le nombre d'inscriptions acceptées pour un événement.
     *
     * @param evenementId L'ID de l'événement
     * @return Le nombre d'inscriptions ACCEPTEE
     */
    long getAcceptedInscriptionsCount(Long evenementId);
}
