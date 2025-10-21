package ma.ensa.tetouan.eventmanagement.dao;

import ma.ensa.tetouan.eventmanagement.model.Inscription;
import ma.ensa.tetouan.eventmanagement.model.StatutInscription;

import java.util.List;

/**
 * Interface DAO pour l'entité Inscription.
 *
 * @author ENSA Tétouan
 */
public interface InscriptionDAO extends GenericDAO<Inscription, Long> {

    /**
     * Recherche les inscriptions d'un participant.
     *
     * @param participantId L'ID du participant
     * @return Liste des inscriptions
     */
    List<Inscription> findByParticipant(Long participantId);

    /**
     * Recherche les inscriptions pour un événement.
     *
     * @param evenementId L'ID de l'événement
     * @return Liste des inscriptions
     */
    List<Inscription> findByEvenement(Long evenementId);

    /**
     * Recherche les inscriptions par statut.
     *
     * @param statut Le statut
     * @return Liste des inscriptions
     */
    List<Inscription> findByStatut(StatutInscription statut);

    /**
     * Vérifie si un participant est déjà inscrit à un événement.
     *
     * @param participantId L'ID du participant
     * @param evenementId L'ID de l'événement
     * @return true si déjà inscrit
     */
    boolean existsByParticipantAndEvenement(Long participantId, Long evenementId);

    /**
     * Compte le nombre d'inscriptions pour un événement.
     *
     * @param evenementId L'ID de l'événement
     * @return Le nombre d'inscriptions
     */
    long countByEvenement(Long evenementId);

    /**
     * Compte le nombre d'inscriptions acceptées pour un événement.
     *
     * @param evenementId L'ID de l'événement
     * @return Le nombre d'inscriptions acceptées
     */
    long countAcceptedByEvenement(Long evenementId);
}
