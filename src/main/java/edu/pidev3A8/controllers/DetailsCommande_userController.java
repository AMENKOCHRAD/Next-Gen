package edu.pidev3A8.controllers;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import edu.pidev3A8.entities.Commande;
import edu.pidev3A8.entities.EmailSender;
import edu.pidev3A8.entities.Produit;
import edu.pidev3A8.services.Commandeservice;
import edu.pidev3A8.services.Produitservice;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class DetailsCommande_userController {

    @FXML
    private ComboBox<Produit> produitComboBox;

    @FXML
    private TextField quantiteField;

    @FXML
    private TextField adresseField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField prixTotalField;

    @FXML
    private TextField idClientField; // Ajout du champ pour l'ID client

    private Commandeservice commandeservice = new Commandeservice();
    private Produitservice produitservice = new Produitservice();

    @FXML
    public void initialize() {
        loadProduits();
        setupComboBox();
        setupListeners();
    }

    private void loadProduits() {
        List<Produit> produits = produitservice.getAllData();
        produitComboBox.getItems().setAll(produits);
    }

    private void setupComboBox() {
        produitComboBox.setCellFactory(listView -> new ListCell<Produit>() {
            @Override
            protected void updateItem(Produit produit, boolean empty) {
                super.updateItem(produit, empty);
                setText(produit == null || empty ? null : String.valueOf(produit.getId_produit()));
            }
        });

        produitComboBox.setButtonCell(new ListCell<Produit>() {
            @Override
            protected void updateItem(Produit produit, boolean empty) {
                super.updateItem(produit, empty);
                setText(produit == null || empty ? null : String.valueOf(produit.getId_produit()));
            }
        });
    }

    private void setupListeners() {
        produitComboBox.valueProperty().addListener((observable, oldValue, newValue) -> updatePrixTotal());
        quantiteField.textProperty().addListener((observable, oldValue, newValue) -> updatePrixTotal());
    }

    private void updatePrixTotal() {
        Produit produit = produitComboBox.getValue();
        int idClient = Integer.parseInt(idClientField.getText());

        String quantiteText = quantiteField.getText();

        if (produit != null && quantiteText != null && !quantiteText.isEmpty()) {
            try {
                int quantite = Integer.parseInt(quantiteText);
                double prixTotal = produit.getPrix() * quantite;
                prixTotalField.setText(String.valueOf(prixTotal));
            } catch (NumberFormatException e) {
                prixTotalField.setText("Erreur de quantité");
            }
        } else {
            prixTotalField.setText("");
        }
    }

    private boolean processPayment(double amount, String currency, String paymentMethodId) {
        try {
            Stripe.apiKey = "sk_test_51Qwp3zQ0S3cYY6GU7FwFfHEoPNqq0HR4cKj8TkxTnBsBab4HM9EpZkZElej63FZwLyKB12XufbUkqjyVI5LIRQ6a00zwDFAxaP";

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount((long) (amount * 100))
                    .setCurrency(currency)
                    .setPaymentMethod(paymentMethodId)
                    .setConfirm(true)
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                                    .build()
                    )
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);
            return "succeeded".equals(intent.getStatus());
        } catch (StripeException e) {
            System.err.println("Erreur de paiement : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String showPaymentDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PaymentDialog.fxml"));
            Parent root = loader.load();
            PaymentDialogController controller = loader.getController();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Paiement");
            stage.showAndWait();
            return controller.getPaymentMethodId();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de PaymentDialog.fxml: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void handleValiderCommande() {
        try {
            if (!validateFields()) {
                return;
            }

            Produit produit = produitComboBox.getValue();
            int idClient = Integer.parseInt(idClientField.getText());
            int quantite = Integer.parseInt(quantiteField.getText());
            String adresse = adresseField.getText();
            String adresseEmail = emailField.getText();
            double prixTotal = Double.parseDouble(prixTotalField.getText());

            String paymentMethodId = showPaymentDialog();
            if (paymentMethodId == null || paymentMethodId.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Paiement invalide", "Veuillez saisir les informations de paiement.");
                return;
            }

            if (processPayment(prixTotal, "usd", paymentMethodId)) {
                Commande commande = new Commande(produit.getId_produit(), idClient, quantite, adresse, adresseEmail, prixTotal);
                commandeservice.addCommande(commande);

                sendConfirmationEmail(adresseEmail, quantite, prixTotal);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Commande ajoutée", "La commande a été ajoutée avec succès.");

                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur de paiement", "Paiement échoué", "Le paiement n'a pas pu être traité.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Format invalide", "Veuillez saisir des nombres valides pour l'ID client, la quantité ou le prix total.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout de la commande", e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateFields() {
        if (produitComboBox.getValue() == null || quantiteField.getText().isEmpty() || adresseField.getText().isEmpty() || emailField.getText().isEmpty() || idClientField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Champs manquants", "Veuillez remplir tous les champs obligatoires.");
            return false;
        }

        if (!emailField.getText().contains("@")) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Adresse e-mail invalide", "L'adresse e-mail doit contenir '@'.");
            return false;
        }

        return true;
    }

    private void sendConfirmationEmail(String adresseEmail, int quantite, double prixTotal) {
        String subject = "Confirmation de commande";
        String body = "Bonjour,\n\nVotre commande a été validée avec succès.\n\nDétails de la commande :\n"
                + "Quantité : " + quantite + "\n"
                + "Prix total : " + prixTotal + " USD\n\n"
                + "Merci pour votre achat !";

        System.out.println("Envoi de l'e-mail à : " + adresseEmail);
        EmailSender.sendEmail(adresseEmail, subject, body);
    }

    private void closeWindow() {
        Stage stage = (Stage) produitComboBox.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}