package ma.ensa.tetouan.eventmanagement.model;

/**
 * Enumération représentant le statut d'un utilisateur dans le système.
 *
 * @author ENSA Tétouan
 */
public enum StatutUtilisateur {
    /**
     * Utilisateur actif - peut accéder à toutes les fonctionnalités
     */
    ACTIF("Actif"),

    /**
     * Utilisateur inactif - compte créé mais non vérifié ou désactivé temporairement
     */
    INACTIF("Inactif"),

    /**
     * Utilisateur suspendu - accès bloqué par l'administrateur
     */
    SUSPENDU("Suspendu");

    private final String libelle;

    StatutUtilisateur(String libelle) {
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
