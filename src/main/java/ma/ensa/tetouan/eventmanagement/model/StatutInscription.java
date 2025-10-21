package ma.ensa.tetouan.eventmanagement.model;

/**
 * Enumération représentant le statut d'une inscription à un événement.
 *
 * @author ENSA Tétouan
 */
public enum StatutInscription {
    /**
     * Inscription en attente de validation par l'organisateur
     */
    EN_ATTENTE("En attente"),

    /**
     * Inscription acceptée par l'organisateur
     */
    ACCEPTEE("Acceptée"),

    /**
     * Inscription refusée par l'organisateur
     */
    REFUSEE("Refusée"),

    /**
     * Inscription annulée par le participant
     */
    ANNULEE("Annulée");

    private final String libelle;

    StatutInscription(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }

    @Override
    public String toString() {
        return libelle;
    }
}
