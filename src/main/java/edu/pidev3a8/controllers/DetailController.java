package edu.pidev3a8.controllers;

import edu.pidev3a8.entities.Nutrition;
import edu.pidev3a8.services.NutritionService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;

public class DetailController {

    @FXML
    private TableView<Nutrition> nutritionTable;
    @FXML
    private TableColumn<Nutrition, Integer> idColumn;
    @FXML
    private TableColumn<Nutrition, Double> poidsColumn;
    @FXML
    private TableColumn<Nutrition, Double> tailleColumn;
    @FXML
    private TableColumn<Nutrition, String> sexeColumn;
    @FXML
    private TableColumn<Nutrition, Float> imcColumn;
    @FXML
    private TableColumn<Nutrition, Void> actionsColumn;
    @FXML
    private Button saveButton;

    private final NutritionService nutritionService = new NutritionService();

    @FXML
    public void initialize() {
        // Configurer les colonnes de la TableView
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id_nut"));
        poidsColumn.setCellValueFactory(new PropertyValueFactory<>("poids"));
        tailleColumn.setCellValueFactory(new PropertyValueFactory<>("taille"));
        sexeColumn.setCellValueFactory(new PropertyValueFactory<>("sexe"));
        imcColumn.setCellValueFactory(new PropertyValueFactory<>("imc"));

        // Ajouter les boutons Modifier et Supprimer à la colonne Actions
        actionsColumn.setCellFactory(param -> new TableCell<Nutrition, Void>() {
            private final Button modifyBtn = new Button("Modifier");
            private final Button deleteBtn = new Button("Supprimer");
            private final HBox buttonContainer = new HBox(10, modifyBtn, deleteBtn);

            {
                modifyBtn.setOnAction(e -> {
                    Nutrition selectedNutrition = getTableView().getItems().get(getIndex());
                    goToModifierNutrition(selectedNutrition);
                });

                deleteBtn.setOnAction(e -> {
                    Nutrition selectedNutrition = getTableView().getItems().get(getIndex());
                    deleteNutrition(selectedNutrition);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonContainer);
                }
            }
        });

        // Charger les données depuis le service
        loadNutritionData();
    }

    private void loadNutritionData() {
        ObservableList<Nutrition> nutritionList = FXCollections.observableArrayList(nutritionService.getAllData());
        System.out.println("Données récupérées : " + nutritionList);
        nutritionTable.setItems(nutritionList);
    }


    private void deleteNutrition(Nutrition nutrition) {
        System.out.println("Suppression de la nutrition: " + nutrition);
        nutritionService.deleteNutrition(nutrition);
        loadNutritionData(); // Rafraîchir la liste après suppression
    }

    @FXML
    private void goToAjouterNutrition() {
        try {
            System.out.println("Chargement de ajouterNutrition.fxml...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterNutrition.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nutritionTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur : fichier ajouterNutrition.fxml introuvable !");
            e.printStackTrace();
        }
    }

    private void goToModifierNutrition(Nutrition selectedNutrition) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModiifierNutrition.fxml"));
            Parent root = loader.load();

            ModifierNutritionController controller = loader.getController();
            controller.setNutrition(selectedNutrition);  // Passer les détails de la nutrition sélectionnée

            Stage stage = (Stage) nutritionTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToGestionCodeBarre(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML correspondant à la gestion des codes à barre
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/detailCode.fxml"));
            Parent root = loader.load();

            // Obtenir la scène de la fenêtre actuelle et la remplacer par la nouvelle scène
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}