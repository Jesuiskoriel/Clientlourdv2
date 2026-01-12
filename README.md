# 🎟️ Projet Client Lourd – Application de Billetterie

## 📌 Aperçu
Application JavaFX de billetterie (client lourd) : authentification avec 2FA par email, boutique de billets, gestion admin (clients, événements, billets, comptes), et base MySQL pilotée via DAO.  
Le projet est conçu pour illustrer un CRUD complet + un vrai flux utilisateur (inscription → connexion → 2FA → achat).

---

## 📂 Arborescence du projet
---

## 🛠️ Technologies utilisées
- **PlantUML** – pour la modélisation UML  
- **MySQL** – base de données relationnelle  
- **Python + Faker** – génération automatique de données  
- **Java (models + DAO)** – accès aux données  
- **JavaFX** – interface utilisateur  
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
- `User.java`
- `Achat.java`
- `Evenement.java`
- `Billet.java`
- `StatutBillet.java`
- `Paiement.java`

### DAO :
- `ClientDAO.java`
- `UserDAO.java`
- `AchatDAO.java`
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

## 🔐 Fonctionnement global

### 1) Authentification + 2FA
- Écran d’accueil : Connexion / Création de compte.
- Mot de passe haché côté application (SHA-256 via `PasswordUtils`).
- Après connexion, un code OTP est généré et envoyé par email (SMTP).
- Validation OTP → accès à l’interface principale.

### 2) Interface Admin
Menu “Gestion” :
- **Clients** : CRUD complet + détails (billets/achats).
- **Événements** : CRUD + liste des événements existants.
- **Billets** : suivi des billets + statuts.
- **Comptes** : liste des utilisateurs, suppression possible (sauf admin).

### 3) Boutique Utilisateur
- Solde fictif stocké en base (`utilisateur.solde`).
- Cartes d’événements, achat de billets, historique d’achats.
- QR code fictif généré pour chaque achat.
- Recharge du solde via un **paiement fictif** (formulaire carte → “paiement accepté”).

---

## ▶️ Lancer l’application

### Via VS Code (recommandé)
Utilisez le bouton **Run JavaFX App** (configuration dans `.vscode/launch.json`).

### macOS (terminal)
```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk
find src -name '*.java' > /tmp/sources.txt
"$JAVA_HOME/bin/javac" \
  --module-path lib \
  --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.swing \
  -d out @/tmp/sources.txt

"$JAVA_HOME/bin/java" \
  --module-path lib \
  --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.swing \
  -cp "out:src:lib/mysql-connector-j-9.4.0.jar" \
  App
```

### Windows (PowerShell)
```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-17"
Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName } | Set-Content .\sources.txt
& "$env:JAVA_HOME\bin\javac.exe" `
  --module-path lib `
  --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.swing `
  -d out @sources.txt

& "$env:JAVA_HOME\bin\java.exe" `
  --module-path lib `
  --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.swing `
  -cp "out;src;lib\mysql-connector-j-9.4.0.jar" `
  App
