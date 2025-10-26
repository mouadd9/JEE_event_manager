-- Test Events for Participant Viewing
-- This script creates diverse events with existing images from uploads/events folder
-- Make sure to run this after test_users.sql

-- First, verify we have the test organizer (id should be 2 from test_users.sql)
-- The organizer user email is: organizer@test.com

-- Get the organizer_id (should be the organisateur record linked to utilisateur id 2)
SET @organizer_id = (SELECT utilisateur_id FROM organisateur WHERE utilisateur_id = 2);

-- Insert test categories if they don't exist
INSERT IGNORE INTO categorie (nom, description, icon) VALUES
('Technologie', 'Événements liés à la technologie et l''innovation', '💻'),
('Business', 'Conférences et événements professionnels', '💼'),
('Art & Culture', 'Événements artistiques et culturels', '🎨'),
('Sport', 'Événements sportifs et activités physiques', '⚽'),
('Musique', 'Concerts et événements musicaux', '🎵'),
('Formation', 'Ateliers et sessions de formation', '📚'),
('Networking', 'Événements de réseautage professionnel', '🤝'),
('Santé & Bien-être', 'Événements de santé et bien-être', '🧘');

-- Get category IDs
SET @tech_cat = (SELECT id FROM categorie WHERE nom = 'Technologie' LIMIT 1);
SET @business_cat = (SELECT id FROM categorie WHERE nom = 'Business' LIMIT 1);
SET @art_cat = (SELECT id FROM categorie WHERE nom = 'Art & Culture' LIMIT 1);
SET @sport_cat = (SELECT id FROM categorie WHERE nom = 'Sport' LIMIT 1);
SET @music_cat = (SELECT id FROM categorie WHERE nom = 'Musique' LIMIT 1);
SET @training_cat = (SELECT id FROM categorie WHERE nom = 'Formation' LIMIT 1);
SET @network_cat = (SELECT id FROM categorie WHERE nom = 'Networking' LIMIT 1);
SET @health_cat = (SELECT id FROM categorie WHERE nom = 'Santé & Bien-être' LIMIT 1);

-- Insert diverse test events with existing images
INSERT INTO evenement (titre, description, date_debut, date_fin, lieu, capacite_max, prix, image_url, statut, organisateur_id, categorie_id, created_at, updated_at) VALUES

-- Event 1: Tech Conference
('Conférence IA & Innovation 2025', 
'Découvrez les dernières avancées en intelligence artificielle et machine learning. Rejoignez les experts de l''industrie pour une journée d''apprentissage et de networking. Au programme : keynotes inspirants, ateliers pratiques, démonstrations en direct et sessions de Q&A.',
'2025-11-15 09:00:00', 
'2025-11-15 18:00:00',
'Centre des Congrès, Paris',
200,
49.99,
'024fefac-80b8-46c2-8b9c-b205e22ab1a7.png',
'PUBLIE',
@organizer_id,
@tech_cat,
NOW(),
NOW()),

-- Event 2: Business Summit
('Summit Digital Marketing 2025', 
'Le rendez-vous incontournable des professionnels du marketing digital. Stratégies SEO, Social Media, Content Marketing, Analytics et plus encore. Networking avec les meilleurs experts du secteur et découverte des outils les plus performants.',
'2025-11-20 08:30:00', 
'2025-11-20 17:00:00',
'Palais des Congrès, Lyon',
150,
79.00,
'0b2bd193-2d3a-4649-be12-32fdf0039faf.png',
'PUBLIE',
@organizer_id,
@business_cat,
NOW(),
NOW()),

-- Event 3: Art Exhibition
('Exposition d''Art Contemporain', 
'Une collection exceptionnelle d''œuvres d''artistes émergents et confirmés. Peinture, sculpture, photographie et installations immersives. Vernissage en présence des artistes avec cocktail et musique live. Une expérience artistique unique à ne pas manquer.',
'2025-11-25 19:00:00', 
'2025-11-25 23:00:00',
'Galerie d''Art Moderne, Bordeaux',
100,
0.00,
'34a4a83a-d1df-48b4-9120-9c1b4e246cd8.png',
'PUBLIE',
@organizer_id,
@art_cat,
NOW(),
NOW()),

-- Event 4: Sports Marathon
('Marathon de la Ville 2025', 
'Participez au plus grand marathon de la région ! 42km à travers les plus beaux quartiers de la ville. Parcours chronométré, ravitaillements tous les 5km, médaille finisher, t-shirt technique et animations musicales tout au long du parcours.',
'2025-12-05 07:00:00', 
'2025-12-05 14:00:00',
'Stade Municipal, Marseille',
500,
25.00,
'4711c67f-7359-47e4-b4dd-3cc1d23fdb88.jpg',
'PUBLIE',
@organizer_id,
@sport_cat,
NOW(),
NOW()),

