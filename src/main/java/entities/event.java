package entities;

import java.util.Date;

public class event {
    private int idEvent;
    private String nom;
    private String type;
    private Date dateDebut;
    private Date dateFin;
    private String lieu;

    // Constructeur par défaut
    public event() {}

    // Constructeur avec tous les attributs
    public event(int idEvent, String nom, String type, Date dateDebut, Date dateFin, String lieu) {
        this.idEvent = idEvent;
        this.nom = nom;
        this.type = type;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieu = lieu;
    }

    // Constructeur sans idEvent (utile pour l'insertion en base de données)
    public event(String nom, String type, Date dateDebut, Date dateFin, String lieu) {
        this.nom = nom;
        this.type = type;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieu = lieu;
    }

    // Getters et Setters
    public int getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(int idEvent) {
        this.idEvent = idEvent;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    // Méthode toString pour l'affichage
    @Override

    public String toString() {
        return "Event{" +
                "idEvent=" + idEvent +
                ", nom='" + nom + '\'' +
                ", type='" + type + '\'' +
                ", dateDebut=" + (dateDebut != null ? dateDebut : "null") +
                ", dateFin=" + (dateFin != null ? dateFin : "null") +
                ", lieu='" + lieu + '\'' +
                '}';
    }


}