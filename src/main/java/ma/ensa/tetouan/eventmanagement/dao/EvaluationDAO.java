package ma.ensa.tetouan.eventmanagement.dao;

import ma.ensa.tetouan.eventmanagement.model.Evaluation;

import java.util.List;
import java.util.Optional;

/**
 * Interface DAO pour l'entité Evaluation.
 *
 * @author ENSA Tétouan
 */
public interface EvaluationDAO extends GenericDAO<Evaluation, Long> {

    /**
     * Recherche les évaluations d'un événement.
     *
     * @param evenementId L'ID de l'événement
     * @return Liste des évaluations
     */
    List<Evaluation> findByEvenement(Long evenementId);

    /**
     * Recherche les évaluations d'un participant.
     *
     * @param participantId L'ID du participant
     * @return Liste des évaluations
     */
    List<Evaluation> findByParticipant(Long participantId);

    /**
     * Calcule la note moyenne d'un événement.
     *
     * @param evenementId L'ID de l'événement
     * @return La note moyenne (ou 0 si aucune évaluation)
     */
    Double calculateAverageRating(Long evenementId);

    /**
     * Recherche les évaluations visibles d'un événement.
     *
     * @param evenementId L'ID de l'événement
     * @return Liste des évaluations visibles
     */
    List<Evaluation> findVisibleByEvenement(Long evenementId);

    /**
     * Recherche une évaluation par participant et événement.
     *
     * @param participantId L'ID du participant
     * @param evenementId L'ID de l'événement
     * @return Optional contenant l'évaluation si elle existe
     */
    Optional<Evaluation> findByParticipantAndEvenement(Long participantId, Long evenementId);
}
