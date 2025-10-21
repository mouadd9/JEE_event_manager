package ma.ensa.tetouan.eventmanagement.service;

import ma.ensa.tetouan.eventmanagement.dao.*;
import ma.ensa.tetouan.eventmanagement.exception.BusinessException;
import ma.ensa.tetouan.eventmanagement.exception.InvalidEventStateException;
import ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException;
import ma.ensa.tetouan.eventmanagement.model.Evenement;
import ma.ensa.tetouan.eventmanagement.model.Organisateur;
import ma.ensa.tetouan.eventmanagement.model.StatutEvenement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation du service de gestion des événements.
 *
 * @author ENSA Tétouan
 */
public class EvenementServiceImpl implements EvenementService {

    private static final Logger logger = LoggerFactory.getLogger(EvenementServiceImpl.class);

    private final EvenementDAO evenementDAO;
    private final OrganisateurDAO organisateurDAO;
    private final CategorieDAO categorieDAO;

    /**
     * Constructeur par défaut
     */
    public EvenementServiceImpl() {
        this.evenementDAO = new EvenementDAOImpl();
        this.organisateurDAO = new OrganisateurDAOImpl();
        this.categorieDAO = new CategorieDAOImpl();
    }

    /**
     * Constructeur pour injection de dépendances (tests)
     */
    public EvenementServiceImpl(EvenementDAO evenementDAO, OrganisateurDAO organisateurDAO, CategorieDAO categorieDAO) {
        this.evenementDAO = evenementDAO;
        this.organisateurDAO = organisateurDAO;
        this.categorieDAO = categorieDAO;
    }

    @Override
    public Evenement createEvenement(Evenement evenement, Long organisateurId) {
        logger.info("Création d'un événement par l'organisateur ID: {}", organisateurId);

        // 1. Valider les données
        validateEvenementForCreation(evenement);

        // 2. Vérifier que l'organisateur existe
        Optional<Organisateur> organisateurOpt = organisateurDAO.findById(organisateurId);
        if (!organisateurOpt.isPresent()) {
            logger.error("Organisateur non trouvé: ID={}", organisateurId);
            throw new ResourceNotFoundException("Organisateur", organisateurId);
        }

        Organisateur organisateur = organisateurOpt.get();

        // 3. Initialiser l'événement
        evenement.setOrganisateur(organisateur);
        evenement.setStatut(StatutEvenement.BROUILLON);
        evenement.setDateCreation(LocalDateTime.now());
        evenement.setPlacesDisponibles(evenement.getCapacite());
        evenement.setNombreVues(0);
        evenement.setNombreInscriptions(0);
        evenement.setNoteMoyenne(0.0);

        // 4. Sauvegarder
        try {
            Evenement created = evenementDAO.save(evenement);
            logger.info("Événement créé avec succès: ID={}, Titre='{}'", created.getId(), created.getTitre());
            return created;
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'événement", e);
            throw new BusinessException("Erreur lors de la création de l'événement", e);
        }
    }

