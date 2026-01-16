package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Evenement {

    private int id;
    private String nom;
    private LocalDate dateEvent;
    private LocalTime heure;
    private String lieu;
    private int capacite;
    private double prixBase;
    private String description;

    // Constructeur vide (utilisé par les DAO).
    public Evenement() {
    }

    // Constructeur complet.
    public Evenement(int id, String nom, LocalDate dateEvent, LocalTime heure, String lieu,
                     int capacite, double prixBase, String description) {
        this.id = id;
        this.nom = nom;
        this.dateEvent = dateEvent;
        this.heure = heure;
        this.lieu = lieu;
        this.capacite = capacite;
        this.prixBase = prixBase;
        this.description = description;
    }

    // Retourne l'identifiant de l'événement.
    public int getId() {
        return id;
    }

    // Définit l'identifiant de l'événement.
    public void setId(int id) {
        this.id = id;
    }

    // Retourne le nom de l'événement.
    public String getNom() {
        return nom;
    }

    // Définit le nom de l'événement.
    public void setNom(String nom) {
        this.nom = nom;
    }

    // Retourne la date de l'événement.
    public LocalDate getDateEvent() {
        return dateEvent;
    }

    // Définit la date de l'événement.
    public void setDateEvent(LocalDate dateEvent) {
        this.dateEvent = dateEvent;
    }

    // Retourne l'heure de l'événement.
    public LocalTime getHeure() {
        return heure;
    }

    // Définit l'heure de l'événement.
    public void setHeure(LocalTime heure) {
        this.heure = heure;
    }

    // Retourne le lieu de l'événement.
    public String getLieu() {
        return lieu;
    }

    // Définit le lieu de l'événement.
    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    // Retourne la capacité maximale.
    public int getCapacite() {
        return capacite;
    }

    // Définit la capacité maximale.
    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    // Retourne le prix de base.
    public double getPrixBase() {
        return prixBase;
    }

    // Définit le prix de base.
    public void setPrixBase(double prixBase) {
        this.prixBase = prixBase;
    }

    // Retourne la description.
    public String getDescription() {
        return description;
    }

    // Définit la description.
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    // Affichage lisible de l'événement.
    public String toString() {
        return nom + " - " + lieu;
    }
}
