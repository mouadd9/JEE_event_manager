-- Event Management Platform - Database Schema
-- This script is for reference only
-- Hibernate will auto-generate tables based on entities if hbm2ddl.auto is set to 'update' or 'create'

-- NOTE: Run this script manually if you prefer to create tables manually
-- Or let Hibernate generate them automatically

-- ========================================
-- ROLE UPGRADE REQUEST TABLE
-- ========================================
CREATE TABLE IF NOT EXISTS role_upgrade_request (
    id_demande BIGSERIAL PRIMARY KEY,
    id_participant BIGINT NOT NULL,
    nom_organisation VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    site_web VARCHAR(255),
    num_siret VARCHAR(14),
    document_justificatif VARCHAR(255),
    statut VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
    date_demande TIMESTAMP NOT NULL,
    date_traitement TIMESTAMP,
    id_admin_traitant BIGINT,
    commentaire_admin TEXT,
    CONSTRAINT fk_upgrade_participant FOREIGN KEY (id_participant)
        REFERENCES utilisateur(id_utilisateur) ON DELETE CASCADE
);

-- ========================================
-- PASSWORD RESET TOKEN TABLE
-- ========================================
CREATE TABLE IF NOT EXISTS password_reset_token (
    id_token BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) UNIQUE NOT NULL,
    id_utilisateur BIGINT NOT NULL,
    email VARCHAR(255) NOT NULL,
    date_creation TIMESTAMP NOT NULL,
    date_expiration TIMESTAMP NOT NULL,
    est_utilise BOOLEAN NOT NULL DEFAULT FALSE,
    date_utilisation TIMESTAMP,
    adresse_ip VARCHAR(45),
    CONSTRAINT fk_token_utilisateur FOREIGN KEY (id_utilisateur)
        REFERENCES utilisateur(id_utilisateur) ON DELETE CASCADE
);

-- ========================================
-- INDEXES FOR PERFORMANCE
-- ========================================

-- Role Upgrade Request Indexes
CREATE INDEX IF NOT EXISTS idx_upgrade_request_participant ON role_upgrade_request(id_participant);
CREATE INDEX IF NOT EXISTS idx_upgrade_request_status ON role_upgrade_request(statut);
CREATE INDEX IF NOT EXISTS idx_upgrade_request_date ON role_upgrade_request(date_demande);

-- Password Reset Token Indexes
CREATE INDEX IF NOT EXISTS idx_token_value ON password_reset_token(token);
CREATE INDEX IF NOT EXISTS idx_token_user ON password_reset_token(id_utilisateur);
CREATE INDEX IF NOT EXISTS idx_token_expiration ON password_reset_token(date_expiration);

-- Comment Indexes (if not created by Hibernate)
CREATE INDEX IF NOT EXISTS idx_commentaire_evenement ON commentaire(id_evenement);
CREATE INDEX IF NOT EXISTS idx_commentaire_participant ON commentaire(id_participant);
CREATE INDEX IF NOT EXISTS idx_commentaire_date ON commentaire(date_creation);

-- Evaluation Indexes (if not created by Hibernate)
CREATE INDEX IF NOT EXISTS idx_evaluation_evenement ON evaluation(id_evenement);
CREATE INDEX IF NOT EXISTS idx_evaluation_participant ON evaluation(id_participant);

-- ========================================
-- NOTES
-- ========================================
-- 1. User tables (utilisateur, participant, organisateur, administrateur) will be created by Hibernate
-- 2. Comment and Evaluation tables will be created by Hibernate
-- 3. This script only adds tables that Hibernate might not create automatically
-- 4. Indexes are created for performance optimization
