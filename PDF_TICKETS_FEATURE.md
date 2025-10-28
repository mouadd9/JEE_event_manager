# Fonctionnalité de génération automatique de billets PDF

## Description

Cette fonctionnalité permet de générer automatiquement des billets PDF personnalisés lorsqu'un participant s'inscrit à un événement. Les billets contiennent toutes les informations nécessaires et sont envoyés par email au participant.

## Fonctionnalités implémentées

### ✅ Génération automatique de billets PDF
- **Déclenchement** : Automatique lors de l'inscription d'un participant
- **Format** : PDF professionnel avec design moderne
- **Contenu** : Logo EventHub, informations complètes de l'événement, détails du participant, numéro unique

### ✅ Types de billets supportés
- **STANDARD** : Billet standard (bleu)
- **VIP** : Billet VIP (orange/doré)
- **PREMIUM** : Billet Premium (violet)

### ✅ Envoi automatique par email
- Email de confirmation avec informations du billet
- Template HTML professionnel
- Envoi automatique après génération

### ✅ Interface de gestion des billets
- Page "Mes Billets" pour chaque participant
- Affichage de tous les billets avec statut
- Téléchargement direct des PDF
- Marquage des billets comme utilisés

### ✅ Système de statuts
- **VALIDE** : Billet utilisable
- **UTILISE** : Billet déjà utilisé
- **ANNULE** : Billet annulé
- **EXPIRE** : Billet expiré

## Architecture technique

### Modèles de données

#### Billet
```java
@Entity
public class Billet {
    private Long id;
    private String numeroBillet;        // Numéro unique généré automatiquement
    private String typeBillet;         // STANDARD, VIP, PREMIUM
    private StatutBillet statut;       // Statut du billet
    private String cheminFichier;      // Chemin vers le PDF
    private LocalDateTime dateGeneration;
    private LocalDateTime dateUtilisation;
    private Boolean utilise;
    private Inscription inscription;    // Relation avec l'inscription
}
```

#### StatutBillet (Enum)
- `VALIDE` : Billet valide et utilisable
- `UTILISE` : Billet déjà utilisé
- `ANNULE` : Billet annulé
- `EXPIRE` : Billet expiré

### Services implémentés

#### BilletPdfService
- **Génération PDF** : Création de PDF avec iText
- **Design** : Template professionnel avec logo et couleurs
- **Stockage** : Sauvegarde dans `uploads/billets/`
- **Numérotation** : Génération de numéros uniques (format: EVT-XXXXXX-XXXXXXXX)

#### BilletService
- **Logique métier** : Gestion des billets
- **Intégration email** : Envoi automatique après génération
- **Validation** : Vérification des statuts et permissions
- **CRUD** : Opérations complètes sur les billets

### Servlets créés

#### BilletDownloadServlet
- **URL** : `/billet/download`
- **Fonction** : Téléchargement des PDF
- **Paramètres** : `id` (ID du billet) ou `numero` (numéro du billet)
- **Sécurité** : Vérification de validité du billet

#### MesBilletsServlet
- **URL** : `/mes-billets`
- **Fonction** : Affichage des billets d'un participant
- **Interface** : Design responsive avec grille de cartes
- **Actions** : Téléchargement et marquage comme utilisé

#### MarquerBilletUtiliseServlet
- **URL** : `/billet/marquer-utilise`
- **Fonction** : Marquage d'un billet comme utilisé
- **Validation** : Vérification des permissions

## Intégration dans le processus d'inscription

### Modification du InscriptionService
```java
// Dans la méthode inscrireParticipant()
Inscription saved = inscriptionRepository.save(inscription);

// Génération automatique du billet
try {
    billetService.genererEtEnvoyerBillet(saved);
    System.out.println("Billet généré et envoyé pour l'inscription: " + saved.getId());
} catch (Exception e) {
    System.err.println("Erreur lors de la génération du billet: " + e.getMessage());
    // Ne pas faire échouer l'inscription si le billet ne peut pas être généré
}
```

## Contenu du billet PDF

### Informations incluses
1. **Logo EventHub** (ou texte si logo non disponible)
2. **Titre** : "Billet d'entrée"
3. **Informations événement** :
   - Nom de l'événement
   - Date et heure
   - Lieu
   - Type de billet (avec couleur)
4. **Informations participant** :
   - Nom du participant
   - Email
5. **Numéro de billet** : Format unique (EVT-XXXXXX-XXXXXXXX)
6. **Code QR** : Simulation avec le numéro de billet
7. **Mentions légales** : "Billet gratuit — non transférable"
8. **Date de génération**

