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

    public User() {
    }

    public User(String nomComplet, String email, String telephone, String passwordHash) {
        this.nomComplet = nomComplet;
        this.email = email;
        this.telephone = telephone;
        this.passwordHash = passwordHash;
        this.solde = 500.0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomComplet() {
        return nomComplet;
    }

    public void setNomComplet(String nomComplet) {
        this.nomComplet = nomComplet;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public double getSolde() {
        return solde;
    }

    public void setSolde(double solde) {
        this.solde = solde;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
