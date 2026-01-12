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

    public Evenement() {
    }

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

    public LocalDate getDateEvent() {
        return dateEvent;
    }

    public void setDateEvent(LocalDate dateEvent) {
        this.dateEvent = dateEvent;
    }

    public LocalTime getHeure() {
        return heure;
    }

    public void setHeure(LocalTime heure) {
        this.heure = heure;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public double getPrixBase() {
        return prixBase;
    }

    public void setPrixBase(double prixBase) {
        this.prixBase = prixBase;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return nom + " - " + lieu;
    }
}
