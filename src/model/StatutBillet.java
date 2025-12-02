package model;

public class StatutBillet {
    private int id;
    private String libelle;

    public StatutBillet(int id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }

    public StatutBillet(String libelle) {
        this.libelle = libelle;
    }

    // Getters
    public int getId() { return id; }
    public String getLibelle() { return libelle; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    @Override
    public String toString() {
        return libelle;
    }
}
