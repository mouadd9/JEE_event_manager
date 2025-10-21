package ma.ensa.tetouan.eventmanagement.service;

import ma.ensa.tetouan.eventmanagement.model.Evaluation;

import java.util.List;
import java.util.Optional;

/**
 * Interface du service de gestion des évaluations avec validation de participation.
 *
 * @author ENSA Tétouan
 */
public interface EvaluationService {

    /**
     * Ajoute une évaluation à un événement.
     * Valide que le participant a assisté à l'événement et que l'événement est terminé.
     *
     * @param participantId L'ID du participant
     * @param evenementId L'ID de l'événement
     * @param note La note (1-5)
     * @param texte Le texte de l'évaluation (optionnel)
     * @return L'évaluation créée
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si participant ou événement n'existe pas
     * @throws ma.ensa.tetouan.eventmanagement.exception.BusinessException Si validation échoue ou déjà évalué
     * @throws ma.ensa.tetouan.eventmanagement.exception.InvalidEventStateException Si événement pas TERMINE
     */
    Evaluation addEvaluation(Long participantId, Long evenementId, int note, String texte);

    /**
     * Met à jour une évaluation existante.
     *
     * @param evaluationId L'ID de l'évaluation
     * @param participantId L'ID du participant (pour vérification)
     * @param note La nouvelle note
     * @param texte Le nouveau texte
     * @return L'évaluation mise à jour
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si évaluation n'existe pas
     * @throws ma.ensa.tetouan.eventmanagement.exception.BusinessException Si pas propriétaire
     */
    Evaluation updateEvaluation(Long evaluationId, Long participantId, int note, String texte);

    /**
     * Supprime une évaluation.
     *
     * @param evaluationId L'ID de l'évaluation
     * @param participantId L'ID du participant (pour vérification)
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si évaluation n'existe pas
     * @throws ma.ensa.tetouan.eventmanagement.exception.BusinessException Si pas propriétaire
     */
    void deleteEvaluation(Long evaluationId, Long participantId);

    /**
     * Récupère les évaluations visibles d'un événement.
     *
     * @param evenementId L'ID de l'événement
     * @return La liste des évaluations visibles
     */
    List<Evaluation> getEvaluationsByEvenement(Long evenementId);

    /**
     * Récupère toutes les évaluations d'un participant.
     *
     * @param participantId L'ID du participant
     * @return La liste des évaluations
     */
    List<Evaluation> getEvaluationsByParticipant(Long participantId);

    /**
     * Calcule la note moyenne d'un événement.
     *
     * @param evenementId L'ID de l'événement
     * @return La note moyenne, ou null si aucune évaluation
     */
    Double getAverageRating(Long evenementId);

    /**
     * Vérifie si un participant peut évaluer un événement.
     *
     * @param participantId L'ID du participant
     * @param evenementId L'ID de l'événement
     * @return true si l'évaluation est possible, false sinon
     */
    boolean canEvaluate(Long participantId, Long evenementId);

    /**
     * Récupère l'évaluation d'un participant pour un événement.
     *
     * @param participantId L'ID du participant
     * @param evenementId L'ID de l'événement
     * @return Optional contenant l'évaluation si elle existe
     */
    Optional<Evaluation> getEvaluationByParticipantAndEvent(Long participantId, Long evenementId);

    /**
     * Récupère une évaluation par son ID.
     *
     * @param evaluationId L'ID de l'évaluation
     * @return L'évaluation
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si non trouvée
     */
    Evaluation getEvaluationById(Long evaluationId);
}
