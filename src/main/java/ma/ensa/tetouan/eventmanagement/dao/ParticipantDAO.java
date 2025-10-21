package ma.ensa.tetouan.eventmanagement.dao;

import ma.ensa.tetouan.eventmanagement.model.Participant;

import java.util.List;
import java.util.Optional;

/**
 * Interface DAO pour l'entité Participant.
 *
 * @author ENSA Tétouan
 */
public interface ParticipantDAO extends GenericDAO<Participant, Long> {

    /**
     * Recherche un participant avec toutes ses inscriptions (eager loading).
     *
     * @param id L'ID du participant
     * @return Optional contenant le participant avec ses inscriptions
     */
    Optional<Participant> findWithInscriptions(Long id);

    /**
     * Recherche des participants par leurs préférences.
     *
     * @param preferences Les préférences à rechercher
     * @return Liste des participants correspondants
     */
    List<Participant> findByPreferences(String preferences);

    /**
     * Recherche des participants par ville.
     *
     * @param ville La ville
     * @return Liste des participants de cette ville
     */
    List<Participant> findByVille(String ville);
}
