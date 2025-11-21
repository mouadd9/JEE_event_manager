-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Nov 14, 2025 at 07:35 PM
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
-- Database: `event_manager`
--

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `permissions` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`permissions`, `role`, `id`) VALUES
('all', 'SUPER_ADMIN', 100);

-- --------------------------------------------------------

--
-- Table structure for table `categorie`
--

CREATE TABLE `categorie` (
  `categorie_id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `nom` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `commentaire`
--

CREATE TABLE `commentaire` (
  `commentaire_id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `horodatage` datetime(6) NOT NULL,
  `texte` text NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `evenement_id` bigint NOT NULL,
  `participant_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `evaluation`
--

CREATE TABLE `evaluation` (
  `evaluation_id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `horodatage` datetime(6) NOT NULL,
  `note` int NOT NULL,
  `texte` text,
  `updated_at` datetime(6) DEFAULT NULL,
  `evenement_id` bigint NOT NULL,
  `participant_id` bigint NOT NULL
) ;

-- --------------------------------------------------------

--
-- Table structure for table `evenement`
--

CREATE TABLE `evenement` (
  `evenement_id` bigint NOT NULL,
  `capacite` int DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `date_debut` datetime(6) NOT NULL,
  `date_fin` datetime(6) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `image_url` varchar(500) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `lieu` varchar(255) NOT NULL,
  `longitude` double DEFAULT NULL,
  `statut` enum('BROUILLON','PUBLIE','ANNULE','CACHE') NOT NULL,
  `titre` varchar(100) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `organisateur_id` bigint NOT NULL
) ;

--
-- Dumping data for table `evenement`
--

INSERT INTO `evenement` (`evenement_id`, `capacite`, `created_at`, `date_debut`, `date_fin`, `description`, `image_url`, `latitude`, `lieu`, `longitude`, `statut`, `titre`, `updated_at`, `organisateur_id`) VALUES
(2, 100, '2025-10-28 09:48:59.384305', '2025-10-31 09:47:00.000000', '2025-10-30 23:48:00.000000', 'description exemple de conference tech', 'uploads/events/c04cde94-f593-4210-8093-941d84a94d65.png', 35.56243876876503, 'ENSA Tétouan', -5.364446018164651, 'PUBLIE', 'conference tech 2', '2025-10-28 09:48:59.384305', 102),
(3, 100, '2025-10-28 09:50:47.639615', '2025-10-31 09:49:00.000000', '2025-10-30 23:50:00.000000', 'exemple de description de conference IA', 'uploads/events/357a319c-4a65-4346-a9c2-1f7fd800d805.png', 35.56896646803115, 'ENSA Tétouan', -5.375987607460174, 'PUBLIE', 'conference IA', '2025-10-28 09:50:47.639615', 102),
(4, 100, '2025-10-28 09:59:23.305501', '2025-10-31 09:00:00.000000', '2025-10-30 23:00:00.000000', 'exemple', 'uploads/events/24087660-c82d-4145-92dd-1723c08fcd6d.png', 35.5698810878492, 'ENSA Tétouan', -5.37148586989791, 'PUBLIE', 'conference tech', '2025-10-28 10:17:31.162845', 102);

-- --------------------------------------------------------

--
-- Table structure for table `evenement_categorie`
--

