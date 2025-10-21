package ma.ensa.tetouan.eventmanagement.dao;

import ma.ensa.tetouan.eventmanagement.model.Organisateur;
import ma.ensa.tetouan.eventmanagement.util.TransactionUtil;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation du DAO pour l'entité Organisateur.
 *
 * @author ENSA Tétouan
 */
public class OrganisateurDAOImpl extends GenericDAOImpl<Organisateur, Long> implements OrganisateurDAO {

    public OrganisateurDAOImpl() {
        super(Organisateur.class);
    }

    @Override
    public List<Organisateur> findByOrganisation(String organisation) {
        logger.debug("Recherche des organisateurs par organisation: {}", organisation);

        return TransactionUtil.executeInTransactionWithResult(em -> {
            TypedQuery<Organisateur> query = em.createQuery(
                "SELECT o FROM Organisateur o WHERE LOWER(o.organisation) LIKE LOWER(:organisation)",
                Organisateur.class);
            query.setParameter("organisation", "%" + organisation + "%");

            List<Organisateur> organisateurs = query.getResultList();
            logger.info("{} organisateur(s) trouvé(s) pour l'organisation: {}",
                       organisateurs.size(), organisation);
            return organisateurs;
        });
    }

    @Override
    public Optional<Organisateur> findWithEvents(Long id) {
        logger.debug("Recherche de l'organisateur ID {} avec ses événements", id);

        return TransactionUtil.executeInTransactionWithResult(em -> {
            try {
                TypedQuery<Organisateur> query = em.createQuery(
                    "SELECT DISTINCT o FROM Organisateur o LEFT JOIN FETCH o.evenements WHERE o.id = :id",
                    Organisateur.class);
                query.setParameter("id", id);

                Organisateur organisateur = query.getSingleResult();
                logger.info("Organisateur ID {} trouvé avec {} événement(s)",
                           id, organisateur.getEvenements().size());
                return Optional.of(organisateur);
            } catch (NoResultException e) {
                logger.debug("Organisateur ID {} non trouvé", id);
                return Optional.empty();
            }
        });
    }

    @Override
    public List<Organisateur> findMostActive(int limit) {
        logger.debug("Recherche des {} organisateurs les plus actifs", limit);

        return TransactionUtil.executeInTransactionWithResult(em -> {
            TypedQuery<Organisateur> query = em.createQuery(
                "SELECT o FROM Organisateur o ORDER BY o.nombreEvenementsOrganises DESC",
                Organisateur.class);
            query.setMaxResults(limit);

            List<Organisateur> organisateurs = query.getResultList();
            logger.info("{} organisateur(s) actif(s) trouvé(s)", organisateurs.size());
            return organisateurs;
        });
    }

    @Override
    public List<Organisateur> findPendingApproval() {
        logger.debug("Recherche des organisateurs en attente d'approbation");

        return TransactionUtil.executeInTransactionWithResult(em -> {
            TypedQuery<Organisateur> query = em.createQuery(
                "SELECT o FROM Organisateur o WHERE o.approved = false ORDER BY o.dateInscription DESC",
                Organisateur.class);

            List<Organisateur> organisateurs = query.getResultList();
            logger.info("{} organisateur(s) en attente d'approbation", organisateurs.size());
            return organisateurs;
        });
    }

    @Override
    public void approveOrganisateur(Long id) {
        logger.debug("Approbation de l'organisateur ID: {}", id);

        TransactionUtil.executeInTransaction(em -> {
            Organisateur organisateur = em.find(Organisateur.class, id);
            if (organisateur != null) {
                organisateur.setApproved(true);
                em.merge(organisateur);
                logger.info("Organisateur ID {} approuvé", id);
            } else {
                logger.warn("Organisateur ID {} non trouvé pour approbation", id);
            }
        });
    }

    @Override
    public void rejectOrganisateur(Long id) {
        logger.debug("Rejet de l'organisateur ID: {}", id);

        TransactionUtil.executeInTransaction(em -> {
            Organisateur organisateur = em.find(Organisateur.class, id);
            if (organisateur != null) {
                // For rejection, we delete the account
                em.remove(organisateur);
                logger.info("Organisateur ID {} rejeté et supprimé", id);
            } else {
                logger.warn("Organisateur ID {} non trouvé pour rejet", id);
            }
        });
    }
}
