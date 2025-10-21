package ma.ensa.tetouan.eventmanagement.service;

import ma.ensa.tetouan.eventmanagement.model.Commentaire;

import java.util.List;

/**
 * Interface du service de gestion des commentaires avec modération.
 *
 * @author ENSA Tétouan
 */
public interface CommentaireService {

    /**
     * Ajoute un commentaire à un événement.
     *
     * @param participantId L'ID du participant
     * @param evenementId L'ID de l'événement
     * @param texte Le texte du commentaire
     * @return Le commentaire créé
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si participant ou événement n'existe pas
     * @throws ma.ensa.tetouan.eventmanagement.exception.BusinessException Si validation échoue
     * @throws ma.ensa.tetouan.eventmanagement.exception.InvalidEventStateException Si événement pas PUBLIE ou TERMINE
     */
    Commentaire addCommentaire(Long participantId, Long evenementId, String texte);

    /**
     * Met à jour un commentaire existant.
     *
     * @param commentaireId L'ID du commentaire
     * @param participantId L'ID du participant (pour vérification)
     * @param texte Le nouveau texte
     * @return Le commentaire mis à jour
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si commentaire n'existe pas
     * @throws ma.ensa.tetouan.eventmanagement.exception.BusinessException Si pas propriétaire ou modéré négativement
     */
    Commentaire updateCommentaire(Long commentaireId, Long participantId, String texte);

    /**
     * Supprime un commentaire.
     *
     * @param commentaireId L'ID du commentaire
     * @param participantId L'ID du participant (pour vérification)
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si commentaire n'existe pas
     * @throws ma.ensa.tetouan.eventmanagement.exception.BusinessException Si pas propriétaire
     */
    void deleteCommentaire(Long commentaireId, Long participantId);

    /**
     * Signale un commentaire inapproprié.
     * Auto-cache le commentaire après 3 signalements.
     *
     * @param commentaireId L'ID du commentaire
     * @return Le commentaire mis à jour
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si commentaire n'existe pas
     */
    Commentaire reportCommentaire(Long commentaireId);

    /**
     * Modère un commentaire (admin uniquement).
     *
     * @param commentaireId L'ID du commentaire
     * @param approved true pour approuver, false pour rejeter
     * @return Le commentaire modéré
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si commentaire n'existe pas
     */
    Commentaire moderateCommentaire(Long commentaireId, boolean approved);

    /**
     * Récupère les commentaires visibles d'un événement.
     *
     * @param evenementId L'ID de l'événement
     * @return La liste des commentaires visibles
     */
    List<Commentaire> getCommentairesByEvenement(Long evenementId);

    /**
     * Récupère tous les commentaires d'un participant.
     *
     * @param participantId L'ID du participant
     * @return La liste des commentaires
     */
    List<Commentaire> getCommentairesByParticipant(Long participantId);

    /**
     * Récupère les commentaires non modérés (pour admin).
     *
     * @return La liste des commentaires non modérés
     */
    List<Commentaire> getUnmoderatedComments();

    /**
     * Récupère les commentaires signalés (pour admin).
     *
     * @return La liste des commentaires signalés
     */
    List<Commentaire> getReportedComments();

    /**
     * Récupère un commentaire par son ID.
     *
     * @param commentaireId L'ID du commentaire
     * @return Le commentaire
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si non trouvé
     */
    Commentaire getCommentaireById(Long commentaireId);
}
