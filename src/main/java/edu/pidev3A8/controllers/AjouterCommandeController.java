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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class AjouterCommandeController {

    @FXML
    private ComboBox<Produit> produitComboBox; // ComboBox pour les produits

    @FXML
    private TextField idClientField;

    @FXML
    private TextField quantiteField;

    @FXML
    private TextField AdresseField;

    @FXML
    private TextField mailField;

    @FXML
    private TextField prixTotalField;

    private Commandeservice commandeservice = new Commandeservice();
    private Produitservice produitservice = new Produitservice();

    private Consumer<Commande> onCommandeAddedCallback; // Callback pour mettre à jour la liste des commandes

    /**
     * Définit un callback pour mettre à jour la liste des commandes après l'ajout.
     *
     * @param callback Le callback à exécuter après l'ajout d'une commande.
     */
    public void setOnCommandeAddedCallback(Consumer<Commande> callback) {
        this.onCommandeAddedCallback = callback;
    }

    @FXML
    public void initialize() {
        // Remplir la ComboBox des produits
        List<Produit> produits = produitservice.getAllData();
        produitComboBox.getItems().setAll(produits); // Ajouter les produits à la ComboBox

        // Définir un CellFactory pour afficher uniquement l'id_produit dans la ComboBox
        produitComboBox.setCellFactory(listView -> new ListCell<Produit>() {
            @Override
            protected void updateItem(Produit produit, boolean empty) {
                super.updateItem(produit, empty);
                if (empty || produit == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(produit.getId_produit())); // Afficher l'id_produit
                }
            }
        });

        // Définir un ButtonCell pour afficher l'id_produit dans le bouton de la ComboBox
        produitComboBox.setButtonCell(new ListCell<Produit>() {
            @Override
            protected void updateItem(Produit produit, boolean empty) {
                super.updateItem(produit, empty);
                if (empty || produit == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(produit.getId_produit())); // Afficher l'id_produit
                }
            }
        });

        // Écouter les changements dans la ComboBox et le champ de quantité
        produitComboBox.valueProperty().addListener((observable, oldValue, newValue) -> updatePrixTotal());
        quantiteField.textProperty().addListener((observable, oldValue, newValue) -> updatePrixTotal());
    }

    /**
     * Met à jour le champ "prix total" en fonction du produit sélectionné et de la quantité.
     */
    private void updatePrixTotal() {
        Produit produit = produitComboBox.getValue();
        String quantiteText = quantiteField.getText();

        if (produit != null && quantiteText != null && !quantiteText.isEmpty()) {
            try {
                int quantite = Integer.parseInt(quantiteText);
                double prixTotal = produit.getPrix() * quantite; // Calcul du prix total
                prixTotalField.setText(String.valueOf(prixTotal)); // Mettre à jour le champ
            } catch (NumberFormatException e) {
                prixTotalField.setText("Erreur de quantité");
            }
        } else {
            prixTotalField.setText("");
        }
    }

    /**
     * Traite le paiement via Stripe.
     *
     * @param amount          Montant du paiement.
     * @param currency        Devise (ex: "usd").
     * @param paymentMethodId ID de la méthode de paiement.
     * @return true si le paiement réussit, sinon false.
     */
    private boolean processPayment(double amount, String currency, String paymentMethodId) {
        try {
            Stripe.apiKey = "sk_test_51Qwp3zQ0S3cYY6GU7FwFfHEoPNqq0HR4cKj8TkxTnBsBab4HM9EpZkZElej63FZwLyKB12XufbUkqjyVI5LIRQ6a00zwDFAxaP";

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount((long) (amount * 100)) // Montant en cents
                    .setCurrency(currency) // Devise (ex: "usd")
                    .setPaymentMethod(paymentMethodId) // ID de la méthode de paiement
                    .setConfirm(true) // Confirmer le paiement immédiatement
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true) // Activer les méthodes de paiement automatiques
                                    .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER) // Désactiver les redirections
                                    .build()
                    )
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);
            return "succeeded".equals(intent.getStatus()); // Retourne true si le paiement réussit
        } catch (StripeException e) {
            System.err.println("Erreur de paiement : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Affiche une boîte de dialogue pour saisir les informations de paiement.
     *
     * @return L'ID de la méthode de paiement (simulé pour l'exemple).
     */
    private String showPaymentDialog() {
        try {
            System.out.println("Chargement de PaymentDialog.fxml...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PaymentDialog.fxml"));
            Parent root = loader.load();
            System.out.println("PaymentDialog.fxml chargé avec succès.");

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
    private void handleAjouter() {
        try {
            // Récupérer les valeurs des champs
            Produit produit = produitComboBox.getValue();
            int idClient = Integer.parseInt(idClientField.getText());
            int quantite = Integer.parseInt(quantiteField.getText());
            String adresse = AdresseField.getText();
            String adresseEmail = mailField.getText(); // Récupérer l'adresse e-mail du champ de texte
            double prixTotal = Double.parseDouble(prixTotalField.getText());

            // Vérifier que l'adresse e-mail est valide
            if (!adresseEmail.contains("@")) {
                showAlert(AlertType.ERROR, "Erreur de saisie", "Adresse e-mail invalide", "L'adresse e-mail doit contenir '@'.");
                return;
            }

            // Afficher l'adresse e-mail récupérée
            System.out.println("Adresse e-mail récupérée : " + adresseEmail);

            // Afficher la boîte de dialogue de paiement
            String paymentMethodId = showPaymentDialog();
            if (paymentMethodId == null || paymentMethodId.isEmpty()) {
                showAlert(AlertType.ERROR, "Erreur de saisie", "Paiement invalide", "Veuillez saisir les informations de paiement.");
                return;
            }

            // Traiter le paiement
            if (processPayment(prixTotal, "usd", paymentMethodId)) {
                // Créer un nouvel objet Commande
                Commande commande = new Commande(produit.getId_produit(), idClient, quantite, adresse, adresseEmail, prixTotal);

                // Ajouter la commande via le service
                commandeservice.addCommande(commande);

                // Envoyer un e-mail de confirmation
                String subject = "Confirmation de commande";
                String body = "Bonjour,\n\nVotre commande a été validée avec succès.\n\nDétails de la commande :\n"
                        + "Quantité : " + quantite + "\n"
                        + "Prix total : " + prixTotal + " USD\n\n"
                        + "Merci pour votre achat !";

                // Appeler la méthode sendEmail pour envoyer l'e-mail
                System.out.println("Envoi de l'e-mail à : " + adresseEmail);
                EmailSender.sendEmail(adresseEmail, subject, body);

                // Afficher une alerte de succès
                showAlert(AlertType.INFORMATION, "Succès", "Commande ajoutée", "La commande a été ajoutée avec succès.");

                // Fermer la fenêtre d'ajout
                Stage stage = (Stage) produitComboBox.getScene().getWindow();
                stage.close();
            } else {
                showAlert(AlertType.ERROR, "Erreur de paiement", "Paiement échoué", "Le paiement n'a pas pu être traité.");
            }
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Erreur de saisie", "Format invalide", "Veuillez saisir des nombres valides pour l'ID client, la quantité ou le prix total.");
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors de l'ajout de la commande", e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * Affiche une boîte de dialogue d'alerte.
     *
     * @param alertType Le type d'alerte.
     * @param title     Le titre de l'alerte.
     * @param header    L'en-tête de l'alerte.
     * @param content   Le contenu de l'alerte.
     */
    private void showAlert(AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}