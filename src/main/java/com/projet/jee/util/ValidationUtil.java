package com.projet.jee.util;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.regex.Pattern;

/**
 * Utility class for input validation and sanitization.
 * Provides methods to validate various types of user input and prevent injection attacks.
 */
public class ValidationUtil {

    // Regex patterns for validation
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{10,15}$");
    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^[0-9]{5}$");
    private static final Pattern SIRET_PATTERN = Pattern.compile("^[0-9]{14}$");
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s-_]+$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-ZÀ-ÿ\\s'-]+$");

    // HTML/XSS dangerous patterns
    private static final Pattern SCRIPT_PATTERN = Pattern.compile("<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE);
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]+>");

    /**
     * Validate email address
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EmailValidator.getInstance().isValid(email);
    }

    /**
     * Validate phone number
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true; // Phone is optional in most cases
        }
        String cleanPhone = phone.replaceAll("[\\s()-]", "");
        return PHONE_PATTERN.matcher(cleanPhone).matches();
    }

    /**
     * Validate postal code (French format)
     */
    public static boolean isValidPostalCode(String postalCode) {
        if (postalCode == null || postalCode.trim().isEmpty()) {
            return true; // Optional field
        }
        return POSTAL_CODE_PATTERN.matcher(postalCode.trim()).matches();
    }

    /**
     * Validate SIRET number (French business identifier)
     */
    public static boolean isValidSiret(String siret) {
        if (siret == null || siret.trim().isEmpty()) {
            return true; // Optional field
        }
        String cleanSiret = siret.replaceAll("\\s", "");
        return SIRET_PATTERN.matcher(cleanSiret).matches();
    }

    /**
     * Validate name (person's first/last name)
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return NAME_PATTERN.matcher(name.trim()).matches() && name.trim().length() >= 2;
    }

    /**
     * Sanitize string input to prevent XSS attacks
     * Removes HTML tags and script elements
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }

        // Remove script tags
        String sanitized = SCRIPT_PATTERN.matcher(input).replaceAll("");

        // Remove HTML tags
        sanitized = HTML_TAG_PATTERN.matcher(sanitized).replaceAll("");

        // Encode special characters
        sanitized = sanitized.replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("/", "&#x2F;");

        return sanitized.trim();
    }

    /**
     * Sanitize input for HTML display
     * More lenient than sanitizeInput - allows basic formatting
     */
    public static String sanitizeForDisplay(String input) {
        if (input == null) {
            return null;
        }

        // Remove script tags but keep basic HTML
        String sanitized = SCRIPT_PATTERN.matcher(input).replaceAll("");

        // Encode dangerous characters
        sanitized = sanitized.replace("<script", "&lt;script")
                .replace("javascript:", "")
                .replace("onerror=", "")
                .replace("onclick=", "")
                .replace("onload=", "");

        return sanitized.trim();
    }

    /**
     * Validate string length
     */
    public static boolean isValidLength(String value, int minLength, int maxLength) {
        if (value == null) {
            return false;
        }
        int length = value.trim().length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * Validate rating (1-5 stars)
     */
    public static boolean isValidRating(Integer rating) {
        return rating != null && rating >= 1 && rating <= 5;
    }

    /**
     * Validate URL format
     */
    public static boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return true; // URL is optional
        }
        try {
            new java.net.URL(url);
            return url.startsWith("http://") || url.startsWith("https://");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if string contains only alphanumeric characters
     */
    public static boolean isAlphanumeric(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        return ALPHANUMERIC_PATTERN.matcher(value).matches();
    }

    /**
     * Trim and normalize whitespace in a string
     */
    public static String normalizeWhitespace(String input) {
        if (input == null) {
            return null;
        }
        return input.trim().replaceAll("\\s+", " ");
    }

    /**
     * Check if string is null, empty, or whitespace only
     */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Check if string is not empty
     */
    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    /**
     * Validate that a value is not null
     */
    public static boolean isNotNull(Object value) {
        return value != null;
    }

    /**
     * Truncate string to maximum length
     */
    public static String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength) + "...";
    }
}
