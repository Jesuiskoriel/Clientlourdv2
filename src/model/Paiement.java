package model;

import java.time.LocalDateTime;

public class Paiement {

    private int id;
    private int billetId;
    private double montant;
    private String modePaiement;
    private LocalDateTime datePaiement;
    private String reference;

    public Paiement() {
    }

    public Paiement(int id, int billetId, double montant, String modePaiement,
                    LocalDateTime datePaiement, String reference) {
        this.id = id;
        this.billetId = billetId;
        this.montant = montant;
        this.modePaiement = modePaiement;
        this.datePaiement = datePaiement;
        this.reference = reference;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBilletId() {
        return billetId;
    }

    public void setBilletId(int billetId) {
        this.billetId = billetId;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public LocalDateTime getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDateTime datePaiement) {
        this.datePaiement = datePaiement;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
