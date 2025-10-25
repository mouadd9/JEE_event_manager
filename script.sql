INSERT INTO utilisateur (id, nom, email, mot_de_passe_hash, user_type, created_at, updated_at)
VALUES (1, 'Organisateur Test', 'organizer@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ORGANISATEUR', NOW(), NOW());

INSERT INTO organisateur (utilisateur_id, description, entreprise, siret, site_web)
VALUES (1, 'Organisateur de test pour les événements', 'Test Company', '12345678901234', 'https://testcompany.com');

INSERT INTO utilisateur (id, nom, email, mot_de_passe_hash, user_type, created_at, updated_at)
VALUES (2, 'Participant Test', 'participant@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'PARTICIPANT', NOW(), NOW());

INSERT INTO participant (utilisateur_id, date_naissance, preferences, telephone)
VALUES (2, '1990-01-01', 'Musique, Sport, Technologie', '+33123456789');
