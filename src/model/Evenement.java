package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class Evenement {
    private int id;
    private String nom;
    private LocalDate dateEvent;
    private LocalTime heure;
    private String lieu;
    private int capacite;
    private BigDecimal prixBase;
    private String description;

    public Evenement(int id, String nom, LocalDate dateEvent, LocalTime heure, String lieu, int capacite, BigDecimal prixBase, String description) {
        this.id = id;
        this.nom = nom;
        this.dateEvent = dateEvent;
        this.heure = heure;
        this.lieu = lieu;
        this.capacite = capacite;
        this.prixBase = prixBase;
        this.description = description;
    }

    public Evenement(String nom, LocalDate dateEvent, LocalTime heure, String lieu, int capacite, BigDecimal prixBase, String description) {
        this.nom = nom;
        this.dateEvent = dateEvent;
        this.heure = heure;
        this.lieu = lieu;
        this.capacite = capacite;
        this.prixBase = prixBase;
        this.description = description;
    }

    // Getters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public LocalDate getDateEvent() { return dateEvent; }
    public LocalTime getHeure() { return heure; }
    public String getLieu() { return lieu; }
    public int getCapacite() { return capacite; }
    public BigDecimal getPrixBase() { return prixBase; }
    public String getDescription() { return description; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setDateEvent(LocalDate dateEvent) { this.dateEvent = dateEvent; }
    public void setHeure(LocalTime heure) { this.heure = heure; }
    public void setLieu(String lieu) { this.lieu = lieu; }
    public void setCapacite(int capacite) { this.capacite = capacite; }
    public void setPrixBase(BigDecimal prixBase) { this.prixBase = prixBase; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return nom + " - " + lieu + " (" + dateEvent + ")";
    }
}
