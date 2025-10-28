package com.example.jee_event_manager.service;

import com.example.jee_event_manager.DAO.BilletRepository;
import com.example.jee_event_manager.model.Billet;
import com.example.jee_event_manager.model.Inscription;
import com.example.jee_event_manager.model.StatutBillet;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Stateless
public class BilletService {
    
    private static final Logger logger = Logger.getLogger(BilletService.class.getName());
    
    @Inject
    private BilletRepository billetRepository;
    
    @Inject
    private BilletPdfService billetPdfService;
    
    @Inject
    private EmailService emailService;
    
    /**
     * Génère un billet pour une inscription
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Billet genererBilletPourInscription(Inscription inscription) {
        logger.info("Génération du billet pour l'inscription ID: " + inscription.getId());
        
        try {
            // Vérifier qu'il n'existe pas déjà un billet pour cette inscription
            List<Billet> billetsExistants = billetRepository.findByInscriptionId(inscription.getId());
            if (!billetsExistants.isEmpty()) {
                logger.warning("Un billet existe déjà pour cette inscription: " + inscription.getId());
                return billetsExistants.get(0);
            }
            
            // Générer le billet PDF
            Billet billet = billetPdfService.genererBillet(inscription);
            
            // Sauvegarder en base
            billet = billetRepository.save(billet);
            
            logger.info("Billet généré avec succès: " + billet.getNumeroBillet());
            return billet;
            
        } catch (Exception e) {
            logger.severe("Erreur lors de la génération du billet: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la génération du billet", e);
        }
    }
    
    /**
     * Génère un billet et l'envoie par email
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Billet genererEtEnvoyerBillet(Inscription inscription) {
        Billet billet = genererBilletPourInscription(inscription);
        
        try {
            // Envoyer le billet par email
            envoyerBilletParEmail(billet);
            logger.info("Billet envoyé par email: " + billet.getNumeroBillet());
        } catch (Exception e) {
            logger.warning("Erreur lors de l'envoi du billet par email: " + e.getMessage());
            // Ne pas faire échouer la transaction pour l'envoi d'email
        }
        
        return billet;
    }
    
    /**
     * Envoie un billet par email
     */
    public void envoyerBilletParEmail(Billet billet) throws IOException {
        Inscription inscription = billet.getInscription();
        String email = inscription.getParticipant().getEmail();
        String nom = inscription.getParticipant().getNom();
        String eventTitle = inscription.getEvenement().getTitre();
        
        // Envoyer l'email de confirmation de billet
        emailService.sendTicketConfirmationEmail(email, nom, eventTitle, billet.getNumeroBillet(), billet.getTypeBillet());
        
        logger.info("Email de billet envoyé à: " + email);
    }
    
    /**
     * Récupère un billet par son ID
     */
    public Optional<Billet> findById(Long id) {
        return billetRepository.findById(id);
    }
    
    /**
     * Récupère un billet par son numéro
     */
    public Optional<Billet> findByNumeroBillet(String numeroBillet) {
        return billetRepository.findByNumeroBillet(numeroBillet);
    }
    
    /**
     * Récupère tous les billets d'un participant
     */
    public List<Billet> getBilletsByParticipant(Long participantId) {
        return billetRepository.findByParticipantId(participantId);
    }
    
    /**
     * Récupère les billets valides d'un participant
     */
    public List<Billet> getBilletsValidesByParticipant(Long participantId) {
        return billetRepository.findBilletsValidesByParticipant(participantId);
    }
    
    /**
     * Récupère tous les billets d'un événement
     */
    public List<Billet> getBilletsByEvenement(Long evenementId) {
        return billetRepository.findByEvenementId(evenementId);
    }
    
    /**
     * Marque un billet comme utilisé
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void marquerBilletUtilise(Long billetId) {
        Optional<Billet> billetOpt = billetRepository.findById(billetId);
        if (billetOpt.isPresent()) {
            Billet billet = billetOpt.get();
            billet.marquerCommeUtilise();
            billetRepository.save(billet);
            logger.info("Billet marqué comme utilisé: " + billet.getNumeroBillet());
        }
    }
    
    /**
     * Annule un billet
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void annulerBillet(Long billetId) {
        Optional<Billet> billetOpt = billetRepository.findById(billetId);
        if (billetOpt.isPresent()) {
            Billet billet = billetOpt.get();
            billet.setStatut(StatutBillet.ANNULE);
            billetRepository.save(billet);
            logger.info("Billet annulé: " + billet.getNumeroBillet());
        }
    }
    
    /**
     * Supprime un billet
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void supprimerBillet(Long billetId) {
        Optional<Billet> billetOpt = billetRepository.findById(billetId);
        if (billetOpt.isPresent()) {
            Billet billet = billetOpt.get();
            
            // Supprimer le fichier PDF
            if (billet.getCheminFichier() != null) {
                billetPdfService.supprimerPdf(billet.getCheminFichier());
            }
            
            // Supprimer de la base
            billetRepository.delete(billet);
            logger.info("Billet supprimé: " + billet.getNumeroBillet());
        }
    }
    
    /**
     * Récupère le contenu PDF d'un billet
     */
    public byte[] getPdfContent(Long billetId) throws IOException {
        Optional<Billet> billetOpt = billetRepository.findById(billetId);
        if (billetOpt.isPresent()) {
            Billet billet = billetOpt.get();
            if (billet.getCheminFichier() != null) {
                return billetPdfService.lirePdf(billet.getCheminFichier());
            }
        }
        throw new IOException("Billet ou fichier PDF non trouvé");
    }
}
