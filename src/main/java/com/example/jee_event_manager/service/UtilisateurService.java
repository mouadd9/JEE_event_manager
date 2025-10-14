package com.example.jee_event_manager.service;

import com.example.jee_event_manager.model.Utilisateur;
import com.example.jee_event_manager.model.Participant;
import com.example.jee_event_manager.model.Organisateur;
import com.example.jee_event_manager.model.UserType;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@ApplicationScoped
public class UtilisateurService {

    @Inject
    private EntityManager em;

    /**
     * Crée un nouvel utilisateur (Participant ou Organisateur)
     */
    public Utilisateur createUser(String nom, String email, String motDePasse, UserType userType) {
        // Vérifier si l'email existe déjà
        if (findByEmail(email) != null) {
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

        em.persist(utilisateur);
        return utilisateur;
    }

    /**
     * Authentifie un utilisateur
     */
    public Utilisateur authenticate(String email, String motDePasse) {
        Utilisateur utilisateur = findByEmail(email);
        
        if (utilisateur == null) {
            return null;
        }

        String hashedPassword = hashPassword(motDePasse);
        if (utilisateur.getMotDePasseHash().equals(hashedPassword)) {
            return utilisateur;
        }

        return null;
    }

    /**
     * Trouve un utilisateur par email
     */
    public Utilisateur findByEmail(String email) {
        try {
            TypedQuery<Utilisateur> query = em.createQuery(
                "SELECT u FROM Utilisateur u WHERE u.email = :email", 
                Utilisateur.class
            );
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Trouve un utilisateur par ID
     */
    public Utilisateur findById(Long id) {
        return em.find(Utilisateur.class, id);
    }

    /**
     * Récupère tous les utilisateurs
     */
    public List<Utilisateur> findAll() {
        TypedQuery<Utilisateur> query = em.createQuery(
            "SELECT u FROM Utilisateur u", 
            Utilisateur.class
        );
        return query.getResultList();
    }

    /**
     * Récupère tous les participants
     */
    public List<Participant> findAllParticipants() {
        TypedQuery<Participant> query = em.createQuery(
            "SELECT p FROM Participant p", 
            Participant.class
        );
        return query.getResultList();
    }

    /**
     * Récupère tous les organisateurs
     */
    public List<Organisateur> findAllOrganisateurs() {
        TypedQuery<Organisateur> query = em.createQuery(
            "SELECT o FROM Organisateur o", 
            Organisateur.class
        );
        return query.getResultList();
    }

    /**
     * Met à jour un utilisateur
     */
    public Utilisateur update(Utilisateur utilisateur) {
        return em.merge(utilisateur);
    }

    /**
     * Supprime un utilisateur
     */
    public void delete(Long id) {
        Utilisateur utilisateur = findById(id);
        if (utilisateur != null) {
            em.remove(utilisateur);
        }
    }

    /**
     * Change le mot de passe d'un utilisateur
     */
    public void changePassword(Long userId, String ancienMotDePasse, String nouveauMotDePasse) {
        Utilisateur utilisateur = findById(userId);
        
        if (utilisateur == null) {
            throw new IllegalArgumentException("Utilisateur introuvable");
        }

        String hashedOldPassword = hashPassword(ancienMotDePasse);
        if (!utilisateur.getMotDePasseHash().equals(hashedOldPassword)) {
            throw new IllegalArgumentException("Ancien mot de passe incorrect");
        }

        utilisateur.setMotDePasseHash(hashPassword(nouveauMotDePasse));
        em.merge(utilisateur);
    }

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

    /**
     * Vérifie si un email est disponible
     */
    public boolean isEmailAvailable(String email) {
        return findByEmail(email) == null;
    }
}
