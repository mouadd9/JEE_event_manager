-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Oct 21, 2025 at 06:45 AM
-- Server version: 8.4.3
-- PHP Version: 8.3.26

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `ev_man`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `accepter_inscription` (IN `p_inscription_id` BIGINT, IN `p_message` TEXT)   BEGIN
    DECLARE v_evenement_id BIGINT;
    DECLARE v_nombre_places INT;
    DECLARE v_places_disponibles INT;

    -- Récupérer les informations de l'inscription
    SELECT evenement_id, nombre_places INTO v_evenement_id, v_nombre_places
    FROM inscriptions WHERE id = p_inscription_id;

    -- Vérifier les places disponibles
    SELECT places_disponibles INTO v_places_disponibles
    FROM evenements WHERE id = v_evenement_id;

    IF v_places_disponibles >= v_nombre_places THEN
        -- Accepter l'inscription
        UPDATE inscriptions
        SET statut = 'ACCEPTEE',
            date_reponse = NOW(),
            message_organisateur = p_message
        WHERE id = p_inscription_id;

        -- Mettre à jour les places disponibles
        UPDATE evenements
        SET places_disponibles = places_disponibles - v_nombre_places,
            nombre_inscriptions = nombre_inscriptions + 1
        WHERE id = v_evenement_id;

        SELECT 'Inscription acceptée avec succès' AS message;
    ELSE
        SELECT 'Places insuffisantes' AS message;
    END IF;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `administrateurs`
--

