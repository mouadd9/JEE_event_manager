package ma.ensa.tetouan.eventmanagement.dao;

import ma.ensa.tetouan.eventmanagement.model.Participant;
import ma.ensa.tetouan.eventmanagement.util.TransactionUtil;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation du DAO pour l'entité Participant.
 *
 * @author ENSA Tétouan
 */
public class ParticipantDAOImpl extends GenericDAOImpl<Participant, Long> implements ParticipantDAO {

    public ParticipantDAOImpl() {
        super(Participant.class);
    }

    @Override
    public Optional<Participant> findWithInscriptions(Long id) {
        logger.debug("Recherche du participant ID {} avec ses inscriptions", id);

        return TransactionUtil.executeInTransactionWithResult(em -> {
            try {
                TypedQuery<Participant> query = em.createQuery(
                    "SELECT DISTINCT p FROM Participant p LEFT JOIN FETCH p.inscriptions WHERE p.id = :id",
                    Participant.class);
                query.setParameter("id", id);

                Participant participant = query.getSingleResult();
                logger.info("Participant ID {} trouvé avec {} inscription(s)",
                           id, participant.getInscriptions().size());
                return Optional.of(participant);
            } catch (NoResultException e) {
                logger.debug("Participant ID {} non trouvé", id);
                return Optional.empty();
            }
        });
    }

    @Override
    public List<Participant> findByPreferences(String preferences) {
        logger.debug("Recherche des participants par préférences: {}", preferences);

        return TransactionUtil.executeInTransactionWithResult(em -> {
            TypedQuery<Participant> query = em.createQuery(
                "SELECT p FROM Participant p WHERE LOWER(p.preferences) LIKE LOWER(:preferences)",
                Participant.class);
            query.setParameter("preferences", "%" + preferences + "%");

            List<Participant> participants = query.getResultList();
            logger.info("{} participant(s) trouvé(s) avec les préférences: {}",
                       participants.size(), preferences);
            return participants;
        });
    }

    @Override
    public List<Participant> findByVille(String ville) {
        logger.debug("Recherche des participants par ville: {}", ville);

        return TransactionUtil.executeInTransactionWithResult(em -> {
            TypedQuery<Participant> query = em.createQuery(
                "SELECT p FROM Participant p WHERE LOWER(p.ville) = LOWER(:ville)",
                Participant.class);
            query.setParameter("ville", ville);

            List<Participant> participants = query.getResultList();
            logger.info("{} participant(s) trouvé(s) dans la ville: {}", participants.size(), ville);
            return participants;
        });
    }
}
