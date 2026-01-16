package model;

import java.time.LocalDateTime;

public class Paiement {

    private int id;
    private int billetId;
    private double montant;
    private String modePaiement;
    private LocalDateTime datePaiement;
    private String reference;

    // Constructeur vide (utilisé par les DAO).
    public Paiement() {
    }

    // Constructeur complet.
    public Paiement(int id, int billetId, double montant, String modePaiement,
                    LocalDateTime datePaiement, String reference) {
        this.id = id;
        this.billetId = billetId;
        this.montant = montant;
        this.modePaiement = modePaiement;
        this.datePaiement = datePaiement;
        this.reference = reference;
    }

    // Retourne l'identifiant du paiement.
    public int getId() {
        return id;
    }

    // Définit l'identifiant du paiement.
    public void setId(int id) {
        this.id = id;
    }

    // Retourne l'id du billet associé.
    public int getBilletId() {
        return billetId;
    }

    // Définit l'id du billet associé.
    public void setBilletId(int billetId) {
        this.billetId = billetId;
    }

    // Retourne le montant du paiement.
    public double getMontant() {
        return montant;
    }

    // Définit le montant du paiement.
    public void setMontant(double montant) {
        this.montant = montant;
    }

    // Retourne le mode de paiement.
    public String getModePaiement() {
        return modePaiement;
    }

    // Définit le mode de paiement.
    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    // Retourne la date de paiement.
    public LocalDateTime getDatePaiement() {
        return datePaiement;
    }

    // Définit la date de paiement.
    public void setDatePaiement(LocalDateTime datePaiement) {
        this.datePaiement = datePaiement;
    }

    // Retourne la référence de transaction.
    public String getReference() {
        return reference;
    }

    // Définit la référence de transaction.
    public void setReference(String reference) {
        this.reference = reference;
    }
}
