package com.example.jee_event_manager.service;

import jakarta.persistence.TypedQuery;
import com.example.jee_event_manager.DAO.EvenementDAO;
import com.example.jee_event_manager.model.Evenement;
import com.example.jee_event_manager.model.StatutEvenement;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class EvenementService {
    
    @Inject
    private EvenementDAO evenementDAO;
    
    /**
     * Récupère la liste des événements publiés avec des filtres optionnels
     * @param date Date de l'événement (optionnel)
     * @param lieu Lieu de l'événement (optionnel)
     * @param categorie Catégorie de l'événement (optionnel)
     * @param search Recherche textuelle dans titre et description (optionnel)
     * @return Liste des événements correspondants aux critères
     */
    public List<Evenement> getEvenementsPublies(LocalDate date, String lieu, String categorie, String search) {
        try {
            // Déterminer si on a besoin d'un filtre de catégorie
            boolean hasCategorieFilter = categorie != null && !categorie.trim().isEmpty();
            Long categorieId = null;
            
            if (hasCategorieFilter) {
                try {
                    categorieId = Long.parseLong(categorie.trim());
                } catch (NumberFormatException ex) {
                    System.err.println("ID de catégorie invalide: " + categorie);
                    hasCategorieFilter = false;
                }
            }
            
            // Créer une requête avec les jointures nécessaires
            String jpql = "SELECT DISTINCT e FROM Evenement e " +
                         "LEFT JOIN FETCH e.categories " +
                         "LEFT JOIN FETCH e.organisateur " +
                         "WHERE e.statut = 'PUBLIE'";
            
            // Liste pour stocker les conditions de filtrage
            List<String> conditions = new ArrayList<>();
            
            // Filtre par date (comparer uniquement la partie date)
            if (date != null) {
                conditions.add("FUNCTION('DATE', e.dateDebut) = :date");
            }
            
            // Filtre par lieu (recherche insensible à la casse)
            if (lieu != null && !lieu.trim().isEmpty()) {
                conditions.add("LOWER(e.lieu) LIKE LOWER(CONCAT('%', :lieu, '%'))");
            }
            
            // Filtre par catégorie (par ID) - utiliser une sous-requête avec jointure
            if (hasCategorieFilter) {
                conditions.add(":categorieId IN (SELECT c.id FROM e.categories c)");
            }
            
            // Filtre par recherche textuelle (titre ou description)
            if (search != null && !search.trim().isEmpty()) {
                conditions.add("(LOWER(e.titre) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%')))");
            }
            
            // Ajouter les conditions à la requête
            if (!conditions.isEmpty()) {
                jpql += " AND " + String.join(" AND ", conditions);
            }
            
            // Trier par date de début croissante
            jpql += " ORDER BY e.dateDebut ASC";
            
            // Créer la requête
            TypedQuery<Evenement> query = evenementDAO.getEntityManager().createQuery(jpql, Evenement.class);
            
            // Définir les paramètres
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
            
            // Exécuter la requête et retourner les résultats
            List<Evenement> result = query.getResultList();
            System.out.println("=== DEBUG: Nombre d'événements trouvés: " + result.size());
            System.out.println("=== DEBUG: Paramètres - search=" + search + ", categorie=" + categorie);
            return result;
        } catch (Exception e) {
            System.err.println("=== ERREUR lors de la récupération des événements:");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Méthode template pour exécuter une opération avec validation de session
     * @param operation Opération à exécuter
     * @param <T> Type de retour de l'opération
     * @return Le résultat de l'opération
     */
    protected <T> T executeWithValidation(DatabaseOperation<T> operation) {
        // Ici, on pourrait ajouter une validation de session utilisateur
        // Pour l'instant, on exécute simplement l'opération
        return operation.execute();
    }
    
    public Evenement findById(Long id) {
        return evenementDAO.findById(id);
    }
    
    public List<Evenement> findByStatut(StatutEvenement statut) {
        return evenementDAO.findByStatut(statut);
    }
    
    public Evenement save(Evenement evenement) {
        if (evenement.validate()) {
            evenementDAO.save(evenement);
            return evenement;
        }
        throw new IllegalArgumentException("Événement invalide");
    }
    
    public void delete(Long id) {
        Evenement evenement = findById(id);
        if (evenement != null) {
            evenementDAO.delete(evenement);
        }
    }
    
    /**
     * Interface fonctionnelle pour les opérations de base de données
     * @param <T> Type de retour de l'opération
     */
    @FunctionalInterface
    public interface DatabaseOperation<T> {
        T execute();
    }
}
