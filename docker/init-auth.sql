USE billeterie;

CREATE TABLE IF NOT EXISTS utilisateur (
    id_utilisateur INT AUTO_INCREMENT PRIMARY KEY,
    prenom VARCHAR(120),
    nom VARCHAR(120),
    nom_complet VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    telephone VARCHAR(40),
    mot_de_passe VARCHAR(255) NOT NULL,
    solde DECIMAL(10,2) DEFAULT 500,
    is_admin TINYINT(1) DEFAULT 0,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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

CREATE TABLE IF NOT EXISTS achat_utilisateur (
    id_achat INT AUTO_INCREMENT PRIMARY KEY,
    id_utilisateur INT NOT NULL,
    id_evenement INT NOT NULL,
    prix_achat DECIMAL(10,2) NOT NULL,
    date_achat TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_achat_user FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id_utilisateur),
    CONSTRAINT fk_achat_event FOREIGN KEY (id_evenement) REFERENCES evenement(id_evenement)
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

INSERT INTO utilisateur (prenom, nom, nom_complet, email, telephone, mot_de_passe, solde, is_admin)
VALUES ('Administrateur', 'Administrateur', 'Administrateur', 'jhawadlajimi@hotmail.com', '0000000000',
'63d5c90459c70cd1fc7d9372d0e60acd6e16b25fa8b83bb06291bc5957ab991c', 0, 1)
ON DUPLICATE KEY UPDATE email = email;
