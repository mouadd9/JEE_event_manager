package ma.ensa.tetouan.eventmanagement.dao;

import ma.ensa.tetouan.eventmanagement.model.Administrateur;
import ma.ensa.tetouan.eventmanagement.util.TransactionUtil;

import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Implémentation du DAO pour l'entité Administrateur.
 *
 * @author ENSA Tétouan
 */
public class AdministrateurDAOImpl extends GenericDAOImpl<Administrateur, Long> implements AdministrateurDAO {

    public AdministrateurDAOImpl() {
        super(Administrateur.class);
    }

    @Override
    public List<Administrateur> findByNiveauAcces(int niveauAcces) {
        logger.debug("Recherche des administrateurs par niveau d'accès: {}", niveauAcces);

        return TransactionUtil.executeInTransactionWithResult(em -> {
            TypedQuery<Administrateur> query = em.createQuery(
                "SELECT a FROM Administrateur a WHERE a.niveauAcces = :niveau",
                Administrateur.class);
            query.setParameter("niveau", niveauAcces);

            List<Administrateur> admins = query.getResultList();
            logger.info("{} administrateur(s) trouvé(s) avec niveau d'accès {}",
                       admins.size(), niveauAcces);
            return admins;
        });
    }

    @Override
    public List<Administrateur> findSuperAdmins() {
        logger.debug("Recherche des super administrateurs");
        return findByNiveauAcces(3);
    }
}
