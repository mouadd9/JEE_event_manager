package ma.ensa.tetouan.eventmanagement.service;

import ma.ensa.tetouan.eventmanagement.dao.CategorieDAO;
import ma.ensa.tetouan.eventmanagement.dao.CategorieDAOImpl;
import ma.ensa.tetouan.eventmanagement.dao.EvenementDAO;
import ma.ensa.tetouan.eventmanagement.dao.EvenementDAOImpl;
import ma.ensa.tetouan.eventmanagement.exception.BusinessException;
import ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException;
import ma.ensa.tetouan.eventmanagement.model.Categorie;
import ma.ensa.tetouan.eventmanagement.model.Evenement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Implémentation du service de gestion des catégories.
 *
 * @author ENSA Tétouan
 */
public class CategorieServiceImpl implements CategorieService {

    private static final Logger logger = LoggerFactory.getLogger(CategorieServiceImpl.class);

    private final CategorieDAO categorieDAO;
    private final EvenementDAO evenementDAO;

    /**
     * Constructeur avec injection des DAOs.
     */
    public CategorieServiceImpl() {
        this.categorieDAO = new CategorieDAOImpl();
        this.evenementDAO = new EvenementDAOImpl();
    }

    /**
     * Constructeur pour les tests (injection de dépendance).
     */
    public CategorieServiceImpl(CategorieDAO categorieDAO, EvenementDAO evenementDAO) {
        this.categorieDAO = categorieDAO;
        this.evenementDAO = evenementDAO;
    }

    @Override
    public Categorie createCategorie(Categorie categorie) {
        logger.info("Tentative de création d'une nouvelle catégorie: {}",
                   categorie != null ? categorie.getNom() : "null");

        // 1. Validation des données
        validateCategorie(categorie);

        // 2. Vérifier l'unicité du nom
        Optional<Categorie> existingCategorie = categorieDAO.findByNom(categorie.getNom());
        if (existingCategorie.isPresent()) {
            logger.warn("Tentative de création d'une catégorie avec un nom existant: {}", categorie.getNom());
            throw new BusinessException("Une catégorie avec ce nom existe déjà");
        }

        // 3. Définir les valeurs par défaut
        if (categorie.getActive() == null) {
            categorie.setActive(true);
        }

        // 4. Sauvegarder la catégorie
        try {
            Categorie savedCategorie = categorieDAO.save(categorie);
            logger.info("Catégorie créée avec succès: ID={}, Nom={}",
                       savedCategorie.getId(), savedCategorie.getNom());
            return savedCategorie;
        } catch (Exception e) {
            logger.error("Erreur lors de la création de la catégorie", e);
            throw new BusinessException("Erreur lors de la création de la catégorie", e);
        }
    }

    @Override
    public Categorie updateCategorie(Long categorieId, Categorie updatedData) {
        logger.info("Tentative de mise à jour de la catégorie ID: {}", categorieId);

        // 1. Vérifier que la catégorie existe
        Categorie existingCategorie = getCategorieById(categorieId);

        // 2. Valider les nouvelles données
        if (updatedData == null) {
            throw new IllegalArgumentException("Les données de mise à jour ne peuvent pas être null");
        }

        // 3. Mettre à jour les champs autorisés
        boolean updated = false;

        if (updatedData.getNom() != null && !updatedData.getNom().equals(existingCategorie.getNom())) {
            // Valider le nouveau nom
            validateNom(updatedData.getNom());

            // Vérifier l'unicité du nouveau nom
            Optional<Categorie> duplicateCategorie = categorieDAO.findByNom(updatedData.getNom());
            if (duplicateCategorie.isPresent() && !duplicateCategorie.get().getId().equals(categorieId)) {
                throw new BusinessException("Une catégorie avec ce nom existe déjà");
            }

            existingCategorie.setNom(updatedData.getNom());
            updated = true;
        }

        if (updatedData.getDescription() != null) {
            existingCategorie.setDescription(updatedData.getDescription());
            updated = true;
        }

        if (updatedData.getCouleur() != null) {
            existingCategorie.setCouleur(updatedData.getCouleur());
            updated = true;
        }

        if (updatedData.getIcone() != null) {
            existingCategorie.setIcone(updatedData.getIcone());
            updated = true;
        }

        if (updatedData.getActive() != null) {
            existingCategorie.setActive(updatedData.getActive());
            updated = true;
        }

        // 4. Sauvegarder les modifications si des changements ont été effectués
        if (!updated) {
            logger.info("Aucune modification à appliquer pour la catégorie ID: {}", categorieId);
            return existingCategorie;
        }

        try {
            Categorie savedCategorie = categorieDAO.update(existingCategorie);
            logger.info("Catégorie mise à jour avec succès: ID={}", categorieId);
            return savedCategorie;
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de la catégorie", e);
            throw new BusinessException("Erreur lors de la mise à jour de la catégorie", e);
        }
    }

