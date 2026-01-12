-- Données générées automatiquement avec Faker
USE billeterie;

-- Utilisateurs de l'application
CREATE TABLE IF NOT EXISTS utilisateur (
    id_utilisateur INT AUTO_INCREMENT PRIMARY KEY,
    nom_complet VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    telephone VARCHAR(40),
    mot_de_passe VARCHAR(255) NOT NULL,
    solde DECIMAL(10,2) DEFAULT 500,
    is_admin TINYINT(1) DEFAULT 0,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2FA et questions de sécurité
CREATE TABLE IF NOT EXISTS security_question (
    id INT PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS security_answer (
    user_id INT NOT NULL,
    question_id INT NOT NULL,
    answer_hash VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id, question_id)
);

CREATE TABLE IF NOT EXISTS otp_token (
    user_id INT PRIMARY KEY,
    code VARCHAR(10) NOT NULL,
    expires_at TIMESTAMP NOT NULL
);

INSERT INTO security_question (id, libelle) VALUES
 (1, 'Quel est le nom de votre premier animal ?'),
 (2, 'Dans quelle ville êtes-vous né(e) ?'),
 (3, 'Quel est votre film préféré ?'),
 (4, 'Quel est le prénom de votre enseignant marquant ?'),
 (5, 'Quel est le nom de jeune fille de votre mère ?'),
 (6, 'Quel est votre plat préféré ?'),
 (7, 'Quel est votre livre préféré ?'),
 (8, 'Quelle est votre chanson fétiche ?'),
 (9, 'Où avez-vous passé vos meilleures vacances ?'),
 (10, 'Quel est le prénom de votre ami d’enfance ?')
ON DUPLICATE KEY UPDATE libelle = VALUES(libelle);

CREATE TABLE IF NOT EXISTS achat_utilisateur (
    id_achat INT AUTO_INCREMENT PRIMARY KEY,
    id_utilisateur INT NOT NULL,
    id_evenement INT NOT NULL,
    prix_achat DECIMAL(10,2) NOT NULL,
    date_achat TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_achat_user FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id_utilisateur),
    CONSTRAINT fk_achat_event FOREIGN KEY (id_evenement) REFERENCES evenement(id_evenement)
);

INSERT INTO utilisateur (nom_complet, email, telephone, mot_de_passe, solde, is_admin)
VALUES ('Administrateur', 'jhawadlajimi@hotmail.com', '0000000000',
'63d5c90459c70cd1fc7d9372d0e60acd6e16b25fa8b83bb06291bc5957ab991c', 0, 1)
ON DUPLICATE KEY UPDATE email = email;

