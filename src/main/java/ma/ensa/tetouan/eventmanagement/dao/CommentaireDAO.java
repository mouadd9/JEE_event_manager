package ma.ensa.tetouan.eventmanagement.dao;

import ma.ensa.tetouan.eventmanagement.model.Commentaire;

import java.util.List;

/**
 * Interface DAO pour l'entité Commentaire.
 *
 * @author ENSA Tétouan
 */
public interface CommentaireDAO extends GenericDAO<Commentaire, Long> {

    /**
     * Recherche les commentaires d'un événement.
     *
     * @param evenementId L'ID de l'événement
     * @return Liste des commentaires
     */
    List<Commentaire> findByEvenement(Long evenementId);

    /**
     * Recherche les commentaires d'un participant.
     *
     * @param participantId L'ID du participant
     * @return Liste des commentaires
     */
    List<Commentaire> findByParticipant(Long participantId);

    /**
     * Recherche les commentaires non modérés.
     *
     * @return Liste des commentaires non modérés
     */
    List<Commentaire> findNonModeres();

    /**
     * Recherche les commentaires signalés.
     *
     * @return Liste des commentaires signalés
     */
    List<Commentaire> findSignales();

    /**
     * Recherche les commentaires visibles d'un événement.
     *
     * @param evenementId L'ID de l'événement
     * @return Liste des commentaires visibles
     */
    List<Commentaire> findVisibleByEvenement(Long evenementId);
}
