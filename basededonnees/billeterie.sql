DROP DATABASE IF EXISTS billeterie;
CREATE DATABASE billeterie CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE billeterie;


CREATE TABLE client (
    id_client INT AUTO_INCREMENT PRIMARY KEY,
    prenom VARCHAR(100) NOT NULL,
    nom VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    telephone VARCHAR(30),
    ville VARCHAR(100),
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;


CREATE TABLE evenement (
    id_evenement INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(200) NOT NULL,
    date_event DATE NOT NULL,
    heure TIME NOT NULL,
    lieu VARCHAR(200) NOT NULL,
    capacite INT DEFAULT 0,
    prix_base DECIMAL(8,2) NOT NULL,
    description TEXT
) ENGINE=InnoDB;


CREATE TABLE statut_billet (
    id_statut INT AUTO_INCREMENT PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL
) ENGINE=InnoDB;


CREATE TABLE billet (
    id_billet INT AUTO_INCREMENT PRIMARY KEY,
    code_unique VARCHAR(100) NOT NULL UNIQUE,
    fk_client INT NOT NULL,
    fk_evenement INT NOT NULL,
    fk_statut INT NOT NULL,
    date_achat DATETIME DEFAULT CURRENT_TIMESTAMP,
    prix_paye DECIMAL(8,2),
    CONSTRAINT fk_billet_client FOREIGN KEY (fk_client) REFERENCES client(id_client) ON DELETE CASCADE,
    CONSTRAINT fk_billet_evenement FOREIGN KEY (fk_evenement) REFERENCES evenement(id_evenement) ON DELETE CASCADE,
    CONSTRAINT fk_billet_statut FOREIGN KEY (fk_statut) REFERENCES statut_billet(id_statut)
) ENGINE=InnoDB;


CREATE TABLE paiement (
    id_paiement INT AUTO_INCREMENT PRIMARY KEY,
    fk_billet INT NOT NULL,
    montant DECIMAL(8,2) NOT NULL,
    mode_paiement VARCHAR(50),
    date_paiement DATETIME DEFAULT CURRENT_TIMESTAMP,
    reference VARCHAR(200),
    CONSTRAINT fk_paiement_billet FOREIGN KEY (fk_billet) REFERENCES billet(id_billet) ON DELETE CASCADE
) ENGINE=InnoDB;
