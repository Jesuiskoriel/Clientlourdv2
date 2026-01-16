package model;

import java.time.LocalDateTime;

/**
 * Achat d'un billet par un utilisateur.
 */
public class Achat {

    private int id;
    private int userId;
    private int eventId;
    private String eventName;
    private double prix;
    private LocalDateTime dateAchat;

    // Retourne l'identifiant de l'achat.
    public int getId() {
        return id;
    }

    // Définit l'identifiant de l'achat.
    public void setId(int id) {
        this.id = id;
    }

    // Retourne l'id utilisateur associé.
    public int getUserId() {
        return userId;
    }

    // Définit l'id utilisateur associé.
    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Retourne l'id de l'événement.
    public int getEventId() {
        return eventId;
    }

    // Définit l'id de l'événement.
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    // Retourne le nom de l'événement.
    public String getEventName() {
        return eventName;
    }

    // Définit le nom de l'événement.
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    // Retourne le prix payé.
    public double getPrix() {
        return prix;
    }

    // Définit le prix payé.
    public void setPrix(double prix) {
        this.prix = prix;
    }

    // Retourne la date d'achat.
    public LocalDateTime getDateAchat() {
        return dateAchat;
    }

    // Définit la date d'achat.
    public void setDateAchat(LocalDateTime dateAchat) {
        this.dateAchat = dateAchat;
    }
}
