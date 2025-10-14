package com.projet.jee.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Utility class for password hashing and verification using BCrypt.
 * BCrypt automatically handles salting and is designed to be slow to resist brute-force attacks.
 */
public class PasswordUtil {

    // BCrypt cost factor (higher = more secure but slower)
    // 12 is a good balance between security and performance
    private static final int BCRYPT_COST = 12;

    /**
     * Hash a password using BCrypt
     *
     * @param plainPassword the plain text password
     * @return the hashed password
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide");
        }
        return BCrypt.withDefaults().hashToString(BCRYPT_COST, plainPassword.toCharArray());
    }

    /**
     * Verify a password against a hashed password
     *
     * @param plainPassword  the plain text password to verify
     * @param hashedPassword the hashed password to verify against
     * @return true if the password matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        BCrypt.Result result = BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword);
        return result.verified;
    }

    /**
     * Validate password strength
     * Password must:
     * - Be at least 8 characters long
     * - Contain at least one uppercase letter
     * - Contain at least one lowercase letter
     * - Contain at least one digit
     * - Optionally contain special characters for stronger passwords
     *
     * @param password the password to validate
     * @return true if password meets strength requirements
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUppercase = true;
            else if (Character.isLowerCase(c)) hasLowercase = true;
            else if (Character.isDigit(c)) hasDigit = true;

            if (hasUppercase && hasLowercase && hasDigit) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get password strength message for user feedback
     *
     * @param password the password to evaluate
     * @return a message describing password strength requirements
     */
    public static String getPasswordStrengthMessage(String password) {
        if (password == null || password.isEmpty()) {
            return "Le mot de passe est requis";
        }

        if (password.length() < 8) {
            return "Le mot de passe doit contenir au moins 8 caractères";
        }

        boolean hasUppercase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLowercase = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);

        if (!hasUppercase) {
            return "Le mot de passe doit contenir au moins une lettre majuscule";
        }
        if (!hasLowercase) {
            return "Le mot de passe doit contenir au moins une lettre minuscule";
        }
        if (!hasDigit) {
            return "Le mot de passe doit contenir au moins un chiffre";
        }

        return "Mot de passe fort";
    }
}
