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
    public List<Categorie> findAll() {
        return em.createQuery("SELECT c FROM Categorie c ORDER BY c.nom", Categorie.class)
                .getResultList();
    }

    public Categorie findById(Long id) {
        return em.find(Categorie.class, id);
    }
    public Categorie save(Categorie categorie) {
        if (categorie.getId() == null) {
            em.persist(categorie);
            return categorie;
        } else {
            return em.merge(categorie);
        }
    }
    public void delete(Long id) {
        Categorie categorie = findById(id);
        if (categorie != null) {
            em.remove(em.contains(categorie) ? categorie : em.merge(categorie));
        }
    }
    public EntityManager getEntityManager() {
        return em;
    }
}
