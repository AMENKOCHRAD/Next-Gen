package edu.pidev3A8.controllers;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import edu.pidev3A8.entities.Etat;
import edu.pidev3A8.entities.Produit;
import edu.pidev3A8.entities.Status;
import edu.pidev3A8.services.Produitservice;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class AjouterProduitController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField typeField;

    @FXML
    private TextField prixField;

    @FXML
    private ComboBox<Etat> etatComboBox;

    @FXML
    private TextField descriptionField;

    @FXML
    private ComboBox<Status> statusComboBox;

    @FXML
    private Button uploadButton;

    @FXML
    private Label imageLabel;

    @FXML
    private ImageView imageView; // Référence à l'ImageView dans le FXML

    private Produitservice produitservice = new Produitservice();

    private DetailsController detailsController; // Référence à DetailsController

    private String imagePath; // Variable to store the image path

    // Méthode pour définir la référence à DetailsController
    public void setDetailsController(DetailsController detailsController) {
        this.detailsController = detailsController;
    }

    @FXML
    public void initialize() {
        // Remplir les ComboBox avec les valeurs des enums
        etatComboBox.getItems().setAll(Etat.values());
        statusComboBox.getItems().setAll(Status.values());
    }

    @FXML
    private void handleUploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());
        if (selectedFile != null) {
            try {
                // Dossier cible dans ton projet
                File destinationFolder = new File("src/main/resources/images/");
                if (!destinationFolder.exists()) {
                    destinationFolder.mkdirs();
                }

                // Nom de fichier unique (évite les conflits)
                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                File destinationFile = new File(destinationFolder, fileName);

                // Copier le fichier
                Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Stocker uniquement le nom du fichier (au lieu du chemin absolu)
              imagePath = "src/main/resources/images/" + fileName;

                // Charger l'image dans l'ImageView
                Image image = new Image(destinationFile.toURI().toString());
                imageView.setImage(image);

                // Afficher le nom du fichier dans le label (optionnel)
               imageLabel.setText(fileName);

                System.out.println("Image enregistrée : " + imagePath);
            } catch (IOException e) {
                showAlert("Erreur", "Impossible d'enregistrer l'image !");
                e.printStackTrace();
            }
        }
    }

    private boolean isValidNomType(String input) {
        return input.matches("^[A-Za-z][A-Za-z ]*$");
    }

    @FXML
    private void handleAjouterProduit() {
        // Récupérer les valeurs des champs
        String nom = nomField.getText();
        String type = typeField.getText();
        String prixStr = prixField.getText();
        Etat etat = etatComboBox.getValue();
        String description = descriptionField.getText();
        Status status = Status.NON_VENDU;

        if (nom.isEmpty() || type.isEmpty() || prixStr.isEmpty() || description.isEmpty() || etat == null || status == null) {
            showAlert("Erreur", "Tous les champs doivent être remplis !");
            return;
        }

        if (!isValidNomType(nom)) {
            showAlert("Erreur", "Le nom doit commencer par une lettre et ne contenir que des lettres et des espaces !");
            return;
        }

        if (!isValidNomType(type)) {
            showAlert("Erreur", "Le type doit commencer par une lettre et ne contenir que des lettres et des espaces !");
            return;
        }

        double prix;
        try {
            prix = Double.parseDouble(prixStr);
            if (prix <= 0) {
                showAlert("Erreur", "Le prix doit être supérieur à zéro !");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le prix doit être un nombre valide !");
            return;
        }

        if (description.length() < 8) {
            showAlert("Erreur", "La description doit contenir au moins 8 caractères !");
            return;
        }

        if (imagePath == null || imagePath.isEmpty()) {
            showAlert("Erreur", "Veuillez sélectionner une image !");
            return;
        }

        // Créer un nouveau produit
        Produit produit = new Produit(nom, type, prix, etat, description, status, imagePath);

        // Ajouter le produit à la base de données
        produitservice.addProduit(produit);

        // Rafraîchir la liste des produits dans DetailsController
        if (detailsController != null) {
            detailsController.loadProduits();
        }

        // Fermer la fenêtre
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }

    // Méthode pour afficher une alerte
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}