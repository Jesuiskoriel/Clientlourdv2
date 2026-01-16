package model;

public class StatutBillet {

    private int id;
    private String libelle;

    // Constructeur vide.
    public StatutBillet() {
    }

    // Constructeur complet.
    public StatutBillet(int id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }

    // Retourne l'id du statut.
    public int getId() {
        return id;
    }

    // Définit l'id du statut.
    public void setId(int id) {
        this.id = id;
    }

    // Retourne le libellé du statut.
    public String getLibelle() {
        return libelle;
    }

    // Définit le libellé du statut.
    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    @Override
    // Affichage lisible du statut.
    public String toString() {
        return libelle;
    }
}
