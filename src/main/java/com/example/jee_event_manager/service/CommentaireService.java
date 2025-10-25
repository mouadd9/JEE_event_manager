package com.example.jee_event_manager.service;

import com.example.jee_event_manager.DAO.CommentaireRepository;
import com.example.jee_event_manager.DAO.InscriptionRepository;
import com.example.jee_event_manager.DAO.EvenementRepository;
import com.example.jee_event_manager.DAO.ParticipantRepository;
import com.example.jee_event_manager.config.qualifiers.ParticipantQualifier;
import com.example.jee_event_manager.model.Commentaire;
import com.example.jee_event_manager.model.Evenement;
import com.example.jee_event_manager.model.Participant;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import java.util.List;


@Stateless
public class CommentaireService {
    
    @Inject
    private CommentaireRepository commentaireRepository;
    
    @Inject
    private InscriptionRepository inscriptionRepository;
    
    @Inject
    private EvenementRepository evenementRepository;
    
    @Inject
    @ParticipantQualifier
    private ParticipantRepository participantRepository;
   
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Commentaire ajouterCommentaire(Long participantId, Long evenementId, String texte) {
        // Validation: vérifier que le participant est inscrit à l'événement
        // TODO: Re-enable this validation after testing
        // if (!inscriptionRepository.isParticipantInscrit(participantId, evenementId)) {
        //     throw new IllegalStateException("Vous devez être inscrit à cet événement pour commenter");
        // }
        
        // Validation du texte
        if (texte == null || texte.trim().isEmpty()) {
            throw new IllegalArgumentException("Le texte du commentaire ne peut pas être vide");
        }
        
        if (texte.length() > 1000) {
            throw new IllegalArgumentException("Le commentaire ne peut pas dépasser 1000 caractères");
        }
        
        // Récupérer les entités
        System.out.println("DEBUG: Looking for participant ID: " + participantId);
        Participant participant = participantRepository.findParticipantById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant introuvable avec ID: " + participantId));
        
        System.out.println("DEBUG: Looking for event ID: " + evenementId);
        Evenement evenement = evenementRepository.findById(evenementId)
                .orElseThrow(() -> new IllegalArgumentException("Événement introuvable avec ID: " + evenementId));
        
        // Créer et sauvegarder le commentaire
        Commentaire commentaire = new Commentaire(texte.trim(), participant, evenement);
        return commentaireRepository.save(commentaire);
    }
    
    public List<Commentaire> getCommentairesEvenement(Long evenementId) {
        return commentaireRepository.findByEvenement(evenementId);
    }
    
    public List<Commentaire> getCommentairesParticipant(Long participantId) {
        return commentaireRepository.findByParticipant(participantId);
    }
    
    public Long countByEvenement(Long evenementId) {
        return commentaireRepository.countByEvenement(evenementId);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void supprimerCommentaire(Long commentaireId, Long participantId) {
        // Vérifier que le commentaire existe
        Commentaire commentaire = commentaireRepository.findById(commentaireId)
            .orElseThrow(() -> new IllegalArgumentException("Commentaire introuvable"));
        
        // Vérifier que le participant est bien l'auteur du commentaire
        if (!commentaire.getParticipant().getId().equals(participantId)) {
            throw new IllegalStateException("Vous ne pouvez supprimer que vos propres commentaires");
        }
        
        // Supprimer le commentaire
        commentaireRepository.delete(commentaireId);
    }
    
    public Commentaire getCommentaireById(Long commentaireId) {
        return commentaireRepository.findById(commentaireId)
            .orElseThrow(() -> new IllegalArgumentException("Commentaire introuvable"));
    }
}
