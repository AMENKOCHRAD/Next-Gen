package controllers;

import entities.ticket;
import services.ticketServices;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ModificationTicketController {

    @FXML
    private TextField idEventField;

    @FXML
    private TextField prixField;

    @FXML
    private TextField quantiteField;

    private ticket selectedTicket;
    private ticketServices ticketServices;

    public void setSelectedTicket(ticket selectedTicket) {
        this.selectedTicket = selectedTicket;
        populateFields();
    }

    public void setTicketServices(ticketServices ticketServices) {
        this.ticketServices = ticketServices;
    }

    private void populateFields() {
        if (selectedTicket != null) {
            idEventField.setText(String.valueOf(selectedTicket.getIdevent()));
            prixField.setText(String.valueOf(selectedTicket.getPrix()));
            quantiteField.setText(String.valueOf(selectedTicket.getQuantite()));
        }
    }

    @FXML
    private void handleModifyTicket() {
        if (selectedTicket != null) {
            // Mise à jour des informations du ticket
            selectedTicket.setIdevent(Integer.parseInt(idEventField.getText()));
            selectedTicket.setPrix(Double.parseDouble(prixField.getText()));
            selectedTicket.setQuantite(Integer.parseInt(quantiteField.getText()));

            // Mettre à jour le ticket dans la base de données
            ticketServices.updateTicket(selectedTicket, selectedTicket.getIdticket());

            // Fermer la fenêtre après modification
            Stage stage = (Stage) idEventField.getScene().getWindow();
            stage.close();
        }
    }
}