    @Override
    public Evenement updateEvenement(Long evenementId, Evenement updatedData, Long organisateurId) {
        logger.info("Mise à jour de l'événement ID: {} par l'organisateur ID: {}", evenementId, organisateurId);

        // 1. Récupérer l'événement existant
        Evenement existing = getEvenementById(evenementId);

        // 2. Vérifier la propriété
        checkOwnership(existing, organisateurId);

        // 3. Vérifier que l'événement peut être modifié
        if (existing.getStatut() == StatutEvenement.TERMINE || existing.getStatut() == StatutEvenement.ANNULE) {
            throw new InvalidEventStateException("Impossible de modifier un événement " +
                existing.getStatut().getLibelle().toLowerCase());
        }

        // 4. Mettre à jour les champs autorisés
        if (updatedData.getTitre() != null && !updatedData.getTitre().trim().isEmpty()) {
            existing.setTitre(updatedData.getTitre());
        }

        if (updatedData.getDescription() != null && !updatedData.getDescription().trim().isEmpty()) {
            existing.setDescription(updatedData.getDescription());
        }

        if (updatedData.getDateDebut() != null && updatedData.getDateFin() != null) {
            validateDates(updatedData.getDateDebut(), updatedData.getDateFin());
            existing.setDateDebut(updatedData.getDateDebut());
            existing.setDateFin(updatedData.getDateFin());
        }

        if (updatedData.getLieu() != null && !updatedData.getLieu().trim().isEmpty()) {
            existing.setLieu(updatedData.getLieu());
        }

        if (updatedData.getAdresseComplete() != null) {
            existing.setAdresseComplete(updatedData.getAdresseComplete());
        }

        if (updatedData.getCapacite() != null && updatedData.getCapacite() > 0) {
            // Ajuster les places disponibles proportionnellement
            int difference = updatedData.getCapacite() - existing.getCapacite();
            existing.setCapacite(updatedData.getCapacite());
            existing.setPlacesDisponibles(existing.getPlacesDisponibles() + difference);
        }

        if (updatedData.getPrix() != null) {
            existing.setPrix(updatedData.getPrix());
        }

        if (updatedData.getGratuit() != null) {
            existing.setGratuit(updatedData.getGratuit());
        }

        if (updatedData.getImageUrl() != null) {
            existing.setImageUrl(updatedData.getImageUrl());
        }

        existing.setDateModification(LocalDateTime.now());

        // 5. Sauvegarder
        try {
            Evenement updated = evenementDAO.update(existing);
            logger.info("Événement mis à jour avec succès: ID={}", evenementId);
            return updated;
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de l'événement", e);
            throw new BusinessException("Erreur lors de la mise à jour de l'événement", e);
        }
    }

    @Override
    public void deleteEvenement(Long evenementId, Long organisateurId) {
        logger.info("Suppression de l'événement ID: {} par l'organisateur ID: {}", evenementId, organisateurId);

        // 1. Récupérer l'événement
        Evenement evenement = getEvenementById(evenementId);

        // 2. Vérifier la propriété
        checkOwnership(evenement, organisateurId);

        // 3. Vérifier que l'événement est en mode brouillon
        if (evenement.getStatut() != StatutEvenement.BROUILLON) {
            logger.warn("Tentative de suppression d'un événement publié: ID={}", evenementId);
            throw new InvalidEventStateException("Seuls les événements en mode brouillon peuvent être supprimés");
        }

        // 4. Supprimer
        try {
            evenementDAO.delete(evenement);
            logger.info("Événement supprimé avec succès: ID={}", evenementId);
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de l'événement", e);
            throw new BusinessException("Erreur lors de la suppression de l'événement", e);
        }
    }

    @Override
    public Evenement publishEvenement(Long evenementId, Long organisateurId) {
        logger.info("Publication de l'événement ID: {} par l'organisateur ID: {}", evenementId, organisateurId);

        // 1. Récupérer l'événement
        Evenement evenement = getEvenementById(evenementId);

        // 2. Vérifier la propriété
        checkOwnership(evenement, organisateurId);

        // 3. Vérifier que l'événement est en mode brouillon
        if (evenement.getStatut() != StatutEvenement.BROUILLON) {
            throw new InvalidEventStateException(
                evenement.getStatut().toString(), StatutEvenement.PUBLIE.toString());
        }

        // 4. Valider que tous les champs requis sont remplis
        validateEvenementForPublication(evenement);

        // 5. Changer le statut
        evenement.setStatut(StatutEvenement.PUBLIE);
        evenement.setDateModification(LocalDateTime.now());

        // 6. Sauvegarder
        try {
            Evenement published = evenementDAO.update(evenement);
            logger.info("Événement publié avec succès: ID={}", evenementId);
            return published;
        } catch (Exception e) {
            logger.error("Erreur lors de la publication de l'événement", e);
            throw new BusinessException("Erreur lors de la publication de l'événement", e);
        }
    }