    @Override
    public void deleteCategorie(Long categorieId) {
        logger.info("Tentative de suppression de la catégorie ID: {}", categorieId);

        // 1. Vérifier que la catégorie existe
        Categorie categorie = getCategorieById(categorieId);

        // 2. Vérifier qu'aucun événement n'utilise cette catégorie
        List<Evenement> evenements = evenementDAO.findByCategorie(categorieId);
        if (!evenements.isEmpty()) {
            logger.warn("Tentative de suppression d'une catégorie avec {} événements associés",
                       evenements.size());
            throw new BusinessException(
                String.format("Impossible de supprimer cette catégorie car elle est utilisée par %d événement(s)",
                             evenements.size())
            );
        }

        // 3. Supprimer la catégorie
        try {
            categorieDAO.delete(categorie);
            logger.info("Catégorie supprimée avec succès: ID={}", categorieId);
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de la catégorie", e);
            throw new BusinessException("Erreur lors de la suppression de la catégorie", e);
        }
    }

    @Override
    public List<Categorie> getAllCategories() {
        logger.debug("Récupération de toutes les catégories");

        try {
            List<Categorie> categories = categorieDAO.findAll();
            logger.debug("{} catégories récupérées", categories.size());
            return categories;
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des catégories", e);
            throw new BusinessException("Erreur lors de la récupération des catégories", e);
        }
    }

    @Override
    public Categorie getCategorieById(Long categorieId) {
        logger.debug("Récupération de la catégorie ID: {}", categorieId);

        if (categorieId == null) {
            throw new IllegalArgumentException("L'ID de la catégorie ne peut pas être null");
        }

        Optional<Categorie> categorieOpt = categorieDAO.findById(categorieId);
        if (!categorieOpt.isPresent()) {
            logger.warn("Catégorie non trouvée: ID={}", categorieId);
            throw new ResourceNotFoundException("Catégorie", categorieId);
        }

        return categorieOpt.get();
    }

    @Override
    public Optional<Categorie> getCategorieByNom(String nom) {
        logger.debug("Récupération de la catégorie par nom: {}", nom);

        if (nom == null || nom.trim().isEmpty()) {
            return Optional.empty();
        }

        try {
            return categorieDAO.findByNom(nom);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de la catégorie par nom", e);
            throw new BusinessException("Erreur lors de la récupération de la catégorie", e);
        }
    }

    @Override
    public Map<Categorie, Long> getCategoriesWithEventCount() {
        logger.debug("Récupération des catégories avec le nombre d'événements");

        try {
            List<Categorie> categories = categorieDAO.findAll();
            Map<Categorie, Long> categoriesWithCount = new LinkedHashMap<>();

            for (Categorie categorie : categories) {
                List<Evenement> evenements = evenementDAO.findByCategorie(categorie.getId());
                categoriesWithCount.put(categorie, (long) evenements.size());
            }

            logger.debug("{} catégories récupérées avec leur nombre d'événements", categories.size());
            return categoriesWithCount;
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des catégories avec comptage", e);
            throw new BusinessException("Erreur lors de la récupération des catégories", e);
        }
    }

    /**
     * Valide les données d'une catégorie.
     *
     * @param categorie La catégorie à valider
     * @throws BusinessException Si la validation échoue
     */
    private void validateCategorie(Categorie categorie) {
        if (categorie == null) {
            throw new IllegalArgumentException("La catégorie ne peut pas être null");
        }

        // Valider le nom
        validateNom(categorie.getNom());

        // Valider la description (optionnelle)
        if (categorie.getDescription() != null && categorie.getDescription().length() > 500) {
            throw new BusinessException("La description ne doit pas dépasser 500 caractères");
        }

        // Valider la couleur (optionnelle, format hexadécimal)
        if (categorie.getCouleur() != null) {
            if (!categorie.getCouleur().matches("^#[0-9A-Fa-f]{6}$")) {
                throw new BusinessException("La couleur doit être au format hexadécimal (ex: #FF5733)");
            }
        }

        // Valider l'icône (optionnelle)
        if (categorie.getIcone() != null && categorie.getIcone().length() > 50) {
            throw new BusinessException("L'icône ne doit pas dépasser 50 caractères");
        }
    }

    /**
     * Valide le nom d'une catégorie.
     *
     * @param nom Le nom à valider
     * @throws BusinessException Si la validation échoue
     */
    private void validateNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new BusinessException("Le nom de la catégorie est obligatoire");
        }

        if (nom.length() < 2) {
            throw new BusinessException("Le nom de la catégorie doit contenir au moins 2 caractères");
        }

        if (nom.length() > 50) {
            throw new BusinessException("Le nom de la catégorie ne doit pas dépasser 50 caractères");
        }
    }
}
