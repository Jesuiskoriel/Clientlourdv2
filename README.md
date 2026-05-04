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
DB_URL=jdbc:mysql://127.0.0.1:3307/billeterie?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
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

## Initialiser la base de données

### Option A - depuis un serveur MySQL local

```bash
mysql -u root -p < basededonnees/data.sql
mysql -u root -p < docker/init-auth.sql
```

### Option B - MySQL dans Docker sur serveur distant (AWS)
Depuis votre machine locale :

```bash
cd ".../Clientlourdv2"
KEY="$HOME/Downloads/Clientlourd.pem"
IP_EC2="13.60.24.212"

ssh -i "$KEY" admin@"$IP_EC2" "sudo docker exec -i billetterie-mysql mysql -uroot -prootpass123 billeterie" < basededonnees/data.sql
ssh -i "$KEY" admin@"$IP_EC2" "sudo docker exec -i billetterie-mysql mysql -uroot -prootpass123 billeterie" < docker/init-auth.sql
```

## Lancer l'application

```bash
./run-local.sh
```

## Mode de connexion AWS recommandé (tunnel SSH)
Si le port MySQL `3306` n'est pas ouvert publiquement, créez un tunnel :

Terminal A :

```bash
ssh -i "$HOME/Downloads/Clientlourd.pem" -N -L 3307:127.0.0.1:3306 admin@13.60.24.212
```

Terminal B :
- gardez `DB_URL` sur `127.0.0.1:3307` dans `.env`
- lancez `./run-local.sh`

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
- tunnel SSH actif
- `DB_URL` JDBC correct
- identifiants DB corrects

### `Communications link failure`
- MySQL non joignable (réseau/port)
- utilisez le tunnel SSH au lieu d'ouvrir `3306` publiquement

### OTP non reçu
- vérifiez `SMTP_*`
- utilisez un mot de passe d'application Gmail

## Sécurité
- ne jamais commiter `.env`
- ne pas publier d’identifiants personnels
- utiliser des comptes de démo pour les présentations

## Auteur
Lajimi Jhawad - BTS SIO SLAM
