package controllers;

import entities.ticket;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.ticketServices;

public class ajouterTicket {

    @FXML
    private TextField idTicketField; // Champ pour idTicket

    @FXML
    private TextField idEventField; // Champ pour idEvent

    @FXML
    private TextField prixField; // Champ pour le prix

    @FXML
    private TextField quantiteField; // Champ pour la quantité

    private ticketServices ticketServices;
    public ajouterTicket() {
        this.ticketServices = new ticketServices(); // Initialisation manuelle
    }


    public void setTicketServices(ticketServices ticketServices) {
        this.ticketServices = ticketServices;
    }

    @FXML
    private void handleAddTicket() {
        // Récupérer les valeurs saisies
        String idTicketText = idTicketField.getText();
        String idEventText = idEventField.getText();
        String prixText = prixField.getText();
        String quantiteText = quantiteField.getText();

        // Valider les saisies
        if (idTicketText == null || idTicketText.trim().isEmpty()) {
            showAlert("Erreur de saisie", "L'ID du ticket est obligatoire.");
            return;
        }

        if (idEventText == null || idEventText.trim().isEmpty()) {
            showAlert("Erreur de saisie", "L'ID de l'événement est obligatoire.");
            return;
        }

        if (prixText == null || prixText.trim().isEmpty()) {
            showAlert("Erreur de saisie", "Le prix du ticket est obligatoire.");
            return;
        }

        if (quantiteText == null || quantiteText.trim().isEmpty()) {
            showAlert("Erreur de saisie", "La quantité du ticket est obligatoire.");
            return;
        }

        // Convertir les valeurs en entiers et double
        int idTicket;
        int idEvent;
        int prix;
        int quantite;
        try {
            idTicket = Integer.parseInt(idTicketText);
            idEvent = Integer.parseInt(idEventText);
            prix = Integer.parseInt(prixText);
            quantite = Integer.parseInt(quantiteText);
        } catch (NumberFormatException e) {
            showAlert("Erreur de saisie", "Veuillez entrer des valeurs numériques valides.");
            return;
        }

        // Créer un nouveau ticket
        ticket newTicket = new ticket();
        newTicket.setIdticket(idTicket);
        newTicket.setIdevent(idEvent);
        newTicket.setPrix(prix);
        newTicket.setQuantite(quantite);

        // Ajouter le ticket via le service
        ticketServices.addTicket(newTicket);

        // Fermer la fenêtre après l'ajout
        Stage stage = (Stage) idTicketField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}