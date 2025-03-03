package edu.pidev3A8.controllers;

import edu.pidev3A8.entities.Etat;
import edu.pidev3A8.entities.Produit;
import edu.pidev3A8.entities.Status;
import edu.pidev3A8.services.Produitservice;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ModifierProduitController {

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
    private ImageView imageVieww; // Référence à l'ImageView dans le FXML
    @FXML
    private Label imageLabel;

    private Produit produitToModify; // Produit à modifier
    private Produitservice produitservice = new Produitservice();
    private DetailsController detailsController; // Référence à DetailsController

    private String imagePath; // Variable to store the image path

    // Méthode pour définir le produit à modifier
    public void setProduitToModify(Produit produit) {
        this.produitToModify = produit;

        // Remplir les champs avec les données du produit
        nomField.setText(produit.getNom_produit());
        typeField.setText(produit.getType_produit());
        prixField.setText(String.valueOf(produit.getPrix()));
        etatComboBox.setValue(produit.getEtat());
        descriptionField.setText(produit.getDescription());
        statusComboBox.setValue(produit.getStatus());
        imageLabel.setText(produit.getImage() != null ? produit.getImage() : "No image selected");
        imagePath = produit.getImage(); // Initialize imagePath with the current image
    }

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

        // Ouvrir la boîte de dialogue pour sélectionner une image
        File selectedFile = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());
        if (selectedFile != null) {
            try {
                // Dossier cible pour stocker les images dans le projet
                File destinationFolder = new File("src/main/resources/images/");
                if (!destinationFolder.exists()) {
                    destinationFolder.mkdirs(); // Créer le dossier s'il n'existe pas
                }

                // Générer un nom de fichier unique pour éviter les conflits
                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                File destinationFile = new File(destinationFolder, fileName);

                // Copier l'image sélectionnée dans le dossier du projet
                Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Stocker le chemin relatif de l'image (pour la base de données)
                imagePath = "src/main/resources/images/" + fileName;
                //imageLabel.setText(fileName); // Afficher le nom de l'image dans l'interface
                // Charger l'image dans l'ImageView
                Image image = new Image(destinationFile.toURI().toString());
                imageVieww.setImage(image);
                // Afficher un message de succès
                System.out.println("Image uploadée avec succès. Chemin : " + imagePath);
            } catch (IOException e) {
                // Gestion des erreurs
                showAlert("Erreur", "Impossible de copier l'image !");
                e.printStackTrace();
            }
        }
    }
    @FXML
    private void handleModifierProduit() {
        // Récupérer les nouvelles valeurs des champs
        String nom = nomField.getText();
        String type = typeField.getText();
        double prix = Double.parseDouble(prixField.getText());
        Etat etat = etatComboBox.getValue();
        String description = descriptionField.getText();
        Status status = statusComboBox.getValue();

        // Si aucune nouvelle image n'est sélectionnée, garder l'image actuelle
        if (imagePath == null || imagePath.isEmpty()) {
            imagePath = produitToModify.getImage(); // Garder l'image actuelle
        }

        // Mettre à jour l'objet Produit
        produitToModify.setNom_produit(nom);
        produitToModify.setType_produit(type);
        produitToModify.setPrix(prix);
        produitToModify.setEtat(etat);
        produitToModify.setDescription(description);
        produitToModify.setStatus(status);
        produitToModify.setImage(imagePath); // Mise à jour de l'image

        // Effectuer la mise à jour dans la base de données
        produitservice.updateProduit(produitToModify.getId_produit(), produitToModify);

        // Rafraîchir la liste des produits dans DetailsController
        if (detailsController != null) {
            detailsController.loadProduits();
        }

        // Fermer la fenêtre de modification
        Stage stage = (Stage) nomField.getScene().getWindow();
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