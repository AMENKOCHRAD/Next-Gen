package edu.pidev3a8.controllers;

import edu.pidev3a8.entities.Nutrition;
import edu.pidev3a8.services.NutritionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import edu.pidev3a8.entities.CodeBarre;
import edu.pidev3a8.services.CodeBarreService;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HomeUserController implements Initializable {

    @FXML
    private TableView<Nutrition> nutritionTable;

    @FXML
    private TableColumn<Nutrition, Double> poidsColumn;

    @FXML
    private TableColumn<Nutrition, Double> tailleColumn;

    @FXML
    private TableColumn<Nutrition, String> sexeColumn;

    @FXML
    private TableColumn<Nutrition, Double> imcColumn;

    @FXML
    private TableColumn<Nutrition, Void> actionColumn;

    private NutritionService nutritionService = new NutritionService();

    private int userId; // ID de l'utilisateur connecté

    // Setter pour userId
    public void setUserId(int userId) {
        this.userId = userId;
        loadNutritionData(); // Recharger les données lorsque l'ID de l'utilisateur est défini
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurer les colonnes de la table
        poidsColumn.setCellValueFactory(new PropertyValueFactory<>("poids"));
        tailleColumn.setCellValueFactory(new PropertyValueFactory<>("taille"));
        sexeColumn.setCellValueFactory(new PropertyValueFactory<>("sexe"));
        imcColumn.setCellValueFactory(new PropertyValueFactory<>("imc"));
        actionColumn.setCellFactory(param -> new javafx.scene.control.TableCell<Nutrition, Void>() {
            private final Button downloadBtn = new Button("Télécharger");
            private final HBox buttonContainer = new HBox(10, downloadBtn);

            {
                downloadBtn.setOnAction(e -> {
                    Nutrition selectedNutrition = getTableView().getItems().get(getIndex());
                    handleDownloadPlan(selectedNutrition);  // Appel de la méthode pour gérer le téléchargement
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
        // Charger les données de la table
        // loadNutritionData() sera appelé après que userId soit défini
    }

    private void loadNutritionData() {
        if (userId == 0) {
            System.out.println("Aucun utilisateur connecté.");
            return;
        }

        // Récupérer la liste des nutrition pour l'utilisateur connecté
        List<Nutrition> nutritionList = nutritionService.getDataByUserId(userId);
        ObservableList<Nutrition> observableList = FXCollections.observableArrayList(nutritionList);
        nutritionTable.setItems(observableList);
    }
    private void handleDownloadPlan(Nutrition selectedNutrition) {
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

    
}