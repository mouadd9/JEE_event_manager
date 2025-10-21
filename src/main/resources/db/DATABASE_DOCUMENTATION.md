# Documentation de la Base de Données - Event Management System

## Vue d'ensemble

Cette base de données utilise MySQL 8+ et implémente un schéma complet pour un système de gestion d'événements avec support multi-utilisateurs.

## Architecture de la Base de Données

### Stratégie d'héritage
Le système utilise la stratégie **JOINED** pour l'héritage des utilisateurs:
- Table parent: `utilisateurs`
- Tables enfants: `organisateurs`, `participants`, `administrateurs`

## Tables Principales

### 1. utilisateurs (Table Parent)
**Description**: Table de base pour tous les types d'utilisateurs

| Colonne | Type | Description |
|---------|------|-------------|
| id | BIGINT (PK) | Identifiant unique auto-incrémenté |
| type_utilisateur | VARCHAR(20) | Discriminateur (ORGANISATEUR, PARTICIPANT, ADMINISTRATEUR) |
| nom | VARCHAR(100) | Nom complet de l'utilisateur |
| email | VARCHAR(150) | Email unique |
| mot_de_passe | VARCHAR(255) | Mot de passe hashé |
| statut | VARCHAR(20) | ACTIF, INACTIF, SUSPENDU |
| date_inscription | DATETIME | Date d'inscription |
| derniere_connexion | DATETIME | Dernière connexion |
| photo_profil | VARCHAR(255) | URL de la photo de profil |

**Contraintes**:
- `email` doit être unique
- Index sur `email`, `statut`, `type_utilisateur`

---

### 2. organisateurs
**Description**: Informations spécifiques aux organisateurs d'événements

| Colonne | Type | Description |
|---------|------|-------------|
| utilisateur_id | BIGINT (PK, FK) | Référence à utilisateurs.id |
| organisation | VARCHAR(150) | Nom de l'organisation |
| telephone | VARCHAR(20) | Numéro de téléphone |
| site_web | VARCHAR(255) | URL du site web |
| description | TEXT | Description de l'organisateur |
| adresse | VARCHAR(255) | Adresse physique |
| nombre_evenements_organises | INT | Compteur d'événements |

---

### 3. participants
**Description**: Informations spécifiques aux participants

| Colonne | Type | Description |
|---------|------|-------------|
| utilisateur_id | BIGINT (PK, FK) | Référence à utilisateurs.id |
| preferences | VARCHAR(500) | Préférences de l'utilisateur |
| interets | VARCHAR(500) | Centres d'intérêt |
| ville | VARCHAR(100) | Ville de résidence |
| date_naissance | DATE | Date de naissance |
| telephone | VARCHAR(20) | Numéro de téléphone |
| nombre_inscriptions | INT | Compteur d'inscriptions |

---

### 4. administrateurs
**Description**: Informations spécifiques aux administrateurs

| Colonne | Type | Description |
|---------|------|-------------|
| utilisateur_id | BIGINT (PK, FK) | Référence à utilisateurs.id |
| niveau_acces | INT | Niveau d'accès (1-3) |
| departement | VARCHAR(100) | Département |
| fonction | VARCHAR(100) | Fonction/titre |
| date_nomination | DATETIME | Date de nomination |
| peut_gerer_utilisateurs | BOOLEAN | Permission de gestion utilisateurs |
| peut_gerer_evenements | BOOLEAN | Permission de gestion événements |
| peut_voir_statistiques | BOOLEAN | Permission de visualisation statistiques |
| peut_moderer_contenu | BOOLEAN | Permission de modération |

**Niveaux d'accès**:
- **Niveau 1**: Consultation uniquement
- **Niveau 2**: Modération et gestion d'événements
- **Niveau 3**: Super administrateur (tous droits)

---

### 5. categories
**Description**: Catégories d'événements

| Colonne | Type | Description |
|---------|------|-------------|
| id | BIGINT (PK) | Identifiant unique |
| nom | VARCHAR(100) | Nom unique de la catégorie |
| description | VARCHAR(500) | Description |
| icone | VARCHAR(100) | Classe CSS de l'icône |
| couleur | VARCHAR(20) | Code couleur (hex) |
| active | BOOLEAN | Catégorie active ou non |

**Catégories par défaut**:
- Technologie, Sport, Culture, Éducation, Business, Musique, Gastronomie, Santé & Bien-être

---

### 6. evenements
**Description**: Événements organisés

| Colonne | Type | Description |
|---------|------|-------------|
| id | BIGINT (PK) | Identifiant unique |
| titre | VARCHAR(200) | Titre de l'événement |
| description | TEXT | Description détaillée |
| date_debut | DATETIME | Date et heure de début |
| date_fin | DATETIME | Date et heure de fin |
| lieu | VARCHAR(255) | Nom du lieu |
| adresse_complete | VARCHAR(500) | Adresse complète |
| latitude | DOUBLE | Coordonnée GPS |
| longitude | DOUBLE | Coordonnée GPS |
| capacite | INT | Capacité maximale |
| places_disponibles | INT | Places restantes |
| statut | VARCHAR(20) | BROUILLON, PUBLIE, ANNULE, TERMINE |
| image_url | VARCHAR(500) | URL de l'image |
| prix | DOUBLE | Prix d'entrée |
| gratuit | BOOLEAN | Événement gratuit ou non |
| date_creation | DATETIME | Date de création |
| date_modification | DATETIME | Dernière modification |
| nombre_vues | INT | Compteur de vues |
| nombre_inscriptions | INT | Nombre d'inscriptions |
| note_moyenne | DOUBLE | Note moyenne (0-5) |
| organisateur_id | BIGINT (FK) | Référence à organisateurs |

