package Utilisateur.Pidev.Controllers;

import Utilisateur.Pidev.Entites.Utilisateur;
import Utilisateur.Pidev.Services.UtilisateurService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class login_user {
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField mail_field;
    @FXML
    private TextField mdp_field;
    @FXML
    private Button seconnecter_button;
    @FXML
    private Button sinscrire_button;
    @FXML
    private Button toggleButton;
    private boolean isPasswordVisible = false;
    private UtilisateurService utilisateurService = new UtilisateurService();

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = mail_field.getText();
        String password = mdp_field.getText();

        Utilisateur user = utilisateurService.authenticateUser(email, password);

        if (user != null) {
            try {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Parent root;

                if (user.getRole() == Utilisateur.Role.Admin || user.getRole() == Utilisateur.Role.Coach) {
                    root = FXMLLoader.load(getClass().getResource("/admin.fxml"));
                } else {
                    root = FXMLLoader.load(getClass().getResource("/update_user.fxml"));
                }

                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Erreur de connexion", "Email ou mot de passe incorrect.");
        }
    }
    @FXML
    private void handleSignUp(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/inscription.fxml"));

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void handleTogglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordField.setText(mdp_field.getText());
            passwordField.setVisible(true);
            mdp_field.setVisible(false);
            isPasswordVisible = false;
        } else {
            mdp_field.setText(passwordField.getText());
            mdp_field.setVisible(true);
            passwordField.setVisible(false);
            isPasswordVisible = true;
        }
    }
}
