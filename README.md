# 🎟️ Projet Client Lourd – Application de Billetterie

## 📌 Aperçu
Ce projet a été réalisé dans le cadre du TP de **client lourd** du BTS SIO (SLAM).  
Il a pour objectif de concevoir une base de données pour une billetterie, de générer automatiquement des données avec Faker, puis de manipuler ces données grâce à des classes Java utilisant des DAO (CRUD complet).

---

## 📂 Arborescence du projet
---

## 🛠️ Technologies utilisées
- **PlantUML** – pour la modélisation UML  
- **MySQL** – base de données relationnelle  
- **Python + Faker** – génération automatique de données  
- **Java (models + DAO)** – accès aux données  
- **Git / GitHub** – versionnement et suivi du projet

---

## 📊 Génération des données
Le script `generate_data.py` génère automatiquement :

- 50 clients  
- 10 événements  
- 100 billets  

Les données produites sont sauvegardées dans `data.sql` et peuvent être importées dans MySQL Workbench.

---

## 🔗 Accès aux données via Java

### Modèles :
- `Client.java`
- `Evenement.java`
- `Billet.java`
- `StatutBillet.java`
- `Paiement.java`

### DAO :
- `ClientDAO.java`
- `EvenementDAO.java`
- `BilletDAO.java`
- `StatutBilletDAO.java`
- `PaiementDAO.java`

### Fonctionnalités des DAO :
- Récupération de tous les enregistrements
- Recherche par ID
- Ajout, modification et suppression (CRUD)
- Gestion de l’intégrité via les clés étrangères et la table `statut_billet`

La connexion MySQL est centralisée dans `Database.java`.

---

## 👤 Auteur
**Lajimi Jhawad** – BTS SIO SLAM  
📅 Année : 2025
