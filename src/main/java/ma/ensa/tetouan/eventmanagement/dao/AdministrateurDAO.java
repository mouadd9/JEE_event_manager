package ma.ensa.tetouan.eventmanagement.dao;

import ma.ensa.tetouan.eventmanagement.model.Administrateur;

import java.util.List;

/**
 * Interface DAO pour l'entité Administrateur.
 *
 * @author ENSA Tétouan
 */
public interface AdministrateurDAO extends GenericDAO<Administrateur, Long> {

    /**
     * Recherche les administrateurs par niveau d'accès.
     *
     * @param niveauAcces Le niveau d'accès (1-3)
     * @return Liste des administrateurs
     */
    List<Administrateur> findByNiveauAcces(int niveauAcces);

    /**
     * Recherche tous les super administrateurs (niveau 3).
     *
     * @return Liste des super administrateurs
     */
    List<Administrateur> findSuperAdmins();
}
