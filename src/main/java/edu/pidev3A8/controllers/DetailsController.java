package edu.pidev3A8.controllers;

import edu.pidev3A8.entities.Commande;
import edu.pidev3A8.entities.Produit;
import edu.pidev3A8.services.Produitservice;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image; // Importation correcte pour JavaFX


import java.io.IOException;
import java.util.Optional;
import java.io.File;
public class DetailsController {

    @FXML
    private TableView<Produit> produitTable;

    @FXML
    private TableColumn<Produit, Integer> idColumn;

    @FXML
    private TableColumn<Produit, String> nomColumn;

    @FXML
    private TableColumn<Produit, String> typeColumn;

    @FXML
    private TableColumn<Produit, Double> prixColumn;

    @FXML
    private TableColumn<Produit, String> etatColumn;

    @FXML
    private TableColumn<Produit, String> descriptionColumn;

    @FXML
    private TableColumn<Produit, String> statusColumn;
    @FXML
    private TableColumn<Produit, String> imageColumn; // Assurez-vous que cette ligne existe

    private Produitservice produitservice = new Produitservice();

    @FXML
    public void initialize() {
        // Lier les colonnes aux propriétés de la classe Produit
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id_produit"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom_produit"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type_produit"));
        prixColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
        etatColumn.setCellValueFactory(new PropertyValueFactory<>("etat"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("image"));

        // Configurer la cellule pour afficher les images
        imageColumn.setCellFactory(column -> {
            return new TableCell<Produit, String>() {
                private final ImageView imageView = new ImageView();

                @Override
                protected void updateItem(String imagePath, boolean empty) {
                    super.updateItem(imagePath, empty);

                    if (empty || imagePath == null || imagePath.isEmpty()) {
                        setGraphic(null);
                    } else {
                        try {
                            File imageFile = new File(imagePath);
                            if (imageFile.exists()) {
                                String encodedImagePath = imageFile.toURI().toString();
                                Image image = new Image(encodedImagePath);
                                imageView.setImage(image);
                                imageView.setFitWidth(100);
                                imageView.setFitHeight(100);
                                imageView.setPreserveRatio(true);
                                setGraphic(imageView);
                            } else {
                                System.err.println("Le fichier image n'existe pas : " + imagePath);
                                // Afficher une image de remplacement
                                Image placeholder = new Image(getClass().getResource("/images/placeholder.png").toExternalForm());
                                imageView.setImage(placeholder);
                                imageView.setFitWidth(100);
                                imageView.setFitHeight(100);
                                imageView.setPreserveRatio(true);
                                setGraphic(imageView);
                            }
                        } catch (Exception e) {
                            System.err.println("Erreur lors du chargement de l'image : " + imagePath);
                            e.printStackTrace();
                            setGraphic(null);
                        }
                    }
                }
            };
        });

        // Charger les produits
        loadProduits();
    }

    // Méthode pour charger les produits depuis la base de données
    public void loadProduits() {
        ObservableList<Produit> produits = FXCollections.observableArrayList(produitservice.getAllData());
        produitTable.setItems(produits);
    }

    // Méthode pour gérer l'ajout d'un produit
    @FXML
    private void handleAjouterProduit() {
        try {
            // Charger le fichier FXML pour l'ajout d'un produit
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterProduit.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur de la fenêtre d'ajout
            AjouterProduitController ajouterProduitController = loader.getController();

            // Passer une référence de DetailsController à AjouterProduitController
            ajouterProduitController.setDetailsController(this);

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Créer une nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("Ajouter un produit");
            stage.setScene(scene);

            // Afficher la fenêtre
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur de chargement de ajouterProduit.fxml : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Méthode pour gérer la suppression d'un produit
    @FXML
    private void handleSupprimerProduit() {
        // Récupérer le produit sélectionné
        Produit selectedProduit = produitTable.getSelectionModel().getSelectedItem();

        if (selectedProduit != null) {
            // Afficher une boîte de dialogue de confirmation
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Supprimer le produit");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer ce produit ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Supprimer le produit via le service
                produitservice.deleteProduit(selectedProduit);

                // Rafraîchir la liste des produits
                loadProduits();
            }
        } else {
            // Afficher un message d'erreur si aucun produit n'est sélectionné
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun produit sélectionné");
            alert.setHeaderText("Aucun produit sélectionné");
            alert.setContentText("Veuillez sélectionner un produit à supprimer.");
            alert.showAndWait();
        }
    }

    // Méthode pour gérer la modification d'un produit
    @FXML
    private void handleModifierProduit() {
        // Récupérer le produit sélectionné
        Produit selectedProduit = produitTable.getSelectionModel().getSelectedItem();

        if (selectedProduit != null) {
            try {
                // Charger le fichier FXML pour la modification d'un produit
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierProduit.fxml"));
                Parent root = loader.load();

                // Obtenir le contrôleur de la fenêtre de modification
                ModifierProduitController modifierProduitController = loader.getController();

                // Passer le produit sélectionné à ModifierProduitController
                modifierProduitController.setProduitToModify(selectedProduit);

                // Passer une référence de DetailsController à ModifierProduitController
                modifierProduitController.setDetailsController(this);

                // Créer une nouvelle scène
                Scene scene = new Scene(root);

                // Créer une nouvelle fenêtre
                Stage stage = new Stage();
                stage.setTitle("Modifier un produit");
                stage.setScene(scene);

                // Afficher la fenêtre
                stage.show();
            } catch (IOException e) {
                System.out.println("Erreur de chargement de modifierProduit.fxml : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // Afficher un message d'erreur si aucun produit n'est sélectionné
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun produit sélectionné");
            alert.setHeaderText("Aucun produit sélectionné");
            alert.setContentText("Veuillez sélectionner un produit à modifier.");
            alert.showAndWait();
        }
    }
    @FXML
    private void handleVoirCommandes() {
        try {
            // Charger le fichier FXML pour afficher les commandes
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailsCommande.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Créer une nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("Liste des Commandes");
            stage.setScene(scene);

            // Afficher la fenêtre
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur de chargement de DetailsCommande.fxml : " + e.getMessage());
            e.printStackTrace();
        }
    }

}


