package edu.pidev3A8.entities;

import javafx.scene.Scene;

import java.util.Date;

public class Commande {
    private int id_commande;
    private int id_produit;  // Utilisation d'un ID au lieu de l'objet Produit
    private int id_client;
    private int quantite;
    private String adresse;
    private String adresseEmail;
    private  Double prixTotal;

    public Commande() {
    }

    public Commande(int id_commande, int id_produit, int id_client, int quantite, String adresse, String adresseEmail, Double prixTotal	   ) {
        this.id_commande = id_commande;
        this.id_produit = id_produit;
        this.id_client = id_client;
        this.quantite = quantite;
        this.adresse=adresse;
        this.	adresseEmail=	adresseEmail;
        this.prixTotal=prixTotal;

    }


    public Commande(int id_produit, int id_client, int quantite, String adresse , String  adresseEmail, Double prixTotal	 ) {
        this.id_produit = id_produit;
        this.id_client = id_client;
        this.quantite = quantite;
        this.adresse=adresse;
        this.adresseEmail=	adresseEmail;
        this.prixTotal=prixTotal;

    }

    public Double getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(Double prixTotal) {
        this.prixTotal = prixTotal;
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

    public String getAdresseEmail() {
        return adresseEmail;
    }

    public void setAdresseEmail(String adresseEmail) {
        this.adresseEmail = adresseEmail;
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

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    @Override
    public String toString() {
        return "Commande{" +
                "id_commande=" + id_commande +
                ", id_produit=" + id_produit +
                ", id_client=" + id_client +
                ", quantite=" + quantite +
                ",adresse="+ adresse +
                ",adresseEmail" + adresseEmail +

                '}';
    }


}
