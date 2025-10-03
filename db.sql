
CREATE TABLE utilisateur (
                             utilisateurId INTEGER PRIMARY KEY AUTO_INCREMENT,
                             nomUtilisateur VARCHAR(100) NOT NULL,
                             motDePasseHash VARCHAR(255) NOT NULL,
                             email VARCHAR(255) NOT NULL UNIQUE,
                             userType ENUM('administrateur', 'organisateur', 'participant') NOT NULL
);

CREATE TABLE administrateur (
                                utilisateurId INTEGER PRIMARY KEY,
                                FOREIGN KEY (utilisateurId) REFERENCES utilisateur(utilisateurId) ON DELETE CASCADE
);

CREATE TABLE organisateur (
                              utilisateurId INTEGER PRIMARY KEY,
                              FOREIGN KEY (utilisateurId) REFERENCES utilisateur(utilisateurId) ON DELETE CASCADE
);

CREATE TABLE participant (
                             utilisateurId INTEGER PRIMARY KEY,
                             FOREIGN KEY (utilisateurId) REFERENCES utilisateur(utilisateurId) ON DELETE CASCADE
);

CREATE TABLE categorie (
                           categorieId INTEGER PRIMARY KEY AUTO_INCREMENT,
                           nom VARCHAR(100) NOT NULL
);

CREATE TABLE statut_evenement (
                                  statut VARCHAR(20) PRIMARY KEY,
                                  CHECK (statut IN ('BROUILLON', 'PUBLIE', 'ANNULE'))
);

CREATE TABLE evenement (
                           evenementId INTEGER PRIMARY KEY AUTO_INCREMENT,
                           titre VARCHAR(255) NOT NULL,
                           description TEXT,
                           dateDebut DATETIME NOT NULL,
                           dateFin DATETIME NOT NULL,
                           statut VARCHAR(20) NOT NULL DEFAULT 'BROUILLON',
                           lieu VARCHAR(255),
                           organisateurId INTEGER NOT NULL,
                           FOREIGN KEY (organisateurId) REFERENCES organisateur(utilisateurId),
                           FOREIGN KEY (statut) REFERENCES statut_evenement(statut)
);

CREATE TABLE statut_inscription (
                                    statut VARCHAR(20) PRIMARY KEY,
                                    CHECK (statut IN ('EN_ATTENTE', 'ACCEPTEE', 'REFUSEE', 'ANNULEE'))
);

-- Registration table
CREATE TABLE inscription (
                             inscriptionId INTEGER PRIMARY KEY AUTO_INCREMENT,
                             participantId INTEGER NOT NULL,
                             evenementId INTEGER NOT NULL,
                             dateInscription DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             statut VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
                             FOREIGN KEY (participantId) REFERENCES participant(utilisateurId),
                             FOREIGN KEY (evenementId) REFERENCES evenement(evenementId) ON DELETE CASCADE,
                             FOREIGN KEY (statut) REFERENCES statut_inscription(statut),
                             UNIQUE KEY unique_participant_event (participantId, evenementId)
);

CREATE TABLE commentaire (
                             commentaireId INTEGER PRIMARY KEY AUTO_INCREMENT,
                             texte TEXT NOT NULL,
                             horodatage DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             participantId INTEGER NOT NULL,
                             evenementId INTEGER NOT NULL,
                             FOREIGN KEY (participantId) REFERENCES participant(utilisateurId),
                             FOREIGN KEY (evenementId) REFERENCES evenement(evenementId) ON DELETE CASCADE
);


CREATE TABLE evaluation (
                            evaluationId INTEGER PRIMARY KEY AUTO_INCREMENT,
                            note INTEGER NOT NULL CHECK (note >= 0 AND note <= 5),
                            texte TEXT,
                            horodatage DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            participantId INTEGER NOT NULL,
                            evenementId INTEGER NOT NULL,
                            FOREIGN KEY (participantId) REFERENCES participant(utilisateurId),
                            FOREIGN KEY (evenementId) REFERENCES evenement(evenementId) ON DELETE CASCADE,
                            UNIQUE KEY unique_participant_event_eval (participantId, evenementId)
);

CREATE TABLE evenement_categorie (
                                     evenementId INTEGER NOT NULL,
                                     categorieId INTEGER NOT NULL,
                                     PRIMARY KEY (evenementId, categorieId),
                                     FOREIGN KEY (evenementId) REFERENCES evenement(evenementId) ON DELETE CASCADE,
                                     FOREIGN KEY (categorieId) REFERENCES categorie(categorieId) ON DELETE CASCADE
);

INSERT INTO statut_evenement (statut) VALUES
                                          ('BROUILLON'),
                                          ('PUBLIE'),
                                          ('ANNULE');

INSERT INTO statut_inscription (statut) VALUES
                                            ('EN_ATTENTE'),
                                            ('ACCEPTEE'),
                                            ('REFUSEE'),
                                            ('ANNULEE');

CREATE INDEX idx_evenement_organisateur ON evenement(organisateurId);
CREATE INDEX idx_evenement_statut ON evenement(statut);
CREATE INDEX idx_evenement_dates ON evenement(dateDebut, dateFin);
CREATE INDEX idx_inscription_participant ON inscription(participantId);
CREATE INDEX idx_inscription_evenement ON inscription(evenementId);
CREATE INDEX idx_inscription_statut ON inscription(statut);
CREATE INDEX idx_commentaire_evenement ON commentaire(evenementId);
CREATE INDEX idx_evaluation_evenement ON evaluation(evenementId);