```

---

## 🧩 Installation complète (pas à pas)

1) **Installer Java**
- JDK 17+ recommandé (ex. OpenJDK via Homebrew).
- Vérifiez : `java -version`

2) **Préparer la base MySQL**
- Créez la base `billeterie`.
- Importez `data.sql` (tables + données de démo).

3) **Vérifier les dépendances**
- Le dossier `lib/` doit contenir :
  - JavaFX (`javafx.controls`, `javafx.fxml`, `javafx.graphics`, `javafx.swing`)
  - `mysql-connector-j-9.4.0.jar`

4) **Compiler et lancer**
- Utilisez VS Code (Run) ou les commandes terminal indiquées plus haut.

5) **Configurer l’OTP par email (optionnel)**
- Voir la section SMTP ci-dessous.

---

## ✉️ Configuration SMTP (OTP par email)
Variables d’environnement :
```
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_STARTTLS=true
SMTP_SSL=false
SMTP_USER=votre_email@gmail.com
SMTP_PASS=mot_de_passe_app
SMTP_FROM=votre_email@gmail.com
SMTP_TIMEOUT_MS=60000
```
Sans ces variables, l’envoi d’email est désactivé.

---

## 🧰 Dépannage rapide

### L’app ne démarre pas / JavaFX introuvable
- Vérifiez `JAVA_HOME` (macOS) : `export JAVA_HOME=/opt/homebrew/opt/openjdk`
- Assurez-vous que `lib/` contient les JAR JavaFX et MySQL.
- Recompilez : `javac ...` puis relancez `java ...`.

### Les emails OTP ne partent pas
- Utilisez un **mot de passe d’application** Gmail.
- Vérifiez les variables `SMTP_*`.
- Pour tester la connexion :  
  `openssl s_client -connect smtp.gmail.com:465 -crlf -quiet`  
  ou `openssl s_client -connect smtp.gmail.com:587 -starttls smtp -crlf -quiet`

### Les événements n’apparaissent pas
- Si votre table a les colonnes `date_evenement` / `prix`, le DAO gère déjà l’héritage (`COALESCE`).  
- Assurez-vous que des données existent dans la table `evenement`.

---

## 🗂️ Structure des tables (résumé)

### utilisateur
- `id_utilisateur` (PK), `nom_complet`, `email` (unique), `telephone`, `mot_de_passe` (hash), `solde`, `is_admin`, `date_creation`

### evenement
- `id_evenement` (PK), `nom`, `date_event` (ou `date_evenement` selon vos données), `heure`, `lieu`, `capacite`, `prix_base` (ou `prix`), `description`

### achat_utilisateur
- `id_achat` (PK), `id_utilisateur` (FK), `id_evenement` (FK), `prix_achat`, `date_achat`

### client / billet / statut_billet / security_question / security_answer / otp_token
- Tables de gestion (clients, billets, statut, questions secrètes, OTP).

---

## ❓ FAQ

**Q : Pourquoi mon OTP n’arrive pas ?**  
R : Vérifiez `SMTP_*` + mot de passe d’application Gmail, et testez la connexion SMTP (openssl).  

**Q : Je ne vois pas les événements existants.**  
R : Assurez-vous que la table `evenement` contient des données et que les colonnes correspondent. Le DAO gère déjà `date_evenement`/`prix` via `COALESCE`.  

**Q : Pourquoi “Run JavaFX App” ne trouve pas JavaFX ?**  
R : Vérifiez que `lib/` contient bien les JAR JavaFX et que le `--module-path lib` est correct.  

**Q : Le solde augmente sans vrai paiement ?**  
R : C’est volontaire : le paiement carte est fictif (simulation d’acceptation).

---

## 🔐 Authentification & boutique (détail)

Au démarrage, l’application affiche un écran d’accueil avec deux choix :

- **Connexion** : ouvre le formulaire de login et charge l’interface principale après validation.
- **Créer un compte** : formulaire avec nom, email, numéro et mot de passe (haché en SHA-256 via `PasswordUtils`) enregistré dans MySQL.
- **Espace admin** : bouton en bas à droite qui préremplit la connexion avec l’administrateur (`admin_email` / `admin_password`) et ouvre l’interface de gestion complète.

Après inscription ou connexion en tant qu’utilisateur, l’application redirige automatiquement vers une boutique de billets (`store.fxml`) :

- Solde fictif initial de **500 €** sur chaque compte (`solde` enregistré dans la table `utilisateur`).
- Liste des événements disponibles et bouton “Acheter ce billet” qui déduit le solde, enregistre l’achat (`achat_utilisateur`) et affiche l’historique dans le panneau de droite.
- Bouton “Se déconnecter” pour revenir vers l’écran d’authentification.

### Préparation de la table

Importez le bloc suivant (déjà présent dans `data.sql`) dans votre base `billeterie` :

```sql
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

CREATE TABLE IF NOT EXISTS achat_utilisateur (
    id_achat INT AUTO_INCREMENT PRIMARY KEY,
    id_utilisateur INT NOT NULL,
    id_evenement INT NOT NULL,
    prix_achat DECIMAL(10,2) NOT NULL,
    date_achat TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

Un administrateur par défaut est inséré par `data.sql` (ou automatiquement au lancement si l’email n’existe pas). Créez ensuite d’autres comptes via l’interface pour profiter du flux boutique. Les mots de passe sont hachés côté application, utilisez donc l’écran “Créer un compte” pour générer les empreintes.

---

## 👤 Auteur
**Lajimi Jhawad** – BTS SIO SLAM  
📅 Année : 2025
