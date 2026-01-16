package model;

import java.time.LocalDateTime;

/**
 * Représentation d'un client de la billetterie.
 */
public class Client {

    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String ville;
    private LocalDateTime dateCreation;

    // Constructeur vide (utilisé par les DAO).
    public Client() {
    }

    // Constructeur complet.
    public Client(int id, String nom, String prenom, String email, String telephone,
                  String ville, LocalDateTime dateCreation) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.ville = ville;
        this.dateCreation = dateCreation;
    }

    // Constructeur simplifié sans id/date.
    public Client(String nom, String prenom, String email, String telephone, String ville) {
        this(0, nom, prenom, email, telephone, ville, null);
    }

    // Retourne l'identifiant du client.
    public int getId() {
        return id;
    }

    // Définit l'identifiant du client.
    public void setId(int id) {
        this.id = id;
    }

    // Retourne le nom du client.
    public String getNom() {
        return nom;
    }

    // Définit le nom du client.
    public void setNom(String nom) {
        this.nom = nom;
    }

    // Retourne le prénom du client.
    public String getPrenom() {
        return prenom;
    }

    // Définit le prénom du client.
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    // Retourne l'email du client.
    public String getEmail() {
        return email;
    }

    // Définit l'email du client.
    public void setEmail(String email) {
        this.email = email;
    }

    // Retourne le téléphone du client.
    public String getTelephone() {
        return telephone;
    }

    // Définit le téléphone du client.
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    // Retourne la ville du client.
    public String getVille() {
        return ville;
    }

    // Définit la ville du client.
    public void setVille(String ville) {
        this.ville = ville;
    }

    // Retourne la date de création.
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    // Définit la date de création.
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    @Override
    // Affichage lisible du client.
    public String toString() {
        return nom + " " + prenom + " (" + email + ")";
    }
}
