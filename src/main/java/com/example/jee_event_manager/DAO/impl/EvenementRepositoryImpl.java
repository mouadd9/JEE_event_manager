package com.example.jee_event_manager.DAO.impl;

import com.example.jee_event_manager.DAO.EvenementRepository;
import com.example.jee_event_manager.model.Evenement;
import com.example.jee_event_manager.model.StatutEvenement;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        try {
            em.getTransaction().begin();
            
            if (evenement.getId() == null) {
                em.persist(evenement);
            } else {
                evenement = em.merge(evenement);
            }
            
            em.getTransaction().commit();
            return evenement;
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Failed to save Evenement: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void delete(Long id) {
        try {
            em.getTransaction().begin();
            
            Evenement evenement = em.find(Evenement.class, id);
            if (evenement != null) {
                em.remove(evenement);
            }
            
            em.getTransaction().commit();
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Failed to delete Evenement: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Evenement update(Evenement evenement) {
        try {
            em.getTransaction().begin();
            
            evenement = em.merge(evenement);
            
            em.getTransaction().commit();
            return evenement;
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Failed to update Evenement: " + e.getMessage(), e);
        }
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
        System.out.println("=== DEBUG EvenementRepositoryImpl.getEvenementsPublies ===");
        System.out.println("Parameters: date=" + date + ", lieu=" + lieu + ", categorie=" + categorie + ", search=" + search);
        
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
        
        System.out.println("JPQL Query: " + jpql);
        
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
        
        List<Evenement> result = query.getResultList();
        System.out.println("Query returned " + result.size() + " events");
        
        if (!result.isEmpty()) {
            System.out.println("First event: " + result.get(0).getTitre() + " - Status: " + result.get(0).getStatut());
        }
        
        return result;
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
    
    @Override
    public List<Evenement> findPopularEvents(int limit) {
        // Query to get popular events ordered by number of recent inscriptions (last 48 hours)
        // This allows to showcase trending events based on recent activity
        LocalDateTime dateLimite = LocalDateTime.now().minusHours(48);
        
        String jpql = "SELECT e FROM Evenement e " +
                     "WHERE e.statut = 'PUBLIE' " +
                     "ORDER BY (SELECT COUNT(i.id) FROM Inscription i " +
                     "          WHERE i.evenement.id = e.id " +
                     "          AND i.dateInscription >= :dateLimite) DESC, " +
                     "         e.dateDebut ASC";
        
        TypedQuery<Evenement> query = em.createQuery(jpql, Evenement.class);
        query.setParameter("dateLimite", dateLimite);
        
        // Limit between 1 and 5 (adjusted to allow display even with few events)
        int actualLimit = Math.max(1, Math.min(5, limit));
        query.setMaxResults(actualLimit);
        
        List<Evenement> result = query.getResultList();
        
        System.out.println("=== DEBUG findPopularEvents (Inscriptions récentes - 48h) ===");
        System.out.println("Date limite (48h): " + dateLimite);
        System.out.println("JPQL Query: " + jpql);
        System.out.println("Limit demandé: " + limit + ", Limit appliqué: " + actualLimit);
        System.out.println("Nombre d'événements retournés: " + result.size());
        
        if (!result.isEmpty()) {
            System.out.println("Premier événement tendance: " + result.get(0).getTitre() + " (ID: " + result.get(0).getId() + ")");
        } else {
            System.out.println("AUCUN événement publié trouvé dans la base de données !");
        }
        
        // Eagerly load categories and organisateur for each event
        for (Evenement e : result) {
            e.getCategories().size(); // Force load
            if (e.getOrganisateur() != null) {
                e.getOrganisateur().getId(); // Force load
            }
        }
        
        return result;
    }
}
