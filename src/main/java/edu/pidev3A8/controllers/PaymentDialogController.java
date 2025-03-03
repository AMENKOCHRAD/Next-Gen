package edu.pidev3A8.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PaymentDialogController {

    @FXML
    private TextField cardNumberField;

    @FXML
    private TextField expiryDateField;

    @FXML
    private TextField cvcField;

    private String paymentMethodId;

    @FXML
    private void handlePayment() {
        // Simuler la création d'un ID de méthode de paiement (à remplacer par une intégration réelle avec Stripe)
        paymentMethodId = "pm_card_visa"; // Exemple d'ID de méthode de paiement

        // Fermer la boîte de dialogue
        Stage stage = (Stage) cardNumberField.getScene().getWindow();
        stage.close();
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }
}