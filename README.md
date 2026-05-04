# Projet Client Lourd - Billetterie (JavaFX)

Application desktop de gestion de billetterie (client lourd) avec :
- authentification (inscription/connexion)
- OTP par email (si SMTP configuré)
- espace admin (gestion clients/événements/billets/comptes)
- espace utilisateur (achat de billets, historique, solde fictif)

## Stack technique
- Java 17+
- JavaFX
- MySQL 8
- Maven
- Docker (pour la base de données)

## Arborescence utile
- `src/` : code source Java/JavaFX
- `basededonnees/` : scripts SQL (schéma + données)
- `docker/init-auth.sql` : tables d'authentification + compte admin de démo
- `run-local.sh` : lancement local rapide
- `pom.xml` : build Maven

## Prérequis
- Java 17 ou plus
- Maven
- Docker (si vous hébergez MySQL en conteneur)

## Configuration `.env`
Le projet lit d’abord les variables d’environnement système, puis `.env`.

Exemple minimal :

```env
DB_URL=jdbc:mysql://13.60.24.212:3306/billeterie?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USER=root
DB_PASSWORD=rootpass123

SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_STARTTLS=true
SMTP_SSL=false
SMTP_USER=votre_email@gmail.com
SMTP_PASS=mot_de_passe_app
SMTP_FROM=votre_email@gmail.com
SMTP_TIMEOUT_MS=60000
```

Ce mode est prévu pour une connexion directe à une base distante (plug and play), sans tunnel SSH.

## Initialiser la base de données

### Option A - depuis un serveur MySQL local

```bash
mysql -u root -p < basededonnees/data.sql
mysql -u root -p < docker/init-auth.sql
```


## Lancer l'application

```bash
./run-local.sh
```

## Compte admin de démonstration
Compte par défaut non personnel :
- email : `admin.demo@billeterie.local`
- mot de passe : `AdminDemo2026!`

Ce compte est défini dans :
- `src/controllers/AuthController.java`
- `docker/init-auth.sql`
- `data.sql`

## Générer le JAR de livraison

```bash
mvn -DskipTests clean package
cp target/clientlourdv2-1.0.0-all.jar target/billeterie.jar
```

Lancer le jar :

```bash
java -jar target/billeterie.jar
```

## Dépannage rapide

### L'app ne s'ouvre pas / fenêtre force quit
Cause fréquente : connexion MySQL impossible.
Vérifiez :
- `DB_URL` JDBC correct
- identifiants DB corrects
- port MySQL `3306` ouvert dans le Security Group AWS (idéalement limité à votre IP publique)

### `Communications link failure`
- MySQL non joignable (réseau/port)
- vérifiez l'IP, le port `3306` et les règles réseau AWS

### OTP non reçu
- vérifiez `SMTP_*`
- utilisez un mot de passe d'application Gmail

## Sécurité
- ne jamais commiter `.env`
- ne pas publier d’identifiants personnels
- utiliser des comptes de démo pour les présentations

## Auteur
Lajimi Jhawad - BTS SIO SLAM
