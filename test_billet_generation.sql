-- Script de test rapide pour vérifier la génération de billets
-- Exécutez ce script puis testez une inscription

-- 1. Créer un événement de test
INSERT INTO evenement (titre, description, date_debut, date_fin, lieu, capacite, statut, organisateur_id, created_at, updated_at)
VALUES (
    'Test Billets PDF - Génération Automatique',
    'Événement de test pour vérifier la génération automatique de billets PDF.',
    NOW() + INTERVAL '10 days',  -- Dans 10 jours
    NOW() + INTERVAL '12 days',  -- Durée de 2h
    'Salle de Test, Paris',
    50,
    'PUBLIE',
    1, -- Assurez-vous que l'organisateur avec ID 1 existe
    NOW(),
    NOW()
);

-- 2. Vérifier que l'événement a été créé
SELECT evenement_id, titre, statut, date_debut FROM evenement 
WHERE titre = 'Test Billets PDF - Génération Automatique';

-- 3. Instructions de test:
-- 1. Allez sur http://localhost:8081/jee-event-manager/catalogue
-- 2. Trouvez l'événement "Test Billets PDF - Génération Automatique"
-- 3. Cliquez sur "S'inscrire"
-- 4. Suivez le processus d'inscription
-- 5. Vérifiez les logs du serveur pour voir "Billet généré et envoyé"
-- 6. Vérifiez votre email pour recevoir le billet
-- 7. Vérifiez le dossier uploads/billets/ pour le fichier PDF

-- 4. Vérifier les billets générés après test:
SELECT 
    b.billet_id,
    b.numero_billet,
    b.type_billet,
    b.statut,
    b.chemin_fichier,
    b.date_generation,
    u.nom as participant,
    e.titre as evenement
FROM billet b
JOIN inscription i ON b.inscription_id = i.inscription_id
JOIN participant p ON i.participant_id = p.id
JOIN utilisateur u ON p.id = u.id
JOIN evenement e ON i.evenement_id = e.evenement_id
WHERE e.titre = 'Test Billets PDF - Génération Automatique'
ORDER BY b.date_generation DESC;

-- 5. Nettoyage après test (optionnel):
-- DELETE FROM billet WHERE inscription_id IN (
--     SELECT inscription_id FROM inscription WHERE evenement_id = (
--         SELECT evenement_id FROM evenement WHERE titre = 'Test Billets PDF - Génération Automatique'
--     )
-- );
-- DELETE FROM inscription WHERE evenement_id = (
--     SELECT evenement_id FROM evenement WHERE titre = 'Test Billets PDF - Génération Automatique'
-- );
-- DELETE FROM evenement WHERE titre = 'Test Billets PDF - Génération Automatique';
