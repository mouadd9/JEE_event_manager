# Résumé des Classes JPA - Event Management System

## Vue d'ensemble

Ce document résume toutes les classes d'entités JPA créées pour le système de gestion d'événements.

---

## Énumérations (Enums)

### 1. StatutUtilisateur.java
**Localisation**: `ma.ensa.tetouan.eventmanagement.model.StatutUtilisateur`

**Valeurs**:
- `ACTIF` - Utilisateur actif, accès complet
- `INACTIF` - Compte non vérifié ou désactivé temporairement
- `SUSPENDU` - Accès bloqué par l'administrateur

---

### 2. StatutEvenement.java
**Localisation**: `ma.ensa.tetouan.eventmanagement.model.StatutEvenement`

**Valeurs**:
- `BROUILLON` - Événement en création, non visible
- `PUBLIE` - Événement publié et ouvert aux inscriptions
- `ANNULE` - Événement annulé
- `TERMINE` - Événement terminé

---

### 3. StatutInscription.java
**Localisation**: `ma.ensa.tetouan.eventmanagement.model.StatutInscription`

**Valeurs**:
- `EN_ATTENTE` - En attente de validation
- `ACCEPTEE` - Inscription acceptée
- `REFUSEE` - Inscription refusée
- `ANNULEE` - Inscription annulée par le participant

---

## Entités JPA

### 1. User.java (Classe Abstraite)
**Localisation**: `ma.ensa.tetouan.eventmanagement.model.User`

**Stratégie d'héritage**: `@Inheritance(strategy = InheritanceType.JOINED)`

**Attributs principaux**:
- `id` (Long) - Identifiant unique
- `nom` (String) - Nom complet
- `email` (String) - Email unique
- `motDePasse` (String) - Mot de passe hashé
- `statut` (StatutUtilisateur) - Statut du compte
- `dateInscription` (LocalDateTime) - Date d'inscription
- `derniereConnexion` (LocalDateTime) - Dernière connexion
- `photoProfil` (String) - URL de la photo

**Méthodes abstraites**:
- `getRole()` - Retourne le rôle de l'utilisateur

**Annotations JPA**:
- `@Entity`, `@Table(name = "utilisateurs")`
- `@DiscriminatorColumn(name = "type_utilisateur")`
- `@PrePersist` pour initialisation automatique

---

### 2. Organisateur.java
**Localisation**: `ma.ensa.tetouan.eventmanagement.model.Organisateur`

**Hérite de**: `User`

**Attributs spécifiques**:
- `organisation` (String) - Nom de l'organisation
- `telephone` (String) - Numéro de téléphone (format: +212XXXXXXXXX)
- `siteWeb` (String) - Site web
- `description` (String) - Description de l'organisateur
- `adresse` (String) - Adresse physique
- `nombreEvenementsOrganises` (Integer) - Compteur

**Relations**:
- `@OneToMany` avec `Evenement` (mappedBy = "organisateur")

**Méthodes utilitaires**:
- `addEvenement(Evenement)` - Ajoute un événement
- `removeEvenement(Evenement)` - Retire un événement
- `getRole()` - Retourne "ORGANISATEUR"

---

### 3. Participant.java
**Localisation**: `ma.ensa.tetouan.eventmanagement.model.Participant`

**Hérite de**: `User`

**Attributs spécifiques**:
- `preferences` (String) - Préférences
- `interets` (String) - Centres d'intérêt
- `ville` (String) - Ville de résidence
- `dateNaissance` (LocalDate) - Date de naissance
- `telephone` (String) - Téléphone
- `nombreInscriptions` (Integer) - Compteur

**Relations**:
- `@OneToMany` avec `Inscription` (mappedBy = "participant")
- `@OneToMany` avec `Commentaire` (mappedBy = "participant")
- `@OneToMany` avec `Evaluation` (mappedBy = "participant")

**Méthodes utilitaires**:
- `addInscription(Inscription)` - Ajoute une inscription
- `addCommentaire(Commentaire)` - Ajoute un commentaire
- `addEvaluation(Evaluation)` - Ajoute une évaluation
- `getRole()` - Retourne "PARTICIPANT"

---

### 4. Administrateur.java
**Localisation**: `ma.ensa.tetouan.eventmanagement.model.Administrateur`

**Hérite de**: `User`

**Attributs spécifiques**:
- `niveauAcces` (Integer) - Niveau d'accès (1-3)
- `departement` (String) - Département
- `fonction` (String) - Fonction/titre
- `dateNomination` (LocalDateTime) - Date de nomination
- `peutGererUtilisateurs` (Boolean) - Permission
- `peutGererEvenements` (Boolean) - Permission
- `peutVoirStatistiques` (Boolean) - Permission
- `peutModererContenu` (Boolean) - Permission

**Méthodes utilitaires**:
- `isSuperAdmin()` - Vérifie si niveau 3
- `hasPermission(String)` - Vérifie une permission spécifique
- `getRole()` - Retourne "ADMINISTRATEUR"

