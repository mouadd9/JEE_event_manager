package ma.ensa.tetouan.eventmanagement.dao;

import ma.ensa.tetouan.eventmanagement.model.Evaluation;
import ma.ensa.tetouan.eventmanagement.util.TransactionUtil;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation du DAO pour l'entité Evaluation.
 *
 * @author ENSA Tétouan
 */
public class EvaluationDAOImpl extends GenericDAOImpl<Evaluation, Long> implements EvaluationDAO {

    public EvaluationDAOImpl() {
        super(Evaluation.class);
    }

    @Override
    public List<Evaluation> findByEvenement(Long evenementId) {
        logger.debug("Recherche des évaluations pour l'événement ID: {}", evenementId);
        return TransactionUtil.executeInTransactionWithResult(em -> {
            return em.createNamedQuery("Evaluation.findByEvenement", Evaluation.class)
                    .setParameter("evenementId", evenementId)
                    .getResultList();
        });
    }

    @Override
    public List<Evaluation> findByParticipant(Long participantId) {
        logger.debug("Recherche des évaluations du participant ID: {}", participantId);
        return TransactionUtil.executeInTransactionWithResult(em -> {
            return em.createNamedQuery("Evaluation.findByParticipant", Evaluation.class)
                    .setParameter("participantId", participantId)
                    .getResultList();
        });
    }

    @Override
    public Double calculateAverageRating(Long evenementId) {
        logger.debug("Calcul de la note moyenne pour l'événement ID: {}", evenementId);
        return TransactionUtil.executeInTransactionWithResult(em -> {
            try {
                Double average = em.createNamedQuery("Evaluation.calculateAverage", Double.class)
                        .setParameter("evenementId", evenementId)
                        .getSingleResult();
                return average != null ? average : 0.0;
            } catch (NoResultException e) {
                return 0.0;
            }
        });
    }

    @Override
    public List<Evaluation> findVisibleByEvenement(Long evenementId) {
        logger.debug("Recherche des évaluations visibles pour l'événement ID: {}", evenementId);
        return TransactionUtil.executeInTransactionWithResult(em -> {
            TypedQuery<Evaluation> query = em.createQuery(
                "SELECT e FROM Evaluation e WHERE e.evenement.id = :eid AND e.visible = true ORDER BY e.horodatage DESC",
                Evaluation.class);
            query.setParameter("eid", evenementId);
            return query.getResultList();
        });
    }

    @Override
    public Optional<Evaluation> findByParticipantAndEvenement(Long participantId, Long evenementId) {
        logger.debug("Recherche de l'évaluation pour participant {} et événement {}", participantId, evenementId);
        return TransactionUtil.executeInTransactionWithResult(em -> {
            try {
                TypedQuery<Evaluation> query = em.createQuery(
                    "SELECT e FROM Evaluation e WHERE e.participant.id = :pid AND e.evenement.id = :eid",
                    Evaluation.class);
                query.setParameter("pid", participantId);
                query.setParameter("eid", evenementId);
                return Optional.of(query.getSingleResult());
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }
}
