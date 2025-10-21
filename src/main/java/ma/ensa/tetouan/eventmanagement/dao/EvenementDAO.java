package ma.ensa.tetouan.eventmanagement.dao;

import ma.ensa.tetouan.eventmanagement.model.Evenement;
import ma.ensa.tetouan.eventmanagement.model.StatutEvenement;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface DAO pour l'entité Evenement.
 *
 * @author ENSA Tétouan
 */
public interface EvenementDAO extends GenericDAO<Evenement, Long> {

    /**
     * Recherche les événements par statut.
     *
     * @param statut Le statut des événements
     * @return Liste des événements
     */
    List<Evenement> findByStatut(StatutEvenement statut);

    /**
     * Recherche les événements d'un organisateur.
     *
     * @param organisateurId L'ID de l'organisateur
     * @return Liste des événements
     */
    List<Evenement> findByOrganisateur(Long organisateurId);

    /**
     * Recherche les événements par catégorie.
     *
     * @param categorieId L'ID de la catégorie
     * @return Liste des événements
     */
    List<Evenement> findByCategorie(Long categorieId);

    /**
     * Recherche les événements publiés.
     *
     * @return Liste des événements publiés
     */
    List<Evenement> findPublishedEvents();

    /**
     * Recherche les événements à venir (publiés et non encore commencés).
     *
     * @return Liste des événements à venir
     */
    List<Evenement> findUpcomingEvents();

    /**
     * Recherche les événements par lieu.
     *
     * @param location Le lieu
     * @return Liste des événements
     */
    List<Evenement> findEventsByLocation(String location);

    /**
     * Recherche des événements par mot-clé et dates.
     *
     * @param keyword Mot-clé (titre ou description)
     * @param startDate Date de début (optionnel)
     * @param endDate Date de fin (optionnel)
     * @return Liste des événements correspondants
     */
    List<Evenement> searchEvents(String keyword, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Recherche les événements les plus populaires (par nombre de vues).
     *
     * @param limit Nombre maximum de résultats
     * @return Liste des événements les plus populaires
     */
    List<Evenement> findMostPopular(int limit);

    /**
     * Recherche les événements les mieux notés.
     *
     * @param limit Nombre maximum de résultats
     * @return Liste des événements les mieux notés
     */
    List<Evenement> findTopRated(int limit);

    /**
     * Compte le nombre d'événements par statut.
     *
     * @param statut Le statut des événements
     * @return Le nombre d'événements
     */
    long countByStatut(StatutEvenement statut);
}
