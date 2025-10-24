package com.example.jee_event_manager.service;

import com.example.jee_event_manager.DAO.InscriptionRepository;
import com.example.jee_event_manager.DAO.EvenementRepository;
import com.example.jee_event_manager.DAO.ParticipantRepository;
import com.example.jee_event_manager.model.Evenement;
import com.example.jee_event_manager.model.Inscription;
import com.example.jee_event_manager.model.Participant;
import com.example.jee_event_manager.model.StatutInscription;
import com.example.jee_event_manager.model.observer.InscriptionNotifier;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Stateless
public class InscriptionService {
    
    @Inject
    private InscriptionRepository inscriptionRepository;
    
    @Inject
    private EvenementRepository evenementRepository;
    
    @Inject
    private ParticipantRepository participantRepository;
    
    @Inject
    private InscriptionNotifier notifier;
    
    /**
     * Trouver une inscription par son ID
     */
    public Optional<Inscription> findById(Long id) {
        return inscriptionRepository.findById(id);
    }
    
    /**
     * Récupérer toutes les inscriptions d'un participant
     */
    public List<Inscription> getInscriptionsParticipant(Long participantId) {
        return inscriptionRepository.findByParticipant(participantId);
    }
    
    /**
     * Récupérer les inscriptions d'un participant filtrées par statut
     */
    public List<Inscription> getInscriptionsParticipantByStatut(Long participantId, StatutInscription statut) {
        return inscriptionRepository.findByParticipantAndStatut(participantId, statut);
    }
    
    /**
     * Inscrire un participant à un événement
     * Validations:
     * - Vérifier que le participant n'est pas déjà inscrit
     * - Vérifier la capacité disponible
     * - Valider la quantité de places demandées
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Inscription inscrireParticipant(Long participantId, Long evenementId, String typeBillet, Integer quantite) {
        // Validation des paramètres
        if (participantId == null || evenementId == null) {
            throw new IllegalArgumentException("Participant et événement sont obligatoires");
        }
        
        if (quantite == null || quantite < 1 || quantite > 10) {
            throw new IllegalArgumentException("La quantité doit être entre 1 et 10");
        }
        
        // Récupérer les entités
        Participant participant = participantRepository.findParticipantById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant introuvable"));
        
        Evenement evenement = evenementRepository.findById(evenementId)
                .orElseThrow(() -> new IllegalArgumentException("Événement introuvable"));
        
        // Vérifier que le participant n'est pas déjà inscrit
        if (inscriptionRepository.isParticipantInscrit(participantId, evenementId)) {
            throw new IllegalStateException("Vous êtes déjà inscrit à cet événement");
        }
        
        // Vérifier la capacité disponible
        Long placesReservees = inscriptionRepository.countPlacesReservees(evenementId);
        int capaciteDisponible = evenement.getCapacite() - placesReservees.intValue();
        
        if (capaciteDisponible < quantite) {
            throw new IllegalStateException("Capacité insuffisante. Places disponibles: " + capaciteDisponible);
        }
        
        // Créer l'inscription
        Inscription inscription = new Inscription();
        inscription.setParticipant(participant);
        inscription.setEvenement(evenement);
        inscription.setTypeBillet(typeBillet != null ? typeBillet : "STANDARD");
        inscription.setQuantite(quantite);
        inscription.setDateInscription(LocalDateTime.now());
        
        // Définir le statut (acceptation automatique ou en attente)
        // Pour simplifier, on accepte automatiquement si capacité OK
        inscription.setStatut(StatutInscription.ACCEPTEE);
        
        // Sauvegarder l'inscription
        Inscription saved = inscriptionRepository.save(inscription);
        
        // Forcer le chargement des relations pour éviter LazyInitializationException
        saved.getEvenement().getTitre(); // Force le chargement de l'événement
        saved.getParticipant().getNom(); // Force le chargement du participant
        
        // Notifier les observateurs
        notifier.notifyInscription(saved);
        
        return saved;
    }
    
    /**
     * Annuler une inscription
     * Validation: vérifier que l'inscription appartient au participant
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void annulerInscription(Long inscriptionId, Long participantId) {
        // Vérifier que l'inscription existe
        Inscription inscription = inscriptionRepository.findById(inscriptionId)
            .orElseThrow(() -> new IllegalArgumentException("Inscription introuvable"));
        
        // Vérifier que l'inscription appartient bien au participant
        if (!inscriptionRepository.isOwner(inscriptionId, participantId)) {
            throw new IllegalStateException("Vous ne pouvez annuler que vos propres inscriptions");
        }
        
        // Vérifier que l'inscription n'est pas déjà annulée
        if (inscription.getStatut() == StatutInscription.ANNULEE) {
            throw new IllegalStateException("Cette inscription est déjà annulée");
        }
        
        // Annuler l'inscription
        inscriptionRepository.cancelInscription(inscriptionId);
    }
    
    /**
     * Supprimer définitivement une inscription
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(Integer id) {
        // Démarrer une transaction manuelle pour RESOURCE_LOCAL
        em.getTransaction().begin();
        
        try {
            Inscription inscription = inscriptionDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inscription introuvable"));
            inscriptionDAO.delete(inscription);
            
            // Committer la transaction
            em.getTransaction().commit();
            
        } catch (Exception e) {
            // Rollback en cas d'erreur
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }
    
    /**
     * Obtenir le statut d'inscription d'un participant pour un événement
     */
    public Optional<StatutInscription> getStatutInscription(Long participantId, Integer evenementId) {
        Optional<Inscription> inscription = inscriptionDAO.findByParticipantAndEvenement(participantId, evenementId);
        return inscription.map(Inscription::getStatut);
    }
    
    /**
     * Vérifier si un participant est inscrit à un événement
     */
    public boolean isParticipantInscrit(Long participantId, Integer evenementId) {
        return inscriptionDAO.isParticipantInscrit(participantId, evenementId);
    }
    
    /**
     * Compter le nombre d'inscrits pour un événement
     */
    public Long countInscritsEvenement(Integer evenementId) {
        return inscriptionDAO.countByEvenement(evenementId);
    }
    
    /**
     * Compter le nombre de places réservées pour un événement
     */
    public Long countPlacesReservees(Integer evenementId) {
        return inscriptionDAO.countPlacesReservees(evenementId);
    }
    
    /**
     * Calculer la capacité disponible pour un événement
     */
    public Integer getCapaciteDisponible(Integer evenementId) {
        Evenement evenement = em.find(Evenement.class, evenementId);
        if (evenement == null) {
            throw new IllegalArgumentException("Événement introuvable");
        }
        
        Long placesReservees = inscriptionDAO.countPlacesReservees(evenementId);
        return evenement.getCapacite() - placesReservees.intValue();
    }
    
    /**
     * Récupérer toutes les inscriptions d'un événement
     */
    public List<Inscription> getInscriptionsEvenement(Integer evenementId) {
        return inscriptionDAO.findByEvenement(evenementId);
    }
    
    /**
     * Récupérer les inscriptions d'un événement par statut
     */
    public List<Inscription> getInscriptionsEvenementByStatut(Integer evenementId, StatutInscription statut) {
        return inscriptionDAO.findByEvenementAndStatut(evenementId, statut);
    }
}
