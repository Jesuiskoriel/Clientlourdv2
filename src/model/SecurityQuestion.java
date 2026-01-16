package model;

public class SecurityQuestion {
    private int id;
    private String libelle;

    // Constructeur complet.
    public SecurityQuestion(int id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }

    // Retourne l'id de la question.
    public int getId() {
        return id;
    }

    // Retourne le libellé de la question.
    public String getLibelle() {
        return libelle;
    }

    @Override
    // Affichage lisible de la question.
    public String toString() {
        return libelle;
    }
}
