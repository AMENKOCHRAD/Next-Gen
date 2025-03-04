package edu.pidev3a8.controllers;

import edu.pidev3a8.utils.CurrentUser;
import edu.pidev3a8.entities.Utilisateur;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;

public class NavbarController {

    @FXML
    private Button coursButton;

    @FXML
    private void handleProfilButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Profil.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.show();

            Stage currentStage = (Stage) coursButton.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeconnexionButtonAction(ActionEvent event) {
        // Set current user values to null
        Utilisateur currentUser = getCurrentUser();
        if (currentUser != null) {
            currentUser.setEmail(null);
            currentUser.setMdp(null);
            currentUser.setNom(null);
            currentUser.setPrenom(null);
            currentUser.setDateNai(null);
            currentUser.setNumTel(0);
            currentUser.setGenre(null);
            currentUser.setAdresse(null);
            currentUser.setRole(null);
            currentUser.setSalaire(0);
            currentUser.setBanned(false);
            currentUser.setImage_user(null);
        }
        // Load the login interface
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current stage
            Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCoursButtonAction(ActionEvent event) {
        Utilisateur currentUser = getCurrentUser();
        if (currentUser != null) {
            try {
                FXMLLoader loader;
                if (currentUser.getRole() == Utilisateur.Role.Admin) {
                    loader = new FXMLLoader(getClass().getResource("/Cours_Back.fxml"));
                } else if (currentUser.getRole() == Utilisateur.Role.Adherent) {
                    loader = new FXMLLoader(getClass().getResource("/Cours_front.fxml"));
                } else {
                    showAlert("Access Denied", "You do not have permission to access this page.");
                    return;
                }

                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();

                // Close the current stage
                Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                currentStage.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private Utilisateur getCurrentUser() {
        return CurrentUser.getInstance().getCurrentUser();
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
