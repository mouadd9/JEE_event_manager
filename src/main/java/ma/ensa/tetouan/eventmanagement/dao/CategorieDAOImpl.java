package ma.ensa.tetouan.eventmanagement.dao;

import ma.ensa.tetouan.eventmanagement.model.Categorie;
import ma.ensa.tetouan.eventmanagement.util.TransactionUtil;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation du DAO pour l'entité Categorie.
 *
 * @author ENSA Tétouan
 */
public class CategorieDAOImpl extends GenericDAOImpl<Categorie, Long> implements CategorieDAO {

    public CategorieDAOImpl() {
        super(Categorie.class);
    }

    @Override
    public Optional<Categorie> findByNom(String nom) {
        logger.debug("Recherche de la catégorie par nom: {}", nom);
        return TransactionUtil.executeInTransactionWithResult(em -> {
            try {
                TypedQuery<Categorie> query = em.createQuery(
                    "SELECT c FROM Categorie c WHERE LOWER(c.nom) = LOWER(:nom)", Categorie.class);
                query.setParameter("nom", nom);
                return Optional.of(query.getSingleResult());
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public List<Categorie> findAllActive() {
        logger.debug("Recherche de toutes les catégories actives");
        return TransactionUtil.executeInTransactionWithResult(em -> {
            TypedQuery<Categorie> query = em.createQuery(
                "SELECT c FROM Categorie c WHERE c.active = true ORDER BY c.nom", Categorie.class);
            return query.getResultList();
        });
    }

    @Override
    public List<Object[]> findAllWithEventCount() {
        logger.debug("Recherche des catégories avec le nombre d'événements");
        return TransactionUtil.executeInTransactionWithResult(em -> {
            TypedQuery<Object[]> query = em.createQuery(
                "SELECT c, COUNT(e) FROM Categorie c LEFT JOIN c.evenements e GROUP BY c ORDER BY c.nom",
                Object[].class);
            return query.getResultList();
        });
    }
}
