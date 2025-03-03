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
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class DetailsController_UserVente extends DetailsController {

    @FXML
    private FlowPane produitContainer; // Conteneur pour les cartes de produits

    @FXML
    private Button ajouterButton;

    @FXML
    private Button ajouterAuPanierButton;

    @FXML
    private Button voirPanierButton;
    private Produit selectedProduit; // Variable pour stocker le produit sélectionné
    @FXML
    private Button passerCommandeButton; // Bouton pour passer une commande

    private Produitservice produitservice = new Produitservice();
    private List<Produit> panier = new ArrayList<>();
    private Map<Produit, Integer> produitsLikes = new HashMap<>(); // Map pour stocker le nombre de likes par produit

    @FXML
    private Button searchButton;
    @FXML
    private TextField searchField;
    private boolean isAscendant = true;
    private ListView<String> commentairesListView; // Déclarer ici
    @FXML
    public void initialize() {
        // Charger les produits sous forme de cartes
        loadProduits();
    }

    @Override
    public void loadProduits() {
        // Vider le conteneur avant de charger les produits
        produitContainer.getChildren().clear();

        // Récupérer tous les produits
        List<Produit> produits = produitservice.getAllData();

        // Créer une carte pour chaque produit
        for (Produit produit : produits) {
            VBox produitCard = createProduitCard(produit);
            produitContainer.getChildren().add(produitCard);
        }
    }
    private VBox createProduitCard(Produit produit) {
        VBox card = new VBox(10);
        card.setStyle("-fx-border-color: #ccc; -fx-border-radius: 5; -fx-padding: 10; -fx-background-color: transparent;");
        card.setPrefWidth(260); // Largeur fixe pour chaque carte

        // Stocker le Produit dans le userData de la carte
        card.setUserData(produit);

        // Ajouter un gestionnaire d'événements pour sélectionner le produit
        card.setOnMouseClicked(event -> {
            selectedProduit = produit; // Mettre à jour le produit sélectionné
            System.out.println("Produit sélectionné : " + selectedProduit.getNom_produit());
        });

        // Image du produit
        ImageView imageView = new ImageView();
        try {
            File imageFile = new File(produit.getImage());
            if (imageFile.exists()) {
                String encodedImagePath = imageFile.toURI().toString();
                Image image = new Image(encodedImagePath);
                imageView.setImage(image);
                imageView.setFitWidth(240); // Largeur de l'image
                imageView.setFitHeight(160); // Hauteur de l'image
                imageView.setPreserveRatio(true);
            } else {
                // Image de remplacement si le fichier n'existe pas
                Image placeholder = new Image(getClass().getResource("/images/placeholder.png").toExternalForm());
                imageView.setImage(placeholder);
                imageView.setFitWidth(240);
                imageView.setFitHeight(160);
                imageView.setPreserveRatio(true);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image : " + produit.getImage());
            e.printStackTrace();
        }

        // Nom du produit
        Label nomLabel = new Label(produit.getNom_produit());
        nomLabel.setStyle("-fx-font-weight: bold;");

        // Prix du produit
        Label prixLabel = new Label("Prix: " + produit.getPrix() + " €");

        // Description du produit
        Label descriptionLabel = new Label(produit.getDescription());
        descriptionLabel.setWrapText(true); // Permettre le retour à la ligne pour la description

        // Bouton "Ajouter au panier"
        Button ajouterButton = new Button("Ajouter au panier");
        ajouterButton.setOnAction(event -> {
            panier.add(produit);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ajout au panier");
            alert.setHeaderText(null);
            alert.setContentText("Produit ajouté au panier avec succès !");
            alert.showAndWait();
        });

        // Bouton "Like"
        Button likeButton = new Button("Like");
        Label likeCountLabel = new Label("Likes: " + produitservice.getLikes(produit.getId_produit())); // Afficher le nombre de likes

        likeButton.setOnAction(event -> {
            // Mettre à jour le nombre de likes pour ce produit
            produitservice.ajouterLike(produit.getId_produit());
            int currentLikes = produitservice.getLikes(produit.getId_produit());
            likeCountLabel.setText("Likes: " + currentLikes);

            // Mettre à jour le texte du bouton et le label
            likeButton.setText("Liked");
            likeButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        });

        // Zone de commentaire
        Label commentLabel = new Label("Commentaire :");
        commentLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: rgba(0, 0, 0, 0.7);");
        TextField commentField = new TextField();
        commentField.setPromptText("Écrivez un commentaire...");

        // Bouton pour soumettre le commentaire
        Button submitCommentButton = new Button("Soumettre");
        submitCommentButton.setOnAction(event -> {
            String comment = commentField.getText().trim();
            if (!comment.isEmpty()) {
                // Enregistrer le commentaire dans la base de données (sans note)
                produitservice.ajouterCommentaire(produit.getId_produit(), comment);

                // Afficher un message de confirmation
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Commentaire soumis");
                alert.setHeaderText(null);
                alert.setContentText("Votre commentaire a été enregistré : " + comment);
                alert.showAndWait();

                // Réinitialiser le champ de commentaire
                commentField.clear();

                // Recharger les produits pour actualiser la page
                loadProduits();
            } else {
                // Afficher un message d'erreur si le commentaire est vide
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Commentaire vide");
                alert.setHeaderText(null);
                alert.setContentText("Veuillez écrire un commentaire.");
                alert.showAndWait();
            }
        });

        // Afficher les commentaires existants
        VBox commentairesBox = new VBox(5); // Conteneur pour les commentaires
        commentairesBox.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        List<String> commentaires = produitservice.getCommentaires(produit.getId_produit());
        for (String commentaire : commentaires) {
            HBox commentaireHBox = new HBox(5); // Conteneur pour un commentaire et son bouton de suppression
            commentaireHBox.setAlignment(Pos.CENTER_LEFT);

            // Label pour le commentaire
            Label commentaireLabel = new Label(commentaire);
            commentaireLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: rgba(0, 0, 0, 0.7);");

            // Bouton de suppression
            Button deleteButton = new Button("X"); // Texte réduit pour minimiser le bouton
            deleteButton.setStyle(
                    "-fx-background-color: #ff4444; " + // Couleur de fond rouge
                            "-fx-text-fill: white; " +         // Texte blanc
                            "-fx-font-size: 10px; " +          // Taille de police réduite
                            "-fx-padding: 2px 5px; " +         // Padding réduit
                            "-fx-min-width: 20px; " +          // Largeur minimale réduite
                            "-fx-min-height: 20px;"           // Hauteur minimale réduite
            );
            deleteButton.setOnAction(event -> {
                // Supprimer le commentaire de la base de données
                produitservice.supprimerCommentaire(produit.getId_produit(), commentaire);

                // Recharger les produits pour actualiser la page
                loadProduits();
            });

            // Ajouter le bouton et le label au HBox
            commentaireHBox.getChildren().addAll(deleteButton, commentaireLabel);

            // Ajouter le HBox à la VBox des commentaires
            commentairesBox.getChildren().add(commentaireHBox);
        }

        // Ajouter les éléments à la carte
        card.getChildren().addAll(
                imageView, nomLabel, prixLabel, descriptionLabel,
                ajouterButton, likeButton, likeCountLabel,
                commentLabel, commentField, submitCommentButton,
                commentairesBox // Utiliser la VBox des commentaires
        );

        return card;
    }
    // Les autres méthodes (handleVoirPanier, handleAjouterProduit, etc.) restent inchangées
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
    private void handleAjouterProduit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterProduit.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur de la fenêtre d'ajout
            AjouterProduitController ajouterProduitController = loader.getController();

            // Passer une référence de DetailsController_UserVente à AjouterProduitController
            ajouterProduitController.setDetailsController(this);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Ajouter un produit");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur de chargement de ajouterProduit.fxml : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePasserCommande() {
        try {
            // Charger la page DetailsCommande_user.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Details_UserCommande.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur de la page DetailsCommande_user
            DetailsCommande_userController detailsCommandeController = loader.getController();

            // Passer les données nécessaires (par exemple, le panier)
            // detailsCommandeController.setPanier(panier);

            // Créer une nouvelle scène et l'afficher
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Passer une commande");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur de chargement de DetailsCommande_user.fxml : " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    private void handleRechercheProduit() {
        String searchQuery = searchField.getText().toLowerCase().trim(); // Trim to remove leading/trailing spaces

        if (searchQuery.isEmpty()) {
            // Si le champ de recherche est vide, afficher tous les produits
            loadProduits();
        } else {
            // Filtrer les produits en fonction du nom
            List<Produit> filteredProduits = produitservice.searchProduitsByName(searchQuery);

            // Vider le conteneur avant d'ajouter les produits filtrés
            produitContainer.getChildren().clear();

            // Ajouter les produits filtrés au conteneur
            for (Produit produit : filteredProduits) {
                VBox produitCard = createProduitCard(produit);
                produitContainer.getChildren().add(produitCard);
            }
        }
    }
    @FXML
    private void handleShowQRCode() {
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
}