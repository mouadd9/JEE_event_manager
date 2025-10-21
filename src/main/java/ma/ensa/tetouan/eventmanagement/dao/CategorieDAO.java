package ma.ensa.tetouan.eventmanagement.dao;

import ma.ensa.tetouan.eventmanagement.model.Categorie;

import java.util.List;
import java.util.Optional;

/**
 * Interface DAO pour l'entité Categorie.
 *
 * @author ENSA Tétouan
 */
public interface CategorieDAO extends GenericDAO<Categorie, Long> {

    /**
     * Recherche une catégorie par son nom.
     *
     * @param nom Le nom de la catégorie
     * @return Optional contenant la catégorie
     */
    Optional<Categorie> findByNom(String nom);

    /**
     * Recherche toutes les catégories actives.
     *
     * @return Liste des catégories actives
     */
    List<Categorie> findAllActive();

    /**
     * Recherche les catégories avec le nombre d'événements associés.
     *
     * @return Liste des catégories avec compteur
     */
    List<Object[]> findAllWithEventCount();
}
