package edu.pidev3a8.entities;

import java.util.Date;

public class Cours {
    private int id;
    private String type;
    private String nom_cours;
    private Date date;
    private String adresse_mail_coach;
    private byte[] image; // New attribute

    // Constructors, getters, and setters
    public Cours(int id, String type, String nom_cours, Date date, String adresse_mail_coach, byte[] image) {
        this.id = id;
        this.type = type;
        this.nom_cours = nom_cours;
        this.date = date;
        this.adresse_mail_coach = adresse_mail_coach;
        this.image = image;
    }

    public Cours() {
    }

    public Cours(String type, String nom_cours, Date date, String adresse_mail_coach, byte[] image) {
        this.type = type;
        this.nom_cours = nom_cours;
        this.date = date;
        this.adresse_mail_coach = adresse_mail_coach;
        this.image = image;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNom_cours() {
        return nom_cours;
    }

    public void setNom_cours(String nom_cours) {
        this.nom_cours = nom_cours;
    }

    public String getAdresse_mail_coach() {
        return adresse_mail_coach;
    }

    public void setAdresse_mail_coach(String adresse_mail_coach) {
        this.adresse_mail_coach = adresse_mail_coach;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}