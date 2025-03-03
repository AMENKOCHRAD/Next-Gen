package edu.pidev3A8.controllers;

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
        Status status = statusComboBox.getValue();

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

        // Afficher les valeurs avant insertion
        System.out.println("Produit à ajouter :");
        System.out.println("Nom: " + nom);
        System.out.println("Type: " + type);
        System.out.println("Prix: " + prix);
        System.out.println("État: " + etat);
        System.out.println("Description: " + description);
        System.out.println("Status: " + status);
        System.out.println("ImagePath: " + imagePath);

        Produit produit = new Produit(nom, type, prix, etat, description, status, imagePath);

        produitservice.addProduit(produit);

        if (detailsController != null) {
            detailsController.loadProduits();
        }

        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }

    // Méthode pour afficher une alerte
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}