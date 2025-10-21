-- Migration: Add organizer approval system
-- Date: 2025-10-21
-- Description: Adds 'approved' column to organisateurs table for admin approval workflow

ALTER TABLE organisateurs 
ADD COLUMN approved BOOLEAN NOT NULL DEFAULT FALSE AFTER nombre_evenements_organises,
ADD INDEX idx_approved (approved);

-- Optional: Approve all existing organizers (so they can continue using the platform)
-- Comment out this line if you want existing organizers to also require approval
UPDATE organisateurs SET approved = TRUE;

-- ============================================
-- Create Admin Account
-- ============================================
-- Note: This uses a temporary password 'admin123' (hashed)
-- You should change this password after first login

-- Insert admin user
INSERT INTO utilisateurs (nom, email, mot_de_passe, statut, email_verified, date_inscription, type_utilisateur)
VALUES (
    'Admin', 
    '003haytam2@gmail.com', 
    '$2a$10$K5V8R7m8YLJQxVZ9YLJQxK5V8R7m8YLJQxVZ9YLJQxK5V8R7m8YLJu',  -- Password: admin123 (you should hash properly)
    'ACTIF', 
    TRUE, 
    NOW(), 
    'ADMINISTRATEUR'
);

-- Get the inserted user ID
SET @admin_id = LAST_INSERT_ID();

-- Insert admin details
INSERT INTO administrateurs (
    utilisateur_id, 
    niveau_acces, 
    fonction, 
    peut_gerer_utilisateurs, 
    peut_gerer_evenements, 
    peut_voir_statistiques, 
    peut_moderer_contenu
)
VALUES (
    @admin_id, 
    3,                  -- Super admin level
    'Super Admin', 
    TRUE,              -- Can manage users
    TRUE,              -- Can manage events
    TRUE,              -- Can view statistics
    TRUE               -- Can moderate content
);

-- Display success message
SELECT CONCAT('Admin account created with ID: ', @admin_id) AS message;
SELECT 'Email: 003haytam2@gmail.com' AS credentials;
SELECT 'Password: admin123 (CHANGE THIS AFTER FIRST LOGIN!)' AS warning;
