package ma.ensa.tetouan.eventmanagement.service;

import ma.ensa.tetouan.eventmanagement.dao.*;
import ma.ensa.tetouan.eventmanagement.exception.*;
import ma.ensa.tetouan.eventmanagement.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation du service de gestion des inscriptions avec gestion automatique de la capacité.
 *
 * @author ENSA Tétouan
 */
public class InscriptionServiceImpl implements InscriptionService {

    private static final Logger logger = LoggerFactory.getLogger(InscriptionServiceImpl.class);

    private final InscriptionDAO inscriptionDAO;
    private final EvenementDAO evenementDAO;
    private final ParticipantDAO participantDAO;

    /**
     * Constructeur avec injection des DAOs.
     */
    public InscriptionServiceImpl() {
        this.inscriptionDAO = new InscriptionDAOImpl();
        this.evenementDAO = new EvenementDAOImpl();
        this.participantDAO = new ParticipantDAOImpl();
    }

    /**
     * Constructeur pour les tests (injection de dépendance).
     */
    public InscriptionServiceImpl(InscriptionDAO inscriptionDAO, EvenementDAO evenementDAO,
                                   ParticipantDAO participantDAO) {
        this.inscriptionDAO = inscriptionDAO;
        this.evenementDAO = evenementDAO;
        this.participantDAO = participantDAO;
    }

    @Override
    public Inscription registerToEvent(Long participantId, Long evenementId) {
        logger.info("Tentative d'inscription du participant {} à l'événement {}", participantId, evenementId);

        // 1. Valider l'existence du participant
        Optional<Participant> participantOpt = participantDAO.findById(participantId);
        if (!participantOpt.isPresent()) {
            logger.warn("Participant non trouvé: ID={}", participantId);
            throw new ResourceNotFoundException("Participant", participantId);
        }
        Participant participant = participantOpt.get();

        // 2. Valider l'existence de l'événement
        Optional<Evenement> evenementOpt = evenementDAO.findById(evenementId);
        if (!evenementOpt.isPresent()) {
            logger.warn("Événement non trouvé: ID={}", evenementId);
            throw new ResourceNotFoundException("Événement", evenementId);
        }
        Evenement evenement = evenementOpt.get();

        // 3. Vérifier que l'événement est PUBLIE
        if (evenement.getStatut() != StatutEvenement.PUBLIE) {
            logger.warn("Tentative d'inscription à un événement non publié: statut={}", evenement.getStatut());
            throw new InvalidEventStateException(
                "Impossible de s'inscrire à un événement qui n'est pas publié. " +
                "Statut actuel: " + evenement.getStatut().getLibelle()
            );
        }

        // 4. Vérifier que l'événement n'est pas déjà passé
        if (evenement.getDateDebut().isBefore(LocalDateTime.now())) {
            logger.warn("Tentative d'inscription à un événement passé: date={}", evenement.getDateDebut());
            throw new BusinessException("Impossible de s'inscrire à un événement déjà passé");
        }

        // 5. Vérifier qu'il n'y a pas déjà une inscription active
        if (inscriptionDAO.existsByParticipantAndEvenement(participantId, evenementId)) {
            logger.warn("Tentative d'inscription en double: participant={}, événement={}",
                       participantId, evenementId);
            throw new DuplicateRegistrationException(participantId, evenementId);
        }

        // 6. Vérifier la capacité et déterminer le statut
        int availableSeats = getAvailableSeats(evenementId);
        StatutInscription statut;

        if (availableSeats > 0) {
            // Places disponibles : accepter automatiquement
            statut = StatutInscription.ACCEPTEE;
            logger.info("Places disponibles ({}), inscription acceptée automatiquement", availableSeats);
        } else {
            // Complet : mettre en liste d'attente
            statut = StatutInscription.EN_ATTENTE;
            logger.info("Événement complet, inscription mise en liste d'attente");
        }

        // 7. Créer l'inscription
        Inscription inscription = new Inscription();
        inscription.setParticipant(participant);
        inscription.setEvenement(evenement);
        inscription.setStatut(statut);
        inscription.setDateInscription(LocalDateTime.now());
        inscription.setNombrePlaces(1); // Par défaut 1 place

        try {
            Inscription savedInscription = inscriptionDAO.save(inscription);

            // 8. Si acceptée, décrémenter les places disponibles
            if (statut == StatutInscription.ACCEPTEE) {
                evenement.setPlacesDisponibles(evenement.getPlacesDisponibles() - 1);
                evenement.setNombreInscriptions(evenement.getNombreInscriptions() + 1);
                evenementDAO.update(evenement);
                logger.info("Places disponibles mises à jour: {} restantes", evenement.getPlacesDisponibles());
            }

            logger.info("Inscription créée avec succès: ID={}, Statut={}",
                       savedInscription.getId(), savedInscription.getStatut());
            return savedInscription;

        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'inscription", e);
            throw new BusinessException("Erreur lors de l'inscription à l'événement", e);
        }
    }

