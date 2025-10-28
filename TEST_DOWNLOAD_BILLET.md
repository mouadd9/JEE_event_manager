# Guide de test - Téléchargement des billets PDF

## ✅ Problèmes résolus

1. **Injection de dépendance** : Ajout de vérification dans `BilletDownloadServlet`
2. **Repository** : Migration de `@PersistenceContext` vers `@Inject` + `@ApplicationScoped` dans `BilletRepository`

## Test de la fonctionnalité

### 1. Vérifier que les billets sont bien générés

Connectez-vous à la base de données et exécutez :

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

### 2. Tester l'URL de téléchargement

Si vous avez un billet avec l'ID 1, testez :
```
http://localhost:8081/jee-event-manager/billet/download?id=1
```

Ou par numéro de billet :
```
http://localhost:8081/jee-event-manager/billet/download?numero=EVT-953842-A2D899BF
```

### 3. Vérifier les billets générés

Après une inscription, vous devriez voir dans les logs :
```
Billet généré avec succès: EVT-XXXXXX-XXXXXXXX
PDF sauvegardé: uploads/billets/billet_EVT-XXXXXX-XXXXXXXX.pdf
```

### 4. Vérifier que le fichier PDF existe

Dans votre dossier du projet, vérifiez :
```
uploads/billets/billet_EVT-XXXXXX-XXXXXXXX.pdf
```

### 5. Tester via l'interface "Mes Billets"

Testez l'URL complète :
```
http://localhost:8081/jee-event-manager/mes-billets?participantId=VOTRE_ID
```

Remplacer `VOTRE_ID` par l'ID de votre participant (par exemple 3).

### 6. Vérifier les logs d'erreur

Si ça ne fonctionne pas, vérifiez les logs du serveur pour des erreurs comme :
- `Cannot invoke "jakarta.persistence.EntityManager..."`
- `NullPointerException`
- `Billet ou fichier PDF non trouvé`

### 7. Problèmes courants et solutions

#### Problème : "Billet ou fichier PDF non trouvé"
**Solution** : Vérifiez que le champ `chemin_fichier` dans la base de données pointe vers un fichier existant.

#### Problème : "EntityManager is null"
**Solution** : C'est corrigé ! Nous avons changé de `@PersistenceContext` vers `@Inject`.

#### Problème : "Service n'a pas pu être injecté"
**Solution** : Redémarrez le serveur et vérifiez la configuration CDI.

### 8. Test complet du flux

1. **Inscription** : Inscrivez-vous à un événement
2. **Vérification email** : Recevez l'email de confirmation avec le numéro de billet
3. **Vérification PDF** : Allez dans `uploads/billets/` et vérifiez que le PDF existe
4. **Téléchargement** : Testez l'URL de téléchargement
5. **Interface** : Allez sur `/mes-billets?participantId=X` et cliquez sur "Télécharger PDF"

### 9. Checklist de vérification

- [ ] Les billets sont créés en base de données
- [ ] Les fichiers PDF sont générés dans `uploads/billets/`
- [ ] L'email de confirmation contient le numéro de billet
- [ ] L'URL `/billet/download?id=X` fonctionne
- [ ] L'URL `/mes-billets?participantId=X` affiche les billets
- [ ] Le bouton "Télécharger PDF" fonctionne

### 10. Commandes SQL utiles

```sql
-- Trouver tous les billets d'un participant
SELECT * FROM billet b
JOIN inscription i ON b.inscription_id = i.inscription_id
WHERE i.participant_id = 3;

-- Vérifier si les chemins de fichiers sont corrects
SELECT billet_id, numero_billet, chemin_fichier 
FROM billet 
ORDER BY date_generation DESC;

-- Compter les billets par statut
SELECT statut, COUNT(*) as nombre 
FROM billet 
GROUP BY statut;
```

## Résultat attendu

✅ PDF téléchargeable depuis l'espace participant  
✅ Fichier sauvegardé sur le serveur  
✅ URL de téléchargement fonctionnelle  
✅ Interface "Mes Billets" opérationnelle  

Si ça ne fonctionne toujours pas, vérifiez les logs du serveur et partagez l'erreur exacte !
