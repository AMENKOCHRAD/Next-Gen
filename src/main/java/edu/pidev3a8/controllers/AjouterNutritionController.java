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

public class AjouterNutritionController implements Initializable {

    @FXML
    private TextField poidsTextField;

    @FXML
    private TextField tailleTextField;

    @FXML
    private ComboBox<String> sexeComboBox;
    @FXML
    private Label imcLabel;
    @FXML
    private ComboBox<Integer> utilisateurComboBox;

    private NutritionService nutritionService = new NutritionService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> options = FXCollections.observableArrayList("Femme", "Homme");
        sexeComboBox.setItems(options);
        sexeComboBox.getSelectionModel().selectFirst();
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

            String poidsText = poidsTextField.getText();
            String tailleText = tailleTextField.getText();
            String sexe = sexeComboBox.getValue();
            Integer idUtilisateur = utilisateurComboBox.getValue();

            if (!validerNombre(poidsText, "poids")) {
                return; // Arrêter si la validation échoue
            }
            if (!validerNombre(tailleText, "taille")) {
                return; // Arrêter si la validation échoue
            }

            double poids = Double.parseDouble(poidsText.replace(",", "."));
            double taille = Double.parseDouble(tailleText.replace(",", "."));


            Nutrition nutrition = new Nutrition(poids, taille, sexe,idUtilisateur);

            nutritionService.addNutrition(nutrition);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText("Nutrition ajoutée avec succès !");
            alert.setContentText("IMC calculé : " + String.format("%.2f", nutrition.getImc()));
            alert.showAndWait();

            goToHome();

        } catch (NumberFormatException e) {
            afficherErreur("Erreur de saisie", "Le poids et la taille doivent être des nombres valides.");
        } catch (Exception e) {
            System.out.println(e);
            afficherErreur("Erreur", "Une erreur s'est produite lors de l'ajout de la nutrition.");
        }
    }


    private boolean validerNombre(String valeur, String champ) {
        // Expression régulière pour valider un nombre avec virgule ou point
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
    private void goToHome() {//retour a la page home
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailNutrition.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) poidsTextField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private double calculerIMC(double poids, double taille) {
        // Calculer l'IMC : IMC = poids (kg) / taille² (m²)
        taille = taille / 100;  // Convertir la taille en mètres
        return poids / (taille * taille);  // Retourner l'IMC calculé
    }
}
