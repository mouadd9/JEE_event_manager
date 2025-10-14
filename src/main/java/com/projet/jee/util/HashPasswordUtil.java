package com.projet.jee.util;

/**
 * Utility to generate password hashes for admin users.
 * Run this class to generate a BCrypt hash for a password.
 */
public class HashPasswordUtil {
    public static void main(String[] args) {
        String[] passwords = {"Admin@123", "Test@123", "Org@123"};

        System.out.println("Password Hashes for Database:");
        System.out.println("================================");

        for (String password : passwords) {
            String hash = PasswordUtil.hashPassword(password);
            System.out.println("\nPassword: " + password);
            System.out.println("BCrypt Hash: " + hash);
        }
    }
}