    @Override
    public Evenement annulerEvenement(Long evenementId, Long organisateurId, String raison) {
        logger.info("Annulation de l'événement ID: {} par l'organisateur ID: {}", evenementId, organisateurId);

        // 1. Récupérer l'événement
        Evenement evenement = getEvenementById(evenementId);

        // 2. Vérifier la propriété
        checkOwnership(evenement, organisateurId);

        // 3. Vérifier que l'événement est publié
        if (evenement.getStatut() != StatutEvenement.PUBLIE) {
            throw new InvalidEventStateException("Seuls les événements publiés peuvent être annulés");
        }

        // 4. Changer le statut
        evenement.setStatut(StatutEvenement.ANNULE);
        evenement.setDateModification(LocalDateTime.now());

        // 5. Sauvegarder
        try {
            Evenement cancelled = evenementDAO.update(evenement);
            logger.info("Événement annulé avec succès: ID={}. Raison: {}", evenementId, raison);

            // TODO: Notifier tous les participants inscrits
            logger.info("Notification d'annulation à envoyer aux {} participants inscrits",
                       evenement.getNombreInscriptions());

            return cancelled;
        } catch (Exception e) {
            logger.error("Erreur lors de l'annulation de l'événement", e);
            throw new BusinessException("Erreur lors de l'annulation de l'événement", e);
        }
    }

    @Override
    public Evenement terminerEvenement(Long evenementId) {
        logger.info("Terminaison automatique de l'événement ID: {}", evenementId);

        Evenement evenement = getEvenementById(evenementId);

        if (evenement.getStatut() == StatutEvenement.PUBLIE) {
            evenement.setStatut(StatutEvenement.TERMINE);
            evenement.setDateModification(LocalDateTime.now());

            try {
                Evenement terminated = evenementDAO.update(evenement);
                logger.info("Événement terminé avec succès: ID={}", evenementId);
                return terminated;
            } catch (Exception e) {
                logger.error("Erreur lors de la terminaison de l'événement", e);
                throw new BusinessException("Erreur lors de la terminaison de l'événement", e);
            }
        }

        return evenement;
    }

    @Override
    public Evenement getEvenementById(Long evenementId) {
        logger.debug("Récupération de l'événement ID: {}", evenementId);

        if (evenementId == null) {
            throw new IllegalArgumentException("L'ID de l'événement ne peut pas être null");
        }

        Optional<Evenement> evenementOpt = evenementDAO.findById(evenementId);
        if (!evenementOpt.isPresent()) {
            logger.warn("Événement non trouvé: ID={}", evenementId);
            throw new ResourceNotFoundException("Événement", evenementId);
        }

        return evenementOpt.get();
    }

    @Override
    public List<Evenement> getAllPublishedEvents(int page, int pageSize) {
        logger.debug("Récupération des événements publiés - page: {}, taille: {}", page, pageSize);

        try {
            List<Evenement> events = evenementDAO.findByStatut(StatutEvenement.PUBLIE);
            // TODO: Implémenter la pagination manuelle si nécessaire
            return events;
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des événements publiés", e);
            throw new BusinessException("Erreur lors de la récupération des événements", e);
        }
    }

    @Override
    public List<Evenement> searchEvenements(String keyword, LocalDate startDate, LocalDate endDate, Long categorieId) {
        logger.debug("Recherche d'événements - keyword: {}, dates: {} - {}, catégorie: {}",
                    keyword, startDate, endDate, categorieId);

        try {
            LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
            LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;

            List<Evenement> events;

            if (categorieId != null) {
                events = evenementDAO.findByCategorie(categorieId);
                // Filtrer par dates et keyword si nécessaire
            } else {
                events = evenementDAO.searchEvents(keyword, startDateTime, endDateTime);
            }

            logger.info("Recherche terminée: {} événement(s) trouvé(s)", events.size());
            return events;
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche d'événements", e);
            throw new BusinessException("Erreur lors de la recherche d'événements", e);
        }
    }

    @Override
    public List<Evenement> getUpcomingEvents(int page, int pageSize) {
        logger.debug("Récupération des événements à venir");

        try {
            return evenementDAO.findUpcomingEvents();
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des événements à venir", e);
            throw new BusinessException("Erreur lors de la récupération des événements", e);
        }
    }

    @Override
    public List<Evenement> getEvenementsByOrganisateur(Long organisateurId) {
        logger.debug("Récupération des événements de l'organisateur ID: {}", organisateurId);

        if (organisateurId == null) {
            throw new IllegalArgumentException("L'ID de l'organisateur ne peut pas être null");
        }

        try {
            return evenementDAO.findByOrganisateur(organisateurId);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des événements de l'organisateur", e);
            throw new BusinessException("Erreur lors de la récupération des événements", e);
        }
    }