    @Override
    public Inscription acceptInscription(Long inscriptionId, Long organisateurId) {
        logger.info("Tentative d'acceptation de l'inscription {} par l'organisateur {}",
                   inscriptionId, organisateurId);

        // 1. Récupérer l'inscription
        Inscription inscription = getInscriptionById(inscriptionId);

        // 2. Vérifier que l'organisateur est bien celui de l'événement
        if (!inscription.getEvenement().getOrganisateur().getId().equals(organisateurId)) {
            logger.warn("Organisateur non autorisé: ID={}", organisateurId);
            throw new BusinessException("Vous n'êtes pas autorisé à accepter cette inscription");
        }

        // 3. Vérifier que l'inscription est EN_ATTENTE
        if (inscription.getStatut() != StatutInscription.EN_ATTENTE) {
            logger.warn("Tentative d'acceptation d'une inscription qui n'est pas en attente: statut={}",
                       inscription.getStatut());
            throw new InvalidEventStateException(
                "Seules les inscriptions en attente peuvent être acceptées. " +
                "Statut actuel: " + inscription.getStatut().getLibelle()
            );
        }

        // 4. Vérifier qu'il reste des places
        Evenement evenement = inscription.getEvenement();
        if (evenement.getPlacesDisponibles() <= 0) {
            logger.warn("Tentative d'acceptation alors que l'événement est complet");
            throw new EventFullException(evenement.getTitre());
        }

        // 5. Accepter l'inscription
        try {
            inscription.accepter("Acceptée par l'organisateur");
            Inscription updated = inscriptionDAO.update(inscription);

            // 6. Décrémenter les places disponibles
            evenement.setPlacesDisponibles(evenement.getPlacesDisponibles() - 1);
            evenement.setNombreInscriptions(evenement.getNombreInscriptions() + 1);
            evenementDAO.update(evenement);

            logger.info("Inscription acceptée avec succès: ID={}", inscriptionId);
            return updated;

        } catch (Exception e) {
            logger.error("Erreur lors de l'acceptation de l'inscription", e);
            throw new BusinessException("Erreur lors de l'acceptation de l'inscription", e);
        }
    }

    @Override
    public Inscription refuseInscription(Long inscriptionId, Long organisateurId) {
        logger.info("Tentative de refus de l'inscription {} par l'organisateur {}",
                   inscriptionId, organisateurId);

        // 1. Récupérer l'inscription
        Inscription inscription = getInscriptionById(inscriptionId);

        // 2. Vérifier que l'organisateur est bien celui de l'événement
        if (!inscription.getEvenement().getOrganisateur().getId().equals(organisateurId)) {
            logger.warn("Organisateur non autorisé: ID={}", organisateurId);
            throw new BusinessException("Vous n'êtes pas autorisé à refuser cette inscription");
        }

        // 3. Vérifier que l'inscription est EN_ATTENTE
        if (inscription.getStatut() != StatutInscription.EN_ATTENTE) {
            logger.warn("Tentative de refus d'une inscription qui n'est pas en attente: statut={}",
                       inscription.getStatut());
            throw new InvalidEventStateException(
                "Seules les inscriptions en attente peuvent être refusées. " +
                "Statut actuel: " + inscription.getStatut().getLibelle()
            );
        }

        // 5. Refuser l'inscription
        try {
            inscription.refuser("Refusée par l'organisateur");
            Inscription updated = inscriptionDAO.update(inscription);

            logger.info("Inscription refusée avec succès: ID={}", inscriptionId);
            return updated;

        } catch (Exception e) {
            logger.error("Erreur lors du refus de l'inscription", e);
            throw new BusinessException("Erreur lors du refus de l'inscription", e);
        }
    }

