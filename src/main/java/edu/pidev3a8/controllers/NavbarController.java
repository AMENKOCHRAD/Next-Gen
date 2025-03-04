package edu.pidev3a8.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class NavbarController {

    @FXML
    private Button nutButton;

    @FXML
    private void handleNutritionButtonAction() {
        try {
            System.out.println("Bouton Nutrition cliqué !");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailNutrition.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            // Fermer la fenêtre actuelle si nécessaire
            Stage currentStage = (Stage) nutButton.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement de Detail.fxml : " + e.getMessage());
            e.printStackTrace();
        }
    }
}