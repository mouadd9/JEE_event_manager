package com.example.jee_event_manager.service;

import com.example.jee_event_manager.DAO.CategorieDAO;
import com.example.jee_event_manager.model.Categorie;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class CategorieService {

    @Inject
    private CategorieDAO categorieDAO;

    /**
     * Récupère toutes les catégories
     * @return Liste de toutes les catégories
     */
    public List<Categorie> findAll() {
        return categorieDAO.findAll();
    }

    /**
     * Trouve une catégorie par son ID
     * @param id ID de la catégorie
     * @return La catégorie trouvée ou null
     */
    public Categorie findById(Long id) {
        return categorieDAO.findById(id);
    }

    /**
     * Enregistre ou met à jour une catégorie
     * @param categorie La catégorie à enregistrer
     * @return La catégorie enregistrée
     */
    public Categorie save(Categorie categorie) {
        return categorieDAO.save(categorie);
    }

    /**
     * Supprime une catégorie
     * @param id ID de la catégorie à supprimer
     */
    public void delete(Long id) {
        categorieDAO.delete(id);
    }
}