    @Override
    public void cancelInscription(Long inscriptionId, Long participantId) {
        logger.info("Tentative d'annulation de l'inscription {} par le participant {}",
                   inscriptionId, participantId);

        // 1. Récupérer l'inscription
        Inscription inscription = getInscriptionById(inscriptionId);

        // 2. Vérifier que c'est bien le participant propriétaire
        if (!inscription.getParticipant().getId().equals(participantId)) {
            logger.warn("Participant non autorisé: ID={}", participantId);
            throw new BusinessException("Vous n'êtes pas autorisé à annuler cette inscription");
        }

        // 3. Vérifier que l'inscription peut être annulée
        if (inscription.getStatut() == StatutInscription.ANNULEE) {
            logger.warn("Tentative d'annulation d'une inscription déjà annulée");
            throw new InvalidEventStateException("Cette inscription est déjà annulée");
        }

        if (inscription.getStatut() == StatutInscription.REFUSEE) {
            logger.warn("Tentative d'annulation d'une inscription refusée");
            throw new InvalidEventStateException("Impossible d'annuler une inscription refusée");
        }

        // 4. Sauvegarder le statut actuel pour savoir si on doit libérer une place
        StatutInscription ancienStatut = inscription.getStatut();

        // 4. Annuler l'inscription
        try {
            inscription.annuler("Annulée par le participant");
            inscriptionDAO.update(inscription);

            // 6. Si l'inscription était ACCEPTEE, libérer la place
            if (ancienStatut == StatutInscription.ACCEPTEE) {
                Evenement evenement = inscription.getEvenement();
                evenement.setPlacesDisponibles(evenement.getPlacesDisponibles() + 1);
                evenement.setNombreInscriptions(evenement.getNombreInscriptions() - 1);
                evenementDAO.update(evenement);

                logger.info("Place libérée suite à l'annulation, {} places disponibles",
                           evenement.getPlacesDisponibles());

                // 7. Accepter automatiquement le premier en attente
                Inscription nextInWaitlist = acceptNextInWaitlist(evenement.getId());
                if (nextInWaitlist != null) {
                    logger.info("Participant {} promu automatiquement depuis la liste d'attente",
                               nextInWaitlist.getParticipant().getId());
                }
            }

            logger.info("Inscription annulée avec succès: ID={}", inscriptionId);

        } catch (Exception e) {
            logger.error("Erreur lors de l'annulation de l'inscription", e);
            throw new BusinessException("Erreur lors de l'annulation de l'inscription", e);
        }
    }

    @Override
    public Inscription acceptNextInWaitlist(Long evenementId) {
        logger.debug("Recherche du prochain participant en attente pour l'événement {}", evenementId);

        // 1. Vérifier qu'il y a des places disponibles
        if (getAvailableSeats(evenementId) <= 0) {
            logger.debug("Aucune place disponible pour promouvoir depuis la liste d'attente");
            return null;
        }

        // 2. Récupérer les participants en attente (triés par date d'inscription)
        List<Inscription> waitlist = getWaitlistedParticipants(evenementId);
        if (waitlist.isEmpty()) {
            logger.debug("Aucun participant en liste d'attente");
            return null;
        }

        // 3. Prendre le premier de la liste (le plus ancien)
        Inscription firstInWaitlist = waitlist.get(0);

        // 4. L'accepter automatiquement
        try {
            firstInWaitlist.accepter("Acceptée automatiquement suite à une annulation");
            Inscription updated = inscriptionDAO.update(firstInWaitlist);

            // 5. Mettre à jour la capacité
            Evenement evenement = firstInWaitlist.getEvenement();
            evenement.setPlacesDisponibles(evenement.getPlacesDisponibles() - 1);
            evenement.setNombreInscriptions(evenement.getNombreInscriptions() + 1);
            evenementDAO.update(evenement);

            logger.info("Participant {} accepté automatiquement depuis la liste d'attente",
                       firstInWaitlist.getParticipant().getId());
            return updated;

        } catch (Exception e) {
            logger.error("Erreur lors de l'acceptation automatique depuis la liste d'attente", e);
            // Ne pas propager l'exception pour ne pas bloquer l'annulation principale
            return null;
        }
    }