CREATE TABLE `evenement_categorie` (
  `evenement_id` bigint NOT NULL,
  `categorie_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `evenement_report`
--

CREATE TABLE `evenement_report` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` text,
  `reason` varchar(255) NOT NULL,
  `evenement_id` bigint NOT NULL,
  `participant_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `inscription`
--

CREATE TABLE `inscription` (
  `inscription_id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `date_inscription` datetime(6) NOT NULL,
  `quantite` int DEFAULT NULL,
  `statut` enum('EN_ATTENTE','ACCEPTEE','REFUSEE','ANNULEE') NOT NULL,
  `type_billet` varchar(50) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `evenement_id` bigint NOT NULL,
  `participant_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `organisateur`
--

CREATE TABLE `organisateur` (
  `description` varchar(255) DEFAULT NULL,
  `entreprise` varchar(255) DEFAULT NULL,
  `siret` varchar(255) DEFAULT NULL,
  `site_web` varchar(255) DEFAULT NULL,
  `utilisateur_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `organisateur`
--

INSERT INTO `organisateur` (`description`, `entreprise`, `siret`, `site_web`, `utilisateur_id`) VALUES
('Organisateur de test pour les événements', 'Test Company', '12345678901234', 'https://testcompany.com', 1),
(NULL, NULL, NULL, NULL, 102),
(NULL, NULL, NULL, NULL, 103);

-- --------------------------------------------------------

--
-- Table structure for table `participant`
--

CREATE TABLE `participant` (
  `date_naissance` date DEFAULT NULL,
  `preferences` varchar(255) DEFAULT NULL,
  `telephone` varchar(255) DEFAULT NULL,
  `utilisateur_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `participant`
--

INSERT INTO `participant` (`date_naissance`, `preferences`, `telephone`, `utilisateur_id`) VALUES
('1990-01-01', 'Musique, Sport, Technologie', '+33123456789', 2),
(NULL, NULL, NULL, 101);

-- --------------------------------------------------------

--
-- Table structure for table `utilisateur`
--

CREATE TABLE `utilisateur` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(255) NOT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `is_suspended` bit(1) DEFAULT NULL,
  `is_verified` bit(1) DEFAULT NULL,
  `mot_de_passe_hash` varchar(255) NOT NULL,
  `nom` varchar(255) NOT NULL,
  `suspension_reason` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_type` enum('ORGANISATEUR','PARTICIPANT','ADMIN') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `utilisateur`
--

INSERT INTO `utilisateur` (`id`, `created_at`, `email`, `is_active`, `is_suspended`, `is_verified`, `mot_de_passe_hash`, `nom`, `suspension_reason`, `updated_at`, `user_type`) VALUES
(1, '2025-10-26 22:51:51.000000', 'organizer@test.com', b'1', b'0', b'1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Organisateur Test', NULL, '2025-10-26 22:51:51.000000', 'ORGANISATEUR'),
(2, '2025-10-26 22:51:51.000000', 'participant@test.com', b'1', b'0', b'1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Participant Test', NULL, '2025-10-26 22:51:51.000000', 'PARTICIPANT'),
(100, '2025-10-26 22:52:22.000000', 'admin@eventmanager.com', b'1', b'0', b'1', 'e86f78a8a3caf0b60d8e74e5942aa6d86dc150cd3c03338aef25b7d2d7e3acc7', 'Administrateur', NULL, '2025-10-26 22:52:22.000000', 'ADMIN'),
(101, '2025-10-26 21:57:16.545337', 'failalijatabi.haytham@etu.uae.ac.ma', b'1', b'0', b'1', '52a1c8e99c105c26dc1ee07b30cecec07f1588fa02df031769ab815c3634a5fb', 'haytham', NULL, '2025-10-26 21:57:16.545337', 'PARTICIPANT'),
(102, '2025-10-26 21:58:47.315628', '003haytam2@gmail.com', b'1', b'0', b'1', 'a333c5bbacce7e374f64e489c0299a1c08f85e9e9065339712095e6138aa12b5', 'haytam', NULL, '2025-10-26 22:30:58.753187', 'ORGANISATEUR'),
(103, '2025-10-26 22:18:46.436171', 'lappahamid@gmail.com', b'1', b'0', b'1', 'a333c5bbacce7e374f64e489c0299a1c08f85e9e9065339712095e6138aa12b5', 'adam', NULL, '2025-10-28 10:18:23.794813', 'ORGANISATEUR');

-- --------------------------------------------------------

--
-- Table structure for table `verification_code`
--

CREATE TABLE `verification_code` (
  `id` bigint NOT NULL,
  `code` varchar(6) NOT NULL,
  `createdAt` datetime(6) NOT NULL,
  `email` varchar(255) NOT NULL,
  `expiresAt` datetime(6) NOT NULL,
  `type` enum('EMAIL_VERIFICATION','PASSWORD_RESET') NOT NULL,
  `used` bit(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `verification_code`
--

INSERT INTO `verification_code` (`id`, `code`, `createdAt`, `email`, `expiresAt`, `type`, `used`) VALUES
(1, '350937', '2025-10-26 21:56:39.755164', 'failalijatabi.haytham@etu.uae.ac.ma', '2025-10-26 22:11:39.755164', 'EMAIL_VERIFICATION', b'1'),
(2, '521462', '2025-10-26 21:58:12.679548', '003haytam2@gmail.com', '2025-10-26 22:13:12.679548', 'EMAIL_VERIFICATION', b'1'),
(3, '522718', '2025-10-26 22:18:15.576926', 'lappahamid@gmail.com', '2025-10-26 22:33:15.576926', 'EMAIL_VERIFICATION', b'1');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `categorie`
--
ALTER TABLE `categorie`
  ADD PRIMARY KEY (`categorie_id`),
  ADD UNIQUE KEY `UK_89y3d23ia9ruhfhdmya9aspq7` (`nom`);

--
-- Indexes for table `commentaire`
--
ALTER TABLE `commentaire`
  ADD PRIMARY KEY (`commentaire_id`),
  ADD KEY `FKhmwurd0yd52bn0r3hlwblpswj` (`evenement_id`),
  ADD KEY `FKm48dvx4wobf96ldqa7y2hsxvg` (`participant_id`);

--
-- Indexes for table `evaluation`
--
ALTER TABLE `evaluation`
  ADD PRIMARY KEY (`evaluation_id`),
  ADD UNIQUE KEY `uk_evaluation_participant_evenement` (`participant_id`,`evenement_id`),
  ADD KEY `FK3qkgc7bv68f92ws78gi8evajv` (`evenement_id`);

--
-- Indexes for table `evenement`
--
ALTER TABLE `evenement`
  ADD PRIMARY KEY (`evenement_id`),
  ADD KEY `FKnpguuiqsowqb7w9l632y74k6k` (`organisateur_id`);

--
-- Indexes for table `evenement_categorie`
--
ALTER TABLE `evenement_categorie`
  ADD PRIMARY KEY (`evenement_id`,`categorie_id`),
  ADD KEY `FKeyjjgvcipuh58m3ly1f3o28co` (`categorie_id`);

--
-- Indexes for table `evenement_report`
--
ALTER TABLE `evenement_report`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKcuvvinlwq2v4butqx3eqr1enn` (`evenement_id`),
  ADD KEY `FKmd2umax2a2mofc1386q37tme1` (`participant_id`);

--
-- Indexes for table `inscription`
--
ALTER TABLE `inscription`
  ADD PRIMARY KEY (`inscription_id`),
  ADD UNIQUE KEY `UK158pfrsr36iabvn6uvmjjvb2n` (`participant_id`,`evenement_id`),
  ADD KEY `FK1a3dd8pqia7dftu5br3abkgcw` (`evenement_id`);

--
-- Indexes for table `organisateur`
--
ALTER TABLE `organisateur`
  ADD PRIMARY KEY (`utilisateur_id`);

--
-- Indexes for table `participant`
--
ALTER TABLE `participant`
  ADD PRIMARY KEY (`utilisateur_id`);

--
-- Indexes for table `utilisateur`
--
ALTER TABLE `utilisateur`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_rma38wvnqfaf66vvmi57c71lo` (`email`);

--
-- Indexes for table `verification_code`
--
ALTER TABLE `verification_code`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `categorie`
--
ALTER TABLE `categorie`
  MODIFY `categorie_id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `commentaire`
--
ALTER TABLE `commentaire`
  MODIFY `commentaire_id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `evaluation`
--
ALTER TABLE `evaluation`
  MODIFY `evaluation_id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `evenement`
--
ALTER TABLE `evenement`
  MODIFY `evenement_id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `evenement_report`
--
ALTER TABLE `evenement_report`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `inscription`
--
ALTER TABLE `inscription`
  MODIFY `inscription_id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `utilisateur`
--
ALTER TABLE `utilisateur`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=104;

--
-- AUTO_INCREMENT for table `verification_code`
--
ALTER TABLE `verification_code`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `admin`
--
ALTER TABLE `admin`
  ADD CONSTRAINT `FKgodqjbbtwk30kf3s0xuxklkr3` FOREIGN KEY (`id`) REFERENCES `utilisateur` (`id`);

--
-- Constraints for table `commentaire`
--
ALTER TABLE `commentaire`
  ADD CONSTRAINT `FKhmwurd0yd52bn0r3hlwblpswj` FOREIGN KEY (`evenement_id`) REFERENCES `evenement` (`evenement_id`),
  ADD CONSTRAINT `FKm48dvx4wobf96ldqa7y2hsxvg` FOREIGN KEY (`participant_id`) REFERENCES `participant` (`utilisateur_id`);

--
-- Constraints for table `evaluation`
--
ALTER TABLE `evaluation`
  ADD CONSTRAINT `FK3qkgc7bv68f92ws78gi8evajv` FOREIGN KEY (`evenement_id`) REFERENCES `evenement` (`evenement_id`),
  ADD CONSTRAINT `FKdv13trgw2f6a104q6xfa4e34s` FOREIGN KEY (`participant_id`) REFERENCES `participant` (`utilisateur_id`);

--
-- Constraints for table `evenement`
--
ALTER TABLE `evenement`
  ADD CONSTRAINT `FKnpguuiqsowqb7w9l632y74k6k` FOREIGN KEY (`organisateur_id`) REFERENCES `organisateur` (`utilisateur_id`);

--
-- Constraints for table `evenement_categorie`
--
ALTER TABLE `evenement_categorie`
  ADD CONSTRAINT `FKeyjjgvcipuh58m3ly1f3o28co` FOREIGN KEY (`categorie_id`) REFERENCES `categorie` (`categorie_id`),
  ADD CONSTRAINT `FKpoe5bw7o8ywie5gigjdwunb4f` FOREIGN KEY (`evenement_id`) REFERENCES `evenement` (`evenement_id`);

--
-- Constraints for table `evenement_report`
--
ALTER TABLE `evenement_report`
  ADD CONSTRAINT `FKcuvvinlwq2v4butqx3eqr1enn` FOREIGN KEY (`evenement_id`) REFERENCES `evenement` (`evenement_id`),
  ADD CONSTRAINT `FKmd2umax2a2mofc1386q37tme1` FOREIGN KEY (`participant_id`) REFERENCES `participant` (`utilisateur_id`);

--
-- Constraints for table `inscription`
--
ALTER TABLE `inscription`
  ADD CONSTRAINT `FK1a3dd8pqia7dftu5br3abkgcw` FOREIGN KEY (`evenement_id`) REFERENCES `evenement` (`evenement_id`),
  ADD CONSTRAINT `FKh2ujwv8wxusc41gcvj4bsjtk7` FOREIGN KEY (`participant_id`) REFERENCES `participant` (`utilisateur_id`);

--
-- Constraints for table `organisateur`
--
ALTER TABLE `organisateur`
  ADD CONSTRAINT `FKij8a8flbicjg4rp9adesg8c49` FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateur` (`id`);

--
-- Constraints for table `participant`
--
ALTER TABLE `participant`
  ADD CONSTRAINT `FKm6yf1yihufyojmfo0ufwrip1q` FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateur` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
