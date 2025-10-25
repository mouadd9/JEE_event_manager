package com.example.jee_event_manager.service;

import com.example.jee_event_manager.DAO.*;
import com.example.jee_event_manager.config.qualifiers.ParticipantQualifier;
import com.example.jee_event_manager.dto.*;
import com.example.jee_event_manager.model.*;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class OrganisateurAnalyticsService {
    
    @Inject 
    private InscriptionRepository inscriptionRepo;
    
    @Inject 
    private CommentaireRepository commentaireRepo;
    
    @Inject 
    private EvaluationRepository evaluationRepo;
    
    @Inject 
    private EvenementRepository evenementRepo;
    
    @Inject 
    @ParticipantQualifier
    private ParticipantRepository participantRepo;
    
    /**
     * Récupère les analytics complètes d'un événement pour un organisateur
     */
    public EventAnalyticsDTO getEventAnalytics(Long evenementId, Long organisateurId) {
        // Vérifier que l'organisateur possède l'événement
        Evenement evenement = evenementRepo.findById(evenementId)
                .orElseThrow(() -> new EntityNotFoundException("Événement introuvable"));
        
        if (!evenement.getOrganisateur().getId().equals(organisateurId)) {
            throw new EntityNotFoundException("Vous n'avez pas accès à cet événement");
        }
        
        EventAnalyticsDTO analytics = new EventAnalyticsDTO();
        
        // Informations de base de l'événement
        analytics.setEvenementId(evenement.getId());
        analytics.setTitre(evenement.getTitre());
        analytics.setDateDebut(evenement.getDateDebut());
        analytics.setDateFin(evenement.getDateFin());
        analytics.setLieu(evenement.getLieu());
        analytics.setCapacite(evenement.getCapacite());
        analytics.setImageUrl(evenement.getImageUrl());
        
        // Statistiques des inscriptions
        List<Inscription> inscriptions = inscriptionRepo.findByEvenement(evenementId);
        analytics.setNombreInscrits((long) inscriptions.size());
        analytics.setCapaciteDisponible(evenement.getCapacite() - inscriptions.size());
        
        // Statistiques des évaluations
        List<Evaluation> evaluations = evaluationRepo.findByEvenement(evenementId);
        analytics.setNombreEvaluations((long) evaluations.size());
        
        if (!evaluations.isEmpty()) {
            double moyenne = evaluations.stream()
                    .mapToInt(Evaluation::getNote)
                    .average()
                    .orElse(0.0);
            analytics.setNoteMoyenne(moyenne);
        } else {
            analytics.setNoteMoyenne(0.0);
        }
        
        // Statistiques des commentaires
        List<Commentaire> commentaires = commentaireRepo.findByEvenement(evenementId);
        analytics.setNombreCommentaires((long) commentaires.size());
        
        // Détails des participants
        List<ParticipantDetailsDTO> participants = getEventParticipants(evenementId);
        analytics.setParticipants(participants);
        
        // Commentaires avec infos participants
        List<CommentaireWithParticipantDTO> commentairesWithParticipants = getCommentairesWithParticipants(evenementId);
        analytics.setCommentaires(commentairesWithParticipants);
        
        // Évaluations avec infos participants
        List<EvaluationWithParticipantDTO> evaluationsWithParticipants = getEvaluationsWithParticipants(evenementId);
        analytics.setEvaluations(evaluationsWithParticipants);
        
        return analytics;
    }
    
    /**
     * Récupère la liste des participants d'un événement avec leurs détails
     */
    public List<ParticipantDetailsDTO> getEventParticipants(Long evenementId) {
        List<Inscription> inscriptions = inscriptionRepo.findByEvenement(evenementId);
        
        return inscriptions.stream()
                .map(inscription -> {
                    Participant participant = inscription.getParticipant();
                    ParticipantDetailsDTO dto = new ParticipantDetailsDTO();
                    
                    dto.setId(participant.getId());
                    dto.setNom(participant.getNom());
                    dto.setEmail(participant.getEmail());
                    dto.setCreatedAt(participant.getCreatedAt());
                    dto.setTelephone(participant.getTelephone());
                    dto.setPreferences(participant.getPreferences());
                    
                    // Statistiques du participant
                    dto.setNombreInscriptions((long) inscriptionRepo.findByParticipant(participant.getId()).size());
                    dto.setNombreCommentaires((long) commentaireRepo.findByParticipant(participant.getId()).size());
                    dto.setNombreEvaluations((long) evaluationRepo.findByParticipant(participant.getId()).size());
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère les commentaires d'un événement avec les infos des participants
     */
    public List<CommentaireWithParticipantDTO> getCommentairesWithParticipants(Long evenementId) {
        List<Commentaire> commentaires = commentaireRepo.findByEvenement(evenementId);
        
        return commentaires.stream()
                .map(commentaire -> {
                    Participant participant = commentaire.getParticipant();
                    CommentaireWithParticipantDTO dto = new CommentaireWithParticipantDTO();
                    
                    dto.setId(commentaire.getId());
                    dto.setTexte(commentaire.getTexte());
                    dto.setCreatedAt(commentaire.getCreatedAt());
                    
                    // Infos participant
                    dto.setParticipantId(participant.getId());
                    dto.setParticipantNom(participant.getNom());
                    dto.setParticipantEmail(participant.getEmail());
                    
                    // TODO: Ajouter les réponses quand les entités seront créées
                    // dto.setReponses(getCommentaireReponses(commentaire.getId()));
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère les évaluations d'un événement avec les infos des participants
     */
    public List<EvaluationWithParticipantDTO> getEvaluationsWithParticipants(Long evenementId) {
        List<Evaluation> evaluations = evaluationRepo.findByEvenement(evenementId);
        
        return evaluations.stream()
                .map(evaluation -> {
                    Participant participant = evaluation.getParticipant();
                    EvaluationWithParticipantDTO dto = new EvaluationWithParticipantDTO();
                    
                    dto.setId(evaluation.getId());
                    dto.setNote(evaluation.getNote());
                    dto.setCommentaire(evaluation.getTexte());
                    dto.setCreatedAt(evaluation.getCreatedAt());
                    
                    // Infos participant
                    dto.setParticipantId(participant.getId());
                    dto.setParticipantNom(participant.getNom());
                    dto.setParticipantEmail(participant.getEmail());
                    
                    // TODO: Ajouter les réponses quand les entités seront créées
                    // dto.setReponses(getEvaluationReponses(evaluation.getId()));
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère les détails d'un participant spécifique
     */
    public ParticipantDetailsDTO getParticipantDetails(Long participantId) {
        Participant participant = participantRepo.findParticipantById(participantId)
                .orElseThrow(() -> new EntityNotFoundException("Participant introuvable"));
        
        ParticipantDetailsDTO dto = new ParticipantDetailsDTO();
        dto.setId(participant.getId());
        dto.setNom(participant.getNom());
        dto.setEmail(participant.getEmail());
        dto.setCreatedAt(participant.getCreatedAt());
        dto.setTelephone(participant.getTelephone());
        dto.setPreferences(participant.getPreferences());
        
        // Statistiques du participant
        dto.setNombreInscriptions((long) inscriptionRepo.findByParticipant(participantId).size());
        dto.setNombreCommentaires((long) commentaireRepo.findByParticipant(participantId).size());
        dto.setNombreEvaluations((long) evaluationRepo.findByParticipant(participantId).size());
        
        return dto;
    }
}
