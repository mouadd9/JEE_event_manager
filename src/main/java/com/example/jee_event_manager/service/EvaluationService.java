package com.example.jee_event_manager.service;

import com.example.jee_event_manager.DAO.EvaluationRepository;
import com.example.jee_event_manager.DAO.InscriptionRepository;
import com.example.jee_event_manager.DAO.EvenementRepository;
import com.example.jee_event_manager.DAO.ParticipantRepository;
import com.example.jee_event_manager.model.Evaluation;
import com.example.jee_event_manager.model.Evenement;
import com.example.jee_event_manager.model.Participant;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;


@Stateless
public class EvaluationService {
    
    @Inject
    private EvaluationRepository evaluationRepository;
    
    @Inject
    private InscriptionRepository inscriptionRepository;
    
    @Inject
    private EvenementRepository evenementRepository;
    
    @Inject
    private ParticipantRepository participantRepository;
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Evaluation ajouterOuModifierEvaluation(Long participantId, Long evenementId, Integer note, String texte) {
        // Validation: vérifier que le participant est inscrit à l'événement
        if (!inscriptionRepository.isParticipantInscrit(participantId, evenementId)) {
            throw new IllegalStateException("Vous devez être inscrit à cet événement pour l'évaluer");
        }
        
        // Validation de la note
        if (note == null || note < 0 || note > 5) {
            throw new IllegalArgumentException("La note doit être entre 0 et 5 étoiles");
        }
        
        // Validation du texte optionnel
        if (texte != null && texte.length() > 500) {
            throw new IllegalArgumentException("Le texte de l'évaluation ne peut pas dépasser 500 caractères");
        }
        
        // Récupérer les entités
        Participant participant = participantRepository.findParticipantById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant introuvable"));
        
        Evenement evenement = evenementRepository.findById(evenementId)
                .orElseThrow(() -> new IllegalArgumentException("Événement introuvable"));
        
        // Vérifier si le participant a déjà évalué cet événement
        Optional<Evaluation> evaluationExistante = evaluationRepository.findByParticipantAndEvenement(participantId, evenementId);
        
        if (evaluationExistante.isPresent()) {
            // Modifier l'évaluation existante
            Evaluation evaluation = evaluationExistante.get();
            evaluation.setNote(note);
            evaluation.setTexte(texte != null ? texte.trim() : null);
            return evaluationRepository.save(evaluation);
        } else {
            // Créer une nouvelle évaluation
            Evaluation evaluation = new Evaluation(note, texte != null ? texte.trim() : null, participant, evenement);
            return evaluationRepository.save(evaluation);
        }
    }
    
    /**
     * Récupérer toutes les évaluations d'un événement
     */
    public List<Evaluation> getEvaluationsEvenement(Long evenementId) {
        return evaluationRepository.findByEvenement(evenementId);
    }
    
    /**
     * Récupérer les évaluations d'un participant
     */
    public List<Evaluation> getEvaluationsParticipant(Long participantId) {
        return evaluationRepository.findByParticipant(participantId);
    }
    
    /**
     * Calculer la note moyenne d'un événement
     */
    public Double getMoyenneEvenement(Long evenementId) {
        return evaluationRepository.getMoyenneNoteByEvenement(evenementId);
    }
    
    /**
     * Récupérer l'évaluation d'un participant pour un événement spécifique
     */
    public Optional<Evaluation> getEvaluationParticipant(Long participantId, Long evenementId) {
        return evaluationRepository.findByParticipantAndEvenement(participantId, evenementId);
    }
    
    /**
     * Vérifier si un participant a déjà évalué un événement
     */
    public boolean hasEvaluated(Long participantId, Long evenementId) {
        return evaluationRepository.findByParticipantAndEvenement(participantId, evenementId).isPresent();
    }
    
    /**
     * Compter le nombre d'évaluations pour un événement
     */
    public Long countEvaluationsEvenement(Long evenementId) {
        return evaluationRepository.countByEvenement(evenementId);
    }
    
    // === ADMINISTRATION METHODS (COMMENTED OUT) ===
    
    /*
    /**
     * Supprimer une évaluation
     * Validation: seul l'auteur peut supprimer son évaluation
     */
    /*
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void supprimerEvaluation(Long evaluationId, Long participantId) {
        // Vérifier que l'évaluation existe
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
            .orElseThrow(() -> new IllegalArgumentException("Évaluation introuvable"));
        
        // Vérifier que le participant est bien l'auteur de l'évaluation
        if (!evaluation.getParticipant().getId().equals(participantId)) {
            throw new IllegalStateException("Vous ne pouvez supprimer que vos propres évaluations");
        }
        
        // Supprimer l'évaluation
        evaluationRepository.delete(evaluationId);
    }
    */
    
    /**
     * Récupérer une évaluation par son ID
     */
    public Evaluation getEvaluationById(Long evaluationId) {
        return evaluationRepository.findById(evaluationId)
            .orElseThrow(() -> new IllegalArgumentException("Évaluation introuvable"));
    }
}
