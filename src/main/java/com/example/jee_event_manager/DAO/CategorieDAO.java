package com.example.jee_event_manager.DAO;

import com.example.jee_event_manager.model.Categorie;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;

@ApplicationScoped
public class CategorieDAO {

    @Inject
    private EntityManager em;

    /**
     * Récupère toutes les catégories
     * @return Liste de toutes les catégories
     */
    public List<Categorie> findAll() {
        return em.createQuery("SELECT c FROM Categorie c ORDER BY c.nom", Categorie.class)
                .getResultList();
    }

    /**
     * Trouve une catégorie par son ID
     * @param id ID de la catégorie
     * @return La catégorie trouvée ou null
     */
    public Categorie findById(Long id) {
        return em.find(Categorie.class, id);
    }

    /**
     * Enregistre ou met à jour une catégorie
     * @param categorie La catégorie à enregistrer
     * @return La catégorie enregistrée
     */
    public Categorie save(Categorie categorie) {
        if (categorie.getId() == null) {
            em.persist(categorie);
            return categorie;
        } else {
            return em.merge(categorie);
        }
    }

    /**
     * Supprime une catégorie par son ID
     * @param id ID de la catégorie à supprimer
     */
    public void delete(Long id) {
        Categorie categorie = findById(id);
        if (categorie != null) {
            em.remove(em.contains(categorie) ? categorie : em.merge(categorie));
        }
    }
    
    /**
     * Récupère l'EntityManager
     * @return L'EntityManager
     */
    public EntityManager getEntityManager() {
        return em;
    }
}