-- Clients
INSERT INTO client (nom, prenom, email) VALUES ('Payet', 'Amélie', 'costaanne@example.org');
INSERT INTO client (nom, prenom, email) VALUES ('Guichard', 'Aimée', 'raymond52@example.com');
INSERT INTO client (nom, prenom, email) VALUES ('Lamy', 'Marcelle', 'denissusan@example.net');
INSERT INTO client (nom, prenom, email) VALUES ('Julien', 'Dorothée', 'juliehamon@example.org');
INSERT INTO client (nom, prenom, email) VALUES ('Lucas', 'Noémi', 'elise48@example.org');
INSERT INTO client (nom, prenom, email) VALUES ('Huet', 'Charlotte', 'bertrandauger@example.org');
INSERT INTO client (nom, prenom, email) VALUES ('Lenoir', 'Clémence', 'zriou@example.com');
INSERT INTO client (nom, prenom, email) VALUES ('Goncalves', 'Maggie', 'mbernard@example.org');
INSERT INTO client (nom, prenom, email) VALUES ('Rodriguez', 'Adrien', 'veroniquedidier@example.org');
INSERT INTO client (nom, prenom, email) VALUES ('Blanchard', 'Tristan', 'chevalierelise@example.com');
INSERT INTO client (nom, prenom, email) VALUES ('Masse', 'Anouk', 'helene85@example.com');
INSERT INTO client (nom, prenom, email) VALUES ('Maillet', 'Renée', 'camilleduhamel@example.com');
INSERT INTO client (nom, prenom, email) VALUES ('Besnard', 'Agnès', 'diane37@example.org');
INSERT INTO client (nom, prenom, email) VALUES ('Chauvet', 'Marthe', 'antoine62@example.net');
INSERT INTO client (nom, prenom, email) VALUES ('Samson', 'Geneviève', 'stephaneguillet@example.org');
INSERT INTO client (nom, prenom, email) VALUES ('Poulain', 'Lucy', 'julien98@example.com');
INSERT INTO client (nom, prenom, email) VALUES ('Lejeune', 'Jérôme', 'gallettheophile@example.com');
INSERT INTO client (nom, prenom, email) VALUES ('Boutin', 'Susan', 'paulette16@example.org');
INSERT INTO client (nom, prenom, email) VALUES ('Lacombe', 'Grégoire', 'jtoussaint@example.net');
INSERT INTO client (nom, prenom, email) VALUES ('Carre', 'Michel', 'xaviervalentin@example.org');
INSERT INTO client (nom, prenom, email) VALUES ('Humbert', 'Julie', 'margaud15@example.org');
INSERT INTO client (nom, prenom, email) VALUES ('Lebrun', 'Jean', 'bferreira@example.org');
INSERT INTO client (nom, prenom, email) VALUES ('Berger', 'Isaac', 'josephalain@example.net');
INSERT INTO client (nom, prenom, email) VALUES ('Bonnin', 'Christiane', 'tvaillant@example.net');
INSERT INTO client (nom, prenom, email) VALUES ('Morel', 'Alix', 'emoreau@example.net');
INSERT INTO client (nom, prenom, email) VALUES ('De Oliveira', 'Susanne', 'guibertlouise@example.com');
INSERT INTO client (nom, prenom, email) VALUES ('Bourdon', 'Maryse', 'milletlucas@example.com');
INSERT INTO client (nom, prenom, email) VALUES ('Pottier', 'François', 'nreynaud@example.org');
INSERT INTO client (nom, prenom, email) VALUES ('Bonnet', 'Rémy', 'georges64@example.net');
INSERT INTO client (nom, prenom, email) VALUES ('Boulay', 'Mathilde', 'andre25@example.net');
INSERT INTO client (nom, prenom, email) VALUES ('Lamy', 'Éric', 'mercierchristelle@example.org');
INSERT INTO client (nom, prenom, email) VALUES ('Wagner', 'Marianne', 'fpottier@example.com');
INSERT INTO client (nom, prenom, email) VALUES ('Besnard', 'Emmanuelle', 'maggie88@example.com');
INSERT INTO client (nom, prenom, email) VALUES ('Parent', 'Robert', 'maurynoel@example.net');
INSERT INTO client (nom, prenom, email) VALUES ('Neveu', 'Éric', 'peltierthibault@example.com');
INSERT INTO client (nom, prenom, email) VALUES ('Leroux', 'Alix', 'martheevrard@example.org');
INSERT INTO client (nom, prenom, email) VALUES ('Rocher', 'Éric', 'qdidier@example.net');
INSERT INTO client (nom, prenom, email) VALUES ('Bègue', 'Margaret', 'chartierandree@example.org');
INSERT INTO client (nom, prenom, email) VALUES ('Morvan', 'David', 'bouchermargaud@example.com');
INSERT INTO client (nom, prenom, email) VALUES ('Meyer', 'Chantal', 'sleger@example.org');
INSERT INTO client (nom, prenom, email) VALUES ('Léger', 'Marcel', 'ephilippe@example.net');
INSERT INTO client (nom, prenom, email) VALUES ('Guibert', 'Benoît', 'glegendre@example.net');
INSERT INTO client (nom, prenom, email) VALUES ('Coste', 'Marguerite', 'jlemoine@example.com');
INSERT INTO client (nom, prenom, email) VALUES ('Costa', 'Gabrielle', 'legrandfrancoise@example.org');
INSERT INTO client (nom, prenom, email) VALUES ('Andre', 'Eugène', 'helenejoseph@example.org');
INSERT INTO client (nom, prenom, email) VALUES ('Barre', 'Charles', 'spichon@example.com');
INSERT INTO client (nom, prenom, email) VALUES ('Rolland', 'Hélène', 'theodore93@example.net');
INSERT INTO client (nom, prenom, email) VALUES ('Besnard', 'Claire', 'dumaslaurence@example.org');
INSERT INTO client (nom, prenom, email) VALUES ('Giraud', 'Édith', 'patrickbailly@example.org');
INSERT INTO client (nom, prenom, email) VALUES ('Pineau', 'Luc', 'gpaul@example.org');

