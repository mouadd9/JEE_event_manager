package com.example.jee_event_manager.service;

import com.example.jee_event_manager.DAO.AdminRepository;
import com.example.jee_event_manager.DAO.UtilisateurRepository;
import com.example.jee_event_manager.DAO.EvenementRepository;
import com.example.jee_event_manager.config.qualifiers.AdminQualifier;
import com.example.jee_event_manager.config.qualifiers.UtilisateurQualifier;
import com.example.jee_event_manager.model.*;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
public class AdminService {

    @Inject
    @AdminQualifier
    private AdminRepository adminRepository;
    
    @Inject
    @UtilisateurQualifier
    private UtilisateurRepository utilisateurRepository;
    
    @Inject
    private EvenementRepository evenementRepository;
    
    /**
     * Get dashboard statistics
     */
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        List<Utilisateur> allUsers = utilisateurRepository.findAll();
        List<Evenement> allEvents = evenementRepository.findAll();
        
        // User statistics
        long totalUsers = allUsers.size();
        long totalParticipants = allUsers.stream()
            .filter(u -> u.getUserType() == UserType.PARTICIPANT)
            .count();
        long totalOrganisateurs = allUsers.stream()
            .filter(u -> u.getUserType() == UserType.ORGANISATEUR)
            .count();
        long totalAdmins = allUsers.stream()
            .filter(u -> u.getUserType() == UserType.ADMIN)
            .count();
        long pendingOrganisateurs = allUsers.stream()
            .filter(u -> u.getUserType() == UserType.ORGANISATEUR && (u.getIsVerified() == null || !u.getIsVerified()))
            .count();
        long suspendedUsers = allUsers.stream()
            .filter(u -> u.getIsSuspended() != null && u.getIsSuspended())
            .count();
        
        stats.put("totalUsers", totalUsers);
        stats.put("totalParticipants", totalParticipants);
        stats.put("totalOrganisateurs", totalOrganisateurs);
        stats.put("totalAdmins", totalAdmins);
        stats.put("pendingOrganisateurs", pendingOrganisateurs);
        stats.put("suspendedUsers", suspendedUsers);
        
        // Event statistics
        long totalEvents = allEvents.size();
        long publishedEvents = allEvents.stream()
            .filter(e -> e.getStatut() == StatutEvenement.PUBLIE)
            .count();
        long draftEvents = allEvents.stream()
            .filter(e -> e.getStatut() == StatutEvenement.BROUILLON)
            .count();
        long cancelledEvents = allEvents.stream()
            .filter(e -> e.getStatut() == StatutEvenement.ANNULE)
            .count();
        long hiddenEvents = allEvents.stream()
            .filter(e -> e.getStatut() == StatutEvenement.CACHE)
            .count();
        
        stats.put("totalEvents", totalEvents);
        stats.put("publishedEvents", publishedEvents);
        stats.put("draftEvents", draftEvents);
        stats.put("cancelledEvents", cancelledEvents);
        stats.put("hiddenEvents", hiddenEvents);
        
        return stats;
    }
    
    /**
     * Get all users with filtering
     */
    public List<Utilisateur> getAllUsers(UserType userType, Boolean isVerified, Boolean isSuspended) {
        List<Utilisateur> users = utilisateurRepository.findAll();
        
        return users.stream()
            .filter(u -> userType == null || u.getUserType() == userType)
            .filter(u -> isVerified == null || (u.getIsVerified() != null && u.getIsVerified().equals(isVerified)))
            .filter(u -> isSuspended == null || (u.getIsSuspended() != null && u.getIsSuspended().equals(isSuspended)))
            .collect(Collectors.toList());
    }
    
    /**
     * Get pending organisateurs (not verified)
     */
    public List<Utilisateur> getPendingOrganisateurs() {
        return utilisateurRepository.findAll().stream()
            .filter(u -> u.getUserType() == UserType.ORGANISATEUR)
            .filter(u -> u.getIsVerified() == null || !u.getIsVerified())
            .collect(Collectors.toList());
    }
    
    /**
     * Verify/validate an organisateur
     */
    public void verifyOrganisateur(Long userId) {
        Optional<Utilisateur> userOpt = utilisateurRepository.findById(userId);
        if (userOpt.isPresent()) {
            Utilisateur user = userOpt.get();
            if (user.getUserType() == UserType.ORGANISATEUR) {
                user.setIsVerified(true);
                utilisateurRepository.update(user);
            }
        }
    }
    
    /**
     * Suspend a user account
     */
    public void suspendUser(Long userId, String reason) {
        Optional<Utilisateur> userOpt = utilisateurRepository.findById(userId);
        if (userOpt.isPresent()) {
            Utilisateur user = userOpt.get();
            user.setIsSuspended(true);
            user.setSuspensionReason(reason);
            user.setIsActive(false);
            utilisateurRepository.update(user);
        }
    }
    
    /**
     * Activate/unsuspend a user account
     */
    public void activateUser(Long userId) {
        Optional<Utilisateur> userOpt = utilisateurRepository.findById(userId);
        if (userOpt.isPresent()) {
            Utilisateur user = userOpt.get();
            user.setIsSuspended(false);
            user.setSuspensionReason(null);
            user.setIsActive(true);
            utilisateurRepository.update(user);
        }
    }
    
    /**
     * Delete a user account
     */
    public void deleteUser(Long userId) {
        utilisateurRepository.delete(userId);
    }
    
    /**
     * Get all events for moderation
     */
    public List<Evenement> getAllEvents(StatutEvenement statut) {
        List<Evenement> events = evenementRepository.findAll();
        
        if (statut != null) {
            return events.stream()
                .filter(e -> e.getStatut() == statut)
                .collect(Collectors.toList());
        }
        
        return events;
    }
    
    /**
     * Hide an event (moderation)
     */
    public void hideEvent(Long eventId) {
        Optional<Evenement> eventOpt = evenementRepository.findById(eventId);
        if (eventOpt.isPresent()) {
            Evenement event = eventOpt.get();
            event.setStatut(StatutEvenement.CACHE);
            evenementRepository.update(event);
        }
    }
    
    /**
     * Unhide/publish an event
     */
    public void unhideEvent(Long eventId) {
        Optional<Evenement> eventOpt = evenementRepository.findById(eventId);
        if (eventOpt.isPresent()) {
            Evenement event = eventOpt.get();
            event.setStatut(StatutEvenement.PUBLIE);
            evenementRepository.update(event);
        }
    }
    
    /**
     * Delete an event
     */
    public void deleteEvent(Long eventId) {
        evenementRepository.delete(eventId);
    }
    
    /**
     * Get user by ID
     */
    public Optional<Utilisateur> getUserById(Long userId) {
        return utilisateurRepository.findById(userId);
    }
    
    /**
     * Get event by ID
     */
    public Optional<Evenement> getEventById(Long eventId) {
        return evenementRepository.findById(eventId);
    }
}
