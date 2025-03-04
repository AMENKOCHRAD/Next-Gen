package edu.pidev3a8.controllers;

import edu.pidev3a8.entities.Nutrition;
import edu.pidev3a8.services.NutritionService;
import edu.pidev3a8.tools.MyConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class UserNutritionController implements Initializable {

    @FXML
    private TextField poidsTextField;

    @FXML
    private TextField tailleTextField;

    @FXML
    private ComboBox<String> sexeComboBox;

    @FXML
    private Label imcLabel;

    @FXML
    private ComboBox<Integer> utilisateurComboBox; // ComboBox pour sélectionner l'utilisateur

    private NutritionService nutritionService = new NutritionService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialiser le ComboBox pour le sexe
        ObservableList<String> options = FXCollections.observableArrayList("Femme", "Homme");
        sexeComboBox.setItems(options);
        sexeComboBox.getSelectionModel().selectFirst();

        // Charger les utilisateurs dans le ComboBox
        loadUtilisateurs();
    }

    private void loadUtilisateurs() {
        ObservableList<Integer> utilisateursList = FXCollections.observableArrayList();

        String query = "SELECT id FROM utilisateur";
        try (Connection conn = MyConnection.getInstance().getCnx();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int idUtilisateur = rs.getInt("id");
                utilisateursList.add(idUtilisateur);
            }
            utilisateurComboBox.setItems(utilisateursList);
        } catch (SQLException e) {
            e.printStackTrace();
            afficherErreur("Erreur", "Erreur lors du chargement des utilisateurs : " + e.getMessage());
        }
    }

    @FXML
    void updateIMC() {
        try {
            String poidsText = poidsTextField.getText();
            String tailleText = tailleTextField.getText();

            if (!poidsText.isEmpty() && !tailleText.isEmpty()) {
                double poids = Double.parseDouble(poidsText.replace(",", "."));
                double taille = Double.parseDouble(tailleText.replace(",", "."));

                double imc = calculerIMC(poids, taille);
                imcLabel.setText(String.format("%.2f", imc));
            } else {
                imcLabel.setText("Non calculé");
            }
        } catch (NumberFormatException e) {
            imcLabel.setText("Non calculé");
        }
    }

    @FXML
    void ajouterNutritionAction(ActionEvent event) {
        try {
            // Récupérer les valeurs des champs
            String poidsText = poidsTextField.getText();
            String tailleText = tailleTextField.getText();
            String sexe = sexeComboBox.getValue();
            Integer idUtilisateur = utilisateurComboBox.getValue();

            // Valider les champs
            if (!validerNombre(poidsText, "poids")) {
                return;
            }
            if (!validerNombre(tailleText, "taille")) {
                return;
            }
            if (idUtilisateur == null) {
                afficherErreur("Erreur", "Veuillez sélectionner un utilisateur.");
                return;
            }

            // Convertir les valeurs en double
            double poids = Double.parseDouble(poidsText.replace(",", "."));
            double taille = Double.parseDouble(tailleText.replace(",", "."));

            // Créer l'objet Nutrition
            Nutrition nutrition = new Nutrition(poids, taille, sexe, idUtilisateur);

            // Ajouter la nutrition à la base de données
            nutritionService.addNutrition(nutrition);

            // Afficher un message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText("Nutrition ajoutée avec succès !");
            alert.setContentText("IMC calculé : " + String.format("%.2f", nutrition.getImc()));
            alert.showAndWait();

           goToHome();

        } catch (NumberFormatException e) {
            afficherErreur("Erreur de saisie", "Le poids et la taille doivent être des nombres valides.");
        } catch (Exception e) {
            e.printStackTrace(); // Afficher la stack trace complète
            afficherErreur("Erreur", "Une erreur s'est produite lors de l'ajout de la nutrition : " + e.getMessage());
        }
    }
    private boolean validerNombre(String valeur, String champ) {
        String regex = "^[0-9]+([,.][0-9]+)?$";

        if (valeur == null || valeur.isEmpty()) {
            afficherErreur("Erreur de saisie", "Le champ " + champ + " est vide.");
            return false;
        }

        if (!valeur.matches(regex)) {
            afficherErreur("Erreur de saisie", "Le champ " + champ + " doit contenir uniquement des chiffres et une virgule ou un point.");
            return false;
        }

        return true;
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void goToHome() {
        try {
            // Charger la page homeUser.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeUser.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de la page homeUser
            HomeUserController homeUserController = loader.getController();

            // Passer l'ID de l'utilisateur connecté au contrôleur homeUser
            homeUserController.setUserId(utilisateurComboBox.getValue());

            // Afficher la nouvelle scène
            Stage stage = (Stage) poidsTextField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            afficherErreur("Erreur", "Une erreur s'est produite lors de la redirection.");
        }
    }

    private double calculerIMC(double poids, double taille) {
        taille = taille / 100;  // Convertir la taille en mètres
        return poids / (taille * taille);  // Retourner l'IMC calculé
    }
}