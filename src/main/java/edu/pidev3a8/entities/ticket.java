package edu.pidev3a8.entities;

public class Ticket {
    private int idTicket;
    private double prix;
    private int quantite;
    private String eventName; // Nom de l'événement
    private int idEvent; // Ajout de l'ID de l'événement


    public Ticket(int idTicket, double prix, int quantite, String eventName, int idEvent) {
        this.idTicket = idTicket;
        this.prix = prix;
        this.quantite = quantite;
        this.eventName = eventName;
        this.idEvent = idEvent;

    }

    // Getters et Setters
    public int getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(int idTicket) {
        this.idTicket = idTicket;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    public int getidEvent() {
        return idEvent;
    }

    public void setidEvent(int idEvent) {
        this.idEvent = idEvent;
    }
}
