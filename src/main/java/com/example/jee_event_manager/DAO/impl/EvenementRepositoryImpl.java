package com.example.jee_event_manager.DAO.impl;

import com.example.jee_event_manager.DAO.EvenementRepository;
import com.example.jee_event_manager.model.Evenement;
import com.example.jee_event_manager.model.StatutEvenement;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class EvenementRepositoryImpl implements EvenementRepository {
    
    @Inject
    private EntityManager em;
    
    @Override
    public List<Evenement> findAll() {
        TypedQuery<Evenement> query = em.createQuery("SELECT e FROM Evenement e ORDER BY e.dateDebut ASC", Evenement.class);
        return query.getResultList();
    }
    
    @Override
    public Optional<Evenement> findById(Long id) {
        Evenement evenement = em.find(Evenement.class, id);
        return Optional.ofNullable(evenement);
    }
    
    @Override
    public Evenement save(Evenement evenement) {
        if (evenement.getId() == null) {
            em.persist(evenement);
        } else {
            evenement = em.merge(evenement);
        }
        return evenement;
    }
    
    @Override
    public void delete(Long id) {
        Evenement evenement = em.find(Evenement.class, id);
        if (evenement != null) {
            em.remove(evenement);
        }
    }
    
    @Override
    public Evenement update(Evenement evenement) {
        return em.merge(evenement);
    }
    
    @Override
    public List<Evenement> findByOrganisateurId(Long organisateurId) {
        TypedQuery<Evenement> query = em.createQuery(
            "SELECT e FROM Evenement e WHERE e.organisateur.id = :organisateurId ORDER BY e.dateDebut ASC", 
            Evenement.class
        );
        query.setParameter("organisateurId", organisateurId);
        return query.getResultList();
    }
    
    @Override
    public List<Evenement> findByStatut(StatutEvenement statut) {
        TypedQuery<Evenement> query = em.createQuery(
            "SELECT e FROM Evenement e WHERE e.statut = :statut ORDER BY e.dateDebut ASC", 
            Evenement.class
        );
        query.setParameter("statut", statut);
        return query.getResultList();
    }
    
    @Override
    public List<Evenement> findEvenementsPublies() {
        return findByStatut(StatutEvenement.PUBLIE);
    }
    
    @Override
    public List<Evenement> getEvenementsPublies(LocalDate date, String lieu, String categorie, String search) {
        // This is the complex query from Branch A's EvenementService
        boolean hasCategorieFilter = categorie != null && !categorie.trim().isEmpty();
        Long categorieId = null;
        
        if (hasCategorieFilter) {
            try {
                categorieId = Long.parseLong(categorie.trim());
            } catch (NumberFormatException ex) {
                hasCategorieFilter = false;
            }
        }
        
        String jpql = "SELECT DISTINCT e FROM Evenement e " +
                     "LEFT JOIN FETCH e.categories " +
                     "LEFT JOIN FETCH e.organisateur " +
                     "WHERE e.statut = 'PUBLIE'";
        
        if (date != null) {
            jpql += " AND FUNCTION('DATE', e.dateDebut) = :date";
        }
        
        if (lieu != null && !lieu.trim().isEmpty()) {
            jpql += " AND LOWER(e.lieu) LIKE LOWER(CONCAT('%', :lieu, '%'))";
        }
        
        if (hasCategorieFilter) {
            jpql += " AND :categorieId IN (SELECT c.id FROM e.categories c)";
        }
        
        if (search != null && !search.trim().isEmpty()) {
            jpql += " AND (LOWER(e.titre) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%')))";
        }
        
        jpql += " ORDER BY e.dateDebut ASC";
        
        TypedQuery<Evenement> query = em.createQuery(jpql, Evenement.class);
        
        if (date != null) {
            query.setParameter("date", date);
        }
        if (lieu != null && !lieu.trim().isEmpty()) {
            query.setParameter("lieu", lieu.trim());
        }
        if (hasCategorieFilter && categorieId != null) {
            query.setParameter("categorieId", categorieId);
        }
        if (search != null && !search.trim().isEmpty()) {
            query.setParameter("search", search.trim());
        }
        
        return query.getResultList();
    }
    
    @Override
    public List<Evenement> findByLieu(String lieu) {
        TypedQuery<Evenement> query = em.createQuery(
            "SELECT e FROM Evenement e WHERE LOWER(e.lieu) LIKE LOWER(CONCAT('%', :lieu, '%')) ORDER BY e.dateDebut ASC", 
            Evenement.class
        );
        query.setParameter("lieu", lieu);
        return query.getResultList();
    }
    
    @Override
    public List<Evenement> findByDateDebut(LocalDate date) {
        TypedQuery<Evenement> query = em.createQuery(
            "SELECT e FROM Evenement e WHERE FUNCTION('DATE', e.dateDebut) = :date ORDER BY e.dateDebut ASC", 
            Evenement.class
        );
        query.setParameter("date", date);
        return query.getResultList();
    }
    
    @Override
    public List<Evenement> findByCategorieId(Long categorieId) {
        TypedQuery<Evenement> query = em.createQuery(
            "SELECT e FROM Evenement e JOIN e.categories c WHERE c.id = :categorieId ORDER BY e.dateDebut ASC", 
            Evenement.class
        );
        query.setParameter("categorieId", categorieId);
        return query.getResultList();
    }
}
