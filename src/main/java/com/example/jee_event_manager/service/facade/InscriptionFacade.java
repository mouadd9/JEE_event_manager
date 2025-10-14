package com.example.jee_event_manager.service.facade;

import com.example.jee_event_manager.DAO.EvenementDAO;
import com.example.jee_event_manager.DAO.InscriptionDAO;
import com.example.jee_event_manager.model.Evenement;
import com.example.jee_event_manager.model.Inscription;
import com.example.jee_event_manager.model.Participant;
import com.example.jee_event_manager.model.StatutInscription;
import com.example.jee_event_manager.service.EvenementService;
import com.example.jee_event_manager.service.InscriptionService;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Facade Pattern pour encapsuler toute la logique d'inscription
 * Inspiré d'Eventbrite avec gestion de types de billets et quantités
 */
@Stateless
public class InscriptionFacade {
    
    @Inject
    private InscriptionService inscriptionService;
    
    @Inject
    private EvenementService evenementService;
    
    @Inject
    private InscriptionDAO inscriptionDAO;
    
    @Inject
    private EvenementDAO evenementDAO;
    
    @PersistenceContext
    private EntityManager em;

    private EntityManager getEm() {
        if (em != null) {
            return em;
        }
        if (evenementDAO != null && evenementDAO.getEntityManager() != null) {
            return evenementDAO.getEntityManager();
        }
        throw new IllegalStateException("EntityManager indisponible");
    }
    
    /**
     * Méthode principale pour enregistrer un participant à un événement
     * Encapsule toutes les validations et la persistance
     * 
     * @param userId ID de l'utilisateur
     * @param evenementId ID de l'événement
     * @param typeBillet Type de billet (STANDARD, VIP, etc.)
     * @param quantite Nombre de places à réserver
     * @return L'inscription créée
     * @throws IllegalStateException Si l'inscription n'est pas possible
     */
    @Transactional
    public Inscription registerParticipant(Long userId, Long evenementId, String typeBillet, int quantite) 
            throws IllegalStateException {
        
        // 1. Validation des données d'entrée
        validateInputData(userId, evenementId, typeBillet, quantite);
        
        // 2. Récupérer l'événement
        Evenement evenement = evenementService.findById(evenementId);
        if (evenement == null) {
            throw new IllegalStateException("Événement introuvable");
        }
        
        // 3. Récupérer le participant
        Participant participant = getEm().find(Participant.class, userId);
        if (participant == null) {
            throw new IllegalStateException("Participant introuvable");
        }
        
        // 4. Vérifier la capacité disponible
        validateCapacity(evenementId, quantite, evenement.getCapacite());
        
        // 5. Vérifier les conflits d'horaire
        validateScheduleConflicts(userId, evenement);
        
        // 6. Vérifier si l'utilisateur n'est pas déjà inscrit
        validateDuplicateRegistration(userId, evenementId);
        
        // 7. Déterminer le statut de l'inscription
        StatutInscription statut = determineInscriptionStatus(evenementId, quantite, evenement.getCapacite());
        
        // 8. Créer l'inscription
        Inscription inscription = createInscription(participant, evenement, typeBillet, quantite, statut);
        
        // 9. Persister l'inscription
        inscriptionDAO.save(inscription);
        
        return inscription;
    }
    
    /**
     * Valide les données d'entrée
     */
    private void validateInputData(Long userId, Long evenementId, String typeBillet, int quantite) {
        if (userId == null || userId <= 0) {
            throw new IllegalStateException("ID utilisateur invalide");
        }
        
        if (evenementId == null || evenementId <= 0) {
            throw new IllegalStateException("ID événement invalide");
        }
        
        if (quantite < 1 || quantite > 10) {
            throw new IllegalStateException("La quantité doit être entre 1 et 10");
        }
        
        if (typeBillet == null || typeBillet.isEmpty()) {
            throw new IllegalStateException("Type de billet requis");
        }
    }
    
    /**
     * Vérifie la capacité disponible de l'événement
     */
    private void validateCapacity(Long evenementId, int quantiteDemandee, int capaciteTotale) {
        // Compter le nombre d'inscriptions acceptées
        Long inscriptionsCount = getEm().createQuery(
            "SELECT COALESCE(SUM(i.quantite), 0) FROM Inscription i " +
            "WHERE i.evenement.id = :evenementId " +
            "AND i.statut IN ('ACCEPTEE', 'EN_ATTENTE')", 
            Long.class)
            .setParameter("evenementId", evenementId)
            .getSingleResult();
        
        int placesOccupees = inscriptionsCount.intValue();
        int placesDisponibles = capaciteTotale - placesOccupees;
        
        if (placesDisponibles < quantiteDemandee) {
            throw new IllegalStateException(
                String.format("Capacité insuffisante. Places disponibles: %d, demandées: %d", 
                    placesDisponibles, quantiteDemandee)
            );
        }
    }
    
