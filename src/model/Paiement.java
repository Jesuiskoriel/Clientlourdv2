package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Paiement {
    private int id;
    private Billet billet;
    private BigDecimal montant;
    private String modePaiement;
    private LocalDateTime datePaiement;
    private String reference;

    public Paiement(int id, Billet billet, BigDecimal montant, String modePaiement, LocalDateTime datePaiement, String reference) {
        this.id = id;
        this.billet = billet;
        this.montant = montant;
        this.modePaiement = modePaiement;
        this.datePaiement = datePaiement;
        this.reference = reference;
    }

    public Paiement(Billet billet, BigDecimal montant, String modePaiement, LocalDateTime datePaiement, String reference) {
        this.billet = billet;
        this.montant = montant;
        this.modePaiement = modePaiement;
        this.datePaiement = datePaiement;
        this.reference = reference;
    }

    // Getters
    public int getId() { return id; }
    public Billet getBillet() { return billet; }
    public BigDecimal getMontant() { return montant; }
    public String getModePaiement() { return modePaiement; }
    public LocalDateTime getDatePaiement() { return datePaiement; }
    public String getReference() { return reference; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setBillet(Billet billet) { this.billet = billet; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public void setModePaiement(String modePaiement) { this.modePaiement = modePaiement; }
    public void setDatePaiement(LocalDateTime datePaiement) { this.datePaiement = datePaiement; }
    public void setReference(String reference) { this.reference = reference; }

    @Override
    public String toString() {
        return "Paiement " + id + " - " + montant + "€ via " + modePaiement;
    }
}