-- Événements
INSERT INTO evenement (nom, date_evenement, lieu, prix) VALUES ('L avantage de rouler sans soucis', '2026-01-09', 'Boulanger-sur-Mer', 70.23);
INSERT INTO evenement (nom, date_evenement, lieu, prix) VALUES ('Le pouvoir d atteindre vos buts à la pointe', '2026-01-02', 'Merle', 15.17);
INSERT INTO evenement (nom, date_evenement, lieu, prix) VALUES ('Le droit de changer à la pointe', '2026-01-12', 'Fleury', 109.04);
INSERT INTO evenement (nom, date_evenement, lieu, prix) VALUES ('L assurance d évoluer autrement', '2026-04-10', 'Saint Stéphanieboeuf', 88.54);
INSERT INTO evenement (nom, date_evenement, lieu, prix) VALUES ('L avantage d avancer plus rapidement', '2026-01-10', 'Ruiz', 43.25);
INSERT INTO evenement (nom, date_evenement, lieu, prix) VALUES ('La liberté d évoluer à l état pur', '2026-04-06', 'Evrard', 36.35);
INSERT INTO evenement (nom, date_evenement, lieu, prix) VALUES ('Le plaisir de changer plus facilement', '2026-01-29', 'Blondelboeuf', 26.74);
INSERT INTO evenement (nom, date_evenement, lieu, prix) VALUES ('L avantage d évoluer de manière sûre', '2026-01-14', 'Lebreton', 75.01);
INSERT INTO evenement (nom, date_evenement, lieu, prix) VALUES ('Le droit d atteindre vos buts sans soucis', '2026-03-28', 'Sainte Antoinette', 24.72);
INSERT INTO evenement (nom, date_evenement, lieu, prix) VALUES ('L avantage d innover à l état pur', '2026-04-14', 'Techer', 50.46);

-- Billets
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (38, 4, '2025-10-27');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (28, 6, '2025-10-18');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (24, 6, '2025-12-12');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (3, 5, '2025-11-19');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (43, 8, '2025-11-12');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (34, 3, '2025-12-03');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (30, 1, '2025-11-17');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (36, 9, '2025-10-20');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (16, 7, '2025-10-21');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (23, 6, '2025-11-30');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (7, 2, '2025-11-04');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (45, 1, '2025-11-03');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (48, 8, '2025-10-21');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (10, 6, '2025-11-21');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (13, 3, '2025-12-11');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (2, 3, '2025-10-27');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (36, 4, '2025-11-11');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (9, 8, '2025-12-07');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (35, 9, '2025-10-30');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (48, 4, '2025-11-01');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (9, 6, '2025-11-06');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (4, 10, '2025-12-15');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (13, 3, '2025-11-22');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (19, 5, '2025-11-22');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (24, 3, '2025-12-03');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (14, 8, '2025-10-31');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (35, 3, '2025-12-07');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (38, 8, '2025-11-24');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (50, 6, '2025-11-29');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (17, 4, '2025-11-25');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (29, 5, '2025-11-04');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (8, 10, '2025-11-20');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (25, 5, '2025-11-11');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (38, 4, '2025-12-03');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (25, 10, '2025-11-11');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (6, 4, '2025-11-17');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (43, 6, '2025-11-11');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (10, 6, '2025-12-05');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (14, 7, '2025-12-09');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (27, 10, '2025-10-25');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (19, 7, '2025-11-26');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (37, 1, '2025-11-14');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (25, 8, '2025-11-12');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (22, 5, '2025-11-24');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (22, 7, '2025-12-02');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (41, 8, '2025-10-23');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (21, 8, '2025-10-20');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (15, 4, '2025-11-25');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (13, 1, '2025-12-02');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (4, 6, '2025-10-30');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (34, 10, '2025-11-12');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (19, 1, '2025-11-04');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (18, 9, '2025-11-17');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (14, 7, '2025-12-11');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (11, 5, '2025-12-01');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (11, 7, '2025-12-16');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (24, 2, '2025-11-07');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (24, 5, '2025-10-26');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (18, 1, '2025-11-02');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (38, 7, '2025-11-01');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (1, 7, '2025-12-14');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (26, 3, '2025-10-25');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (34, 9, '2025-10-22');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (20, 1, '2025-10-21');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (28, 2, '2025-12-03');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (22, 5, '2025-11-10');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (17, 6, '2025-11-29');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (50, 7, '2025-12-13');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (47, 4, '2025-11-04');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (15, 8, '2025-11-07');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (37, 7, '2025-11-02');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (23, 6, '2025-10-23');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (40, 8, '2025-10-30');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (46, 9, '2025-12-12');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (35, 2, '2025-10-31');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (12, 1, '2025-10-20');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (24, 3, '2025-11-25');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (4, 7, '2025-11-12');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (39, 8, '2025-11-19');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (43, 10, '2025-11-19');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (26, 6, '2025-11-06');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (27, 5, '2025-11-29');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (12, 5, '2025-11-30');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (48, 6, '2025-10-24');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (40, 9, '2025-11-22');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (20, 2, '2025-11-15');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (41, 8, '2025-12-15');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (41, 4, '2025-12-01');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (29, 1, '2025-11-07');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (40, 8, '2025-10-22');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (11, 6, '2025-11-02');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (13, 7, '2025-12-06');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (15, 3, '2025-10-25');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (20, 7, '2025-11-15');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (28, 1, '2025-10-28');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (13, 1, '2025-11-16');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (28, 5, '2025-12-03');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (6, 6, '2025-10-18');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (17, 2, '2025-11-28');
INSERT INTO billet (id_client, id_evenement, date_achat) VALUES (39, 8, '2025-11-16');