    /**
     * Vérifie les conflits d'horaire avec les autres inscriptions du participant
     */
    private void validateScheduleConflicts(Long userId, Evenement nouvelEvenement) {
        List<Inscription> inscriptionsExistantes = getEm().createQuery(
            "SELECT i FROM Inscription i " +
            "WHERE i.participant.id = :userId " +
            "AND i.statut IN ('ACCEPTEE', 'EN_ATTENTE')", 
            Inscription.class)
            .setParameter("userId", userId)
            .getResultList();
        
        for (Inscription inscription : inscriptionsExistantes) {
            Evenement evenementExistant = inscription.getEvenement();
            
            // Vérifier si les événements se chevauchent
            if (eventsOverlap(evenementExistant, nouvelEvenement)) {
                throw new IllegalStateException(
                    String.format("Conflit d'horaire avec l'événement '%s' le %s", 
                        evenementExistant.getTitre(), 
                        evenementExistant.getDate())
                );
            }
        }
    }
    
    /**
     * Vérifie si deux événements se chevauchent dans le temps
     */
    private boolean eventsOverlap(Evenement event1, Evenement event2) {
        LocalDateTime start1 = event1.getDate();
        LocalDateTime end1 = start1.plusHours(event1.getDuree() != null ? event1.getDuree() : 2);
        
        LocalDateTime start2 = event2.getDate();
        LocalDateTime end2 = start2.plusHours(event2.getDuree() != null ? event2.getDuree() : 2);
        
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
    
    /**
     * Vérifie si l'utilisateur n'est pas déjà inscrit à cet événement
     */
    private void validateDuplicateRegistration(Long userId, Long evenementId) {
        Long count = getEm().createQuery(
            "SELECT COUNT(i) FROM Inscription i " +
            "WHERE i.participant.id = :userId " +
            "AND i.evenement.id = :evenementId " +
            "AND i.statut IN ('ACCEPTEE', 'EN_ATTENTE')", 
            Long.class)
            .setParameter("userId", userId)
            .setParameter("evenementId", evenementId)
            .getSingleResult();
        
        if (count > 0) {
            throw new IllegalStateException("Vous êtes déjà inscrit à cet événement");
        }
    }
    
    /**
     * Détermine le statut de l'inscription en fonction de la capacité
     */
    private StatutInscription determineInscriptionStatus(Long evenementId, int quantite, int capaciteTotale) {
        Long inscriptionsCount = getEm().createQuery(
            "SELECT COALESCE(SUM(i.quantite), 0) FROM Inscription i " +
            "WHERE i.evenement.id = :evenementId " +
            "AND i.statut = 'ACCEPTEE'", 
            Long.class)
            .setParameter("evenementId", evenementId)
            .getSingleResult();
        
        int placesOccupees = inscriptionsCount.intValue();
        int placesRestantes = capaciteTotale - placesOccupees;
        
        // Si suffisamment de places, accepter directement
        if (placesRestantes >= quantite) {
            return StatutInscription.ACCEPTEE;
        } else {
            // Sinon, mettre en attente
            return StatutInscription.EN_ATTENTE;
        }
    }
    
    /**
     * Crée une nouvelle inscription
     */
    private Inscription createInscription(Participant participant, Evenement evenement, 
                                         String typeBillet, int quantite, StatutInscription statut) {
        Inscription inscription = new Inscription();
        inscription.setParticipant(participant);
        inscription.setEvenement(evenement);
        inscription.setTypeBillet(typeBillet);
        inscription.setQuantite(quantite);
        inscription.setStatut(statut);
        inscription.setDateInscription(LocalDateTime.now());
        
        return inscription;
    }
    
    /**
     * Récupère le nombre de places disponibles pour un événement
     */
    public int getPlacesDisponibles(Long evenementId) {
        Evenement evenement = evenementService.findById(evenementId);
        if (evenement == null) {
            return 0;
        }
        
        Long inscriptionsCount = getEm().createQuery(
            "SELECT COALESCE(SUM(i.quantite), 0) FROM Inscription i " +
            "WHERE i.evenement.id = :evenementId " +
            "AND i.statut IN ('ACCEPTEE', 'EN_ATTENTE')", 
            Long.class)
            .setParameter("evenementId", evenementId)
            .getSingleResult();
        
        return evenement.getCapacite() - inscriptionsCount.intValue();
    }
}