    @Override
    public List<Inscription> getInscriptionsByParticipant(Long participantId) {
        logger.debug("Récupération des inscriptions du participant {}", participantId);

        if (participantId == null) {
            throw new IllegalArgumentException("L'ID du participant ne peut pas être null");
        }

        try {
            return inscriptionDAO.findByParticipant(participantId);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des inscriptions du participant", e);
            throw new BusinessException("Erreur lors de la récupération des inscriptions", e);
        }
    }

    @Override
    public List<Inscription> getInscriptionsByEvenement(Long evenementId) {
        logger.debug("Récupération des inscriptions de l'événement {}", evenementId);

        if (evenementId == null) {
            throw new IllegalArgumentException("L'ID de l'événement ne peut pas être null");
        }

        try {
            return inscriptionDAO.findByEvenement(evenementId);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des inscriptions de l'événement", e);
            throw new BusinessException("Erreur lors de la récupération des inscriptions", e);
        }
    }

    @Override
    public List<Inscription> getWaitlistedParticipants(Long evenementId) {
        logger.debug("Récupération des participants en attente pour l'événement {}", evenementId);

        if (evenementId == null) {
            throw new IllegalArgumentException("L'ID de l'événement ne peut pas être null");
        }

        try {
            // Récupérer toutes les inscriptions et filtrer les EN_ATTENTE
            List<Inscription> allInscriptions = inscriptionDAO.findByEvenement(evenementId);
            return allInscriptions.stream()
                .filter(i -> i.getStatut() == StatutInscription.EN_ATTENTE)
                .sorted((i1, i2) -> i1.getDateInscription().compareTo(i2.getDateInscription()))
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de la liste d'attente", e);
            throw new BusinessException("Erreur lors de la récupération de la liste d'attente", e);
        }
    }

    @Override
    public List<Inscription> getAcceptedParticipants(Long evenementId) {
        logger.debug("Récupération des participants acceptés pour l'événement {}", evenementId);

        if (evenementId == null) {
            throw new IllegalArgumentException("L'ID de l'événement ne peut pas être null");
        }

        try {
            // Récupérer toutes les inscriptions et filtrer les ACCEPTEE
            List<Inscription> allInscriptions = inscriptionDAO.findByEvenement(evenementId);
            return allInscriptions.stream()
                .filter(i -> i.getStatut() == StatutInscription.ACCEPTEE)
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des participants acceptés", e);
            throw new BusinessException("Erreur lors de la récupération des participants acceptés", e);
        }
    }

    @Override
    public int getAvailableSeats(Long evenementId) {
        logger.debug("Calcul des places disponibles pour l'événement {}", evenementId);

        if (evenementId == null) {
            throw new IllegalArgumentException("L'ID de l'événement ne peut pas être null");
        }

        try {
            // Récupérer l'événement
            Optional<Evenement> evenementOpt = evenementDAO.findById(evenementId);
            if (!evenementOpt.isPresent()) {
                throw new ResourceNotFoundException("Événement", evenementId);
            }

            Evenement evenement = evenementOpt.get();

            // Calculer les places disponibles
            // Méthode 1 : utiliser le champ placesDisponibles de l'événement
            int availableSeats = evenement.getPlacesDisponibles();

            // Méthode 2 (vérification) : calculer depuis la capacité totale
            long acceptedCount = getAcceptedInscriptionsCount(evenementId);
            int calculatedSeats = evenement.getCapacite() - (int) acceptedCount;

            // Si différence, utiliser le calcul (plus fiable)
            if (availableSeats != calculatedSeats) {
                logger.warn("Incohérence détectée: placesDisponibles={}, calculé={} pour événement {}",
                           availableSeats, calculatedSeats, evenementId);
                availableSeats = calculatedSeats;
            }

            logger.debug("Places disponibles: {} / {}", availableSeats, evenement.getCapacite());
            return Math.max(0, availableSeats); // Ne jamais retourner négatif

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Erreur lors du calcul des places disponibles", e);
            throw new BusinessException("Erreur lors du calcul des places disponibles", e);
        }
    }

