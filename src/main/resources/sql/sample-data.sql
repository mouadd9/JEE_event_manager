-- Event Management Platform - Sample Data
-- Insert test users for development and testing

-- ========================================
-- ADMIN USER
-- ========================================
-- Email: admin@eventmanagement.com
-- Password: Admin@123 (BCrypt hashed)
INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, est_actif, notifications_activees, date_creation, date_modification, user_type)
VALUES ('Admin', 'System', 'admin@eventmanagement.com',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIBXOzO7Wa',
        true, true, NOW(), NOW(), 'administrateur')
ON CONFLICT (email) DO NOTHING;

-- Insert into administrateur table
INSERT INTO administrateur (id_utilisateur, role_admin, permissions, nombre_actions_effectuees)
SELECT id_utilisateur, 'SUPER_ADMIN', 'ALL', 0
FROM utilisateur WHERE email = 'admin@eventmanagement.com'
ON CONFLICT DO NOTHING;

-- ========================================
-- TEST PARTICIPANT
-- ========================================
-- Email: jean.dupont@example.com
-- Password: Test@123
INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, est_actif, notifications_activees, date_creation, date_modification, user_type)
VALUES ('Dupont', 'Jean', 'jean.dupont@example.com',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIBXOzO7Wa',
        true, true, NOW(), NOW(), 'participant')
ON CONFLICT (email) DO NOTHING;

-- Insert into participant table
INSERT INTO participant (id_utilisateur, centres_interet, preferences_notification)
SELECT id_utilisateur, 'Musique, Sport, Technologie', 'email,sms'
FROM utilisateur WHERE email = 'jean.dupont@example.com'
ON CONFLICT DO NOTHING;

-- ========================================
-- TEST ORGANIZER
-- ========================================
-- Email: marie.martin@example.com
-- Password: Org@123
INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, telephone, ville, est_actif, notifications_activees, date_creation, date_modification, user_type)
VALUES ('Martin', 'Marie', 'marie.martin@example.com',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIBXOzO7Wa',
        '0612345678', 'Paris', true, true, NOW(), NOW(), 'organisateur')
ON CONFLICT (email) DO NOTHING;

-- Insert into organisateur table
INSERT INTO organisateur (id_utilisateur, nom_organisation, description_organisation, est_verifie, nombre_evenements_organises)
SELECT id_utilisateur, 'Events Paris', 'Organisation d''événements culturels à Paris', true, 0
FROM utilisateur WHERE email = 'marie.martin@example.com'
ON CONFLICT DO NOTHING;

-- ========================================
-- ADDITIONAL TEST PARTICIPANTS
-- ========================================
-- Password for all: Test@123
INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, est_actif, notifications_activees, date_creation, date_modification, user_type)
VALUES
('Petit', 'Sophie', 'sophie.petit@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIBXOzO7Wa', true, true, NOW(), NOW(), 'participant'),
('Bernard', 'Pierre', 'pierre.bernard@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIBXOzO7Wa', true, true, NOW(), NOW(), 'participant')
ON CONFLICT (email) DO NOTHING;

-- Insert into participant table for additional users
INSERT INTO participant (id_utilisateur, centres_interet)
SELECT id_utilisateur, 'Art, Culture'
FROM utilisateur WHERE email IN ('sophie.petit@example.com', 'pierre.bernard@example.com')
ON CONFLICT DO NOTHING;

-- ========================================
-- TEST DATA SUMMARY
-- ========================================
-- Admin: admin@eventmanagement.com / Admin@123
-- Participant: jean.dupont@example.com / Test@123
-- Organizer: marie.martin@example.com / Org@123
-- Additional Participants: sophie.petit@example.com, pierre.bernard@example.com / Test@123
