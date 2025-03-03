package edu.pidev3A8.controllers;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
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
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image; // Importation correcte pour JavaFX


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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

    @FXML
    private Button ajouterAuPanierButton;
    @FXML
    private Button voirPanierButton;

    private Produitservice produitservice = new Produitservice();
    private List<Produit> panier = new ArrayList<>();

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    private boolean isAscendant = true;  // Pour garder la trace du sens du tri

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
    @FXML
    private void handleAjouterAuPanier() {
        Produit selectedProduit = produitTable.getSelectionModel().getSelectedItem();
        if (selectedProduit != null) {
            panier.add(selectedProduit);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ajout au panier");
            alert.setHeaderText(null);
            alert.setContentText("Produit ajouté au panier avec succès !");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun produit sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un produit à ajouter au panier.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleVoirPanier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Panier.fxml"));
            Parent root = loader.load();

            // Passer la référence du PanierController
            PanierController panierController = loader.getController();
            panierController.setProduitsPanier(panier);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Panier");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur de chargement de Panier.fxml : " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void handleRechercheProduit() {
        String searchQuery = searchField.getText().toLowerCase();

        if (searchQuery.isEmpty()) {
            // Si le champ de recherche est vide, afficher tous les produits
            loadProduits();
        } else {
            // Filtrer les produits en fonction du nom
            List<Produit> filteredProduits = produitservice.searchProduitsByName(searchQuery);
            ObservableList<Produit> produits = FXCollections.observableArrayList(filteredProduits);
            produitTable.setItems(produits);
        }
    }
    @FXML
    private void handleTriParPrix() {
        ObservableList<Produit> produits = produitTable.getItems();

        if (isAscendant) {
            // Tri ascendant
            produits.sort(Comparator.comparingDouble(Produit::getPrix));
        } else {
            // Tri descendant
            produits.sort(Comparator.comparingDouble(Produit::getPrix).reversed());
        }

        // Rafraîchir le tableau avec la nouvelle liste triée
        produitTable.setItems(produits);

        // Inverser le sens du tri pour le prochain clic
        isAscendant = !isAscendant;
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
    @FXML
    private void handleShowQRCode() {
        // Récupérer le produit sélectionné
        Produit selectedProduit = produitTable.getSelectionModel().getSelectedItem();

        if (selectedProduit != null) {
            // Générer le texte du code QR avec l'image
            String qrCodeText = "Produit ID: " + selectedProduit.getId_produit() + "\n"
                    + "Nom: " + selectedProduit.getNom_produit() + "\n"
                    + "Type: " + selectedProduit.getType_produit() + "\n"
                    + "Prix: " + selectedProduit.getPrix() + "\n"
                    + "État: " + selectedProduit.getEtat() + "\n"
                    + "Description: " + selectedProduit.getDescription() + "\n"
                    + "Statut: " + selectedProduit.getStatus() + "\n"
                    + "Image: " + selectedProduit.getImage(); // Ajouter l'image

            try {
                // Générer le code QR en mémoire
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, 200, 200);

                // Convertir le code QR en image JavaFX
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                Image qrCodeImage = new Image(inputStream);

                // Afficher l'image dans une nouvelle fenêtre
                ImageView imageView = new ImageView(qrCodeImage);
                VBox vbox = new VBox(imageView);
                Scene scene = new Scene(vbox, 250, 250);
                Stage stage = new Stage();
                stage.setTitle("Code QR du Produit");
                stage.setScene(scene);
                stage.show();
            } catch (WriterException | IOException e) {
                // Afficher un message d'erreur en cas de problème
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Erreur lors de la génération du code QR : " + e.getMessage());
                alert.showAndWait();
            }
        } else {
            // Afficher un message d'erreur si aucun produit n'est sélectionné
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun produit sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un produit pour afficher son code QR.");
            alert.showAndWait();
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


