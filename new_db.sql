-- ========================================
-- Event Management Platform - MySQL Database Schema
-- Complete database setup for MySQL
-- ========================================

-- Drop database if exists and create fresh
CREATE DATABASE event_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE event_management;

-- ========================================
-- Main User Table (Parent table for inheritance)
-- ========================================
CREATE TABLE utilisateur (
    id_utilisateur BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    telephone VARCHAR(20),
    adresse TEXT,
    ville VARCHAR(100),
    code_postal VARCHAR(10),
    pays VARCHAR(100),
    photo_profil VARCHAR(255),
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    est_actif BOOLEAN NOT NULL DEFAULT TRUE,
    notifications_activees BOOLEAN NOT NULL DEFAULT TRUE,
    user_type VARCHAR(31) NOT NULL,
    INDEX idx_email (email),
    INDEX idx_user_type (user_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- Participant Table (Inherits from utilisateur)
-- ========================================
CREATE TABLE participant (
    id_utilisateur BIGINT PRIMARY KEY,
    centres_interet TEXT,
    preferences_notification VARCHAR(255),
    FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id_utilisateur) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- Organisateur Table (Inherits from utilisateur)
-- ========================================
CREATE TABLE organisateur (
    id_utilisateur BIGINT PRIMARY KEY,
    nom_organisation VARCHAR(200) NOT NULL,
    description_organisation TEXT,
    site_web VARCHAR(255),
    num_siret VARCHAR(14),
    est_verifie BOOLEAN NOT NULL DEFAULT FALSE,
    date_verification DATETIME,
    nombre_evenements_organises INT NOT NULL DEFAULT 0,
    FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id_utilisateur) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- Administrateur Table (Inherits from utilisateur)
-- ========================================
CREATE TABLE administrateur (
    id_utilisateur BIGINT PRIMARY KEY,
    role_admin VARCHAR(50) NOT NULL DEFAULT 'ADMIN',
    permissions VARCHAR(255),
    nombre_actions_effectuees INT NOT NULL DEFAULT 0,
    FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id_utilisateur) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- Commentaire Table
-- ========================================
CREATE TABLE commentaire (
    id_commentaire BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_evenement BIGINT NOT NULL,
    id_participant BIGINT NOT NULL,
    contenu TEXT NOT NULL,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME,
    est_modere BOOLEAN NOT NULL DEFAULT FALSE,
    raison_moderation TEXT,
    modere_par BIGINT,
    date_moderation DATETIME,
    est_supprime BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (id_participant) REFERENCES participant(id_utilisateur) ON DELETE CASCADE,
    INDEX idx_evenement (id_evenement),
    INDEX idx_participant (id_participant),
    INDEX idx_date_creation (date_creation)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- Evaluation Table
-- ========================================
CREATE TABLE evaluation (
    id_evaluation BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_evenement BIGINT NOT NULL,
    id_participant BIGINT NOT NULL,
    note INT NOT NULL CHECK (note >= 1 AND note <= 5),
    commentaire TEXT,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME,
    FOREIGN KEY (id_participant) REFERENCES participant(id_utilisateur) ON DELETE CASCADE,
    UNIQUE KEY unique_evaluation (id_evenement, id_participant),
    INDEX idx_evenement (id_evenement),
    INDEX idx_participant (id_participant),
    INDEX idx_note (note)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- Role Upgrade Request Table
-- ========================================
CREATE TABLE role_upgrade_request (
    id_demande BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_participant BIGINT NOT NULL,
    nom_organisation VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    site_web VARCHAR(255),
    num_siret VARCHAR(14),
    statut VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
    date_demande DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_traitement DATETIME,
    traite_par BIGINT,
    commentaire_admin TEXT,
    FOREIGN KEY (id_participant) REFERENCES participant(id_utilisateur) ON DELETE CASCADE,
    INDEX idx_statut (statut),
    INDEX idx_participant (id_participant),
    INDEX idx_date_demande (date_demande)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- Password Reset Token Table
-- ========================================
CREATE TABLE password_reset_token (
    id_token BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_utilisateur BIGINT NOT NULL,
    email VARCHAR(255) NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_expiration DATETIME NOT NULL,
    est_utilise BOOLEAN NOT NULL DEFAULT FALSE,
    date_utilisation DATETIME,
    FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id_utilisateur) ON DELETE CASCADE,
    INDEX idx_token (token),
    INDEX idx_email (email),
    INDEX idx_expiration (date_expiration)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- Sample Data - Test Users
-- ========================================

-- Admin User
-- Email: admin@eventmanagement.com
-- Password: Admin@123
INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, est_actif, notifications_activees, user_type)
VALUES ('Admin', 'System', 'admin@eventmanagement.com',
        '$2a$12$H4QVGps6cUJGWjOlOAn7Ke5ZQCPmEj0vDpdaER.L1JiLDGWqmGgv.',
        TRUE, TRUE, 'administrateur');

INSERT INTO administrateur (id_utilisateur, role_admin, permissions, nombre_actions_effectuees)
VALUES (LAST_INSERT_ID(), 'SUPER_ADMIN', 'ALL', 0);

-- Test Participant
-- Email: jean.dupont@example.com
-- Password: Test@123
INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, est_actif, notifications_activees, user_type)
VALUES ('Dupont', 'Jean', 'jean.dupont@example.com',
        '$2a$12$/ZgiqBXOT.reO2xubAbTO.y3AdZdNz.l6GCXZkesomqCfgwaeZL4m',
        TRUE, TRUE, 'participant');

INSERT INTO participant (id_utilisateur, centres_interet, preferences_notification)
VALUES (LAST_INSERT_ID(), 'Musique, Sport, Technologie', 'email,sms');

-- Test Organizer
-- Email: marie.martin@example.com
-- Password: Org@123
INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, telephone, ville, est_actif, notifications_activees, user_type)
VALUES ('Martin', 'Marie', 'marie.martin@example.com',
        '$2a$12$39BgBhy4f6c/V9TE0DDboeFny9rB1bhwsD.qEtIhvoJNsR/6uCeca',
        '0612345678', 'Paris', TRUE, TRUE, 'organisateur');

INSERT INTO organisateur (id_utilisateur, nom_organisation, description_organisation, est_verifie, nombre_evenements_organises)
VALUES (LAST_INSERT_ID(), 'Events Paris', 'Organisation d''événements culturels à Paris', TRUE, 0);

-- Additional Test Participants
-- Password for all: Test@123
INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, est_actif, notifications_activees, user_type)
VALUES
('Petit', 'Sophie', 'sophie.petit@example.com', '$2a$12$/ZgiqBXOT.reO2xubAbTO.y3AdZdNz.l6GCXZkesomqCfgwaeZL4m', TRUE, TRUE, 'participant'),
('Bernard', 'Pierre', 'pierre.bernard@example.com', '$2a$12$/ZgiqBXOT.reO2xubAbTO.y3AdZdNz.l6GCXZkesomqCfgwaeZL4m', TRUE, TRUE, 'participant');

INSERT INTO participant (id_utilisateur, centres_interet)
SELECT id_utilisateur, 'Art, Culture'
FROM utilisateur WHERE email IN ('sophie.petit@example.com', 'pierre.bernard@example.com');

-- ========================================
-- Summary
-- ========================================
-- Database: event_management
-- Tables Created: 8
-- Sample Users: 5
--
-- Test Login Credentials:
-- Admin: admin@eventmanagement.com / Admin@123
-- Participant: jean.dupont@example.com / Test@123
-- Organizer: marie.martin@example.com / Org@123
-- Participants: sophie.petit@example.com, pierre.bernard@example.com / Test@123
-- ========================================
