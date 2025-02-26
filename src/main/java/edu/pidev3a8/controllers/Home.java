package edu.pidev3a8.controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Home extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage)    {
        try {
            Parent root= FXMLLoader.load(getClass().getResource("/ChatbotView.fxml"));
            Scene scene=new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur de lecture"+e.getMessage());
            e.printStackTrace();
        }
    }
}
