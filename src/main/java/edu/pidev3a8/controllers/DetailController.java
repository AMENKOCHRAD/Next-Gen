package edu.pidev3a8.controllers;

import edu.pidev3a8.entities.Reclamation;
import edu.pidev3a8.services.ReclamtionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class DetailController {
    @FXML
    private TableView<Reclamation> reclamationsTable;

    @FXML
    private TableColumn<Reclamation, String> sujetColumn;

    @FXML
    private TableColumn<Reclamation, String> descriptionColumn;

    @FXML
    private TableColumn<Reclamation, String> categorieColumn;

    @FXML
    private TableColumn<Reclamation, HBox> piecesJointesColumn;
    @FXML
    private TableColumn<Reclamation, Integer> idColumn; // Colonne ID
    @FXML
    private TableColumn<Reclamation, String> dateColumn; // Colonne Date
    @FXML
    private TableColumn<Reclamation, String> statutColumn; // Colonne Statut

    private ObservableList<Reclamation> reclamations = FXCollections.observableArrayList();
    private ReclamtionService reclamtionService = new ReclamtionService();

    @FXML
    public void initialize() {
        // Configurer les colonnes
        // Configurer les colonnes
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        sujetColumn.setCellValueFactory(new PropertyValueFactory<>("sujet"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        categorieColumn.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Configurer la colonne des pièces jointes
        piecesJointesColumn.setCellValueFactory(new PropertyValueFactory<>("piecesJointesBox"));
        piecesJointesColumn.setCellFactory(param -> new TableCell<Reclamation, HBox>() {
            @Override
            protected void updateItem(HBox item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(item);
                }
            }
        });

        // Charger les réclamations depuis la base de données
        loadReclamationsFromDatabase();
        reclamationsTable.setItems(reclamations);
    }


    public void loadReclamationsFromDatabase() {
        List<Reclamation> reclamationList = reclamtionService.getAllData();
        reclamations.clear();
        reclamations.addAll(reclamationList);

        // Configurer les pièces jointes pour chaque réclamation
        for (Reclamation reclamation : reclamations) {
            updatePiecesJointesBox(reclamation);
        }
    }
    private void updatePiecesJointesBox(Reclamation reclamation) {
        HBox piecesJointesBox = new HBox();
        piecesJointesBox.setSpacing(10);

        // Vider la HBox avant d'ajouter de nouvelles images
        piecesJointesBox.getChildren().clear();

        for (String pieceJointe : reclamation.getPiecesJointes()) {
            File fichier = new File(pieceJointe);
            if (fichier.exists()) {
                if (pieceJointe.toLowerCase().endsWith(".png") || pieceJointe.toLowerCase().endsWith(".jpg") || pieceJointe.toLowerCase().endsWith(".jpeg")) {
                    // Si c'est une image, l'afficher dans un ImageView
                    Image image = new Image(fichier.toURI().toString());
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(160); // Ajuster la largeur de l'image
                    imageView.setFitHeight(160); // Ajuster la hauteur de l'image
                    piecesJointesBox.getChildren().add(imageView);
                } else {
                    // Si c'est un autre type de fichier, afficher une icône
                    Label fichierLabel = new Label("📄 " + fichier.getName());
                    piecesJointesBox.getChildren().add(fichierLabel);
                }
            }
        }

        reclamation.setPiecesJointesBox(piecesJointesBox); // Mettre à jour la HBox
    }

    public void addReclamation(Reclamation reclamation) {
        reclamations.add(reclamation); // Ajouter un objet Reclamation à la liste
        updatePiecesJointesBox(reclamation); // Mettre à jour les pièces jointes
        reclamationsTable.refresh(); // Rafraîchir la TableView
        loadReclamationsFromDatabase(); // Recharger les données depuis la base de données
    }

    @FXML
    private void handleSupprimerReclamation() {
        Reclamation selectedReclamation = reclamationsTable.getSelectionModel().getSelectedItem();
        if (selectedReclamation != null) {
            // Confirmation avant suppression
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Voulez-vous vraiment supprimer cette réclamation ?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Supprimer la réclamation de la base de données
                reclamtionService.deleteEntity(selectedReclamation);
                // Recharger la liste des réclamations
                loadReclamationsFromDatabase();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Avertissement");
            alert.setHeaderText("Aucune réclamation sélectionnée !");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleModifierReclamation() {
        Reclamation selectedReclamation = reclamationsTable.getSelectionModel().getSelectedItem();
        if (selectedReclamation != null) {
            try {
                // Charger la page ModifierReclamation.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierReclamation.fxml"));
                Parent root = loader.load();

                // Passer la réclamation sélectionnée au contrôleur de la page ModifierReclamation
                ModifierReclamationController modifierController = loader.getController();
                modifierController.setReclamation(selectedReclamation);

                // Définir le callback pour rafraîchir la TableView après la mise à jour
                modifierController.setOnReclamationUpdated(() -> {
                    loadReclamationsFromDatabase(); // Recharger les données depuis la base de données
                    reclamationsTable.refresh(); // Rafraîchir la TableView
                });

                // Afficher la page ModifierReclamation
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Modifier Réclamation");
                stage.show();
            } catch (IOException e) {
                System.err.println("Erreur lors du chargement de la page ModifierReclamation : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Avertissement");
            alert.setHeaderText("Aucune réclamation sélectionnée !");
            alert.showAndWait();
        }
    }
    @FXML
    private void handleAjouterReclamation() {
        try {
            // Charger la page AjouterReclamation.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterReclamation.fxml"));
            Parent root = loader.load();

            // Passer le contrôleur actuel au contrôleur de la page AjouterReclamation
            AjouterReclamationController ajouterController = loader.getController();
            ajouterController.setDetailController(this);

            // Afficher la page AjouterReclamation
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter une Réclamation");
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la page AjouterReclamation : " + e.getMessage());
            e.printStackTrace();
        }
    }
}