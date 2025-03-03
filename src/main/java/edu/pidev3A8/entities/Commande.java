package edu.pidev3A8.entities;

import javafx.scene.Scene;

import java.util.Date;

public class Commande {
    private int id_commande;
    private int id_produit;  // Utilisation d'un ID au lieu de l'objet Produit
    private int id_client;
    private int quantite;
    private double prix_total;
    private Date date_commande;
    private StatutCommande statut;

    public Commande(int id_produit, int id_client, int quantite, double prix_total, java.sql.Date date_commande, Status statut) {
    }

    public Commande(int id_commande, int id_produit, int id_client, int quantite, double prix_total, Date date_commande, StatutCommande statut) {
        this.id_commande = id_commande;
        this.id_produit = id_produit;
        this.id_client = id_client;
        this.quantite = quantite;
        this.prix_total = prix_total;
        this.date_commande = date_commande;
        this.statut = statut;
    }


    public Commande(int id_produit, int id_client, int quantite, double prix_total, Date date_commande, StatutCommande statut) {
        this.id_produit = id_produit;
        this.id_client = id_client;
        this.quantite = quantite;
        this.prix_total = prix_total;
        this.date_commande = date_commande;
        this.statut = statut;
    }

    public int getId_commande() {
        return id_commande;
    }

    public int getId_produit() {
        return id_produit;
    }

    public int getId_client() {
        return id_client;
    }

    public int getQuantite() {
        return quantite;
    }

    public double getPrix_total() {
        return prix_total;
    }

    public Date getDate_commande() {
        return date_commande;
    }


    public StatutCommande getStatut() {
        return statut;
    }

    public void setId_commande(int id_commande) {
        this.id_commande = id_commande;
    }

    public void setId_produit(int id_produit) {
        this.id_produit = id_produit;
    }

    public void setId_client(int id_client) {
        this.id_client = id_client;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public void setPrix_total(double prix_total) {
        this.prix_total = prix_total;
    }

    public void setDate_commande(Date date_commande) {
        this.date_commande = date_commande;
    }
    public void setStatut(StatutCommande statut) {
        this.statut = statut;  // Utilisation de l'Enum
    }

    @Override
    public String toString() {
        return "Commande{" +
                "id_commande=" + id_commande +
                ", id_produit=" + id_produit +
                ", id_client=" + id_client +
                ", quantite=" + quantite +
                ", prix_total=" + prix_total +
                ", date_commande=" + date_commande +
                ", statut='" + statut + '\'' +
                '}';
    }


}