    @Override
    public boolean isEventFull(Long evenementId) {
        return getAvailableSeats(evenementId) <= 0;
    }

    @Override
    public boolean canRegister(Long participantId, Long evenementId) {
        logger.debug("Vérification si le participant {} peut s'inscrire à l'événement {}",
                    participantId, evenementId);

        if (participantId == null || evenementId == null) {
            return false;
        }

        try {
            // 1. Vérifier que l'événement existe
            Optional<Evenement> evenementOpt = evenementDAO.findById(evenementId);
            if (!evenementOpt.isPresent()) {
                return false;
            }

            Evenement evenement = evenementOpt.get();

            // 2. Vérifier que l'événement est PUBLIE
            if (evenement.getStatut() != StatutEvenement.PUBLIE) {
                logger.debug("Événement pas publié: statut={}", evenement.getStatut());
                return false;
            }

            // 3. Vérifier que l'événement n'est pas passé
            if (evenement.getDateDebut().isBefore(LocalDateTime.now())) {
                logger.debug("Événement passé: date={}", evenement.getDateDebut());
                return false;
            }

            // 4. Vérifier qu'il n'y a pas déjà une inscription active
            if (inscriptionDAO.existsByParticipantAndEvenement(participantId, evenementId)) {
                logger.debug("Participant déjà inscrit");
                return false;
            }

            // Toutes les conditions sont remplies
            return true;

        } catch (Exception e) {
            logger.error("Erreur lors de la vérification de la possibilité d'inscription", e);
            return false;
        }
    }

    @Override
    public Inscription getInscriptionById(Long inscriptionId) {
        logger.debug("Récupération de l'inscription {}", inscriptionId);

        if (inscriptionId == null) {
            throw new IllegalArgumentException("L'ID de l'inscription ne peut pas être null");
        }

        Optional<Inscription> inscriptionOpt = inscriptionDAO.findById(inscriptionId);
        if (!inscriptionOpt.isPresent()) {
            logger.warn("Inscription non trouvée: ID={}", inscriptionId);
            throw new ResourceNotFoundException("Inscription", inscriptionId);
        }

        return inscriptionOpt.get();
    }

    @Override
    public long getTotalInscriptions(Long evenementId) {
        logger.debug("Comptage des inscriptions pour l'événement {}", evenementId);

        if (evenementId == null) {
            throw new IllegalArgumentException("L'ID de l'événement ne peut pas être null");
        }

        try {
            return inscriptionDAO.countByEvenement(evenementId);
        } catch (Exception e) {
            logger.error("Erreur lors du comptage des inscriptions", e);
            throw new BusinessException("Erreur lors du comptage des inscriptions", e);
        }
    }

    @Override
    public long getAcceptedInscriptionsCount(Long evenementId) {
        logger.debug("Comptage des inscriptions acceptées pour l'événement {}", evenementId);

        if (evenementId == null) {
            throw new IllegalArgumentException("L'ID de l'événement ne peut pas être null");
        }

        try {
            return inscriptionDAO.countAcceptedByEvenement(evenementId);
        } catch (Exception e) {
            logger.error("Erreur lors du comptage des inscriptions acceptées", e);
            throw new BusinessException("Erreur lors du comptage des inscriptions acceptées", e);
        }
    }
}
