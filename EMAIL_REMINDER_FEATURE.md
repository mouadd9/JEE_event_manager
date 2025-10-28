# Fonctionnalité d'envoi automatique d'emails de rappel

## Description

Cette fonctionnalité permet d'envoyer automatiquement des emails de rappel aux participants inscrits à des événements, 24 heures avant le début de chaque événement.

## Fonctionnement

### Principe
- **Déclenchement** : Le système vérifie toutes les heures les événements qui commencent dans 24 heures (±1h de tolérance)
- **Critères** : Seuls les événements avec le statut `PUBLIE` sont considérés
- **Participants** : Seuls les participants avec des inscriptions au statut `ACCEPTEE` reçoivent des rappels
- **Timing** : Si un événement est prévu le 28 octobre 2025 à 18h00, le participant reçoit un rappel le 27 octobre 2025 à 18h00 (±1h)

### Composants implémentés

#### 1. EventReminderScheduler
- **Fichier** : `src/main/java/com/example/jee_event_manager/scheduler/EventReminderScheduler.java`
- **Type** : ServletContextListener avec Timer
- **Fonction** : 
  - S'exécute automatiquement au démarrage de l'application
  - Programme une tâche qui s'exécute toutes les heures
  - Recherche les événements nécessitant un rappel
  - Envoie les emails aux participants concernés

#### 2. EmailService (étendu)
- **Fichier** : `src/main/java/com/example/jee_event_manager/service/EmailService.java`
- **Nouvelle méthode** : `sendEventReminderEmail()`
- **Fonction** : Envoie des emails de rappel avec un template HTML professionnel

#### 3. Template email de rappel
- **Design** : Template HTML responsive avec CSS intégré
- **Contenu** : 
  - Informations complètes de l'événement (titre, date, heure, lieu, description)
  - Catégories de l'événement
  - Conseils pratiques pour les participants
  - Design cohérent avec l'identité visuelle de l'application

#### 4. Servlet de test
- **Fichier** : `src/main/java/com/example/jee_event_manager/servlet/TestReminderServlet.java`
- **URL** : `/test-reminders`
- **Fonction** : Permet de déclencher manuellement l'envoi des rappels pour les tests

## Configuration

### Base de données
Aucune modification de schéma nécessaire. La fonctionnalité utilise les tables existantes :
- `evenement` : Pour récupérer les événements
- `inscription` : Pour identifier les participants inscrits
- `participant` et `utilisateur` : Pour récupérer les informations des participants

### Email
La configuration email existante dans `EmailService` est utilisée :
- **SMTP** : Gmail (smtp.gmail.com:587)
- **Authentification** : App Password Gmail
- **Format** : HTML avec CSS intégré

## Installation et utilisation

### 1. Déploiement
La fonctionnalité est automatiquement activée au démarrage de l'application grâce à l'annotation `@WebListener`.

### 2. Test manuel
Pour tester la fonctionnalité :
1. Accédez à : `http://localhost:8081/jee-event-manager/test-reminders`
2. Consultez les logs du serveur pour voir l'exécution
3. Vérifiez les emails reçus

### 3. Test avec données
Utilisez le script `test_email_reminders.sql` pour créer des événements de test :
1. Exécutez le script dans votre base de données
2. Attendez ou utilisez le servlet de test
3. Vérifiez les emails reçus par les participants de test

## Logs et monitoring

### Logs générés
- **Démarrage** : Confirmation de l'initialisation du scheduler
- **Exécution** : Nombre d'événements trouvés nécessitant un rappel
- **Envoi** : Détail des emails envoyés avec succès ou en échec
- **Résumé** : Statistiques par événement (nombre de rappels envoyés/échoués)

### Niveaux de log
- `INFO` : Opérations normales
- `WARNING` : Échecs d'envoi d'emails individuels
- `SEVERE` : Erreurs critiques du système

## Personnalisation

### Modification de la fréquence
Dans `EventReminderScheduler.java`, ligne 49 :
```java
timer.scheduleAtFixedRate(new ReminderTask(), 60000, 3600000); // 1 min initial, puis toutes les heures
```
- Premier paramètre : délai initial (60000ms = 1 minute)
- Deuxième paramètre : intervalle (3600000ms = 1 heure)

### Modification de la fenêtre de rappel
Dans `findEventsStartingIn24Hours()`, lignes 130-131 :
```java
LocalDateTime startWindow = reminderTime.minusHours(1);
LocalDateTime endWindow = reminderTime.plusHours(1);
```

### Modification du template email
Dans `EmailService.java`, méthode `buildEventReminderEmailBody()`.

## Sécurité et performance

### Sécurité
- Utilisation de l'EntityManager avec gestion des transactions
- Validation des statuts avant envoi
- Gestion des erreurs sans interruption du service

### Performance
- Requêtes optimisées avec index sur les dates
- Fenêtre de tolérance pour éviter les rappels multiples
- Gestion des erreurs individuelles sans impact global

## Dépannage

### Problèmes courants
1. **Pas d'emails envoyés** : Vérifiez la configuration SMTP et les logs
2. **Scheduler ne démarre pas** : Vérifiez les logs de démarrage de l'application
3. **Erreurs de base de données** : Vérifiez la configuration de persistence.xml

### Vérifications
- Logs de démarrage du scheduler
- Existence d'événements avec le bon statut et les bonnes dates
- Existence d'inscriptions avec le statut ACCEPTEE
- Configuration email fonctionnelle

## Évolutions possibles

### Fonctionnalités additionnelles
- Rappels multiples (7 jours, 1 heure avant)
- Personnalisation des templates par type d'événement
- Statistiques d'envoi et de réception
- Interface d'administration pour gérer les rappels
- Support des fuseaux horaires
- Rappels par SMS ou notifications push
