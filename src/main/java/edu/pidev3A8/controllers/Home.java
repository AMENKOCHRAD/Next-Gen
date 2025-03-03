package edu.pidev3A8.controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Home extends Application {
    public static void main(String[] args) {
        launch(args); // Lancer l'application JavaFX
    }

    @Override
    public void start(Stage stage) {
        try {
            // Charger le fichier FXML
            Parent root = FXMLLoader.load(getClass().getResource("/Details_UserVente.fxml"));

            // Configurer la scène
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Afficher la fenêtre
            stage.setTitle("Détails des produits");
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur de lecture du fichier FXML : " + e.getMessage());
            e.printStackTrace(); // Afficher la stack trace pour plus de détails
        }
    }
}