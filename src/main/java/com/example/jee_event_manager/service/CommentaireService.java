package com.example.jee_event_manager.service;

import com.example.jee_event_manager.DAO.CommentaireDAO;
import com.example.jee_event_manager.DAO.InscriptionDAO;
import com.example.jee_event_manager.model.Commentaire;
import com.example.jee_event_manager.model.Evenement;
import com.example.jee_event_manager.model.Participant;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;


@Stateless
public class CommentaireService {
    
    @Inject
    private CommentaireDAO commentaireDAO;
    
    @Inject
    private InscriptionDAO inscriptionDAO;
    
    @Inject
    private EntityManager em;
   
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Commentaire ajouterCommentaire(Long participantId, Integer evenementId, String texte) {
        // Démarrer une transaction manuelle pour RESOURCE_LOCAL
        em.getTransaction().begin();
        
        try {
            // Validation: vérifier que le participant est inscrit à l'événement
            if (!inscriptionDAO.isParticipantInscrit(participantId, evenementId)) {
                throw new IllegalStateException("Vous devez être inscrit à cet événement pour commenter");
            }
            
            // Validation du texte
            if (texte == null || texte.trim().isEmpty()) {
                throw new IllegalArgumentException("Le texte du commentaire ne peut pas être vide");
            }
            
            if (texte.length() > 1000) {
                throw new IllegalArgumentException("Le commentaire ne peut pas dépasser 1000 caractères");
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
            
            // Créer et sauvegarder le commentaire
            Commentaire commentaire = new Commentaire(texte.trim(), participant, evenement);
            Commentaire result = commentaireDAO.save(commentaire);
            
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
    
    public List<Commentaire> getCommentairesEvenement(Integer evenementId) {
        return commentaireDAO.findByEvenement(evenementId);
    }
    public List<Commentaire> getCommentairesParticipant(Long participantId) {
        return commentaireDAO.findByParticipant(participantId);
    }
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void supprimerCommentaire(Integer commentaireId, Long participantId) {
        // Démarrer une transaction manuelle pour RESOURCE_LOCAL
        em.getTransaction().begin();
        
        try {
            // Vérifier que le commentaire existe
            Commentaire commentaire = commentaireDAO.findById(commentaireId)
                .orElseThrow(() -> new IllegalArgumentException("Commentaire introuvable"));
            
            // Vérifier que le participant est bien l'auteur du commentaire
            if (!commentaireDAO.isOwner(commentaireId, participantId)) {
                throw new IllegalStateException("Vous ne pouvez supprimer que vos propres commentaires");
            }
            
            // Supprimer le commentaire
            commentaireDAO.delete(commentaire);
            
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
    
    public Long countCommentairesEvenement(Integer evenementId) {
        return commentaireDAO.countByEvenement(evenementId);
    }
    public Commentaire getCommentaireById(Integer commentaireId) {
        return commentaireDAO.findById(commentaireId)
            .orElseThrow(() -> new IllegalArgumentException("Commentaire introuvable"));
    }
}