-- Event 5: Music Festival
('Festival de Jazz en Ville', 
'Trois jours de musique jazz avec des artistes internationaux renommés. Plus de 20 concerts, scènes multiples, food trucks gastronomiques et espace détente. Une ambiance conviviale pour les amoureux du jazz et des découvertes musicales.',
'2025-12-10 18:00:00', 
'2025-12-12 23:00:00',
'Parc des Expositions, Toulouse',
1000,
89.00,
'54400b9e-8daa-440d-8389-864c06796b37.png',
'PUBLIE',
@organizer_id,
@music_cat,
NOW(),
NOW()),

-- Event 6: Training Workshop
('Atelier Développement Web Full-Stack', 
'Formation intensive de 3 jours pour maîtriser le développement web moderne. HTML5, CSS3, JavaScript, React, Node.js, MongoDB. Projets pratiques, code reviews et certification à la fin. Niveau intermédiaire requis. Ordinateur portable nécessaire.',
'2025-11-28 09:00:00', 
'2025-11-30 17:00:00',
'École d''Informatique, Lille',
30,
299.00,
'5cd24455-2d4c-446b-ba68-39e50315af1e.png',
'PUBLIE',
@organizer_id,
@training_cat,
NOW(),
NOW()),

-- Event 7: Networking Event
('Soirée Networking Entrepreneurs', 
'Rencontrez des entrepreneurs, investisseurs et innovateurs lors de cette soirée exclusive. Pitchs de startups, speed networking, cocktail dînatoire et DJ set. L''occasion idéale pour développer votre réseau professionnel dans une ambiance décontractée.',
'2025-12-15 18:30:00', 
'2025-12-15 22:30:00',
'Rooftop Business Center, Nice',
80,
35.00,
'5e4649b4-1b55-4214-b843-9d08fe0b951c.png',
'PUBLIE',
@organizer_id,
@network_cat,
NOW(),
NOW()),

-- Event 8: Yoga & Wellness
('Retraite Yoga & Méditation', 
'Week-end ressourçant dans un cadre naturel exceptionnel. Sessions de yoga matin et soir, méditation guidée, ateliers de pleine conscience, cuisine végétarienne bio et bains de forêt. Tous niveaux bienvenus. Hébergement et repas inclus.',
'2025-12-20 16:00:00', 
'2025-12-22 11:00:00',
'Centre de Bien-être, Provence',
40,
199.00,
'c97402f2-3a13-4728-ad5c-dea25e20e7cf.png',
'PUBLIE',
@organizer_id,
@health_cat,
NOW(),
NOW()),

-- Event 9: Tech Meetup (Free Event)
('Meetup Développeurs JavaScript', 
'Rencontre mensuelle de la communauté JS locale. Présentations techniques, retours d''expérience, discussions sur les dernières tendances et outils. Pizza et boissons offertes. Ambiance conviviale garantie !',
'2025-11-18 19:00:00', 
'2025-11-18 22:00:00',
'CoWorking Space, Nantes',
50,
0.00,
'e00ace46-4c83-452f-a930-029da22adb2f.png',
'PUBLIE',
@organizer_id,
@tech_cat,
NOW(),
NOW()),

-- Event 10: Business Conference (Draft - not published yet)
('Conférence Leadership 2026', 
'La plus grande conférence sur le leadership et le management en France. Speakers internationaux, workshops interactifs et masterclasses exclusives.',
'2026-02-10 09:00:00', 
'2026-02-11 18:00:00',
'Grand Hôtel, Paris',
300,
150.00,
NULL,
'BROUILLON',
@organizer_id,
@business_cat,
NOW(),
NOW());

-- Display summary
SELECT 
    'Events créés avec succès!' as Message,
    COUNT(*) as 'Nombre d''événements',
    SUM(CASE WHEN statut = 'PUBLIE' THEN 1 ELSE 0 END) as 'Publiés',
    SUM(CASE WHEN statut = 'BROUILLON' THEN 1 ELSE 0 END) as 'Brouillons'
FROM evenement 
WHERE organisateur_id = @organizer_id;

-- Show all created events
SELECT 
    e.id,
    e.titre,
    c.nom as categorie,
    e.date_debut,
    e.lieu,
    e.prix,
    e.capacite_max,
    e.statut,
    e.image_url
FROM evenement e
LEFT JOIN categorie c ON e.categorie_id = c.id
WHERE e.organisateur_id = @organizer_id
ORDER BY e.date_debut;
