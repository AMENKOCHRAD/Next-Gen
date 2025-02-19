package controllers;

import entities.event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import services.eventServices;

import java.sql.Date;

public class ajouterEvent {

    @FXML
    private TextField dateDebutEvent;

    @FXML
    private TextField dateFinEvent;

    @FXML
    private TextField lieuEvent;

    @FXML
    private TextField nomEvent;

    @FXML
    private TextField typeEvent;

    // Méthode appelée lorsqu'on clique sur le bouton "Ajouter"
    @FXML
    private void handleAddEvent(ActionEvent event) {
        ajouterEvent();
    }

    // Méthode pour ajouter l'événement (logique métier)
    private void ajouterEvent() {
        String nom = nomEvent.getText();
        String dateDebutText = dateDebutEvent.getText();
        String dateFinText = dateFinEvent.getText();
        String lieu = lieuEvent.getText();
        String type = typeEvent.getText();

        // Vérification des champs avant conversion
        if (nom.isEmpty() || dateDebutText.isEmpty() || dateFinText.isEmpty() || lieu.isEmpty() || type.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs.");
            return;
        }

        // Conversion des dates avec gestion des erreurs
        Date dateDebut = convertToDate(dateDebutText);
        Date dateFin = convertToDate(dateFinText);

        if (dateDebut == null || dateFin == null) {
            showAlert(Alert.AlertType.ERROR, "Format de date incorrect", "Veuillez entrer une date au format YYYY-MM-DD.");
            return;
        }

        // Création et ajout de l'événement
        event e = new event(nom, type, dateDebut, dateFin, lieu);
        eventServices eventService = new eventServices();
        eventService.addEvent(e);

        // Confirmation à l'utilisateur
        showAlert(Alert.AlertType.INFORMATION, "Événement ajouté", "L'événement '" + e.getNom() + "' a été ajouté avec succès !");

        // Réinitialiser les champs après l'ajout
        resetFields();
    }

    // Méthode pour convertir une chaîne de texte en java.sql.Date
    private Date convertToDate(String dateText) {
        try {
            return Date.valueOf(dateText); // Format attendu : YYYY-MM-DD
        } catch (IllegalArgumentException e) {
            return null; // Retourne null si la date est invalide
        }
    }

    // Méthode pour afficher les alertes
    private void showAlert(Alert.AlertType alertType, String title, String header) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.showAndWait();
    }

    // Méthode pour vider les champs après l'ajout
    private void resetFields() {
        nomEvent.clear();
        dateDebutEvent.clear();
        dateFinEvent.clear();
        lieuEvent.clear();
        typeEvent.clear();
    }
}
