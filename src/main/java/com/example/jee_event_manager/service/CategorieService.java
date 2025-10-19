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

    public List<Categorie> findAll() {
        return categorieDAO.findAll();
    }

    public Categorie findById(Long id) {
        return categorieDAO.findById(id);
    }

    public Categorie save(Categorie categorie) {
        return categorieDAO.save(categorie);
    }
    public void delete(Long id) {
        categorieDAO.delete(id);
    }
}
