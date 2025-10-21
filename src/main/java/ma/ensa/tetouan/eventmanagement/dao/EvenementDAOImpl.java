package ma.ensa.tetouan.eventmanagement.dao;

import ma.ensa.tetouan.eventmanagement.model.Evenement;
import ma.ensa.tetouan.eventmanagement.model.StatutEvenement;
import ma.ensa.tetouan.eventmanagement.util.TransactionUtil;

import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation du DAO pour l'entité Evenement.
 *
 * @author ENSA Tétouan
 */
public class EvenementDAOImpl extends GenericDAOImpl<Evenement, Long> implements EvenementDAO {

    public EvenementDAOImpl() {
        super(Evenement.class);
    }

    @Override
    public Optional<Evenement> findById(Long id) {
        logger.debug("Recherche de l'événement avec ID: {} avec chargement eager des catégories et organisateur", id);
        return TransactionUtil.executeInTransactionWithResult(em -> {
            TypedQuery<Evenement> query = em.createQuery(
                "SELECT DISTINCT e FROM Evenement e LEFT JOIN FETCH e.categories LEFT JOIN FETCH e.organisateur WHERE e.id = :id",
                Evenement.class);
            query.setParameter("id", id);
            List<Evenement> results = query.getResultList();
            if (!results.isEmpty()) {
                logger.debug("Événement avec ID {} trouvé avec catégories et organisateur chargés", id);
                return Optional.of(results.get(0));
            } else {
                logger.debug("Événement avec ID {} non trouvé", id);
                return Optional.empty();
            }
        });
    }

    @Override
    public List<Evenement> findByStatut(StatutEvenement statut) {
        logger.debug("Recherche des événements par statut: {}", statut);
        return TransactionUtil.executeInTransactionWithResult(em -> {
            TypedQuery<Evenement> query = em.createQuery(
                "SELECT DISTINCT e FROM Evenement e LEFT JOIN FETCH e.categories LEFT JOIN FETCH e.organisateur WHERE e.statut = :statut ORDER BY e.dateDebut DESC",
                Evenement.class);
            query.setParameter("statut", statut);
            return query.getResultList();
        });
    }

    @Override
    public List<Evenement> findByOrganisateur(Long organisateurId) {
        logger.debug("Recherche des événements pour l'organisateur ID: {}", organisateurId);
        return TransactionUtil.executeInTransactionWithResult(em -> {
            TypedQuery<Evenement> query = em.createQuery(
                "SELECT DISTINCT e FROM Evenement e LEFT JOIN FETCH e.categories LEFT JOIN FETCH e.organisateur WHERE e.organisateur.id = :orgId ORDER BY e.dateDebut DESC",
                Evenement.class);
            query.setParameter("orgId", organisateurId);
            return query.getResultList();
        });
    }

    @Override
    public List<Evenement> findByCategorie(Long categorieId) {
        logger.debug("Recherche des événements par catégorie ID: {}", categorieId);
        return TransactionUtil.executeInTransactionWithResult(em -> {
            TypedQuery<Evenement> query = em.createQuery(
                "SELECT DISTINCT e FROM Evenement e LEFT JOIN FETCH e.categories LEFT JOIN FETCH e.organisateur JOIN e.categories c WHERE c.id = :catId AND e.statut = :statut ORDER BY e.dateDebut DESC",
                Evenement.class);
            query.setParameter("catId", categorieId);
            query.setParameter("statut", StatutEvenement.PUBLIE);
            return query.getResultList();
        });
    }

    @Override
    public List<Evenement> findPublishedEvents() {
        logger.debug("Recherche des événements publiés");
        return TransactionUtil.executeInTransactionWithResult(em -> {
            return em.createNamedQuery("Evenement.findPublished", Evenement.class)
                    .setParameter("now", LocalDateTime.now())
                    .getResultList();
        });
    }

