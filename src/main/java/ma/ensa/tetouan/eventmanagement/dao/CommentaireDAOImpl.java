package ma.ensa.tetouan.eventmanagement.dao;

import ma.ensa.tetouan.eventmanagement.model.Commentaire;
import ma.ensa.tetouan.eventmanagement.util.TransactionUtil;

import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Implémentation du DAO pour l'entité Commentaire.
 *
 * @author ENSA Tétouan
 */
public class CommentaireDAOImpl extends GenericDAOImpl<Commentaire, Long> implements CommentaireDAO {

    public CommentaireDAOImpl() {
        super(Commentaire.class);
    }

    @Override
    public List<Commentaire> findByEvenement(Long evenementId) {
        logger.debug("Recherche des commentaires pour l'événement ID: {}", evenementId);
        return TransactionUtil.executeInTransactionWithResult(em -> {
            return em.createNamedQuery("Commentaire.findByEvenement", Commentaire.class)
                    .setParameter("evenementId", evenementId)
                    .getResultList();
        });
    }

    @Override
    public List<Commentaire> findByParticipant(Long participantId) {
        logger.debug("Recherche des commentaires du participant ID: {}", participantId);
        return TransactionUtil.executeInTransactionWithResult(em -> {
            return em.createNamedQuery("Commentaire.findByParticipant", Commentaire.class)
                    .setParameter("participantId", participantId)
                    .getResultList();
        });
    }

    @Override
    public List<Commentaire> findNonModeres() {
        logger.debug("Recherche des commentaires non modérés");
        return TransactionUtil.executeInTransactionWithResult(em -> {
            TypedQuery<Commentaire> query = em.createQuery(
                "SELECT c FROM Commentaire c WHERE c.modere = false ORDER BY c.dateCreation DESC",
                Commentaire.class);
            return query.getResultList();
        });
    }

    @Override
    public List<Commentaire> findSignales() {
        logger.debug("Recherche des commentaires signalés");
        return TransactionUtil.executeInTransactionWithResult(em -> {
            TypedQuery<Commentaire> query = em.createQuery(
                "SELECT c FROM Commentaire c WHERE c.signale = true ORDER BY c.nombreSignalements DESC, c.dateCreation DESC",
                Commentaire.class);
            return query.getResultList();
        });
    }

    @Override
    public List<Commentaire> findVisibleByEvenement(Long evenementId) {
        logger.debug("Recherche des commentaires visibles pour l'événement ID: {}", evenementId);
        return TransactionUtil.executeInTransactionWithResult(em -> {
            TypedQuery<Commentaire> query = em.createQuery(
                "SELECT c FROM Commentaire c WHERE c.evenement.id = :eid AND c.visible = true ORDER BY c.dateCreation DESC",
                Commentaire.class);
            query.setParameter("eid", evenementId);
            return query.getResultList();
        });
    }
}
