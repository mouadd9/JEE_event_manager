package ma.ensa.tetouan.eventmanagement.util;

/**
 * Utility to generate hashed password for admin account creation
 * Run this to get the proper hash for your admin password
 */
public class GenerateAdminPassword {

    public static void main(String[] args) {
        String password = "admin123";
        
        System.out.println("===========================================");
        System.out.println("Admin Password Hash Generator");
        System.out.println("===========================================");
        System.out.println();
        
        try {
            String hashedPassword = PasswordUtil.hashPassword(password);
            
            System.out.println("Plain Password: " + password);
            System.out.println("Hashed Password: " + hashedPassword);
            System.out.println();
            System.out.println("SQL INSERT Statement:");
            System.out.println("-------------------------------------------");
            System.out.println("INSERT INTO utilisateurs (nom, email, mot_de_passe, statut, email_verified, date_inscription, type_utilisateur)");
            System.out.println("VALUES (");
            System.out.println("    'Admin',");
            System.out.println("    '003haytam2@gmail.com',");
            System.out.println("    '" + hashedPassword + "',");
            System.out.println("    'ACTIF',");
            System.out.println("    TRUE,");
            System.out.println("    NOW(),");
            System.out.println("    'ADMINISTRATEUR'");
            System.out.println(");");
            System.out.println("-------------------------------------------");
            
            // Verify the hash works
            boolean isValid = PasswordUtil.verifyPassword(password, hashedPassword);
            System.out.println();
            System.out.println("Verification test: " + (isValid ? "✓ SUCCESS" : "✗ FAILED"));
            
        } catch (Exception e) {
            System.err.println("Error generating hash: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
