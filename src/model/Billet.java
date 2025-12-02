package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Billet {
    private int id;
    private String codeUnique;
    private Client client;
    private Evenement evenement;
    private StatutBillet statut;
    private LocalDateTime dateAchat;
    private BigDecimal prixPaye;

    public Billet(int id, String codeUnique, Client client, Evenement evenement, StatutBillet statut, LocalDateTime dateAchat, BigDecimal prixPaye) {
        this.id = id;
        this.codeUnique = codeUnique;
        this.client = client;
        this.evenement = evenement;
        this.statut = statut;
        this.dateAchat = dateAchat;
        this.prixPaye = prixPaye;
    }

    public Billet(String codeUnique, Client client, Evenement evenement, StatutBillet statut, LocalDateTime dateAchat, BigDecimal prixPaye) {
        this.codeUnique = codeUnique;
        this.client = client;
        this.evenement = evenement;
        this.statut = statut;
        this.dateAchat = dateAchat;
        this.prixPaye = prixPaye;
    }

    // Getters
    public int getId() { return id; }
    public String getCodeUnique() { return codeUnique; }
    public Client getClient() { return client; }
    public Evenement getEvenement() { return evenement; }
    public StatutBillet getStatut() { return statut; }
    public LocalDateTime getDateAchat() { return dateAchat; }
    public BigDecimal getPrixPaye() { return prixPaye; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setCodeUnique(String codeUnique) { this.codeUnique = codeUnique; }
    public void setClient(Client client) { this.client = client; }
    public void setEvenement(Evenement evenement) { this.evenement = evenement; }
    public void setStatut(StatutBillet statut) { this.statut = statut; }
    public void setDateAchat(LocalDateTime dateAchat) { this.dateAchat = dateAchat; }
    public void setPrixPaye(BigDecimal prixPaye) { this.prixPaye = prixPaye; }

    @Override
    public String toString() {
        return codeUnique + " - " + evenement.getNom() + " (" + client.getNom() + ")";
    }
}