**Logique métier**:
- Niveau 1: Consultation uniquement
- Niveau 2: Modération + gestion événements
- Niveau 3: Tous droits (super admin)

---

### 5. Categorie.java
**Localisation**: `ma.ensa.tetouan.eventmanagement.model.Categorie`

**Attributs**:
- `id` (Long) - Identifiant unique
- `nom` (String) - Nom unique de la catégorie
- `description` (String) - Description
- `icone` (String) - Classe CSS de l'icône
- `couleur` (String) - Code couleur hex
- `active` (Boolean) - Catégorie active ou non

**Relations**:
- `@ManyToMany` avec `Evenement` (mappedBy = "categories")

**Annotations JPA**:
- `@Entity`, `@Table(name = "categories")`
- `@PrePersist` pour initialisation

---

### 6. Evenement.java
**Localisation**: `ma.ensa.tetouan.eventmanagement.model.Evenement`

**Attributs principaux**:
- `id` (Long) - Identifiant unique
- `titre` (String) - Titre de l'événement
- `description` (String) - Description détaillée
- `dateDebut` (LocalDateTime) - Date/heure de début
- `dateFin` (LocalDateTime) - Date/heure de fin
- `lieu` (String) - Nom du lieu
- `adresseComplete` (String) - Adresse complète
- `latitude`, `longitude` (Double) - Coordonnées GPS
- `capacite` (Integer) - Capacité maximale
- `placesDisponibles` (Integer) - Places restantes
- `statut` (StatutEvenement) - Statut de l'événement
- `imageUrl` (String) - URL de l'image
- `prix` (Double) - Prix d'entrée
- `gratuit` (Boolean) - Gratuit ou payant
- `nombreVues` (Integer) - Compteur de vues
- `nombreInscriptions` (Integer) - Nombre d'inscriptions
- `noteMoyenne` (Double) - Note moyenne (0-5)

**Relations**:
- `@ManyToOne` avec `Organisateur`
- `@ManyToMany` avec `Categorie`
- `@OneToMany` avec `Inscription`, `Commentaire`, `Evaluation`

**Named Queries**:
- `Evenement.findAll`
- `Evenement.findByStatut`
- `Evenement.findPublished`

**Méthodes utilitaires**:
- `addCategorie(Categorie)` - Ajoute une catégorie
- `incrementerVues()` - Incrémente les vues
- `isComplet()` - Vérifie si complet
- `isEnCours()` - Vérifie si en cours
- `isTermine()` - Vérifie si terminé
- `calculerNoteMoyenne()` - Calcule la note moyenne

**Lifecycle callbacks**:
- `@PrePersist` - Initialisation des valeurs par défaut
- `@PreUpdate` - Met à jour dateModification

---

### 7. Inscription.java
**Localisation**: `ma.ensa.tetouan.eventmanagement.model.Inscription`

**Attributs**:
- `id` (Long) - Identifiant unique
- `dateInscription` (LocalDateTime) - Date d'inscription
- `statut` (StatutInscription) - Statut de l'inscription
- `dateReponse` (LocalDateTime) - Date de réponse
- `messageParticipant` (String) - Message du participant
- `messageOrganisateur` (String) - Message de l'organisateur
- `nombrePlaces` (Integer) - Nombre de places réservées
- `presenceConfirmee` (Boolean) - Présence confirmée
- `dateAnnulation` (LocalDateTime) - Date d'annulation
- `motifAnnulation` (String) - Motif d'annulation

**Relations**:
- `@ManyToOne` avec `Participant` (nullable = false)
- `@ManyToOne` avec `Evenement` (nullable = false)

**Contrainte unique**: (participant_id, evenement_id)

**Named Queries**:
- `Inscription.findByParticipant`
- `Inscription.findByEvenement`
- `Inscription.findByStatut`

**Méthodes utilitaires**:
- `accepter(String message)` - Accepte l'inscription
- `refuser(String message)` - Refuse l'inscription
- `annuler(String motif)` - Annule l'inscription
- `isActive()` - Vérifie si active

---

### 8. Commentaire.java
**Localisation**: `ma.ensa.tetouan.eventmanagement.model.Commentaire`

**Attributs**:
- `id` (Long) - Identifiant unique
- `texte` (String) - Contenu du commentaire
- `dateCreation` (LocalDateTime) - Date de création
- `dateModification` (LocalDateTime) - Dernière modification
- `modere` (Boolean) - Commentaire modéré
- `signale` (Boolean) - Commentaire signalé
- `nombreSignalements` (Integer) - Nombre de signalements
- `visible` (Boolean) - Visible publiquement

**Relations**:
- `@ManyToOne` avec `Participant` (nullable = false)
- `@ManyToOne` avec `Evenement` (nullable = false)

**Named Queries**:
- `Commentaire.findByEvenement`
- `Commentaire.findByParticipant`
- `Commentaire.findRecent`

