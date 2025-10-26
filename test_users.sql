-- Test Users for Event Manager
-- Password for all users: password123

-- 1. ORGANIZER USER
INSERT INTO utilisateur (nom, email, mot_de_passe_hash, user_type, is_active, is_suspended, is_verified, created_at, updated_at)
VALUES ('Test Organizer', 'organizer@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ORGANISATEUR', 1, 0, 1, NOW(), NOW());

SET @organizer_id = LAST_INSERT_ID();

INSERT INTO organisateur (utilisateur_id, description, entreprise, siret, site_web)
VALUES (@organizer_id, 'Professional event organizer', 'Test Events Company', '12345678901234', 'https://testevents.com');

-- 2. PARTICIPANT USER
INSERT INTO utilisateur (nom, email, mot_de_passe_hash, user_type, is_active, is_suspended, is_verified, created_at, updated_at)
VALUES ('Test Participant', 'participant@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'PARTICIPANT', 1, 0, 1, NOW(), NOW());

SET @participant_id = LAST_INSERT_ID();

INSERT INTO participant (utilisateur_id, date_naissance, preferences, telephone)
VALUES (@participant_id, '1990-01-01', 'Music, Sports, Technology', '+33123456789');

-- 3. ADMIN USER
INSERT INTO utilisateur (nom, email, mot_de_passe_hash, user_type, is_active, is_suspended, is_verified, created_at, updated_at)
VALUES ('Admin User', 'admin@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', 1, 0, 1, NOW(), NOW());

SET @admin_id = LAST_INSERT_ID();

INSERT INTO admin (id, role, permissions)
VALUES (@admin_id, 'SUPER_ADMIN', 'all');
