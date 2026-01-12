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

    public Client() {
    }

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

    public Client(String nom, String prenom, String email, String telephone, String ville) {
        this(0, nom, prenom, email, telephone, ville, null);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    @Override
    public String toString() {
        return nom + " " + prenom + " (" + email + ")";
    }
}
