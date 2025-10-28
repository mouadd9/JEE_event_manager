package com.example.jee_event_manager.service;

import com.example.jee_event_manager.DAO.InscriptionRepository;
import com.example.jee_event_manager.DAO.EvenementRepository;
import com.example.jee_event_manager.DAO.ParticipantRepository;
import com.example.jee_event_manager.config.qualifiers.ParticipantQualifier;
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
    @ParticipantQualifier
    private ParticipantRepository participantRepository;
    
    @Inject
    private InscriptionNotifier notifier;
    
    @Inject
    private BilletService billetService;
    
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
        
        // Générer le billet PDF automatiquement
        try {
            billetService.genererEtEnvoyerBillet(saved);
            System.out.println("Billet généré et envoyé pour l'inscription: " + saved.getId());
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du billet: " + e.getMessage());
            // Ne pas faire échouer l'inscription si le billet ne peut pas être généré
        }
        
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
    
    // === ADMINISTRATION METHODS (COMMENTED OUT) ===
    
    /*
    /**
     * Supprimer définitivement une inscription
     */
    /*
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(Long id) {
        Inscription inscription = inscriptionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Inscription introuvable"));
        inscriptionRepository.delete(id);
    }
    */
    
    /**
     * Obtenir le statut d'inscription d'un participant pour un événement
     */
    public Optional<StatutInscription> getStatutInscription(Long participantId, Long evenementId) {
        Optional<Inscription> inscription = inscriptionRepository.findByParticipantAndEvenement(participantId, evenementId);
        return inscription.map(Inscription::getStatut);
    }
    
    /**
     * Vérifier si un participant est inscrit à un événement
     */
    public boolean isParticipantInscrit(Long participantId, Long evenementId) {
        return inscriptionRepository.isParticipantInscrit(participantId, evenementId);
    }
    
    /**
     * Compter le nombre d'inscrits pour un événement
     */
    public Long countInscritsEvenement(Long evenementId) {
        return inscriptionRepository.countByEvenement(evenementId);
    }
    
    /**
     * Compter le nombre de places réservées pour un événement
     */
    public Long countPlacesReservees(Long evenementId) {
        return inscriptionRepository.countPlacesReservees(evenementId);
    }
    
    /**
     * Calculer la capacité disponible pour un événement
     */
    public Integer getCapaciteDisponible(Long evenementId) {
        Evenement evenement = evenementRepository.findById(evenementId)
                .orElseThrow(() -> new IllegalArgumentException("Événement introuvable"));
        
        Long placesReservees = inscriptionRepository.countPlacesReservees(evenementId);
        return evenement.getCapacite() - placesReservees.intValue();
    }
    
    /**
     * Récupérer toutes les inscriptions d'un événement
     */
    public List<Inscription> getInscriptionsEvenement(Long evenementId) {
        return inscriptionRepository.findByEvenement(evenementId);
    }
    
    /**
     * Récupérer les inscriptions d'un événement par statut
     */
    public List<Inscription> getInscriptionsEvenementByStatut(Long evenementId, StatutInscription statut) {
        return inscriptionRepository.findByEvenementAndStatut(evenementId, statut);
    }
}