### Design du PDF
- **Police** : Arial standard
- **Couleurs** : 
  - STANDARD : Bleu (#1976d2)
  - VIP : Orange/Doré (#f57c00)
  - PREMIUM : Violet (#7b1fa2)
- **Layout** : Tableaux structurés avec en-têtes
- **Format** : A4 avec marges appropriées

## Configuration et installation

### Dépendances ajoutées
```xml
<!-- iText PDF Generation -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext-core</artifactId>
    <version>8.0.2</version>
    <type>pom</type>
</dependency>
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>kernel</artifactId>
    <version>8.0.2</version>
</dependency>
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>io</artifactId>
    <version>8.0.2</version>
</dependency>
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>layout</artifactId>
    <version>8.0.2</version>
</dependency>
```

### Répertoires créés
- `uploads/billets/` : Stockage des PDF générés
- `src/main/webapp/images/` : Pour le logo EventHub

### Configuration de base de données
- Table `billet` créée automatiquement par Hibernate
- Relation avec la table `inscription`
- Index sur `numero_billet` pour les recherches rapides

## Utilisation

### Pour les participants
1. **Inscription** : Le billet est généré automatiquement
2. **Email** : Réception de l'email de confirmation avec billet
3. **Téléchargement** : Accès via "Mes Billets" ou lien direct
4. **Utilisation** : Présentation du PDF ou numéro de billet

### Pour les organisateurs
1. **Gestion** : Visualisation des billets générés par événement
2. **Validation** : Marquage des billets comme utilisés
3. **Statistiques** : Suivi des téléchargements et utilisations

### URLs disponibles
- `/mes-billets?participantId=X` : Affichage des billets
- `/billet/download?id=X` : Téléchargement PDF
- `/billet/download?numero=EVT-XXXXXX-XXXXXXXX` : Téléchargement par numéro
- `/billet/marquer-utilise?id=X` : Marquage comme utilisé

## Tests et validation

### Script de test
Le fichier `test_pdf_tickets.sql` contient :
- Création d'événements de test
- Inscriptions avec différents types de billets
- Requêtes de vérification
- Instructions complètes de test

### Points de test
1. **Génération automatique** : Vérifier lors de l'inscription
2. **Contenu PDF** : Vérifier toutes les informations
3. **Envoi email** : Vérifier la réception
4. **Interface web** : Tester l'affichage et téléchargement
5. **Gestion statuts** : Tester le marquage comme utilisé
6. **Sécurité** : Vérifier les permissions d'accès

## Sécurité et performance

### Sécurité
- **Validation** : Vérification des statuts avant téléchargement
- **Permissions** : Accès limité aux billets valides
- **Fichiers** : Stockage sécurisé dans le répertoire uploads
- **Numérotation** : Numéros uniques pour éviter les collisions

### Performance
- **Génération** : PDF créés à la demande
- **Stockage** : Fichiers persistés pour éviter la régénération
- **Cache** : Headers HTTP appropriés pour le cache navigateur
- **Base de données** : Index sur les champs de recherche

## Évolutions possibles

### Fonctionnalités additionnelles
- **QR Code réel** : Intégration d'une bibliothèque QR Code
- **Logo personnalisé** : Upload de logo par organisateur
- **Templates multiples** : Différents designs selon le type d'événement
- **Pièces jointes email** : Envoi du PDF en pièce jointe
- **API REST** : Endpoints pour applications mobiles
- **Statistiques** : Dashboard de suivi des billets
- **Validation en temps réel** : Scanner QR Code à l'entrée
- **Billets groupés** : Support des inscriptions multiples
- **Expiration automatique** : Marquage automatique des billets expirés

### Améliorations techniques
- **Compression PDF** : Optimisation de la taille des fichiers
- **Cache Redis** : Mise en cache des PDF générés
- **CDN** : Distribution des fichiers PDF
- **Monitoring** : Logs détaillés de génération et téléchargement
- **Backup** : Sauvegarde automatique des billets

## Dépannage

### Problèmes courants
1. **PDF non généré** : Vérifier les logs et la configuration iText
2. **Email non envoyé** : Vérifier la configuration SMTP
3. **Fichier non trouvé** : Vérifier les permissions du répertoire uploads
4. **Erreur de compilation** : Vérifier les dépendances iText

### Logs à surveiller
- Génération de billets : `BilletPdfService`
- Envoi d'emails : `EmailService`
- Téléchargements : `BilletDownloadServlet`
- Erreurs de base : `BilletService`

Cette fonctionnalité est maintenant complètement intégrée et prête à être utilisée !
