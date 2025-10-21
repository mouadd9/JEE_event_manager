package ma.ensa.tetouan.eventmanagement.service;

import ma.ensa.tetouan.eventmanagement.dao.EvenementDAO;
import ma.ensa.tetouan.eventmanagement.dao.EvenementDAOImpl;
import ma.ensa.tetouan.eventmanagement.dao.OrganisateurDAO;
import ma.ensa.tetouan.eventmanagement.dao.OrganisateurDAOImpl;
import ma.ensa.tetouan.eventmanagement.exception.BusinessException;
import ma.ensa.tetouan.eventmanagement.exception.ResourceNotFoundException;
import ma.ensa.tetouan.eventmanagement.model.Evenement;
import ma.ensa.tetouan.eventmanagement.model.Organisateur;
import ma.ensa.tetouan.eventmanagement.model.StatutEvenement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Implémentation du service de gestion des organisateurs.
 *
 * @author ENSA Tétouan
 */
public class OrganisateurServiceImpl implements OrganisateurService {

    private static final Logger logger = LoggerFactory.getLogger(OrganisateurServiceImpl.class);
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\+212|0)[5-7][0-9]{8}$");

    private final OrganisateurDAO organisateurDAO;
    private final EvenementDAO evenementDAO;

    /**
     * Constructeur avec injection des DAOs.
     */
    public OrganisateurServiceImpl() {
        this.organisateurDAO = new OrganisateurDAOImpl();
        this.evenementDAO = new EvenementDAOImpl();
    }

    /**
     * Constructeur pour les tests (injection de dépendance).
     */
    public OrganisateurServiceImpl(OrganisateurDAO organisateurDAO, EvenementDAO evenementDAO) {
        this.organisateurDAO = organisateurDAO;
        this.evenementDAO = evenementDAO;
    }

    @Override
    public Optional<Organisateur> getOrganisateurWithEvents(Long organisateurId) {
        logger.debug("Récupération de l'organisateur {} avec ses événements", organisateurId);

        if (organisateurId == null) {
            return Optional.empty();
        }

        try {
            return organisateurDAO.findWithEvents(organisateurId);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de l'organisateur avec événements", e);
            throw new BusinessException("Erreur lors de la récupération de l'organisateur", e);
        }
    }

    @Override
    public Organisateur updateOrganisation(Long organisateurId, String organisation, String telephone) {
        logger.info("Mise à jour des informations d'organisation pour l'organisateur {}", organisateurId);

        // 1. Vérifier que l'organisateur existe
        Organisateur organisateur = getOrganisateurById(organisateurId);

        // 2. Valider l'organisation
        if (organisation == null || organisation.trim().isEmpty()) {
            throw new BusinessException("Le nom de l'organisation est obligatoire");
        }

        if (organisation.length() < 2 || organisation.length() > 100) {
            throw new BusinessException("Le nom de l'organisation doit contenir entre 2 et 100 caractères");
        }

        // 3. Valider le téléphone (optionnel)
        if (telephone != null && !telephone.trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(telephone).matches()) {
                throw new BusinessException(
                    "Le numéro de téléphone doit être au format marocain valide (ex: 0612345678 ou +212612345678)"
                );
            }
        }

        // 4. Mettre à jour les champs
        organisateur.setOrganisation(organisation);
        organisateur.setTelephone(telephone);

        // 5. Sauvegarder
        try {
            Organisateur updated = organisateurDAO.update(organisateur);
            logger.info("Informations d'organisation mises à jour pour l'organisateur {}", organisateurId);
            return updated;
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de l'organisation", e);
            throw new BusinessException("Erreur lors de la mise à jour des informations", e);
        }
    }

    @Override
    public List<Organisateur> getTopOrganisateurs(int limit) {
        logger.debug("Récupération des {} organisateurs les plus actifs", limit);

        if (limit <= 0) {
            throw new IllegalArgumentException("La limite doit être positive");
        }

        try {
            return organisateurDAO.findMostActive(limit);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des top organisateurs", e);
            throw new BusinessException("Erreur lors de la récupération des organisateurs", e);
        }
    }

    @Override
    public List<Evenement> getEventsByOrganisateur(Long organisateurId) {
        logger.debug("Récupération des événements de l'organisateur {}", organisateurId);

        // Vérifier que l'organisateur existe
        getOrganisateurById(organisateurId);

        try {
            return evenementDAO.findByOrganisateur(organisateurId);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des événements de l'organisateur", e);
            throw new BusinessException("Erreur lors de la récupération des événements", e);
        }
    }

    @Override
    public Map<String, Object> getOrganisateurStats(Long organisateurId) {
        logger.debug("Calcul des statistiques pour l'organisateur {}", organisateurId);

        // Vérifier que l'organisateur existe
        getOrganisateurById(organisateurId);

        try {
            List<Evenement> events = evenementDAO.findByOrganisateur(organisateurId);

            Map<String, Object> stats = new LinkedHashMap<>();

            // Nombre total d'événements
            stats.put("totalEvents", events.size());

            // Événements par statut
            long publishedEvents = events.stream()
                .filter(e -> e.getStatut() == StatutEvenement.PUBLIE)
                .count();
            long draftEvents = events.stream()
                .filter(e -> e.getStatut() == StatutEvenement.BROUILLON)
                .count();
            long cancelledEvents = events.stream()
                .filter(e -> e.getStatut() == StatutEvenement.ANNULE)
                .count();
            long completedEvents = events.stream()
                .filter(e -> e.getStatut() == StatutEvenement.TERMINE)
                .count();

            stats.put("publishedEvents", publishedEvents);
            stats.put("activeEvents", publishedEvents); // For dashboard compatibility
            stats.put("draftEvents", draftEvents);
            stats.put("cancelledEvents", cancelledEvents);
            stats.put("completedEvents", completedEvents);

            // Nombre total de participants (inscriptions acceptées)
            long totalParticipants = events.stream()
                .mapToLong(e -> e.getNombreInscriptions())
                .sum();
            stats.put("totalParticipants", totalParticipants);

            // For now, set pendingInscriptions to 0 (will be implemented with Inscription entity)
            stats.put("pendingInscriptions", 0L);

            // Capacité totale proposée
            long totalCapacity = events.stream()
                .filter(e -> e.getStatut() == StatutEvenement.PUBLIE ||
                            e.getStatut() == StatutEvenement.TERMINE)
                .mapToLong(Evenement::getCapacite)
                .sum();
            stats.put("totalCapacity", totalCapacity);

            // Taux de remplissage moyen
            if (totalCapacity > 0) {
                double fillRate = (totalParticipants * 100.0) / totalCapacity;
                stats.put("averageFillRate", Math.round(fillRate * 100.0) / 100.0);
            } else {
                stats.put("averageFillRate", 0.0);
            }

            // Note moyenne de tous les événements
            OptionalDouble avgRating = events.stream()
                .filter(e -> e.getNoteMoyenne() != null)
                .mapToDouble(Evenement::getNoteMoyenne)
                .average();
            stats.put("averageRating", avgRating.isPresent() ?
                     Math.round(avgRating.getAsDouble() * 100.0) / 100.0 : null);

            // Nombre d'événements notés
            long ratedEvents = events.stream()
                .filter(e -> e.getNoteMoyenne() != null && e.getNoteMoyenne() > 0)
                .count();
            stats.put("ratedEvents", ratedEvents);

            logger.debug("Statistiques calculées pour l'organisateur {}: {} événements",
                        organisateurId, stats.get("totalEvents"));

            return stats;

        } catch (Exception e) {
            logger.error("Erreur lors du calcul des statistiques de l'organisateur", e);
            throw new BusinessException("Erreur lors du calcul des statistiques", e);
        }
    }

    @Override
    public Organisateur getOrganisateurById(Long organisateurId) {
        logger.debug("Récupération de l'organisateur {}", organisateurId);

        if (organisateurId == null) {
            throw new IllegalArgumentException("L'ID de l'organisateur ne peut pas être null");
        }

        Optional<Organisateur> organisateurOpt = organisateurDAO.findById(organisateurId);
        if (!organisateurOpt.isPresent()) {
            logger.warn("Organisateur non trouvé: ID={}", organisateurId);
            throw new ResourceNotFoundException("Organisateur", organisateurId);
        }

        return organisateurOpt.get();
    }

    @Override
    public List<Organisateur> getAllOrganisateurs(int page, int pageSize) {
        logger.debug("Récupération de tous les organisateurs - page: {}, taille: {}", page, pageSize);

        if (page < 0 || pageSize <= 0) {
            throw new IllegalArgumentException("Les paramètres de pagination doivent être valides");
        }

        try {
            return organisateurDAO.findAll(page, pageSize);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des organisateurs", e);
            throw new BusinessException("Erreur lors de la récupération des organisateurs", e);
        }
    }

    @Override
    public long getTotalOrganisateurs() {
        logger.debug("Comptage du nombre total d'organisateurs");

        try {
            return organisateurDAO.count();
        } catch (Exception e) {
            logger.error("Erreur lors du comptage des organisateurs", e);
            throw new BusinessException("Erreur lors du comptage des organisateurs", e);
        }
    }
}
