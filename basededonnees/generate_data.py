from faker import Faker
import random
from datetime import datetime, timedelta


fake = Faker('fr_FR')


output_file = "data.sql"


NB_CLIENTS = 50
NB_EVENEMENTS = 10
NB_BILLETS = 100


clients_sql = []
for i in range(NB_CLIENTS):
    nom = fake.last_name().replace("'", " ")
    prenom = fake.first_name().replace("'", " ")
    email = fake.email().replace("'", " ")
    clients_sql.append(f"INSERT INTO client (nom, prenom, email) VALUES ('{nom}', '{prenom}', '{email}');")


evenements_sql = []
for i in range(NB_EVENEMENTS):
    nom = fake.catch_phrase().replace("'", " ")
    date_evenement = fake.date_between(start_date="+10d", end_date="+120d")
    lieu = fake.city().replace("'", " ")
    prix = round(random.uniform(15, 120), 2)
    evenements_sql.append(f"INSERT INTO evenement (nom, date_evenement, lieu, prix) VALUES ('{nom}', '{date_evenement}', '{lieu}', {prix});")


billets_sql = []
for i in range(NB_BILLETS):
    id_client = random.randint(1, NB_CLIENTS)
    id_evenement = random.randint(1, NB_EVENEMENTS)
    date_achat = fake.date_between(start_date="-60d", end_date="today")
    billets_sql.append(f"INSERT INTO billet (id_client, id_evenement, date_achat) VALUES ({id_client}, {id_evenement}, '{date_achat}');")


with open(output_file, "w", encoding="utf-8") as f:
    f.write("-- Données générées automatiquement avec Faker\n")
    f.write("USE billeterie;\n\n")
    
    f.write("-- Clients\n")
    f.write("\n".join(clients_sql))
    f.write("\n\n-- Événements\n")
    f.write("\n".join(evenements_sql))
    f.write("\n\n-- Billets\n")
    f.write("\n".join(billets_sql))  

print(f" Fichier '{output_file}' généré avec succès ({NB_CLIENTS} clients, {NB_EVENEMENTS} événements, {NB_BILLETS} billets) !")