CREATE TABLE `administrateurs` (
  `utilisateur_id` bigint NOT NULL,
  `niveau_acces` int NOT NULL DEFAULT '1',
  `departement` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fonction` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `date_nomination` datetime DEFAULT CURRENT_TIMESTAMP,
  `peut_gerer_utilisateurs` tinyint(1) DEFAULT '0',
  `peut_gerer_evenements` tinyint(1) DEFAULT '0',
  `peut_voir_statistiques` tinyint(1) DEFAULT '1',
  `peut_moderer_contenu` tinyint(1) DEFAULT '0'
) ;

--
-- Dumping data for table `administrateurs`
--

INSERT INTO `administrateurs` (`utilisateur_id`, `niveau_acces`, `departement`, `fonction`, `date_nomination`, `peut_gerer_utilisateurs`, `peut_gerer_evenements`, `peut_voir_statistiques`, `peut_moderer_contenu`) VALUES
(9, 3, NULL, 'Super Admin', '2025-10-21 00:56:55', 1, 1, 1, 1);

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `id` bigint NOT NULL,
  `nom` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `icone` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `couleur` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `active` tinyint(1) DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`id`, `nom`, `description`, `icone`, `couleur`, `active`) VALUES
(1, 'Technologie', 'Événements liés à la technologie et l\'informatique', 'fa-laptop-code', '#3498db', 1),
(2, 'Sport', 'Événements sportifs et activités physiques', 'fa-running', '#e74c3c', 1),
(3, 'Culture', 'Événements culturels, arts et spectacles', 'fa-palette', '#9b59b6', 1),
(4, 'Éducation', 'Conférences, formations et ateliers éducatifs', 'fa-graduation-cap', '#2ecc71', 1),
(5, 'Business', 'Événements professionnels et networking', 'fa-briefcase', '#34495e', 1),
(6, 'Musique', 'Concerts et événements musicaux', 'fa-music', '#e67e22', 1),
(7, 'Gastronomie', 'Événements culinaires et gastronomiques', 'fa-utensils', '#f39c12', 1),
(8, 'Santé & Bien-être', 'Événements de santé, fitness et bien-être', 'fa-heartbeat', '#1abc9c', 1);

-- --------------------------------------------------------

--
-- Table structure for table `commentaires`
--

CREATE TABLE `commentaires` (
  `id` bigint NOT NULL,
  `texte` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `date_creation` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `date_modification` datetime DEFAULT NULL,
  `modere` tinyint(1) DEFAULT '0',
  `signale` tinyint(1) DEFAULT '0',
  `nombre_signalements` int DEFAULT '0',
  `visible` tinyint(1) DEFAULT '1',
  `participant_id` bigint NOT NULL,
  `evenement_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `evaluations`
--

CREATE TABLE `evaluations` (
  `id` bigint NOT NULL,
  `note` int NOT NULL,
  `texte` text COLLATE utf8mb4_unicode_ci,
  `horodatage` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `date_modification` datetime DEFAULT NULL,
  `visible` tinyint(1) DEFAULT '1',
  `verifie` tinyint(1) DEFAULT '0',
  `utile_count` int DEFAULT '0',
  `participant_id` bigint NOT NULL,
  `evenement_id` bigint NOT NULL
) ;

-- --------------------------------------------------------

--
-- Table structure for table `evenements`
--

CREATE TABLE `evenements` (
  `id` bigint NOT NULL,
  `titre` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `date_debut` datetime NOT NULL,
  `date_fin` datetime NOT NULL,
  `lieu` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `adresse_complete` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `capacite` int NOT NULL,
  `places_disponibles` int DEFAULT NULL,
  `statut` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'BROUILLON',
  `image_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `prix` double DEFAULT NULL,
  `gratuit` tinyint(1) DEFAULT '0',
  `date_creation` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `date_modification` datetime DEFAULT NULL,
  `nombre_vues` int DEFAULT '0',
  `nombre_inscriptions` int DEFAULT '0',
  `note_moyenne` double DEFAULT '0',
  `organisateur_id` bigint NOT NULL
) ;

--
-- Dumping data for table `evenements`
--

INSERT INTO `evenements` (`id`, `titre`, `description`, `date_debut`, `date_fin`, `lieu`, `adresse_complete`, `latitude`, `longitude`, `capacite`, `places_disponibles`, `statut`, `image_url`, `prix`, `gratuit`, `date_creation`, `date_modification`, `nombre_vues`, `nombre_inscriptions`, `note_moyenne`, `organisateur_id`) VALUES
(8, 'Conférence IA 2025', 'Conférence sur l\'intelligence artificielle et ses applications.', '2025-11-10 09:00:00', '2025-11-10 17:00:00', 'ENSA Tétouan', 'Amphithéâtre principal, ENSA Tétouan', NULL, NULL, 200, 200, 'PUBLIE', NULL, 0, 1, '2025-10-21 07:07:23', '2025-10-21 07:43:25', 1, 0, 0, 10),
(9, 'Hackathon ENSA', 'Compétition de programmation pour étudiants.', '2025-12-05 08:00:00', '2025-12-06 20:00:00', 'ENSA Tétouan', 'Salle informatique, ENSA Tétouan', NULL, NULL, 100, 100, 'PUBLIE', NULL, 0, 1, '2025-10-21 07:07:23', NULL, 0, 0, 0, 10),
(10, 'Journée Portes Ouvertes', 'Découverte des laboratoires et projets étudiants.', '2025-11-20 10:00:00', '2025-11-20 16:00:00', 'ENSA Tétouan', 'Hall principal, ENSA Tétouan', NULL, NULL, 150, 150, 'PUBLIE', NULL, 0, 1, '2025-10-21 07:07:23', NULL, 0, 0, 0, 10),
(11, 'Atelier DevOps', 'Atelier pratique sur les outils DevOps.', '2025-11-25 14:00:00', '2025-11-25 18:00:00', 'ENSA Tétouan', 'Salle 204, ENSA Tétouan', NULL, NULL, 50, 50, 'PUBLIE', NULL, 0, 1, '2025-10-21 07:07:23', NULL, 0, 0, 0, 10),
(12, 'Séminaire Sécurité', 'Séminaire sur la cybersécurité et la protection des données.', '2025-12-01 09:00:00', '2025-12-01 13:00:00', 'ENSA Tétouan', 'Salle 101, ENSA Tétouan', NULL, NULL, 80, 80, 'PUBLIE', NULL, 0, 1, '2025-10-21 07:07:23', NULL, 0, 0, 0, 10),
(13, 'Forum Entreprises', 'Rencontre avec les entreprises partenaires.', '2025-12-10 10:00:00', '2025-12-10 17:00:00', 'ENSA Tétouan', 'Salle polyvalente, ENSA Tétouan', NULL, NULL, 120, 120, 'PUBLIE', NULL, 0, 1, '2025-10-21 07:07:23', NULL, 0, 0, 0, 10),
(14, 'Workshop Cloud', 'Atelier sur les technologies Cloud.', '2025-11-28 15:00:00', '2025-11-28 19:00:00', 'ENSA Tétouan', 'Salle 303, ENSA Tétouan', NULL, NULL, 60, 60, 'PUBLIE', NULL, 0, 1, '2025-10-21 07:07:23', NULL, 0, 0, 0, 10),
(15, 'Conférence Big Data', 'Conférence sur le Big Data et l\'analyse de données.', '2025-12-15 09:00:00', '2025-12-15 17:00:00', 'ENSA Tétouan', 'Amphithéâtre principal, ENSA Tétouan', NULL, NULL, 180, 180, 'PUBLIE', NULL, 0, 1, '2025-10-21 07:07:23', NULL, 0, 0, 0, 10),
(16, 'Meetup Développeurs', 'Rencontre informelle entre développeurs.', '2025-11-30 18:00:00', '2025-11-30 21:00:00', 'ENSA Tétouan', 'Cafétéria, ENSA Tétouan', NULL, NULL, 40, 40, 'PUBLIE', NULL, 0, 1, '2025-10-21 07:07:23', NULL, 0, 0, 0, 10),
(17, 'Salon de l\'Innovation', 'Exposition des projets innovants des étudiants.', '2025-12-20 10:00:00', '2025-12-20 16:00:00', 'ENSA Tétouan', 'Hall principal, ENSA Tétouan', NULL, NULL, 160, 160, 'PUBLIE', NULL, 0, 1, '2025-10-21 07:07:23', '2025-10-21 07:16:08', 0, 0, 0, 10);

--
-- Triggers `evenements`
--
DELIMITER $$
CREATE TRIGGER `before_evenement_insert` BEFORE INSERT ON `evenements` FOR EACH ROW BEGIN
    IF NEW.places_disponibles IS NULL THEN
        SET NEW.places_disponibles = NEW.capacite;
    END IF;
    IF NEW.gratuit IS NULL THEN
        SET NEW.gratuit = FALSE;
    END IF;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `before_evenement_update` BEFORE UPDATE ON `evenements` FOR EACH ROW BEGIN
    SET NEW.date_modification = NOW();
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `evenement_categorie`
--

CREATE TABLE `evenement_categorie` (
  `evenement_id` bigint NOT NULL,
  `categorie_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `evenement_categorie`
--

INSERT INTO `evenement_categorie` (`evenement_id`, `categorie_id`) VALUES
(8, 1),
(9, 1),
(10, 4),
(11, 1),
(12, 1),
(13, 4),
(14, 1),
(15, 1),
(16, 1),
(17, 4);

-- --------------------------------------------------------

--
-- Table structure for table `inscriptions`
--

CREATE TABLE `inscriptions` (
  `id` bigint NOT NULL,
  `date_inscription` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `statut` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'EN_ATTENTE',
  `date_reponse` datetime DEFAULT NULL,
  `message_participant` text COLLATE utf8mb4_unicode_ci,
  `message_organisateur` text COLLATE utf8mb4_unicode_ci,
  `nombre_places` int DEFAULT '1',
  `presence_confirmee` tinyint(1) DEFAULT '0',
  `date_annulation` datetime DEFAULT NULL,
  `motif_annulation` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `participant_id` bigint NOT NULL,
  `evenement_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `organisateurs`
--

CREATE TABLE `organisateurs` (
  `utilisateur_id` bigint NOT NULL,
  `organisation` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `telephone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `site_web` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `adresse` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nombre_evenements_organises` int DEFAULT '0',
  `approved` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `organisateurs`
--

INSERT INTO `organisateurs` (`utilisateur_id`, `organisation`, `telephone`, `site_web`, `description`, `adresse`, `nombre_evenements_organises`, `approved`) VALUES
(2, 'École Nationale des Sciences Appliquées', '+212539688000', 'https://ensa-tetouan.ac.ma', 'École d\'ingénieurs à Tétouan', 'Tétouan, Maroc', 0, 1),
(10, 'ENSAM', '0623584039', NULL, NULL, NULL, 0, 1);

-- --------------------------------------------------------

--
-- Table structure for table `participants`
--

CREATE TABLE `participants` (
  `utilisateur_id` bigint NOT NULL,
  `preferences` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `interets` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ville` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `date_naissance` date DEFAULT NULL,
  `telephone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nombre_inscriptions` int DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `participants`
--

INSERT INTO `participants` (`utilisateur_id`, `preferences`, `interets`, `ville`, `date_naissance`, `telephone`, `nombre_inscriptions`) VALUES
(3, 'Technologie, Innovation', 'Développement web, Intelligence artificielle', 'Tétouan', NULL, NULL, 0),
(5, NULL, NULL, NULL, NULL, NULL, 0),
(8, NULL, NULL, NULL, NULL, NULL, 0);

-- --------------------------------------------------------

--
-- Table structure for table `utilisateurs`
--

CREATE TABLE `utilisateurs` (
  `id` bigint NOT NULL,
  `type_utilisateur` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nom` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mot_de_passe` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `statut` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIF',
  `date_inscription` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `derniere_connexion` datetime DEFAULT NULL,
  `photo_profil` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `verification_code` varchar(6) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `verification_code_expiry` datetime DEFAULT NULL,
  `email_verified` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `utilisateurs`
--

INSERT INTO `utilisateurs` (`id`, `type_utilisateur`, `nom`, `email`, `mot_de_passe`, `statut`, `date_inscription`, `derniere_connexion`, `photo_profil`, `verification_code`, `verification_code_expiry`, `email_verified`) VALUES
(2, 'ORGANISATEUR', 'ENSA Tétouan', 'organisateur@ensa.ma', 'organizer123', 'SUSPENDU', '2025-10-20 14:34:45', NULL, NULL, NULL, NULL, 0),
(3, 'PARTICIPANT', 'Mohammed Alami', 'participant@test.ma', 'participant123', 'ACTIF', '2025-10-20 14:34:45', NULL, NULL, NULL, NULL, 0),
(5, 'PARTICIPANT', 'youssef', 'youssef@gmail.com', 'Z2hzmdm69H8J3xm23Rth5Q==:7724a8eea7be3aef46bdcc4083940accfc1e61dcd2a242a60e46bd1a1c83786b', 'ACTIF', '2025-10-20 17:01:42', '2025-10-21 06:43:16', NULL, NULL, NULL, 1),
(8, 'PARTICIPANT', 'adam', 'haytamprimo3@gmail.com', 'n1YERQih3ei3l5LyJB892w==:b64bf1fc46195f88fdbb71e7eb8796cce2b369356fd8d29363705f6fbc398c14', 'ACTIF', '2025-10-20 23:27:55', '2025-10-20 23:39:35', NULL, NULL, NULL, 1),
(9, 'ADMINISTRATEUR', 'Admin', '003haytam2@gmail.com', 'Jw1PbkJeNkOYugHZigJrAA==:2c0598b9a77827a2efcfc6b0b777045f6cdfbf10c7aa947cdb249c364043bc83', 'ACTIF', '2025-10-21 00:56:06', '2025-10-21 00:07:35', NULL, NULL, NULL, 1),
(10, 'ORGANISATEUR', 'ahmad', 'lappahamid@gmail.com', 'hTiPQQxKSkBcenULlunDNg==:8dbc64996330476ac9fe837d20aa246df7cac4f7417a62599ba50f2d14a63730', 'ACTIF', '2025-10-21 00:06:48', '2025-10-21 06:38:02', NULL, NULL, NULL, 1);

-- --------------------------------------------------------

--
-- Stand-in structure for view `v_evenements_publies`
-- (See below for the actual view)
--
CREATE TABLE `v_evenements_publies` (
`id` bigint
,`titre` varchar(200)
,`description` text
,`date_debut` datetime
,`date_fin` datetime
,`lieu` varchar(255)
,`capacite` int
,`places_disponibles` int
,`prix` double
,`gratuit` tinyint(1)
,`image_url` varchar(500)
,`nombre_vues` int
,`nombre_inscriptions` int
,`note_moyenne` double
,`organisateur_nom` varchar(100)
,`organisateur_email` varchar(150)
,`organisateur_organisation` varchar(150)
,`categories` text
);

-- --------------------------------------------------------

--
-- Stand-in structure for view `v_statistiques_globales`
-- (See below for the actual view)
--
CREATE TABLE `v_statistiques_globales` (
`total_organisateurs` bigint
,`total_participants` bigint
,`total_evenements_publies` bigint
,`evenements_a_venir` bigint
,`total_inscriptions_acceptees` bigint
,`moyenne_notes_evenements` double
);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `administrateurs`
--
ALTER TABLE `administrateurs`
  ADD PRIMARY KEY (`utilisateur_id`);

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nom` (`nom`),
  ADD KEY `idx_nom` (`nom`),
  ADD KEY `idx_active` (`active`);

--
-- Indexes for table `commentaires`
--
ALTER TABLE `commentaires`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_participant` (`participant_id`),
  ADD KEY `idx_evenement` (`evenement_id`),
  ADD KEY `idx_date_creation` (`date_creation`),
  ADD KEY `idx_visible` (`visible`);

--
-- Indexes for table `evaluations`
--
ALTER TABLE `evaluations`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_evaluation` (`participant_id`,`evenement_id`),
  ADD UNIQUE KEY `UKoa2fj6w7b9r8j2mkpwy3flk8q` (`participant_id`,`evenement_id`),
  ADD KEY `idx_participant` (`participant_id`),
  ADD KEY `idx_evenement` (`evenement_id`),
  ADD KEY `idx_note` (`note`),
  ADD KEY `idx_visible` (`visible`);

--
-- Indexes for table `evenements`
--
ALTER TABLE `evenements`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_statut` (`statut`),
  ADD KEY `idx_date_debut` (`date_debut`),
  ADD KEY `idx_organisateur` (`organisateur_id`),
  ADD KEY `idx_lieu` (`lieu`);

--
-- Indexes for table `evenement_categorie`
--
ALTER TABLE `evenement_categorie`
  ADD PRIMARY KEY (`evenement_id`,`categorie_id`),
  ADD KEY `idx_evenement` (`evenement_id`),
  ADD KEY `idx_categorie` (`categorie_id`);

--
-- Indexes for table `inscriptions`
--
ALTER TABLE `inscriptions`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_inscription` (`participant_id`,`evenement_id`),
  ADD UNIQUE KEY `UK3wrk342ooxr7vaxg4t9ygf6i` (`participant_id`,`evenement_id`),
  ADD KEY `idx_participant` (`participant_id`),
  ADD KEY `idx_evenement` (`evenement_id`),
  ADD KEY `idx_statut` (`statut`),
  ADD KEY `idx_date_inscription` (`date_inscription`);

--
-- Indexes for table `organisateurs`
--
ALTER TABLE `organisateurs`
  ADD PRIMARY KEY (`utilisateur_id`),
  ADD KEY `idx_organisation` (`organisation`),
  ADD KEY `idx_approved` (`approved`);

--
-- Indexes for table `participants`
--
ALTER TABLE `participants`
  ADD PRIMARY KEY (`utilisateur_id`),
  ADD KEY `idx_ville` (`ville`);

--
-- Indexes for table `utilisateurs`
--
ALTER TABLE `utilisateurs`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `idx_email` (`email`),
  ADD KEY `idx_statut` (`statut`),
  ADD KEY `idx_type` (`type_utilisateur`),
  ADD KEY `idx_email_verified` (`email_verified`),
  ADD KEY `idx_verification_code` (`verification_code`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `commentaires`
--
ALTER TABLE `commentaires`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `evaluations`
--
ALTER TABLE `evaluations`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `evenements`
--
ALTER TABLE `evenements`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `inscriptions`
--
ALTER TABLE `inscriptions`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `utilisateurs`
--
ALTER TABLE `utilisateurs`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

-- --------------------------------------------------------

--
-- Structure for view `v_evenements_publies`
--
DROP TABLE IF EXISTS `v_evenements_publies`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `v_evenements_publies`  AS SELECT `e`.`id` AS `id`, `e`.`titre` AS `titre`, `e`.`description` AS `description`, `e`.`date_debut` AS `date_debut`, `e`.`date_fin` AS `date_fin`, `e`.`lieu` AS `lieu`, `e`.`capacite` AS `capacite`, `e`.`places_disponibles` AS `places_disponibles`, `e`.`prix` AS `prix`, `e`.`gratuit` AS `gratuit`, `e`.`image_url` AS `image_url`, `e`.`nombre_vues` AS `nombre_vues`, `e`.`nombre_inscriptions` AS `nombre_inscriptions`, `e`.`note_moyenne` AS `note_moyenne`, `u`.`nom` AS `organisateur_nom`, `u`.`email` AS `organisateur_email`, `o`.`organisation` AS `organisateur_organisation`, group_concat(`c`.`nom` separator ', ') AS `categories` FROM ((((`evenements` `e` join `organisateurs` `o` on((`e`.`organisateur_id` = `o`.`utilisateur_id`))) join `utilisateurs` `u` on((`o`.`utilisateur_id` = `u`.`id`))) left join `evenement_categorie` `ec` on((`e`.`id` = `ec`.`evenement_id`))) left join `categories` `c` on((`ec`.`categorie_id` = `c`.`id`))) WHERE (`e`.`statut` = 'PUBLIE') GROUP BY `e`.`id`, `e`.`titre`, `e`.`description`, `e`.`date_debut`, `e`.`date_fin`, `e`.`lieu`, `e`.`capacite`, `e`.`places_disponibles`, `e`.`prix`, `e`.`gratuit`, `e`.`image_url`, `e`.`nombre_vues`, `e`.`nombre_inscriptions`, `e`.`note_moyenne`, `u`.`nom`, `u`.`email`, `o`.`organisation` ;

-- --------------------------------------------------------

--
-- Structure for view `v_statistiques_globales`
--
DROP TABLE IF EXISTS `v_statistiques_globales`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `v_statistiques_globales`  AS SELECT (select count(0) from `utilisateurs` where ((`utilisateurs`.`type_utilisateur` = 'ORGANISATEUR') and (`utilisateurs`.`statut` = 'ACTIF'))) AS `total_organisateurs`, (select count(0) from `utilisateurs` where ((`utilisateurs`.`type_utilisateur` = 'PARTICIPANT') and (`utilisateurs`.`statut` = 'ACTIF'))) AS `total_participants`, (select count(0) from `evenements` where (`evenements`.`statut` = 'PUBLIE')) AS `total_evenements_publies`, (select count(0) from `evenements` where ((`evenements`.`statut` = 'PUBLIE') and (`evenements`.`date_debut` > now()))) AS `evenements_a_venir`, (select count(0) from `inscriptions` where (`inscriptions`.`statut` = 'ACCEPTEE')) AS `total_inscriptions_acceptees`, (select avg(`evenements`.`note_moyenne`) from `evenements` where (`evenements`.`note_moyenne` > 0)) AS `moyenne_notes_evenements` ;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `administrateurs`
--
ALTER TABLE `administrateurs`
  ADD CONSTRAINT `administrateurs_ibfk_1` FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateurs` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `commentaires`
--
ALTER TABLE `commentaires`
  ADD CONSTRAINT `commentaires_ibfk_1` FOREIGN KEY (`participant_id`) REFERENCES `participants` (`utilisateur_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `commentaires_ibfk_2` FOREIGN KEY (`evenement_id`) REFERENCES `evenements` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `evaluations`
--
ALTER TABLE `evaluations`
  ADD CONSTRAINT `evaluations_ibfk_1` FOREIGN KEY (`participant_id`) REFERENCES `participants` (`utilisateur_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `evaluations_ibfk_2` FOREIGN KEY (`evenement_id`) REFERENCES `evenements` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `evenements`
--
ALTER TABLE `evenements`
  ADD CONSTRAINT `evenements_ibfk_1` FOREIGN KEY (`organisateur_id`) REFERENCES `organisateurs` (`utilisateur_id`) ON DELETE CASCADE;

--
-- Constraints for table `evenement_categorie`
--
ALTER TABLE `evenement_categorie`
  ADD CONSTRAINT `evenement_categorie_ibfk_1` FOREIGN KEY (`evenement_id`) REFERENCES `evenements` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `evenement_categorie_ibfk_2` FOREIGN KEY (`categorie_id`) REFERENCES `categories` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `inscriptions`
--
ALTER TABLE `inscriptions`
  ADD CONSTRAINT `inscriptions_ibfk_1` FOREIGN KEY (`participant_id`) REFERENCES `participants` (`utilisateur_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `inscriptions_ibfk_2` FOREIGN KEY (`evenement_id`) REFERENCES `evenements` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `organisateurs`
--
ALTER TABLE `organisateurs`
  ADD CONSTRAINT `organisateurs_ibfk_1` FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateurs` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `participants`
--
ALTER TABLE `participants`
  ADD CONSTRAINT `participants_ibfk_1` FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateurs` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
