-- ============================================
-- Schema SQL pour Event Management System
-- Base de données: MySQL 8+
-- ENSA Tétouan
-- ============================================

-- Suppression des tables si elles existent (dans l'ordre inverse des dépendances)
DROP TABLE IF EXISTS evaluations;
DROP TABLE IF EXISTS commentaires;
DROP TABLE IF EXISTS inscriptions;
DROP TABLE IF EXISTS evenement_categorie;
DROP TABLE IF EXISTS evenements;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS administrateurs;
DROP TABLE IF EXISTS participants;
DROP TABLE IF EXISTS organisateurs;
DROP TABLE IF EXISTS utilisateurs;

-- ============================================
-- Table des utilisateurs (parent)
-- ============================================
CREATE TABLE utilisateurs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type_utilisateur VARCHAR(20) NOT NULL,
    nom VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    statut VARCHAR(20) NOT NULL DEFAULT 'ACTIF',
    date_inscription DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    derniere_connexion DATETIME,
    photo_profil VARCHAR(255),
    verification_code VARCHAR(6),
    verification_code_expiry DATETIME,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    INDEX idx_email (email),
    INDEX idx_statut (statut),
    INDEX idx_type (type_utilisateur)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table des organisateurs
-- ============================================
CREATE TABLE organisateurs (
    utilisateur_id BIGINT PRIMARY KEY,
    organisation VARCHAR(150),
    telephone VARCHAR(20),
    site_web VARCHAR(255),
    description TEXT,
    adresse VARCHAR(255),
    nombre_evenements_organises INT DEFAULT 0,
    approved BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id) ON DELETE CASCADE,
    INDEX idx_organisation (organisation),
    INDEX idx_approved (approved)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table des participants
-- ============================================
CREATE TABLE participants (
    utilisateur_id BIGINT PRIMARY KEY,
    preferences VARCHAR(500),
    interets VARCHAR(500),
    ville VARCHAR(100),
    date_naissance DATE,
    telephone VARCHAR(20),
    nombre_inscriptions INT DEFAULT 0,
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id) ON DELETE CASCADE,
    INDEX idx_ville (ville)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table des administrateurs
-- ============================================
CREATE TABLE administrateurs (
    utilisateur_id BIGINT PRIMARY KEY,
    niveau_acces INT NOT NULL DEFAULT 1,
    departement VARCHAR(100),
    fonction VARCHAR(100),
    date_nomination DATETIME DEFAULT CURRENT_TIMESTAMP,
    peut_gerer_utilisateurs BOOLEAN DEFAULT FALSE,
    peut_gerer_evenements BOOLEAN DEFAULT FALSE,
    peut_voir_statistiques BOOLEAN DEFAULT TRUE,
    peut_moderer_contenu BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id) ON DELETE CASCADE,
    CONSTRAINT chk_niveau_acces CHECK (niveau_acces >= 1 AND niveau_acces <= 3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table des catégories
-- ============================================
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    icone VARCHAR(100),
    couleur VARCHAR(20),
    active BOOLEAN DEFAULT TRUE,
    INDEX idx_nom (nom),
    INDEX idx_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table des événements
-- ============================================
CREATE TABLE evenements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    date_debut DATETIME NOT NULL,
    date_fin DATETIME NOT NULL,
    lieu VARCHAR(255) NOT NULL,
    adresse_complete VARCHAR(500),
    latitude DOUBLE,
    longitude DOUBLE,
    capacite INT NOT NULL,
    places_disponibles INT,
    statut VARCHAR(20) NOT NULL DEFAULT 'BROUILLON',
    image_url VARCHAR(500),
    prix DOUBLE,
    gratuit BOOLEAN DEFAULT FALSE,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME,
    nombre_vues INT DEFAULT 0,
    nombre_inscriptions INT DEFAULT 0,
    note_moyenne DOUBLE DEFAULT 0.0,
    organisateur_id BIGINT NOT NULL,
    FOREIGN KEY (organisateur_id) REFERENCES organisateurs(utilisateur_id) ON DELETE CASCADE,
    INDEX idx_statut (statut),
    INDEX idx_date_debut (date_debut),
    INDEX idx_organisateur (organisateur_id),
    INDEX idx_lieu (lieu),
    CONSTRAINT chk_capacite CHECK (capacite > 0),
    CONSTRAINT chk_dates CHECK (date_fin > date_debut)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table de liaison événements-catégories (Many-to-Many)
-- ============================================
CREATE TABLE evenement_categorie (
    evenement_id BIGINT NOT NULL,
    categorie_id BIGINT NOT NULL,
    PRIMARY KEY (evenement_id, categorie_id),
    FOREIGN KEY (evenement_id) REFERENCES evenements(id) ON DELETE CASCADE,
    FOREIGN KEY (categorie_id) REFERENCES categories(id) ON DELETE CASCADE,
    INDEX idx_evenement (evenement_id),
    INDEX idx_categorie (categorie_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table des inscriptions
-- ============================================
CREATE TABLE inscriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date_inscription DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    statut VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
    date_reponse DATETIME,
    message_participant TEXT,
    message_organisateur TEXT,
    nombre_places INT DEFAULT 1,
    presence_confirmee BOOLEAN DEFAULT FALSE,
    date_annulation DATETIME,
    motif_annulation VARCHAR(500),
    participant_id BIGINT NOT NULL,
    evenement_id BIGINT NOT NULL,
    FOREIGN KEY (participant_id) REFERENCES participants(utilisateur_id) ON DELETE CASCADE,
    FOREIGN KEY (evenement_id) REFERENCES evenements(id) ON DELETE CASCADE,
    UNIQUE KEY unique_inscription (participant_id, evenement_id),
    INDEX idx_participant (participant_id),
    INDEX idx_evenement (evenement_id),
    INDEX idx_statut (statut),
    INDEX idx_date_inscription (date_inscription)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table des commentaires
-- ============================================
CREATE TABLE commentaires (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    texte TEXT NOT NULL,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME,
    modere BOOLEAN DEFAULT FALSE,
    signale BOOLEAN DEFAULT FALSE,
    nombre_signalements INT DEFAULT 0,
    visible BOOLEAN DEFAULT TRUE,
    participant_id BIGINT NOT NULL,
    evenement_id BIGINT NOT NULL,
    FOREIGN KEY (participant_id) REFERENCES participants(utilisateur_id) ON DELETE CASCADE,
    FOREIGN KEY (evenement_id) REFERENCES evenements(id) ON DELETE CASCADE,
    INDEX idx_participant (participant_id),
    INDEX idx_evenement (evenement_id),
    INDEX idx_date_creation (date_creation),
    INDEX idx_visible (visible)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table des évaluations
-- ============================================
CREATE TABLE evaluations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    note INT NOT NULL,
    texte TEXT,
    horodatage DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME,
    visible BOOLEAN DEFAULT TRUE,
    verifie BOOLEAN DEFAULT FALSE,
    utile_count INT DEFAULT 0,
    participant_id BIGINT NOT NULL,
    evenement_id BIGINT NOT NULL,
    FOREIGN KEY (participant_id) REFERENCES participants(utilisateur_id) ON DELETE CASCADE,
    FOREIGN KEY (evenement_id) REFERENCES evenements(id) ON DELETE CASCADE,
    UNIQUE KEY unique_evaluation (participant_id, evenement_id),
    INDEX idx_participant (participant_id),
    INDEX idx_evenement (evenement_id),
    INDEX idx_note (note),
    INDEX idx_visible (visible),
    CONSTRAINT chk_note CHECK (note >= 1 AND note <= 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Insertion de données de test
-- ============================================

-- Insertion de catégories par défaut
INSERT INTO categories (nom, description, icone, couleur, active) VALUES
('Technologie', 'Événements liés à la technologie et l''informatique', 'fa-laptop-code', '#3498db', TRUE),
('Sport', 'Événements sportifs et activités physiques', 'fa-running', '#e74c3c', TRUE),
('Culture', 'Événements culturels, arts et spectacles', 'fa-palette', '#9b59b6', TRUE),
('Éducation', 'Conférences, formations et ateliers éducatifs', 'fa-graduation-cap', '#2ecc71', TRUE),
('Business', 'Événements professionnels et networking', 'fa-briefcase', '#34495e', TRUE),
('Musique', 'Concerts et événements musicaux', 'fa-music', '#e67e22', TRUE),
('Gastronomie', 'Événements culinaires et gastronomiques', 'fa-utensils', '#f39c12', TRUE),
('Santé & Bien-être', 'Événements de santé, fitness et bien-être', 'fa-heartbeat', '#1abc9c', TRUE);

-- Insertion d'un administrateur par défaut
-- Mot de passe: admin123 (hashed)
INSERT INTO utilisateurs (type_utilisateur, nom, email, mot_de_passe, statut, email_verified, date_inscription)
VALUES ('ADMINISTRATEUR', 'Admin Système', '003haytam2@gmail.com', 'Jw1PbkJeNkOYugHZigJrAA==:2c0598b9a77827a2efcfc6b0b777045f6cdfbf10c7aa947cdb249c364043bc83', TRUE, NOW());

SET @admin_id = LAST_INSERT_ID();

INSERT INTO administrateurs (utilisateur_id, niveau_acces, departement, fonction, date_nomination,
                             peut_gerer_utilisateurs, peut_gerer_evenements, peut_voir_statistiques, peut_moderer_contenu)
VALUES (@admin_id, 3, 'IT', 'Super Administrateur', NOW(), TRUE, TRUE, TRUE, TRUE);

-- Insertion d'un organisateur de test
INSERT INTO utilisateurs (type_utilisateur, nom, email, mot_de_passe, statut, email_verified, date_inscription)
VALUES ('ORGANISATEUR', 'ENSA Tétouan', 'organisateur@ensa.ma', 'organizer123', 'ACTIF', TRUE, NOW());

SET @org_id = LAST_INSERT_ID();

INSERT INTO organisateurs (utilisateur_id, organisation, telephone, site_web, description, adresse, nombre_evenements_organises, approved)
VALUES (@org_id, 'École Nationale des Sciences Appliquées', '+212539688000', 'https://ensa-tetouan.ac.ma',
        'École d''ingénieurs à Tétouan', 'Tétouan, Maroc', 0, TRUE);

INSERT INTO utilisateurs (type_utilisateur, nom, email, mot_de_passe, statut, email_verified, date_inscription)
VALUES ('PARTICIPANT', 'Mohammed Alami', 'participant@test.ma', 'participant123', 'ACTIF', TRUE, NOW());

SET @part_id = LAST_INSERT_ID();

INSERT INTO participants (utilisateur_id, preferences, interets, ville, nombre_inscriptions)
VALUES (@part_id, 'Technologie, Innovation', 'Développement web, Intelligence artificielle', 'Tétouan', 0);


-- ============================================
-- Vues utiles
-- ============================================

-- Vue pour les événements publiés avec leurs statistiques
CREATE OR REPLACE VIEW v_evenements_publies AS
SELECT
    e.id,
    e.titre,
    e.description,
    e.date_debut,
    e.date_fin,
    e.lieu,
    e.capacite,
    e.places_disponibles,
    e.prix,
    e.gratuit,
    e.image_url,
    e.nombre_vues,
    e.nombre_inscriptions,
    e.note_moyenne,
    u.nom AS organisateur_nom,
    u.email AS organisateur_email,
    o.organisation AS organisateur_organisation,
    GROUP_CONCAT(c.nom SEPARATOR ', ') AS categories
FROM evenements e
INNER JOIN organisateurs o ON e.organisateur_id = o.utilisateur_id
INNER JOIN utilisateurs u ON o.utilisateur_id = u.id
LEFT JOIN evenement_categorie ec ON e.id = ec.evenement_id
LEFT JOIN categories c ON ec.categorie_id = c.id
WHERE e.statut = 'PUBLIE'
GROUP BY e.id, e.titre, e.description, e.date_debut, e.date_fin, e.lieu,
         e.capacite, e.places_disponibles, e.prix, e.gratuit, e.image_url,
         e.nombre_vues, e.nombre_inscriptions, e.note_moyenne,
         u.nom, u.email, o.organisation;

-- Vue pour les statistiques globales
CREATE OR REPLACE VIEW v_statistiques_globales AS
SELECT
    (SELECT COUNT(*) FROM utilisateurs WHERE type_utilisateur = 'ORGANISATEUR' AND statut = 'ACTIF') AS total_organisateurs,
    (SELECT COUNT(*) FROM utilisateurs WHERE type_utilisateur = 'PARTICIPANT' AND statut = 'ACTIF') AS total_participants,
    (SELECT COUNT(*) FROM evenements WHERE statut = 'PUBLIE') AS total_evenements_publies,
    (SELECT COUNT(*) FROM evenements WHERE statut = 'PUBLIE' AND date_debut > NOW()) AS evenements_a_venir,
    (SELECT COUNT(*) FROM inscriptions WHERE statut = 'ACCEPTEE') AS total_inscriptions_acceptees,
    (SELECT AVG(note_moyenne) FROM evenements WHERE note_moyenne > 0) AS moyenne_notes_evenements;

-- ============================================
-- Procédures stockées utiles
-- ============================================

DELIMITER //

-- Procédure pour accepter une inscription et mettre à jour les places disponibles
CREATE PROCEDURE accepter_inscription(IN p_inscription_id BIGINT, IN p_message TEXT)
BEGIN
    DECLARE v_evenement_id BIGINT;
    DECLARE v_nombre_places INT;
    DECLARE v_places_disponibles INT;

    -- Récupérer les informations de l'inscription
    SELECT evenement_id, nombre_places INTO v_evenement_id, v_nombre_places
    FROM inscriptions WHERE id = p_inscription_id;

    -- Vérifier les places disponibles
    SELECT places_disponibles INTO v_places_disponibles
    FROM evenements WHERE id = v_evenement_id;

    IF v_places_disponibles >= v_nombre_places THEN
        -- Accepter l'inscription
        UPDATE inscriptions
        SET statut = 'ACCEPTEE',
            date_reponse = NOW(),
            message_organisateur = p_message
        WHERE id = p_inscription_id;

        -- Mettre à jour les places disponibles
        UPDATE evenements
        SET places_disponibles = places_disponibles - v_nombre_places,
            nombre_inscriptions = nombre_inscriptions + 1
        WHERE id = v_evenement_id;

        SELECT 'Inscription acceptée avec succès' AS message;
    ELSE
        SELECT 'Places insuffisantes' AS message;
    END IF;
END //

DELIMITER ;

-- ============================================
-- Triggers
-- ============================================

DELIMITER //

-- Trigger pour initialiser places_disponibles lors de la création d'un événement
CREATE TRIGGER before_evenement_insert
BEFORE INSERT ON evenements
FOR EACH ROW
BEGIN
    IF NEW.places_disponibles IS NULL THEN
        SET NEW.places_disponibles = NEW.capacite;
    END IF;
    IF NEW.gratuit IS NULL THEN
        SET NEW.gratuit = FALSE;
    END IF;
END //

-- Trigger pour mettre à jour la date de modification
CREATE TRIGGER before_evenement_update
BEFORE UPDATE ON evenements
FOR EACH ROW
BEGIN
    SET NEW.date_modification = NOW();
END //

DELIMITER ;

-- ============================================
-- Fin du script
-- ============================================
