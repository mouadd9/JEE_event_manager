package ma.ensa.tetouan.eventmanagement.dao;

import ma.ensa.tetouan.eventmanagement.model.Inscription;
import ma.ensa.tetouan.eventmanagement.model.StatutInscription;
import ma.ensa.tetouan.eventmanagement.util.TransactionUtil;

import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Implémentation du DAO pour l'entité Inscription.
 *
 * @author ENSA Tétouan
 */
public class InscriptionDAOImpl extends GenericDAOImpl<Inscription, Long> implements InscriptionDAO {

    public InscriptionDAOImpl() {
        super(Inscription.class);
    }

    @Override
    public List<Inscription> findByParticipant(Long participantId) {
        logger.debug("Recherche des inscriptions pour le participant ID: {}", participantId);
        return TransactionUtil.executeInTransactionWithResult(em -> {
            TypedQuery<Inscription> query = em.createQuery(
                "SELECT DISTINCT i FROM Inscription i " +
                "LEFT JOIN FETCH i.evenement e " +
                "LEFT JOIN FETCH e.categories " +
                "LEFT JOIN FETCH e.organisateur " +
                "WHERE i.participant.id = :participantId " +
                "ORDER BY i.dateInscription DESC",
                Inscription.class);
            query.setParameter("participantId", participantId);
            return query.getResultList();
        });
    }

    @Override
    public List<Inscription> findByEvenement(Long evenementId) {
        logger.debug("Recherche des inscriptions pour l'événement ID: {}", evenementId);
        return TransactionUtil.executeInTransactionWithResult(em -> {
            return em.createNamedQuery("Inscription.findByEvenement", Inscription.class)
                    .setParameter("evenementId", evenementId)
                    .getResultList();
        });
    }

    @Override
    public List<Inscription> findByStatut(StatutInscription statut) {
        logger.debug("Recherche des inscriptions par statut: {}", statut);
        return TransactionUtil.executeInTransactionWithResult(em -> {
            return em.createNamedQuery("Inscription.findByStatut", Inscription.class)
                    .setParameter("statut", statut)
                    .getResultList();
        });
    }

    @Override
    public boolean existsByParticipantAndEvenement(Long participantId, Long evenementId) {
        logger.debug("Vérification de l'existence d'une inscription pour participant {} et événement {}",
                    participantId, evenementId);
        return TransactionUtil.executeInTransactionWithResult(em -> {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(i) FROM Inscription i WHERE i.participant.id = :pid AND i.evenement.id = :eid",
                Long.class);
            query.setParameter("pid", participantId);
            query.setParameter("eid", evenementId);
            return query.getSingleResult() > 0;
        });
    }

    @Override
    public long countByEvenement(Long evenementId) {
        logger.debug("Comptage des inscriptions pour l'événement ID: {}", evenementId);
        return TransactionUtil.executeInTransactionWithResult(em -> {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(i) FROM Inscription i WHERE i.evenement.id = :eid", Long.class);
            query.setParameter("eid", evenementId);
            return query.getSingleResult();
        });
    }

    @Override
    public long countAcceptedByEvenement(Long evenementId) {
        logger.debug("Comptage des inscriptions acceptées pour l'événement ID: {}", evenementId);
        return TransactionUtil.executeInTransactionWithResult(em -> {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(i) FROM Inscription i WHERE i.evenement.id = :eid AND i.statut = :statut",
                Long.class);
            query.setParameter("eid", evenementId);
            query.setParameter("statut", StatutInscription.ACCEPTEE);
            return query.getSingleResult();
        });
    }
}
