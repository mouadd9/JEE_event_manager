-- Script SQL pour tester la fonctionnalité de génération de billets PDF
-- Ce script crée des données de test pour vérifier le fonctionnement complet

-- 1. Créer un événement de test
INSERT INTO evenement (titre, description, date_debut, date_fin, lieu, capacite, statut, organisateur_id, created_at, updated_at)
VALUES (
    'Test Billets PDF - Conférence Tech 2025',
    'Conférence de test pour vérifier la génération de billets PDF avec toutes les fonctionnalités.',
    NOW() + INTERVAL '7 days',  -- Dans 7 jours
    NOW() + INTERVAL '9 days',  -- Durée de 2h
    'Centre de Conférences Tech, Paris',
    100,
    'PUBLIE',
    1, -- Assurez-vous que l'organisateur avec ID 1 existe
    NOW(),
    NOW()
);

-- 2. Récupérer l'ID de l'événement créé
SET @test_event_id = (SELECT evenement_id FROM evenement WHERE titre = 'Test Billets PDF - Conférence Tech 2025' ORDER BY evenement_id DESC LIMIT 1);

-- 3. Créer des inscriptions de test avec différents types de billets
-- Inscription Standard
INSERT INTO inscription (participant_id, evenement_id, date_inscription, statut, type_billet, quantite, created_at, updated_at)
VALUES (1, @test_event_id, NOW(), 'ACCEPTEE', 'STANDARD', 1, NOW(), NOW());

-- Inscription VIP
INSERT INTO inscription (participant_id, evenement_id, date_inscription, statut, type_billet, quantite, created_at, updated_at)
VALUES (2, @test_event_id, NOW(), 'ACCEPTEE', 'VIP', 1, NOW(), NOW());

-- Inscription Premium
INSERT INTO inscription (participant_id, evenement_id, date_inscription, statut, type_billet, quantite, created_at, updated_at)
VALUES (3, @test_event_id, NOW(), 'ACCEPTEE', 'PREMIUM', 1, NOW(), NOW());

-- 4. Vérifier les données créées
SELECT 
    i.inscription_id,
    u.nom as participant,
    u.email,
    e.titre as evenement,
    i.type_billet,
    i.statut,
    i.date_inscription
FROM inscription i
JOIN participant p ON i.participant_id = p.id
JOIN utilisateur u ON p.id = u.id
JOIN evenement e ON i.evenement_id = e.evenement_id
WHERE e.titre = 'Test Billets PDF - Conférence Tech 2025'
ORDER BY i.type_billet;

-- 5. Instructions pour tester la fonctionnalité:

-- ÉTAPE 1: Compiler et déployer l'application
-- mvn clean package
-- mvn cargo:run

-- ÉTAPE 2: Tester l'inscription automatique
-- 1. Aller sur http://localhost:8081/jee-event-manager/catalogue
-- 2. S'inscrire à l'événement "Test Billets PDF - Conférence Tech 2025"
-- 3. Vérifier que le billet est généré automatiquement
-- 4. Vérifier l'email de confirmation avec le billet

-- ÉTAPE 3: Tester l'interface des billets
-- 1. Aller sur http://localhost:8081/jee-event-manager/mes-billets?participantId=1
-- 2. Vérifier l'affichage des billets
-- 3. Tester le téléchargement PDF
-- 4. Tester le marquage comme utilisé

-- ÉTAPE 4: Tester le téléchargement direct
-- 1. Utiliser l'URL: http://localhost:8081/jee-event-manager/billet/download?id=BILLET_ID
-- 2. Vérifier que le PDF se télécharge correctement

-- ÉTAPE 5: Vérifier les fichiers générés
-- 1. Aller dans le dossier uploads/billets/
-- 2. Vérifier que les fichiers PDF sont créés
-- 3. Ouvrir un PDF pour vérifier le contenu

-- ÉTAPE 6: Vérifier la base de données
-- Exécuter cette requête pour voir les billets générés:
SELECT 
    b.billet_id,
    b.numero_billet,
    b.type_billet,
    b.statut,
    b.chemin_fichier,
    b.date_generation,
    b.utilise,
    u.nom as participant,
    e.titre as evenement
FROM billet b
JOIN inscription i ON b.inscription_id = i.inscription_id
JOIN participant p ON i.participant_id = p.id
JOIN utilisateur u ON p.id = u.id
JOIN evenement e ON i.evenement_id = e.evenement_id
WHERE e.titre = 'Test Billets PDF - Conférence Tech 2025'
ORDER BY b.date_generation DESC;

-- ÉTAPE 7: Tests de validation
-- 1. Tester avec différents types de billets (STANDARD, VIP, PREMIUM)
-- 2. Tester le marquage comme utilisé
-- 3. Tester l'annulation de billet
-- 4. Vérifier les emails automatiques

-- ÉTAPE 8: Nettoyage après test (optionnel)
-- DELETE FROM billet WHERE inscription_id IN (
--     SELECT inscription_id FROM inscription WHERE evenement_id = @test_event_id
-- );
-- DELETE FROM inscription WHERE evenement_id = @test_event_id;
-- DELETE FROM evenement WHERE evenement_id = @test_event_id;

-- NOTES IMPORTANTES:
-- 1. Assurez-vous que les participants avec ID 1, 2, 3 existent
-- 2. Assurez-vous que l'organisateur avec ID 1 existe
-- 3. Vérifiez la configuration email dans EmailService
-- 4. Les fichiers PDF sont stockés dans uploads/billets/
-- 5. Les billets sont générés automatiquement lors de l'inscription
-- 6. Les emails sont envoyés automatiquement avec les informations du billet
