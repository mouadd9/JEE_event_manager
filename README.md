# Event Management System (Système de Gestion d'Événements)

## Description
Système de gestion d'événements développé avec Java EE pour ENSA Tétouan.
Cette application web permet aux organisateurs de créer et gérer des événements, et aux participants de découvrir et s'inscrire à ces événements.

## Technologies Utilisées

### Backend
- Java 8+
- Java EE 8
- JPA 2.2 avec Hibernate 5.4+
- Apache Tomcat 9+

### Base de Données
- MySQL 8+ (via Laragon)

### Frontend
- JSP + JSTL
- Bootstrap 5
- jQuery
- Chart.js (pour les statistiques)
- Google Maps API (pour la géolocalisation)

### Build Tool
- Maven 3.6+

## Structure du Projet

```
event-management-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── ma/ensa/tetouan/eventmanagement/
│   │   │       ├── model/          # Entités JPA
│   │   │       ├── dao/            # Couche d'accès aux données
│   │   │       ├── service/        # Logique métier
│   │   │       ├── controller/     # Servlets
│   │   │       ├── filter/         # Filtres HTTP
│   │   │       ├── listener/       # Listeners d'application
│   │   │       └── util/           # Classes utilitaires
│   │   ├── resources/
│   │   │   ├── META-INF/
│   │   │   │   └── persistence.xml
│   │   │   ├── application.properties
│   │   │   └── logback.xml
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── web.xml
│   │       │   └── views/          # Pages JSP
│   │       ├── assets/
│   │       │   ├── css/
│   │       │   ├── js/
│   │       │   └── images/
│   │       └── index.jsp
│   └── test/
│       └── java/
├── pom.xml
└── README.md
```

## Prérequis

1. **JDK 8 ou supérieur**
2. **Apache Maven 3.6+**
3. **Apache Tomcat 9+**
4. **MySQL 8+** (via Laragon)
5. **IDE**: Eclipse, IntelliJ IDEA, ou NetBeans

## Installation et Configuration

### 1. Cloner le projet
```bash
git clone <repository-url>
cd event-management-system
```

### 2. Configurer la base de données
- Démarrer Laragon
- Créer une base de données nommée `event_management_db`
- Vérifier les paramètres de connexion dans `src/main/resources/META-INF/persistence.xml`

### 3. Compiler le projet
```bash
mvn clean install
```

### 4. Déployer sur Tomcat
```bash
mvn tomcat7:deploy
```
ou copier le fichier WAR généré dans le dossier `webapps` de Tomcat.

### 5. Accéder à l'application
```
http://localhost:8080/event-management
```

## Configuration

### Base de données (persistence.xml)
- URL: `jdbc:mysql://localhost:3306/event_management_db`
- Utilisateur: `root`
- Mot de passe: `` (vide par défaut pour Laragon)

### Google Maps API
- Ajouter votre clé API dans `src/main/resources/application.properties`

## Fonctionnalités

### Phase 1 - Fonctionnalités Essentielles
- [ ] Authentification et gestion des utilisateurs multi-rôles
- [ ] Gestion des événements (CRUD)
- [ ] Découverte et inscription aux événements
- [ ] Tableaux de bord par rôle

### Phase 2 - Fonctionnalités Avancées (Optionnel)
- [ ] Système de notifications
- [ ] Commentaires et évaluations
- [ ] Géolocalisation avec Google Maps
- [ ] Statistiques et rapports

## Design Patterns Utilisés

1. **Observer Pattern** - Système de notifications
2. **Factory Pattern** - Création d'événements
3. **Template Method Pattern** - Opérations CRUD
4. **Facade Pattern** - Modules complexes

## Auteurs

Projet académique - ENSA Tétouan

## Licence

Ce projet est développé dans un cadre académique.
