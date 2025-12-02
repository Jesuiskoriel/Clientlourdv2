
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


INSERT INTO statut_billet (libelle) VALUES 
('vendu'),
('reservé'),
('annulé');


INSERT INTO client (nom, prenom, email) VALUES 
('Moulin', 'Isabelle', 'isabelle.moulin1@example.org'),
('Ruiz', 'Auguste', 'auguste.ruiz2@example.com'),
('Masse', 'Célina', 'celina.masse3@example.net'),
('Perez', 'Juliette', 'juliette.perez4@example.com'),
('Guyot', 'Auguste', 'auguste.guyot5@example.net'),
('Ruiz', 'Honoré', 'honore.ruiz6@example.com'),
('Garnier', 'Zacharie', 'zacharie.garnier7@example.org'),
('Leconte', 'Jeanne', 'jeanne.leconte8@example.com'),
('Duhamel', 'Hugues', 'hugues.duhamel9@example.org'),
('Samson', 'Michelle', 'michelle.samson10@example.com'),
('Lefebvre', 'Alex', 'alex.lefebvre11@example.org'),
('Carpentier', 'Sophie', 'sophie.carpentier12@example.com'),
('Perrier', 'Pierre', 'pierre.perrier13@example.net'),
('Lévêque', 'Maurice', 'maurice.leveque14@example.com'),
('Jacquot', 'Augustin', 'augustin.jacquot15@example.com'),
('Boucher', 'Marthe', 'marthe.boucher16@example.com'),
('Blanc', 'Anne', 'anne.blanc17@example.net'),
('Hardy', 'Philippe', 'philippe.hardy18@example.org'),
('Courtois', 'Marguerite', 'marguerite.courtois19@example.org'),
('Guichard', 'Robert', 'robert.guichard20@example.com'),
('Leconte', 'Paul', 'paul.leconte21@example.net'),
('Da Costa', 'Maggie', 'maggie.dacosta22@example.com'),
('Durand', 'Corinne', 'corinne.durand23@example.com'),
('Tessier', 'Gilles', 'gilles.tessier24@example.org'),
('Normand', 'René', 'rene.normand25@example.net'),
('Legrand', 'Alfred', 'alfred.legrand26@example.net'),
('Étienne', 'Jeanne', 'jeanne.etienne27@example.com'),
('Prévost', 'Chantal', 'chantal.prevost28@example.net'),
('Vaillant', 'Zacharie', 'zacharie.vaillant29@example.com'),
('Millet', 'Célina', 'celina.millet30@example.org'),
('Lesage', 'Claudine', 'claudine.lesage31@example.net'),
('Rémy', 'Adélaïde', 'adelaide.remy32@example.net'),
('Andre', 'Corinne', 'corinne.andre33@example.org'),
('Giraud', 'Daniel', 'daniel.giraud34@example.net'),
('Martel', 'Bernard', 'bernard.martel35@example.com'),
('Blanc', 'Anne', 'anne.blanc36@example.com'),
('Berger', 'Stéphane', 'stephane.berger37@example.org'),
('Langlois', 'Danielle', 'danielle.langlois38@example.net'),
('Thierry', 'Guy', 'guy.thierry39@example.net'),
('Goncalves', 'Denis', 'denis.goncalves40@example.com'),
('Maillet', 'Arnaude', 'arnaude.maillet41@example.org'),
('Lopez', 'Noémi', 'noemi.lopez42@example.org'),
('Diaz', 'Jean', 'jean.diaz43@example.org'),
('Merle', 'Françoise', 'francoise.merle44@example.net'),
('Lévy', 'Michelle', 'michelle.levy45@example.com'),
('Laporte', 'Étienne', 'etienne.laporte46@example.net'),
('Gimenez', 'Adèle', 'adele.gimenez47@example.com'),
('Hamel', 'Théophile', 'theophile.hamel48@example.com'),
('Rey', 'Brigitte', 'brigitte.rey49@example.net'),
('Pelletier', 'Laetitia', 'laetitia.pelletier50@example.org');


INSERT INTO evenement (nom, date_event, heure, lieu, capacite, prix_base, description) VALUES
('L assurance d avancer à la pointe', '2026-01-02', '20:00:00', 'Lesage-sur-Marchand', 500, 35.88, 'Un séminaire inspirant sur les nouvelles technologies.'),
('La possibilité d atteindre vos buts à l état pur', '2025-11-12', '18:30:00', 'Sainte Alexandre', 300, 65.60, 'Conférence sur la réussite personnelle.'),
('Le confort de rouler à la pointe', '2025-12-12', '19:00:00', 'De Sousa-sur-Verdier', 200, 65.31, 'Présentation d’innovations automobiles.'),
('Le pouvoir de louer plus rapidement', '2025-12-16', '15:00:00', 'Weiss-sur-Fouquet', 150, 23.77, 'Salon de l’immobilier.'),
('L assurance de rouler autrement', '2026-01-09', '21:00:00', 'LesageVille', 1000, 111.32, 'Événement automobile international.'),
('La liberté d innover naturellement', '2025-12-04', '09:00:00', 'Lemoineboeuf', 600, 116.31, 'Forum sur l’innovation verte.'),
('Le plaisir de rouler naturellement', '2025-11-06', '14:00:00', 'Sainte Martin', 400, 30.40, 'Salon éco-mobilité.'),
('L assurance d innover en toute tranquilité', '2026-01-18', '19:30:00', 'Saint Alfred', 250, 112.62, 'Conférence sur la sécurité technologique.'),
('La possibilité d évoluer de manière sûre', '2025-11-06', '16:00:00', 'Henry', 800, 27.27, 'Salon de la cybersécurité.'),
('La possibilité d évoluer autrement', '2025-12-05', '10:00:00', 'Royer-sur-Pinto', 300, 55.81, 'Atelier de développement personnel.');


INSERT INTO billet (code_unique, fk_client, fk_evenement, fk_statut, date_achat, prix_paye) VALUES
('BIL001', 44, 4, 1, '2025-09-13', 23.77),
('BIL002', 42, 9, 2, '2025-08-20', 27.27),
('BIL003', 18, 9, 1, '2025-10-01', 27.27),
('BIL004', 38, 1, 1, '2025-10-04', 35.88),
('BIL005', 42, 8, 2, '2025-08-25', 112.62),
('BIL006', 30, 1, 1, '2025-09-19', 35.88),
('BIL007', 11, 1, 1, '2025-08-15', 35.88),
('BIL008', 2, 5, 3, '2025-10-05', 111.32),
('BIL009', 39, 3, 1, '2025-08-20', 65.31),
('BIL010', 40, 10, 2, '2025-09-11', 55.81);


INSERT INTO paiement (fk_billet, montant, mode_paiement, date_paiement, reference) VALUES
(1, 23.77, 'carte bancaire', '2025-09-13', 'PAY001'),
(2, 27.27, 'paypal', '2025-08-20', 'PAY002'),
(3, 27.27, 'espèces', '2025-10-01', 'PAY003'),
(4, 35.88, 'carte bancaire', '2025-10-04', 'PAY004'),
(5, 112.62, 'paypal', '2025-08-25', 'PAY005'),
(6, 35.88, 'espèces', '2025-09-19', 'PAY006'),
(7, 35.88, 'carte bancaire', '2025-08-15', 'PAY007'),
(8, 111.32, 'virement', '2025-10-05', 'PAY008'),
(9, 65.31, 'carte bancaire', '2025-08-20', 'PAY009'),
(10, 55.81, 'paypal', '2025-09-11', 'PAY010');
