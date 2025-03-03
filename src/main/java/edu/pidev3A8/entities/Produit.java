package edu.pidev3A8.entities;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class Produit {

    private int id_produit;
    private String nom_produit;
    private String type_produit;
    private double prix;
    private Etat etat;  // Utilisation de l'enum
    private String description;
    private Status status; // Utilisation de l'enum
    private String image; // New attribute for image path or URL

    public Produit() {
    }

    public Produit(int id_produit, String nom_produit, String type_produit, double prix, Etat etat, String description, Status status, String image) {
        this.id_produit = id_produit;
        this.nom_produit = nom_produit;
        this.type_produit = type_produit;
        this.prix = prix;
        this.etat = etat;
        this.description = description;
        this.status = status;
        this.image = image; // Initialize image
    }

    public Produit(String nom_produit, String type_produit, double prix, Etat etat, String description, Status status, String image) {
        this.nom_produit = nom_produit;
        this.type_produit = type_produit;
        this.prix = prix;
        this.etat = etat;
        this.description = description;
        this.status = status;
        this.image = image; // Initialize image
    }

    // Getters and Setters
    public int getId_produit() {
        return id_produit;
    }
    public void setId_produit(int id_produit) {
        this.id_produit = id_produit;
    }

    public String getNom_produit() {
        return nom_produit;
    }
    public void setNom_produit(String nom_produit) {
        this.nom_produit = nom_produit;
    }

    public String getType_produit() {
        return type_produit;
    }
    public void setType_produit(String type_produit) {
        this.type_produit = type_produit;
    }

    public double getPrix() {
        return prix;
    }
    public void setPrix(double prix) {
        this.prix = prix;
    }

    public Etat getEtat() {
        return etat;
    }
    public void setEtat(Etat etat) {
        this.etat = etat;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Produit{" +
                "id_produit=" + id_produit +
                ", nom_produit='" + nom_produit + '\'' +
                ", type_produit='" + type_produit + '\'' +
                ", prix=" + prix +
                ", etat=" + etat +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", image='" + image + '\'' +
                '}';
    }

}