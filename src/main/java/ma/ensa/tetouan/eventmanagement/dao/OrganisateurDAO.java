package ma.ensa.tetouan.eventmanagement.dao;

import ma.ensa.tetouan.eventmanagement.model.Organisateur;

import java.util.List;
import java.util.Optional;

/**
 * Interface DAO pour l'entité Organisateur.
 *
 * @author ENSA Tétouan
 */
public interface OrganisateurDAO extends GenericDAO<Organisateur, Long> {

    /**
     * Recherche un organisateur par son organisation.
     *
     * @param organisation Le nom de l'organisation
     * @return Liste des organisateurs
     */
    List<Organisateur> findByOrganisation(String organisation);

    /**
     * Recherche un organisateur avec tous ses événements (eager loading).
     *
     * @param id L'ID de l'organisateur
     * @return Optional contenant l'organisateur avec ses événements
     */
    Optional<Organisateur> findWithEvents(Long id);

    /**
     * Recherche les organisateurs les plus actifs.
     *
     * @param limit Le nombre maximum de résultats
     * @return Liste des organisateurs triés par nombre d'événements
     */
    List<Organisateur> findMostActive(int limit);

    /**
     * Recherche les organisateurs en attente d'approbation par l'admin.
     *
     * @return Liste des organisateurs non approuvés
     */
    List<Organisateur> findPendingApproval();

    /**
     * Approuve un organisateur.
     *
     * @param id L'ID de l'organisateur
     */
    void approveOrganisateur(Long id);

    /**
     * Rejette un organisateur.
     *
     * @param id L'ID de l'organisateur
     */
    void rejectOrganisateur(Long id);
}
