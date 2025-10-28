# Guide de test - Génération automatique de billets PDF

## Problème résolu ✅

Le problème était que l'application utilisait `InscriptionFacade` au lieu de `InscriptionService` pour les inscriptions via l'interface web. J'ai ajouté la génération automatique de billets dans `InscriptionFacade`.

## Test rapide

### 1. Préparer les données de test
Exécutez le script `test_billet_generation.sql` dans votre base de données.

### 2. Tester l'inscription
1. Allez sur : `http://localhost:8081/jee-event-manager/catalogue`
2. Trouvez l'événement "Test Billets PDF - Génération Automatique"
3. Cliquez sur "S'inscrire"
4. Suivez le processus d'inscription complet

### 3. Vérifier la génération
Après l'inscription, vous devriez voir dans les logs du serveur :
```
Billet généré et envoyé pour l'inscription: [ID]
```

### 4. Vérifier l'email
- Vérifiez votre boîte email pour recevoir l'email de confirmation avec le billet
- L'email contient le numéro de billet et les informations

### 5. Vérifier le fichier PDF
- Allez dans le dossier `uploads/billets/`
- Vous devriez voir un fichier `billet_EVT-XXXXXX-XXXXXXXX.pdf`

### 6. Tester le téléchargement
- Allez sur : `http://localhost:8081/jee-event-manager/mes-billets?participantId=[VOTRE_ID]`
- Cliquez sur "Télécharger PDF"

## URLs utiles

- **Catalogue** : `/catalogue`
- **Mes billets** : `/mes-billets?participantId=X`
- **Téléchargement** : `/billet/download?id=X`

## Vérification en base de données

Exécutez cette requête pour voir les billets générés :
```sql
SELECT 
    b.billet_id,
    b.numero_billet,
    b.type_billet,
    b.statut,
    b.chemin_fichier,
    u.nom as participant,
    e.titre as evenement
FROM billet b
JOIN inscription i ON b.inscription_id = i.inscription_id
JOIN participant p ON i.participant_id = p.id
JOIN utilisateur u ON p.id = u.id
JOIN evenement e ON i.evenement_id = e.evenement_id
ORDER BY b.date_generation DESC;
```

## Résultat attendu

✅ **Billet PDF généré automatiquement** lors de l'inscription  
✅ **Email envoyé** avec confirmation et informations du billet  
✅ **Fichier PDF sauvegardé** dans `uploads/billets/`  
✅ **Interface de téléchargement** fonctionnelle  
✅ **Numéro de billet unique** généré (format: EVT-XXXXXX-XXXXXXXX)  

La fonctionnalité est maintenant complètement opérationnelle !
