package com.example.jee_event_manager.service;

import com.example.jee_event_manager.DAO.EvaluationDAO;
import com.example.jee_event_manager.DAO.InscriptionDAO;
import com.example.jee_event_manager.model.Evaluation;
import com.example.jee_event_manager.model.Evenement;
import com.example.jee_event_manager.model.Participant;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;


@Stateless
public class EvaluationService {
    
    @Inject
    private EvaluationDAO evaluationDAO;
    
    @Inject
    private InscriptionDAO inscriptionDAO;
    
    @Inject
    private EntityManager em;
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Evaluation ajouterOuModifierEvaluation(Long participantId, Integer evenementId, Integer note, String texte) {
        // Démarrer une transaction manuelle pour RESOURCE_LOCAL
        em.getTransaction().begin();
        
        try {
            // Validation: vérifier que le participant est inscrit à l'événement
            if (!inscriptionDAO.isParticipantInscrit(participantId, evenementId)) {
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
            Participant participant = em.find(Participant.class, participantId);
            if (participant == null) {
                throw new IllegalArgumentException("Participant introuvable");
            }
            
            Evenement evenement = em.find(Evenement.class, evenementId);
            if (evenement == null) {
                throw new IllegalArgumentException("Événement introuvable");
            }
            
            // Vérifier si le participant a déjà évalué cet événement
            Optional<Evaluation> evaluationExistante = evaluationDAO.findByParticipantAndEvenement(participantId, evenementId);
            
            Evaluation result;
            if (evaluationExistante.isPresent()) {
                // Modifier l'évaluation existante
                Evaluation evaluation = evaluationExistante.get();
                evaluation.setNote(note);
                evaluation.setTexte(texte != null ? texte.trim() : null);
                result = evaluationDAO.save(evaluation);
            } else {
                // Créer une nouvelle évaluation
                Evaluation evaluation = new Evaluation(note, texte != null ? texte.trim() : null, participant, evenement);
                result = evaluationDAO.save(evaluation);
            }
            
            // Committer la transaction
            em.getTransaction().commit();
            return result;
            
        } catch (Exception e) {
            // Rollback en cas d'erreur
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }
    
    /**
     * Récupérer toutes les évaluations d'un événement
     */
    public List<Evaluation> getEvaluationsEvenement(Integer evenementId) {
        return evaluationDAO.findByEvenement(evenementId);
    }
    
    /**
     * Récupérer les évaluations d'un participant
     */
    public List<Evaluation> getEvaluationsParticipant(Long participantId) {
        return evaluationDAO.findByParticipant(participantId);
    }
    
    /**
     * Calculer la note moyenne d'un événement
     */
    public Double getMoyenneEvenement(Integer evenementId) {
        return evaluationDAO.getAverageRating(evenementId);
    }
    
    /**
     * Récupérer l'évaluation d'un participant pour un événement spécifique
     */
    public Optional<Evaluation> getEvaluationParticipant(Long participantId, Integer evenementId) {
        return evaluationDAO.findByParticipantAndEvenement(participantId, evenementId);
    }
    
    /**
     * Vérifier si un participant a déjà évalué un événement
     */
    public boolean hasEvaluated(Long participantId, Integer evenementId) {
        return evaluationDAO.hasEvaluated(participantId, evenementId);
    }
    
    /**
     * Compter le nombre d'évaluations pour un événement
     */
    public Long countEvaluationsEvenement(Integer evenementId) {
        return evaluationDAO.countByEvenement(evenementId);
    }
    
    /**
     * Obtenir la distribution des notes pour un événement
     */
    public List<Object[]> getRatingDistribution(Integer evenementId) {
        return evaluationDAO.getRatingDistribution(evenementId);
    }
    
    /**
     * Supprimer une évaluation
     * Validation: seul l'auteur peut supprimer son évaluation
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void supprimerEvaluation(Integer evaluationId, Long participantId) {
        // Démarrer une transaction manuelle pour RESOURCE_LOCAL
        em.getTransaction().begin();
        
        try {
            // Vérifier que l'évaluation existe
            Evaluation evaluation = evaluationDAO.findById(evaluationId)
                .orElseThrow(() -> new IllegalArgumentException("Évaluation introuvable"));
            
            // Vérifier que le participant est bien l'auteur de l'évaluation
            if (!evaluationDAO.isOwner(evaluationId, participantId)) {
                throw new IllegalStateException("Vous ne pouvez supprimer que vos propres évaluations");
            }
            
            // Supprimer l'évaluation
            evaluationDAO.delete(evaluation);
            
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
     * Récupérer une évaluation par son ID
     */
    public Evaluation getEvaluationById(Integer evaluationId) {
        return evaluationDAO.findById(evaluationId)
            .orElseThrow(() -> new IllegalArgumentException("Évaluation introuvable"));
    }
}