**Méthodes utilitaires**:
- `modifier(String nouveauTexte)` - Modifie le commentaire
- `signaler()` - Signale le commentaire (auto-masquage après 3 signalements)
- `moderer(boolean approuve)` - Modère le commentaire (admin)
- `estModifie()` - Vérifie si modifié

---

### 9. Evaluation.java
**Localisation**: `ma.ensa.tetouan.eventmanagement.model.Evaluation`

**Attributs**:
- `id` (Long) - Identifiant unique
- `note` (Integer) - Note de 1 à 5
- `texte` (String) - Avis textuel (optionnel)
- `horodatage` (LocalDateTime) - Date de création
- `dateModification` (LocalDateTime) - Dernière modification
- `visible` (Boolean) - Visible publiquement
- `verifie` (Boolean) - Vérifié par admin
- `utileCount` (Integer) - Nombre de "utile"

**Relations**:
- `@ManyToOne` avec `Participant` (nullable = false)
- `@ManyToOne` avec `Evenement` (nullable = false)

**Contrainte unique**: (participant_id, evenement_id)

**Named Queries**:
- `Evaluation.findByEvenement`
- `Evaluation.findByParticipant`
- `Evaluation.calculateAverage`

**Validation**:
- `@Min(1)`, `@Max(5)` sur note
- `@AssertTrue` pour validation personnalisée

**Méthodes utilitaires**:
- `modifier(Integer note, String texte)` - Modifie l'évaluation
- `verifier()` - Vérifie l'évaluation (admin)
- `marquerUtile()` - Incrémente le compteur
- `estModifiee()` - Vérifie si modifiée
- `getNoteEtoiles()` - Retourne représentation étoiles (★★★☆☆)

**Lifecycle callbacks**:
- `@PostLoad` - Met à jour la note moyenne de l'événement

---

## Annotations JPA Utilisées

### Annotations d'Entité
- `@Entity` - Marque la classe comme entité JPA
- `@Table(name = "...")` - Spécifie le nom de la table
- `@Inheritance` - Définit la stratégie d'héritage
- `@DiscriminatorColumn` - Colonne discriminante pour l'héritage
- `@DiscriminatorValue` - Valeur discriminante pour la sous-classe
- `@PrimaryKeyJoinColumn` - Clé primaire pour héritage JOINED

### Annotations d'Attribut
- `@Id` - Clé primaire
- `@GeneratedValue` - Génération automatique d'ID
- `@Column` - Configuration de colonne
- `@Enumerated(EnumType.STRING)` - Stockage enum comme string
- `@Temporal` - Type temporel (remplacé par LocalDateTime)

### Annotations de Relation
- `@OneToMany` - Relation un-à-plusieurs
- `@ManyToOne` - Relation plusieurs-à-un
- `@ManyToMany` - Relation plusieurs-à-plusieurs
- `@JoinColumn` - Configuration de la colonne de jointure
- `@JoinTable` - Table de liaison pour ManyToMany

### Annotations de Validation (Bean Validation)
- `@NotNull` - Non null
- `@NotBlank` - Non vide
- `@Size` - Taille min/max
- `@Min`, `@Max` - Valeur min/max
- `@Email` - Format email
- `@Pattern` - Expression régulière
- `@Future` - Date future
- `@AssertTrue` - Validation booléenne personnalisée

### Annotations de Cycle de Vie
- `@PrePersist` - Avant insertion
- `@PreUpdate` - Avant mise à jour
- `@PostLoad` - Après chargement

### Annotations de Requête
- `@NamedQuery` - Requête nommée JPQL
- `@NamedQueries` - Groupe de requêtes nommées

---

## Patterns de Conception Implémentés

### 1. Template Method Pattern
Implémenté dans la classe abstraite `User` avec la méthode abstraite `getRole()`.

### 2. Factory Pattern
Préparé pour la création d'utilisateurs via les constructeurs spécialisés.

### 3. Observer Pattern
Préparé via les lifecycle callbacks (@PrePersist, @PreUpdate, @PostLoad) pour les notifications futures.

---

## Bonnes Pratiques Appliquées

1. **Sérialisation**: Toutes les entités implémentent `Serializable`
2. **equals() et hashCode()**: Basés uniquement sur l'ID
3. **Bidirectional Relations**: Méthodes helper pour maintenir la cohérence
4. **Lazy Loading**: Utilisé par défaut pour optimiser les performances
5. **Cascade Operations**: Configuré de manière appropriée pour chaque relation
6. **Validation**: Bean Validation intégré
7. **Lifecycle Callbacks**: Pour initialisation et mises à jour automatiques
8. **Named Queries**: Pour requêtes réutilisables et optimisées

---

## Prochaines Étapes

1. **Créer la couche DAO** (Data Access Objects)
2. **Implémenter la couche Service** (logique métier)
3. **Développer les Servlets** (contrôleurs)
4. **Créer les pages JSP** (vues)
5. **Implémenter les filtres** et **listeners**
6. **Ajouter les tests unitaires**

---

**Version**: 1.0
**Date**: 2025
**Auteur**: ENSA Tétouan
