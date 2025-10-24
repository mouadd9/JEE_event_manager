package com.example.jee_event_manager.service;

import com.example.jee_event_manager.DAO.UtilisateurRepository;
import com.example.jee_event_manager.DAO.ParticipantRepository;
import com.example.jee_event_manager.DAO.OrganisateurRepository;
import com.example.jee_event_manager.model.Utilisateur;
import com.example.jee_event_manager.model.Participant;
import com.example.jee_event_manager.model.Organisateur;
import com.example.jee_event_manager.model.UserType;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Stateless
public class UtilisateurService {

    @Inject
    private UtilisateurRepository utilisateurRepository;
    
    @Inject
    private ParticipantRepository participantRepository;
    
    @Inject
    private OrganisateurRepository organisateurRepository;

    // === AUTHENTICATION METHODS (COMMENTED OUT) ===
    
   
    /**
     * Crée un nouvel utilisateur (Participant ou Organisateur)
     */
   
    public Utilisateur createUser(String nom, String email, String motDePasse, UserType userType) {
        // Vérifier si l'email existe déjà
        if (findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Un compte avec cet email existe déjà");
        }

        // Créer l'utilisateur selon le type
        Utilisateur utilisateur;
        if (userType == UserType.PARTICIPANT) {
            utilisateur = new Participant();
        } else {
            utilisateur = new Organisateur();
        }

        utilisateur.setNom(nom);
        utilisateur.setEmail(email);
        utilisateur.setMotDePasseHash(hashPassword(motDePasse));
        utilisateur.setUserType(userType);

        // Sauvegarder avec le repository approprié
        if (userType == UserType.PARTICIPANT) {
            return participantRepository.saveParticipant((Participant) utilisateur);
        } else {
            return organisateurRepository.saveOrganisateur((Organisateur) utilisateur);
        }
    }
   

    
    /**
     * Authentifie un utilisateur
     */
   
    public Utilisateur authenticate(String email, String motDePasse) {
        Optional<Utilisateur> utilisateurOpt = findByEmail(email);
        
        if (utilisateurOpt.isEmpty()) {
            return null;
        }

        Utilisateur utilisateur = utilisateurOpt.get();
        String hashedPassword = hashPassword(motDePasse);
        if (utilisateur.getMotDePasseHash().equals(hashedPassword)) {
            return utilisateur;
        }

        return null;
    }
  

    /**
     * Trouve un utilisateur par email
     */
    public Optional<Utilisateur> findByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    /**
     * Trouve un utilisateur par ID
     */
    public Optional<Utilisateur> findById(Long id) {
        return utilisateurRepository.findById(id);
    }

    /**
     * Récupère tous les utilisateurs
     */
    public List<Utilisateur> findAll() {
        return utilisateurRepository.findAll();
    }

    /**
     * Récupère tous les participants
     */
    public List<Participant> findAllParticipants() {
        return participantRepository.findAllParticipants();
    }

    /**
     * Récupère tous les organisateurs
     */
    public List<Organisateur> findAllOrganisateurs() {
        return organisateurRepository.findAllOrganisateurs();
    }

    // === ADMINISTRATION METHODS (COMMENTED OUT) ===
    

    /**
     * Met à jour un utilisateur
     */

    public Utilisateur update(Utilisateur utilisateur) {
        if (utilisateur instanceof Participant) {
            return participantRepository.update(utilisateur);
        } else if (utilisateur instanceof Organisateur) {
            return organisateurRepository.update(utilisateur);
        } else {
            return utilisateurRepository.save(utilisateur);
        }
    }



    /**
     * Supprime un utilisateur
     */

    public void delete(Long id) {
        utilisateurRepository.delete(id);
    }


    /**
     * Change le mot de passe d'un utilisateur
     */
    public void changePassword(Long userId, String ancienMotDePasse, String nouveauMotDePasse) {
        Utilisateur utilisateur = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        String hashedOldPassword = hashPassword(ancienMotDePasse);
        if (!utilisateur.getMotDePasseHash().equals(hashedOldPassword)) {
            throw new IllegalArgumentException("Ancien mot de passe incorrect");
        }

        utilisateur.setMotDePasseHash(hashPassword(nouveauMotDePasse));
        update(utilisateur);
    }

    // === PASSWORD & EMAIL UTILITY METHODS (COMMENTED OUT) ===
    
    /**
     * Hash un mot de passe avec SHA-256
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du hashage du mot de passe", e);
        }
    }

    /*
    /**
     * Vérifie si un email est disponible
     */
  
    public boolean isEmailAvailable(String email) {
        return findByEmail(email).isEmpty();
    }
   
    
  
    /**
     * Vérifie si un email existe déjà
     */
   
    public boolean emailExists(String email) {
        return findByEmail(email).isPresent();
    }
   
    
    
    /**
     * Vérifie un mot de passe contre son hash
     */

    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        String hashedInput = hashPassword(plainPassword);
        return hashedInput.equals(hashedPassword);
    }
    
    

    /**
     * Change le mot de passe d'un utilisateur (surcharge sans vérification ancien mot de passe)
     */
    public void changePassword(Long userId, String nouveauMotDePasse) {
        Utilisateur utilisateur = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        
        utilisateur.setMotDePasseHash(hashPassword(nouveauMotDePasse));
        update(utilisateur);
    }
}
