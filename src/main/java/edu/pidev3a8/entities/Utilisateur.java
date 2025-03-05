package edu.pidev3a8.entities;


import javafx.scene.image.Image;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Date;

public class Utilisateur {

    public enum Role {
        Admin,
        Coach,
        Adherent,
    }

 private int id;
 private String email;
 private String mdp;
 private String nom ;
 private String prenom;
 private Date dateNai ;
 private int numTel ;
 private String genre ;
 private String adresse;
 private Role role;
 private float salaire;
 private boolean banned;
 private Blob image_user;
 private int age;

 public Utilisateur()
 {

 }

 public Utilisateur(String email,String mdp,
                    String nom,String prenom,Date dateNai,int numTel,
                    String genre, String adresse,Role role,float salaire,boolean banned,Blob image_user)
 {
     this.email=email;
  this.mdp=mdp;
  this.nom=nom;
  this.prenom=prenom;
  this.dateNai=dateNai;
  this.numTel=numTel;
  this.genre=genre;
  this.adresse=adresse;
  this.role=role;
  this.salaire=salaire;
  this.banned=banned;
  this.image_user = image_user;
 }
 //admin
    public Utilisateur(String email,String mdp,
                       String nom,String prenom,int numTel,
                       String genre,Role role,Blob image_user)
    {
        this.email=email;
        this.mdp=mdp;
        this.nom=nom;
        this.prenom=prenom;
        this.numTel=numTel;
        this.genre=genre;
        this.role=role;
        this.image_user = image_user;

    }
    //coach
    public Utilisateur(String email,String mdp,
                       String nom,String prenom,int numTel,
                       String genre, String adresse,Role role,float salaire,boolean banned,Blob image_user)
    {
        this.email=email;
        this.mdp=mdp;
        this.nom=nom;
        this.prenom=prenom;
        this.numTel=numTel;
        this.genre=genre;
        this.adresse=adresse;
        this.role=role;
        this.salaire=salaire;
        this.banned=banned;
        this.image_user = image_user;
    }
    //adherent
    public Utilisateur(String email,String mdp,
                       String nom,String prenom,Date dateNai,int numTel,
                       String genre, String adresse,Blob image_user,int age)
    {
        this.email=email;
        this.mdp=mdp;
        this.nom=nom;
        this.prenom=prenom;
        this.dateNai=dateNai;
        this.numTel=numTel;
        this.genre=genre;
        this.adresse=adresse;
        this.image_user = image_user;
        this.age = age ;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Date getDateNai() {
        return dateNai;
    }

    public void setDateNai(Date dateNai) {
        this.dateNai = dateNai;
    }

    public int getNumTel() {
        return numTel;
    }

    public void setNumTel(int numTel) {
        this.numTel = numTel;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public float getSalaire() { return salaire; }

    public void setSalaire(float salaire) { this.salaire = salaire; }

    public boolean isBanned() { return banned; }

    public void setBanned(boolean banned) { this.banned = banned; }

    public Blob getImage_user() { return image_user; }

    public void setImage_user(Blob image_user) { this.image_user = image_user; }

    public int getAge() { return age; }

    public void setAge(int age) { this.age = age; }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "banned=" + banned +
                ", salaire=" + salaire +
                ", role='" + role + '\'' +
                ", adresse='" + adresse + '\'' +
                ", genre='" + genre + '\'' +
                ", numTel=" + numTel +
                ", dateNai=" + dateNai +
                ", prenom='" + prenom + '\'' +
                ", nom='" + nom + '\'' +
                ", email='" + email + '\'' +
                ", mdp='" + mdp + '\'' +
                ", id=" + id +
                ", image_user=" + image_user +
                ", age=" + age +
                '}';
    }
    public Image getImage() {
        if (image_user != null) {
            try (InputStream is = image_user.getBinaryStream()) {
                return new Image(is);
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
