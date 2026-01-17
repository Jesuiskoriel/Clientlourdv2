# 🎟️ Projet Client Lourd – Application de Billetterie

## 📌 Aperçu (en mots simples)
Ce projet est une application de billetterie "sur ordinateur" (pas un site web).
Elle permet :
- de créer un compte et se connecter,
- de recevoir un code par email pour confirmer la connexion,
- d'acheter des billets,
- et d'administrer clients, evenements, billets et comptes.

L'objectif est d'avoir un exemple complet de gestion de donnees : creation, lecture, modification, suppression.

---

## 📂 Arborescence du projet
(resume)
- `src/` : le code de l'application
- `src/views/` : les ecrans (interface JavaFX)
- `src/controllers/` : la logique liee aux ecrans
- `src/dao/` : la couche qui parle a la base de donnees
- `src/model/` : les objets metier (Client, Billet, etc.)
- `data.sql` / `basededonnees/` : scripts pour la base MySQL
- `pom.xml` : configuration Maven (construction du projet)
- `docker-compose.yml` : demarrer la base via Docker

## 🛠️ Technologies utilisees (explication simple)
- **Java** : langage principal de l'application.
- **JavaFX** : outil pour creer les ecrans (fenetres, boutons, tableaux).
- **MySQL** : la base de donnees (ou sont ranges clients, evenements, billets).
- **DAO** : "Data Access Object", une classe qui sert d'intermediaire entre Java et la base.
- **Maven** : outil qui telecharge les dependances et compile le projet.
- **Docker** : outil pour lancer une base MySQL prete a l'emploi, sans installation manuelle.
- **PlantUML** : pour dessiner des schemas UML.
- **Python + Faker** : pour creer des donnees de test.
- **Git / GitHub** : pour versionner le code.

---

## 📊 Generation des donnees
Le script `generate_data.py` cree automatiquement :

- 50 clients  
- 10 événements  
- 100 billets  

Les donnees produites sont sauvegardees dans `data.sql` et peuvent etre importees dans MySQL Workbench.

---

## 🔗 Acces aux donnees via Java (DAO)

### Modèles :
- `Client.java`
- `User.java`
- `Achat.java`
- `Evenement.java`
- `Billet.java`
- `StatutBillet.java`
- `Paiement.java`

### DAO (classes qui parlent a la base) :
- `ClientDAO.java`
- `UserDAO.java`
- `AchatDAO.java`
- `EvenementDAO.java`
- `BilletDAO.java`
- `StatutBilletDAO.java`
- `PaiementDAO.java`

### Ce que font les DAO (en clair) :
- lire les donnees (ex: liste des clients)
- trouver un element par id
- ajouter, modifier, supprimer (CRUD)
- respecter les liens entre tables (ex: un billet est lie a un client)

La connexion MySQL est centralisee dans `Database.java`.

---

## 🔐 Fonctionnement global (explication simple)

### 1) Connexion + code de verification (2FA)
- Ecran d'accueil : Connexion / Creation de compte.
- Le mot de passe est transforme en "hash" (une version protegee).
- Apres connexion, un code est envoye par email.
- Si le code est bon, l'utilisateur accede a l'application.

### 2) Interface Admin
Menu “Gestion” :
- **Clients** : CRUD complet + détails (billets/achats).
- **Événements** : CRUD + liste des événements existants.
- **Billets** : suivi des billets + statuts.
- **Comptes** : liste des utilisateurs, suppression possible (sauf admin).

### 3) Boutique Utilisateur
- Solde fictif stocké en base (`utilisateur.solde`).
- Cartes d’événements, achat de billets, historique d’achats.
- QR code fictif genere pour chaque achat.
- Recharge du solde via un **paiement fictif** (formulaire carte → "paiement accepte").

---

## ▶️ Lancer l'application

### Option A: Maven (le plus simple)
Si Maven est installe, vous pouvez compiler plus facilement.
Exemple :
```bash
mvn clean compile
```
Ensuite lancez l'application via votre IDE ou une commande Java (voir plus bas).

### Via VS Code (recommande)
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

## 🧩 Installation complete (pas a pas, tres simple)

1) **Installer Java**
- Version 17 ou plus.
- Verifiez : `java -version`

2) **Preparer la base MySQL**
- Creez la base `billeterie`.
- Importez `data.sql` (tables + donnees de demo).

Alternative simple avec Docker (si installe) :
```bash
docker compose up -d
```
Cela demarre une base MySQL automatiquement.

3) **Verifier les dependances**
- Le dossier `lib/` doit contenir :
  - JavaFX (`javafx.controls`, `javafx.fxml`, `javafx.graphics`, `javafx.swing`)
  - `mysql-connector-j-9.4.0.jar`

4) **Compiler et lancer**
- Utilisez VS Code (Run) ou les commandes terminal indiquées plus haut.

5) **Configurer l'OTP par email (optionnel)**
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
Sans ces variables, l'envoi d'email est desactive.

---

## 🧰 Depannage rapide

### L'app ne demarre pas / JavaFX introuvable
- Verifiez `JAVA_HOME` (macOS) : `export JAVA_HOME=/opt/homebrew/opt/openjdk`
- Assurez-vous que `lib/` contient les JAR JavaFX et MySQL.
- Recompilez : `javac ...` puis relancez `java ...`.

### Les emails OTP ne partent pas
- Utilisez un **mot de passe d'application** Gmail.
- Verifiez les variables `SMTP_*`.
- Pour tester la connexion :
  `openssl s_client -connect smtp.gmail.com:465 -crlf -quiet`
  ou `openssl s_client -connect smtp.gmail.com:587 -starttls smtp -crlf -quiet`

### Les evenements n'apparaissent pas
- Si votre table a les colonnes `date_evenement` / `prix`, le DAO s'adapte.
- Assurez-vous que des donnees existent dans la table `evenement`.

---

## 🗂️ Structure des tables (resume)

### utilisateur
- `id_utilisateur` (PK), `nom_complet`, `email` (unique), `telephone`, `mot_de_passe` (hash), `solde`, `is_admin`, `date_creation`

### evenement
- `id_evenement` (PK), `nom`, `date_event` (ou `date_evenement` selon vos données), `heure`, `lieu`, `capacite`, `prix_base` (ou `prix`), `description`

### achat_utilisateur
- `id_achat` (PK), `id_utilisateur` (FK), `id_evenement` (FK), `prix_achat`, `date_achat`

### client / billet / statut_billet / security_question / security_answer / otp_token
- Tables de gestion (clients, billets, statut, questions secrètes, OTP).

---

## ❓ Lexique (pour debutants)
- **Maven** : outil qui telecharge les librairies et compile le projet.
- **Docker** : outil pour lancer des services (ex: MySQL) sans installation manuelle.
- **DAO** : classe qui fait le lien entre le code Java et la base de donnees.
- **CRUD** : Create, Read, Update, Delete (creer, lire, modifier, supprimer).
- **OTP** : code temporaire recu par email pour verifier la connexion.
- **Hash** : transformation d'un mot de passe en valeur securisee.

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
