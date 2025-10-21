package ma.ensa.tetouan.eventmanagement.model;

/**
 * Enumération représentant le statut d'un événement.
 *
 * @author ENSA Tétouan
 */
public enum StatutEvenement {
    /**
     * Événement en mode brouillon - non visible par les participants
     */
    BROUILLON("Brouillon"),

    /**
     * Événement publié - visible et ouvert aux inscriptions
     */
    PUBLIE("Publié"),

    /**
     * Événement annulé - inscriptions fermées
     */
    ANNULE("Annulé"),

    /**
     * Événement terminé - après la date de fin
     */
    TERMINE("Terminé");

    private final String libelle;

    StatutEvenement(String libelle) {
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