**Contraintes**:
- `date_fin` > `date_debut`
- `capacite` > 0
- Index sur `statut`, `date_debut`, `organisateur_id`, `lieu`

---

### 7. evenement_categorie (Table de liaison)
**Description**: Relation Many-to-Many entre événements et catégories

| Colonne | Type | Description |
|---------|------|-------------|
| evenement_id | BIGINT (PK, FK) | Référence à evenements.id |
| categorie_id | BIGINT (PK, FK) | Référence à categories.id |

---

### 8. inscriptions
**Description**: Inscriptions des participants aux événements

| Colonne | Type | Description |
|---------|------|-------------|
| id | BIGINT (PK) | Identifiant unique |
| date_inscription | DATETIME | Date d'inscription |
| statut | VARCHAR(20) | EN_ATTENTE, ACCEPTEE, REFUSEE, ANNULEE |
| date_reponse | DATETIME | Date de réponse de l'organisateur |
| message_participant | TEXT | Message du participant |
| message_organisateur | TEXT | Message de l'organisateur |
| nombre_places | INT | Nombre de places réservées |
| presence_confirmee | BOOLEAN | Présence confirmée |
| date_annulation | DATETIME | Date d'annulation |
| motif_annulation | VARCHAR(500) | Raison de l'annulation |
| participant_id | BIGINT (FK) | Référence à participants |
| evenement_id | BIGINT (FK) | Référence à evenements |

**Contraintes**:
- Combinaison unique: (participant_id, evenement_id)
- Index sur `statut`, `date_inscription`

---

### 9. commentaires
**Description**: Commentaires sur les événements

| Colonne | Type | Description |
|---------|------|-------------|
| id | BIGINT (PK) | Identifiant unique |
| texte | TEXT | Contenu du commentaire |
| date_creation | DATETIME | Date de création |
| date_modification | DATETIME | Dernière modification |
| modere | BOOLEAN | Commentaire modéré ou non |
| signale | BOOLEAN | Commentaire signalé |
| nombre_signalements | INT | Nombre de signalements |
| visible | BOOLEAN | Visible publiquement |
| participant_id | BIGINT (FK) | Auteur du commentaire |
| evenement_id | BIGINT (FK) | Événement concerné |

---

### 10. evaluations
**Description**: Évaluations (notes et avis) des événements

| Colonne | Type | Description |
|---------|------|-------------|
| id | BIGINT (PK) | Identifiant unique |
| note | INT | Note de 1 à 5 |
| texte | TEXT | Avis textuel (optionnel) |
| horodatage | DATETIME | Date de création |
| date_modification | DATETIME | Dernière modification |
| visible | BOOLEAN | Visible publiquement |
| verifie | BOOLEAN | Vérifié par admin |
| utile_count | INT | Nombre de "utile" |
| participant_id | BIGINT (FK) | Auteur de l'évaluation |
| evenement_id | BIGINT (FK) | Événement évalué |

**Contraintes**:
- Combinaison unique: (participant_id, evenement_id)
- `note` entre 1 et 5

---

## Vues SQL

### v_evenements_publies
Vue complète des événements publiés avec informations de l'organisateur et catégories.

### v_statistiques_globales
Vue agrégée des statistiques principales du système.

---

## Procédures Stockées

### accepter_inscription(inscription_id, message)
- Accepte une inscription
- Met à jour les places disponibles
- Enregistre le message de l'organisateur

---

## Triggers

### before_evenement_insert
- Initialise `places_disponibles` = `capacite`
- Définit `gratuit` = FALSE par défaut

### before_evenement_update
- Met à jour automatiquement `date_modification`

---

## Données de Test

Le script inclut:
- **1 Administrateur**: admin@eventmanagement.ma (mot de passe: admin123)
- **1 Organisateur**: organisateur@ensa.ma (mot de passe: organizer123)
- **1 Participant**: participant@test.ma (mot de passe: participant123)
- **8 Catégories** prédéfinies

---

## Instructions d'utilisation

### 1. Créer la base de données
```sql
CREATE DATABASE event_management_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE event_management_db;
```

### 2. Exécuter le script
```bash
mysql -u root -p event_management_db < src/main/resources/db/schema.sql
```

### 3. Vérifier l'installation
```sql
SHOW TABLES;
SELECT * FROM v_statistiques_globales;
```

---

## Notes de Sécurité

⚠️ **IMPORTANT**:
- Les mots de passe dans les données de test sont en clair
- En production, tous les mots de passe doivent être hashés (BCrypt recommandé)
- Utilisez des variables d'environnement pour les credentials de connexion
- Activez SSL pour les connexions MySQL en production

---

## Diagramme ER (Entités-Relations)

```
utilisateurs (parent)
├── organisateurs → evenements → evenement_categorie → categories
│                       ↓
│                   inscriptions ← participants
│                       ↓
│                   commentaires ← participants
│                       ↓
│                   evaluations ← participants
└── administrateurs
```

---

## Maintenance

### Backup régulier
```bash
mysqldump -u root -p event_management_db > backup_$(date +%Y%m%d).sql
```

### Statistiques des tables
```sql
SELECT table_name, table_rows
FROM information_schema.tables
WHERE table_schema = 'event_management_db';
```

---

**Version**: 1.0
**Date**: 2025
**Auteur**: ENSA Tétouan
