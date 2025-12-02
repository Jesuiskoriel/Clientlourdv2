# Projet Client Lourd - Billetterie

## Description
Ce projet a été réalisé dans le cadre du TP de **client lourd** pour le BTS SIO SLAM.  
L’objectif est de modéliser, créer et alimenter une base de données de billetterie d’événements, puis de générer automatiquement des données avec Faker.  
Le projet inclut également des classes Java pour accéder à la base de données via des DAO (CRUD complet).

## Structure du projet
- `modélisation/billeterie.puml` → Diagramme UML de la base de données  
- `basededonnees/billeterie.sql` → Script SQL de création de la base  
- `basededonnees/generate_data.py` → Script Python utilisant Faker pour générer les données  
- `basededonnees/data.sql` → Fichier SQL contenant les données générées  
- `src/` → Classes Java modèles et DAO pour l’accès à la base de données

## Technologies utilisées
- PlantUML (modélisation)
- MySQL (base de données)
- Python + Faker (génération de données)
- Java (classes modèles et DAO pour l’accès à la DB)
- Git / GitHub (versionnement)

## Génération des données

Le fichier `generate_data.py` utilise la librairie **Faker** pour créer automatiquement :
- 50 clients
- 10 événements
- 100 billets

Les données sont enregistrées dans le fichier `data.sql` et peuvent être importées dans la base de données via MySQL Workbench.

## Accès aux données

Les classes Java sont divisées en :
- Modèles : `Client.java`, `Evenement.java`, `Billet.java`, `StatutBillet.java`, `Paiement.java`
- DAO : `ClientDAO.java`, `EvenementDAO.java`, `BilletDAO.java`, `StatutBilletDAO.java`, `PaiementDAO.java`  

Chaque DAO permet :
- Récupérer tous les enregistrements ou un enregistrement spécifique par ID
- Ajouter, modifier et supprimer des enregistrements
- Assurer l’intégrité avec les clés étrangères et la table `statut_billet`

`Database.java` gère la connexion à la base de données.

## Auteur
**Meriem Kachaf** – BTS SIO SLAM  
Professeur : Rémi Durand  
Année : 2025
