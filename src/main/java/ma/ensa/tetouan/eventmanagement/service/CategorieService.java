package ma.ensa.tetouan.eventmanagement.service;

import ma.ensa.tetouan.eventmanagement.model.Categorie;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Interface du service de gestion des catégories.
 *
 * @author ENSA Tétouan
 */
public interface CategorieService {

    /**
     * Crée une nouvelle catégorie.
     *
     * @param categorie La catégorie à créer
     * @return La catégorie créée
     * @throws ma.ensa.tetouan.eventmanagement.exception.BusinessException Si validation échoue ou nom existe déjà
     */
    Categorie createCategorie(Categorie categorie);

    /**
     * Met à jour une catégorie existante.
     *
     * @param categorieId L'ID de la catégorie
     * @param updatedData Les données mises à jour
     * @return La catégorie mise à jour
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si la catégorie n'existe pas
     * @throws ma.ensa.tetouan.eventmanagement.exception.BusinessException Si validation échoue ou nom existe déjà
     */
    Categorie updateCategorie(Long categorieId, Categorie updatedData);

    /**
     * Supprime une catégorie (seulement si aucun événement ne l'utilise).
     *
     * @param categorieId L'ID de la catégorie
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si la catégorie n'existe pas
     * @throws ma.ensa.tetouan.eventmanagement.exception.BusinessException Si la catégorie a des événements associés
     */
    void deleteCategorie(Long categorieId);

    /**
     * Récupère toutes les catégories.
     *
     * @return La liste de toutes les catégories
     */
    List<Categorie> getAllCategories();

    /**
     * Récupère une catégorie par son ID.
     *
     * @param categorieId L'ID de la catégorie
     * @return La catégorie
     * @throws ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException Si la catégorie n'existe pas
     */
    Categorie getCategorieById(Long categorieId);

    /**
     * Récupère une catégorie par son nom.
     *
     * @param nom Le nom de la catégorie
     * @return Optional contenant la catégorie si trouvée
     */
    Optional<Categorie> getCategorieByNom(String nom);

    /**
     * Récupère toutes les catégories avec le nombre d'événements pour chacune.
     *
     * @return Map associant chaque catégorie à son nombre d'événements
     */
    Map<Categorie, Long> getCategoriesWithEventCount();
}
