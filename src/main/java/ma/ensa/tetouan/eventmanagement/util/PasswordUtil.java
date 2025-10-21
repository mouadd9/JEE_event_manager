package ma.ensa.tetouan.eventmanagement.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utilitaire pour le hachage et la vérification des mots de passe.
 * Utilise SHA-256 avec salt pour une sécurité renforcée.
 *
 * Note: Pour une application en production, il est recommandé d'utiliser BCrypt ou Argon2.
 *
 * @author ENSA Tétouan
 */
public class PasswordUtil {

    private static final Logger logger = LoggerFactory.getLogger(PasswordUtil.class);
    private static final int SALT_LENGTH = 16;
    private static final String SEPARATOR = ":";

    /**
     * Constructeur privé pour empêcher l'instanciation
     */
    private PasswordUtil() {
        // Classe utilitaire
    }

    /**
     * Hache un mot de passe en clair avec un salt généré aléatoirement.
     * Le résultat est au format: salt:hash
     *
     * @param plainPassword Le mot de passe en clair
     * @return Le mot de passe haché avec son salt
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide");
        }

        try {
            // Générer un salt aléatoire
            String salt = generateSalt();

            // Hacher le mot de passe avec le salt
            String hash = hashWithSalt(plainPassword, salt);

            // Retourner salt:hash
            String result = salt + SEPARATOR + hash;
            logger.debug("Mot de passe haché avec succès");
            return result;

        } catch (Exception e) {
            logger.error("Erreur lors du hachage du mot de passe", e);
            throw new RuntimeException("Erreur lors du hachage du mot de passe", e);
        }
    }

    /**
     * Vérifie si un mot de passe en clair correspond au hash stocké.
     *
     * @param plainPassword Le mot de passe en clair à vérifier
     * @param storedPassword Le mot de passe haché stocké (format: salt:hash)
     * @return true si le mot de passe correspond, false sinon
     */
    public static boolean verifyPassword(String plainPassword, String storedPassword) {
        if (plainPassword == null || storedPassword == null) {
            logger.warn("Tentative de vérification avec mot de passe null");
            return false;
        }

        try {
            // Extraire le salt et le hash du mot de passe stocké
            String[] parts = storedPassword.split(SEPARATOR);
            if (parts.length != 2) {
                logger.error("Format de mot de passe stocké invalide");
                return false;
            }

            String salt = parts[0];
            String storedHash = parts[1];

            // Hacher le mot de passe fourni avec le même salt
            String computedHash = hashWithSalt(plainPassword, salt);

            // Comparer les hash
            boolean matches = storedHash.equals(computedHash);
            logger.debug("Vérification du mot de passe: {}", matches ? "succès" : "échec");
            return matches;

        } catch (Exception e) {
            logger.error("Erreur lors de la vérification du mot de passe", e);
            return false;
        }
    }

    /**
     * Génère un salt aléatoire.
     *
     * @return Le salt en Base64
     */
    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hache un mot de passe avec un salt donné en utilisant SHA-256.
     *
     * @param password Le mot de passe en clair
     * @param salt Le salt
     * @return Le hash en hexadécimal
     */
    private static String hashWithSalt(String password, String salt) {
        String saltedPassword = password + salt;
        return DigestUtils.sha256Hex(saltedPassword);
    }

    /**
     * Valide la force d'un mot de passe.
     *
     * @param password Le mot de passe à valider
     * @return true si le mot de passe est fort, false sinon
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch));

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    /**
     * Génère un mot de passe temporaire aléatoire.
     *
     * @param length La longueur du mot de passe
     * @return Un mot de passe aléatoire
     */
    public static String generateTemporaryPassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("La longueur minimale est de 8 caractères");
        }

        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*";
        String allChars = upperCase + lowerCase + digits + special;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        // Assurer au moins un caractère de chaque type
        password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(special.charAt(random.nextInt(special.length())));

        // Remplir le reste aléatoirement
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Mélanger les caractères
        char[] chars = password.toString().toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }

        return new String(chars);
    }
}
