package model;

import java.time.LocalDateTime;

public class Billet {

    private int id;
    private String codeUnique;
    private Client client;
    private Evenement evenement;
    private StatutBillet statut;
    private LocalDateTime dateAchat;
    private double prixPaye;

    // Constructeur vide (utilisé par les DAO).
    public Billet() {
    }

    // Constructeur complet.
    public Billet(int id, String codeUnique, Client client, Evenement evenement,
                  StatutBillet statut, LocalDateTime dateAchat, double prixPaye) {
        this.id = id;
        this.codeUnique = codeUnique;
        this.client = client;
        this.evenement = evenement;
        this.statut = statut;
        this.dateAchat = dateAchat;
        this.prixPaye = prixPaye;
    }

    // Retourne l'identifiant du billet.
    public int getId() {
        return id;
    }

    // Définit l'identifiant du billet.
    public void setId(int id) {
        this.id = id;
    }

    // Retourne le code unique du billet.
    public String getCodeUnique() {
        return codeUnique;
    }

    // Définit le code unique du billet.
    public void setCodeUnique(String codeUnique) {
        this.codeUnique = codeUnique;
    }

    // Retourne le client associé.
    public Client getClient() {
        return client;
    }

    // Définit le client associé.
    public void setClient(Client client) {
        this.client = client;
    }

    // Retourne l'événement associé.
    public Evenement getEvenement() {
        return evenement;
    }

    // Définit l'événement associé.
    public void setEvenement(Evenement evenement) {
        this.evenement = evenement;
    }

    // Retourne le statut du billet.
    public StatutBillet getStatut() {
        return statut;
    }

    // Définit le statut du billet.
    public void setStatut(StatutBillet statut) {
        this.statut = statut;
    }

    // Retourne la date d'achat.
    public LocalDateTime getDateAchat() {
        return dateAchat;
    }

    // Définit la date d'achat.
    public void setDateAchat(LocalDateTime dateAchat) {
        this.dateAchat = dateAchat;
    }

    // Retourne le prix payé.
    public double getPrixPaye() {
        return prixPaye;
    }

    // Définit le prix payé.
    public void setPrixPaye(double prixPaye) {
        this.prixPaye = prixPaye;
    }
}
