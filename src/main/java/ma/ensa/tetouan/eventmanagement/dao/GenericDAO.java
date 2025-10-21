package ma.ensa.tetouan.eventmanagement.dao;

import javax.persistence.PersistenceException;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Interface générique pour les opérations CRUD sur les entités.
 * Définit les méthodes de base pour l'accès aux données.
 *
 * @param <T> Le type de l'entité
 * @param <ID> Le type de l'identifiant de l'entité
 *
 * @author ENSA Tétouan
 */
public interface GenericDAO<T, ID extends Serializable> {

    /**
     * Persiste une nouvelle entité dans la base de données.
     *
     * @param entity L'entité à persister
     * @return L'entité persistée avec son ID généré
     * @throws PersistenceException Si une erreur se produit lors de la persistance
     */
    T save(T entity);

    /**
     * Met à jour une entité existante dans la base de données.
     *
     * @param entity L'entité à mettre à jour
     * @return L'entité mise à jour
     * @throws PersistenceException Si une erreur se produit lors de la mise à jour
     */
    T update(T entity);

    /**
     * Supprime une entité de la base de données.
     *
     * @param entity L'entité à supprimer
     * @throws PersistenceException Si une erreur se produit lors de la suppression
     */
    void delete(T entity);

    /**
     * Supprime une entité par son identifiant.
     *
     * @param id L'identifiant de l'entité à supprimer
     * @throws PersistenceException Si une erreur se produit lors de la suppression
     */
    void deleteById(ID id);

    /**
     * Recherche une entité par son identifiant.
     *
     * @param id L'identifiant de l'entité à rechercher
     * @return Un Optional contenant l'entité si elle existe, Optional.empty() sinon
     * @throws PersistenceException Si une erreur se produit lors de la recherche
     */
    Optional<T> findById(ID id);

    /**
     * Récupère toutes les entités du type donné.
     *
     * @return La liste de toutes les entités
     * @throws PersistenceException Si une erreur se produit lors de la récupération
     */
    List<T> findAll();

    /**
     * Récupère toutes les entités avec pagination.
     *
     * @param page Le numéro de la page (commence à 0)
     * @param pageSize Le nombre d'éléments par page
     * @return La liste des entités pour la page demandée
     * @throws PersistenceException Si une erreur se produit lors de la récupération
     * @throws IllegalArgumentException Si page < 0 ou pageSize <= 0
     */
    List<T> findAll(int page, int pageSize);

    /**
     * Compte le nombre total d'entités.
     *
     * @return Le nombre total d'entités
     * @throws PersistenceException Si une erreur se produit lors du comptage
     */
    long count();

    /**
     * Vérifie si une entité existe par son identifiant.
     *
     * @param id L'identifiant à vérifier
     * @return true si l'entité existe, false sinon
     * @throws PersistenceException Si une erreur se produit lors de la vérification
     */
    boolean existsById(ID id);

    /**
     * Rafraîchit l'état d'une entité depuis la base de données.
     *
     * @param entity L'entité à rafraîchir
     * @throws PersistenceException Si une erreur se produit lors du rafraîchissement
     */
    void refresh(T entity);

    /**
     * Détache une entité du contexte de persistance.
     *
     * @param entity L'entité à détacher
     */
    void detach(T entity);

    /**
     * Vide le cache de premier niveau (persistence context).
     * Toutes les modifications non synchronisées sont perdues.
     */
    void clear();

    /**
     * Synchronise le contexte de persistance avec la base de données.
     * Force l'exécution de toutes les opérations en attente.
     *
     * @throws PersistenceException Si une erreur se produit lors de la synchronisation
     */
    void flush();
}
