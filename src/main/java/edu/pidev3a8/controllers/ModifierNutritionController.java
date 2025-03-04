package edu.pidev3a8.controllers;

import edu.pidev3a8.entities.Nutrition;
import edu.pidev3a8.services.NutritionService;
import edu.pidev3a8.tools.MyConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ModifierNutritionController {

    @FXML
    private TextField poidsTextField;

    @FXML
    private TextField tailleTextField;

    @FXML
    private ComboBox<String> sexeComboBox;

    @FXML
    private Label idNutLabel;

    @FXML
    private Label imcLabel;

    @FXML
    private ComboBox<Integer> idUtilisateurComboBox; // ComboBox pour id_utilisateur

    private NutritionService nutritionService = new NutritionService();
    private Nutrition nutritionToModify;

    @FXML
    void initialize() {
        // Initialiser le ComboBox du sexe
        sexeComboBox.getItems().addAll("Femme", "Homme");
        sexeComboBox.getSelectionModel().selectFirst();

        // Charger les IDs des utilisateurs disponibles
        loadUtilisateurs();

    }

    public void setNutrition(Nutrition nutrition) {
        this.nutritionToModify = nutrition;

        // Afficher les données actuelles de la nutrition
        poidsTextField.setText(String.valueOf(nutrition.getPoids()));
        tailleTextField.setText(String.valueOf(nutrition.getTaille()));
        sexeComboBox.setValue(nutrition.getSexe());
        idNutLabel.setText(String.valueOf(nutrition.getId_nut()));
        idUtilisateurComboBox.setValue(nutrition.getId_utilisateur());

    }

    @FXML
    void enregistrerAction(ActionEvent event) {
        try {
            // Récupérer les valeurs saisies
            String poidsText = poidsTextField.getText();
            String tailleText = tailleTextField.getText();
            String sexe = sexeComboBox.getValue();
            Integer idUtilisateur = idUtilisateurComboBox.getValue(); // Récupérer l'ID utilisateur sélectionné

            // Valider les champs
            if (!validerNombre(poidsText, "poids") || !validerNombre(tailleText, "taille")) {
                return;
            }

            // Convertir les valeurs
            double poids = Double.parseDouble(poidsText.replace(",", "."));
            double taille = Double.parseDouble(tailleText.replace(",", "."));

            // Calculer l'IMC
            Double imc = calculerIMC(poids, taille);

            // Mettre à jour l'objet Nutrition
            nutritionToModify.setPoids(poids);
            nutritionToModify.setTaille(taille);
            nutritionToModify.setSexe(sexe);
            nutritionToModify.setImc(imc);
            nutritionToModify.setId_utilisateur(idUtilisateur); // Mettre à jour l'id_utilisateur

            // Enregistrer les modifications dans la base de données
            nutritionService.updateNutrition(nutritionToModify.getId_nut(), nutritionToModify);

            // Afficher un message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText("Les informations ont été mises à jour avec succès.");
            alert.setContentText("IMC calculé : " + String.format("%.2f", imc));
            alert.showAndWait();

            // Fermer la fenêtre et retourner à la liste
            Stage stage = (Stage) poidsTextField.getScene().getWindow();
            stage.close();
            retourListeAction(event);

        } catch (NumberFormatException e) {
            afficherErreur("Erreur de saisie", "Le poids et la taille doivent être des nombres valides.");
        }
    }

    @FXML
    void retourListeAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailNutrition.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) poidsTextField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            afficherErreur("Erreur", "Impossible de charger la liste des nourritures.");
        }
    }

    @FXML
    void annulerAction(ActionEvent event) {
        Stage stage = (Stage) poidsTextField.getScene().getWindow();
        stage.close();
        retourListeAction(event);
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

    private Double calculerIMC(Double poids, Double taille) {
        taille = taille / 100;
        return poids / (taille * taille);
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

    // Méthode pour charger les IDs des utilisateurs depuis la table utilisateur
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
            idUtilisateurComboBox.setItems(utilisateursList);
        } catch (SQLException e) {
            e.printStackTrace();
            afficherErreur("Erreur", "Erreur lors du chargement des utilisateurs : " + e.getMessage());
        }
    }
}