    @Override
    public List<Evenement> getEvenementsByCategorie(Long categorieId) {
        logger.debug("Récupération des événements de la catégorie ID: {}", categorieId);

        if (categorieId == null) {
            throw new IllegalArgumentException("L'ID de la catégorie ne peut pas être null");
        }

        try {
            return evenementDAO.findByCategorie(categorieId);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des événements par catégorie", e);
            throw new BusinessException("Erreur lors de la récupération des événements", e);
        }
    }

    @Override
    public List<Evenement> getMostPopularEvents(int limit) {
        logger.debug("Récupération des {} événements les plus populaires", limit);

        try {
            return evenementDAO.findMostPopular(limit);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des événements populaires", e);
            throw new BusinessException("Erreur lors de la récupération des événements", e);
        }
    }

    @Override
    public void incrementViews(Long evenementId) {
        logger.debug("Incrémentation des vues pour l'événement ID: {}", evenementId);

        Evenement evenement = getEvenementById(evenementId);
        evenement.incrementerVues();

        try {
            evenementDAO.update(evenement);
        } catch (Exception e) {
            logger.error("Erreur lors de l'incrémentation des vues", e);
            // Ne pas échouer pour cette erreur non critique
        }
    }

    @Override
    public long getTotalPublishedEvents() {
        try {
            List<Evenement> published = evenementDAO.findByStatut(StatutEvenement.PUBLIE);
            return published.size();
        } catch (Exception e) {
            logger.error("Erreur lors du comptage des événements publiés", e);
            throw new BusinessException("Erreur lors du comptage des événements", e);
        }
    }

    // ========== Méthodes privées de validation ==========

    /**
     * Valide un événement pour la création.
     */
    private void validateEvenementForCreation(Evenement evenement) {
        if (evenement == null) {
            throw new IllegalArgumentException("L'événement ne peut pas être null");
        }

        // Titre
        if (evenement.getTitre() == null || evenement.getTitre().trim().isEmpty()) {
            throw new BusinessException("Le titre est obligatoire");
        }
        if (evenement.getTitre().length() < 5 || evenement.getTitre().length() > 200) {
            throw new BusinessException("Le titre doit contenir entre 5 et 200 caractères");
        }

        // Description
        if (evenement.getDescription() == null || evenement.getDescription().trim().isEmpty()) {
            throw new BusinessException("La description est obligatoire");
        }
        if (evenement.getDescription().length() < 20) {
            throw new BusinessException("La description doit contenir au moins 20 caractères");
        }

        // Dates
        if (evenement.getDateDebut() == null || evenement.getDateFin() == null) {
            throw new BusinessException("Les dates de début et de fin sont obligatoires");
        }
        validateDates(evenement.getDateDebut(), evenement.getDateFin());

        // Lieu
        if (evenement.getLieu() == null || evenement.getLieu().trim().isEmpty()) {
            throw new BusinessException("Le lieu est obligatoire");
        }

        // Capacité
        if (evenement.getCapacite() == null || evenement.getCapacite() <= 0) {
            throw new BusinessException("La capacité doit être supérieure à 0");
        }
    }

    /**
     * Valide un événement pour la publication.
     */
    private void validateEvenementForPublication(Evenement evenement) {
        validateEvenementForCreation(evenement);

        // Vérifier que l'événement est dans le futur
        if (evenement.getDateDebut().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Impossible de publier un événement dont la date de début est passée");
        }
    }

    /**
     * Valide les dates d'un événement.
     */
    private void validateDates(LocalDateTime dateDebut, LocalDateTime dateFin) {
        if (dateDebut.isAfter(dateFin)) {
            throw new BusinessException("La date de fin doit être après la date de début");
        }

        if (dateDebut.isBefore(LocalDateTime.now())) {
            throw new BusinessException("La date de début doit être dans le futur");
        }
    }

    /**
     * Vérifie que l'organisateur est propriétaire de l'événement.
     */
    private void checkOwnership(Evenement evenement, Long organisateurId) {
        if (!evenement.getOrganisateur().getId().equals(organisateurId)) {
            logger.warn("Tentative de modification d'un événement par un non-propriétaire. " +
                       "Événement: {}, Organisateur demandeur: {}, Propriétaire: {}",
                       evenement.getId(), organisateurId, evenement.getOrganisateur().getId());
            throw new BusinessException("Vous n'êtes pas autorisé à modifier cet événement");
        }
    }
}
