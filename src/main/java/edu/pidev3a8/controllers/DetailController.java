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
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

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
            private final Button downloadBtn = new Button("Télécharger");
            private final HBox buttonContainer = new HBox(10, modifyBtn, deleteBtn,downloadBtn);
            {
                modifyBtn.setOnAction(e -> {
                    Nutrition selectedNutrition = getTableView().getItems().get(getIndex());
                    goToModifierNutrition(selectedNutrition);
                });

                deleteBtn.setOnAction(e -> {
                    Nutrition selectedNutrition = getTableView().getItems().get(getIndex());
                    deleteNutrition(selectedNutrition);
                });
                downloadBtn.setOnAction(e -> {
                    Nutrition selectedNutrition = getTableView().getItems().get(getIndex());
                    handleDownloadPlan(selectedNutrition);  // Appel de la fonction pour gérer le téléchargement
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
    @FXML
    private void handleDownloadPlan(Nutrition event) {
        // Récupérer la ligne sélectionnée
        Nutrition selectedNutrition = nutritionTable.getSelectionModel().getSelectedItem();

        if (selectedNutrition != null) {
            // Calculer l'IMC de la nutrition sélectionnée
            Double imc = selectedNutrition.getImc();

            // Obtenir l'URL du plan basé sur l'IMC
            String planUrl = getPlanUrlForIMC(imc);

            try {
                // Ouvrir ou télécharger le plan
                File planFile = new File(getClass().getResource(planUrl).toURI());
                if (planFile.exists()) {
                    try {
                        Desktop.getDesktop().open(planFile);  // Ouvre le fichier PDF dans le lecteur par défaut
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Fichier de plan non trouvé !");
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
                System.out.println("Erreur lors de la création de l'URI pour le plan.");
            }
        } else {
            System.out.println("Aucune nutrition sélectionnée !");
        }
    }

    private String getPlanUrlForIMC(Double imc) {
        // Arrondir l'IMC à deux décimales pour éviter les erreurs de précision
        imc = Math.round(imc * 100.0) / 100.0;

        // Afficher l'IMC pour le débogage avec plus de décimales
        System.out.println("IMC calculé avec précision : " + imc);

        // Vérification de la condition pour chaque plage d'IMC
        if (imc >= 24) {
            System.out.println("IMC >= 24, plan doc3.pdf sélectionné");
            return "/plan/doc3.pdf"; // Plan pour IMC >= 24
        } else if (imc >= 18.5 && imc < 24) {
            System.out.println("18.5 <= IMC < 24, plan doc2.pdf sélectionné");
            return "/plan/doc2.pdf"; // Plan pour 18.5 <= IMC < 24
        } else if (imc < 18.5) {
            System.out.println("IMC < 18.5, plan doc1.pdf sélectionné");
            return "/plan/plan1.pdf"; // Plan pour IMC < 18.5
        } else {
            System.out.println("Erreur : Cas inconnu d'IMC");
            return "/plan/plan1.pdf"; // Cas par défaut
        }
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