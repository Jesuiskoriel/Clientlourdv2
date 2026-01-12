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

    public Billet() {
    }

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodeUnique() {
        return codeUnique;
    }

    public void setCodeUnique(String codeUnique) {
        this.codeUnique = codeUnique;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Evenement getEvenement() {
        return evenement;
    }

    public void setEvenement(Evenement evenement) {
        this.evenement = evenement;
    }

    public StatutBillet getStatut() {
        return statut;
    }

    public void setStatut(StatutBillet statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateAchat() {
        return dateAchat;
    }

    public void setDateAchat(LocalDateTime dateAchat) {
        this.dateAchat = dateAchat;
    }

    public double getPrixPaye() {
        return prixPaye;
    }

    public void setPrixPaye(double prixPaye) {
        this.prixPaye = prixPaye;
    }
}
