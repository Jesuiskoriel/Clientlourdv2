package model;

import java.time.LocalDateTime;

/**
 * Représente un utilisateur pouvant se connecter à l'application.
 */
public class User {

    private int id;
    private String nomComplet;
    private String email;
    private String telephone;
    private String passwordHash;
    private LocalDateTime dateCreation;
    private double solde;
    private boolean admin;

    // Constructeur vide (utilisé par les DAO).
    public User() {
    }

    // Constructeur de base pour création d'un compte.
    public User(String nomComplet, String email, String telephone, String passwordHash) {
        this.nomComplet = nomComplet;
        this.email = email;
        this.telephone = telephone;
        this.passwordHash = passwordHash;
        this.solde = 500.0;
    }

    // Retourne l'identifiant utilisateur.
    public int getId() {
        return id;
    }

    // Définit l'identifiant utilisateur.
    public void setId(int id) {
        this.id = id;
    }

    // Retourne le nom complet.
    public String getNomComplet() {
        return nomComplet;
    }

    // Définit le nom complet.
    public void setNomComplet(String nomComplet) {
        this.nomComplet = nomComplet;
    }

    // Retourne l'email.
    public String getEmail() {
        return email;
    }

    // Définit l'email.
    public void setEmail(String email) {
        this.email = email;
    }

    // Retourne le téléphone.
    public String getTelephone() {
        return telephone;
    }

    // Définit le téléphone.
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    // Retourne le hash du mot de passe.
    public String getPasswordHash() {
        return passwordHash;
    }

    // Définit le hash du mot de passe.
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    // Retourne la date de création du compte.
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    // Définit la date de création du compte.
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    // Retourne le solde disponible.
    public double getSolde() {
        return solde;
    }

    // Définit le solde disponible.
    public void setSolde(double solde) {
        this.solde = solde;
    }

    // Indique si l'utilisateur est admin.
    public boolean isAdmin() {
        return admin;
    }

    // Définit le rôle admin.
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