    @Override
    public List<Evenement> findUpcomingEvents() {
        logger.debug("Recherche des événements à venir");
        return TransactionUtil.executeInTransactionWithResult(em -> {
            TypedQuery<Evenement> query = em.createQuery(
                "SELECT DISTINCT e FROM Evenement e LEFT JOIN FETCH e.categories LEFT JOIN FETCH e.organisateur WHERE e.statut = :statut AND e.dateDebut > :now ORDER BY e.dateDebut ASC",
                Evenement.class);
            query.setParameter("statut", StatutEvenement.PUBLIE);
            query.setParameter("now", LocalDateTime.now());
            return query.getResultList();
        });
    }

    @Override
    public List<Evenement> findEventsByLocation(String location) {
        logger.debug("Recherche des événements par lieu: {}", location);
        return TransactionUtil.executeInTransactionWithResult(em -> {
            TypedQuery<Evenement> query = em.createQuery(
                "SELECT e FROM Evenement e WHERE LOWER(e.lieu) LIKE LOWER(:location) ORDER BY e.dateDebut DESC",
                Evenement.class);
            query.setParameter("location", "%" + location + "%");
            return query.getResultList();
        });
    }

    @Override
    public List<Evenement> searchEvents(String keyword, LocalDateTime startDate, LocalDateTime endDate) {
        logger.debug("Recherche des événements avec mot-clé: {}", keyword);
        return TransactionUtil.executeInTransactionWithResult(em -> {
            StringBuilder jpql = new StringBuilder(
                "SELECT DISTINCT e FROM Evenement e LEFT JOIN FETCH e.categories LEFT JOIN FETCH e.organisateur WHERE e.statut = :statut");

            if (keyword != null && !keyword.trim().isEmpty()) {
                jpql.append(" AND (LOWER(e.titre) LIKE LOWER(:keyword) OR LOWER(e.description) LIKE LOWER(:keyword))");
            }
            if (startDate != null) {
                jpql.append(" AND e.dateDebut >= :startDate");
            }
            if (endDate != null) {
                jpql.append(" AND e.dateFin <= :endDate");
            }
            jpql.append(" ORDER BY e.dateDebut DESC");

            TypedQuery<Evenement> query = em.createQuery(jpql.toString(), Evenement.class);
            query.setParameter("statut", StatutEvenement.PUBLIE);

            if (keyword != null && !keyword.trim().isEmpty()) {
                query.setParameter("keyword", "%" + keyword + "%");
            }
            if (startDate != null) {
                query.setParameter("startDate", startDate);
            }
            if (endDate != null) {
                query.setParameter("endDate", endDate);
            }

            return query.getResultList();
        });
    }

    @Override
    public List<Evenement> findMostPopular(int limit) {
        logger.debug("Recherche des {} événements les plus populaires", limit);
        return TransactionUtil.executeInTransactionWithResult(em -> {
            TypedQuery<Evenement> query = em.createQuery(
                "SELECT DISTINCT e FROM Evenement e LEFT JOIN FETCH e.categories LEFT JOIN FETCH e.organisateur WHERE e.statut = :statut ORDER BY e.nombreVues DESC",
                Evenement.class);
            query.setParameter("statut", StatutEvenement.PUBLIE);
            query.setMaxResults(limit);
            return query.getResultList();
        });
    }

    @Override
    public List<Evenement> findTopRated(int limit) {
        logger.debug("Recherche des {} événements les mieux notés", limit);
        return TransactionUtil.executeInTransactionWithResult(em -> {
            TypedQuery<Evenement> query = em.createQuery(
                "SELECT e FROM Evenement e WHERE e.statut = :statut AND e.noteMoyenne > 0 ORDER BY e.noteMoyenne DESC",
                Evenement.class);
            query.setParameter("statut", StatutEvenement.PUBLIE);
            query.setMaxResults(limit);
            return query.getResultList();
        });
    }

    @Override
    public long countByStatut(StatutEvenement statut) {
        logger.debug("Comptage des événements par statut: {}", statut);
        return TransactionUtil.executeInTransactionWithResult(em -> {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(e) FROM Evenement e WHERE e.statut = :statut",
                Long.class);
            query.setParameter("statut", statut);
            return query.getSingleResult();
        });
    }
}
