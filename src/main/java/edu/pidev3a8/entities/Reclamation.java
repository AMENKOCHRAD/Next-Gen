package edu.pidev3a8.entities;

import javafx.scene.layout.HBox;
import java.time.LocalDateTime;
import java.util.List;

public class Reclamation {
    private int id;
    private String sujet; // Correction : Utilisation de "sujet" au lieu de "Sujet" pour respecter les conventions de nommage
    private String description;
    private String statut;
    private LocalDateTime date;
    private int user;
    private Categorie categorie; // Utilisation de l'enum Categorie
    private List<String> piecesJointes;
    private HBox piecesJointesBox; // Nouveau champ pour stocker les pièces jointes sous forme de HBox

    // Constructeur par défaut
    public Reclamation() {
        this.date = LocalDateTime.now(); // Initialiser la date avec la date et l'heure actuelles
        this.statut = "EN_ATTENTE"; // Statut par défaut
        this.user = 1; // ID de l'utilisateur par défaut (à adapter selon votre logique)
    }

    // Constructeur avec tous les attributs
    public Reclamation(int id, String sujet, String description, String statut, LocalDateTime date, int user, Categorie categorie, List<String> piecesJointes) {
        this.id = id;
        this.sujet = sujet;
        this.description = description;
        this.statut = statut;
        this.date = date;
        this.user = user;
        this.categorie = categorie;
        this.piecesJointes = piecesJointes;
    }

    // Constructeur pour l'ajout d'une réclamation (sans ID ni date)
    public Reclamation(String sujet, String description, Categorie categorie, List<String> piecesJointes) {
        this.sujet = sujet;
        this.description = description;
        this.categorie = categorie;
        this.piecesJointes = piecesJointes;
        this.date = LocalDateTime.now(); // Initialiser la date avec la date et l'heure actuelles
        this.statut = "EN_ATTENTE"; // Statut par défaut
        this.user = 1; // ID de l'utilisateur par défaut (à adapter selon votre logique)
    }

    // Constructeur simplifié (pour compatibilité avec votre code existant)
    public Reclamation(String sujet, String description) {
        this.sujet = sujet;
        this.description = description;
        this.date = LocalDateTime.now(); // Initialiser la date avec la date et l'heure actuelles
        this.statut = "EN_ATTENTE"; // Statut par défaut
        this.user = 1; // ID de l'utilisateur par défaut (à adapter selon votre logique)
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSujet() {
        return sujet;
    }

    public void setSujet(String sujet) {
        this.sujet = sujet;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public List<String> getPiecesJointes() {
        return piecesJointes;
    }

    public void setPiecesJointes(List<String> piecesJointes) {
        this.piecesJointes = piecesJointes;
    }

    public HBox getPiecesJointesBox() {
        return piecesJointesBox;
    }

    public void setPiecesJointesBox(HBox piecesJointesBox) {
        this.piecesJointesBox = piecesJointesBox;
    }

    @Override
    public String toString() {
        return "Reclamation{" +
                "id=" + id +
                ", sujet='" + sujet + '\'' +
                ", description='" + description + '\'' +
                ", statut='" + statut + '\'' +
                ", date=" + date +
                ", user=" + user +
                ", categorie=" + categorie +
                ", piecesJointes=" + piecesJointes +
                '}';
    }
}