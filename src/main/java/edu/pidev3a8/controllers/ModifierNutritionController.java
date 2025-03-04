package edu.pidev3a8.controllers;

import edu.pidev3a8.entities.Nutrition;
import edu.pidev3a8.services.NutritionService;
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

public class ModifierNutritionController {

    @FXML
    private TextField poidsTextField;

    @FXML
    private TextField tailleTextField;

    @FXML
    private ComboBox<String> sexeComboBox;

    @FXML
    private Label idNutLabel;  // Label pour afficher l'ID de la nutrition
    @FXML
    private Label imcLabel;

    private NutritionService nutritionService = new NutritionService();
    private Nutrition nutritionToModify; // Nutrition à modifier

    @FXML
    void initialize() {
        // Initialiser la ComboBox avec les options "Femme" et "Homme"
        sexeComboBox.getItems().addAll("Femme", "Homme");
        sexeComboBox.getSelectionModel().selectFirst(); // Sélectionner "Femme" par défaut
    }

    // Méthode pour passer l'objet Nutrition depuis le DetailController
    public void setNutrition(Nutrition nutrition) {
        this.nutritionToModify = nutrition;

        // Pré-remplir les champs avec les données de l'objet Nutrition
        poidsTextField.setText(String.valueOf(nutrition.getPoids()));
        tailleTextField.setText(String.valueOf(nutrition.getTaille()));
        sexeComboBox.setValue(nutrition.getSexe());

        // Afficher l'ID dans le Label
        idNutLabel.setText(String.valueOf(nutrition.getId_nut()));
    }

    @FXML
    void enregistrerAction(ActionEvent event) {
        try {
            // Récupérer les valeurs des champs
            String poidsText = poidsTextField.getText();
            String tailleText = tailleTextField.getText();
            String sexe = sexeComboBox.getValue();

            // Valider les champs
            if (!validerNombre(poidsText, "poids") || !validerNombre(tailleText, "taille")) {
                return; // Si la validation échoue, ne rien faire
            }

            double poids = Float.parseFloat(poidsText.replace(",", ".")); // Utilisation de float au lieu de double
            double taille = Float.parseFloat(tailleText.replace(",", ".")); // Utilisation de float au lieu de double

            // Calculer l'IMC
            Double imc = calculerIMC(poids, taille); // IMC est maintenant un float

            // Mettre à jour la nutrition via le service en utilisant l'ID existant
            nutritionToModify.setPoids(poids);
            nutritionToModify.setTaille(taille);
            nutritionToModify.setSexe(sexe);
            nutritionToModify.setImc(imc);

            // Utiliser l'ID de la nutrition existante pour l'update
            nutritionService.updateNutrition(nutritionToModify.getId_nut(), nutritionToModify);

            // Afficher l'IMC dans un message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText("Les informations ont été mises à jour avec succès.");
            alert.setContentText("IMC calculé : " + String.format("%.2f", imc));
            alert.showAndWait();

            // Fermer la fenêtre de modification
            Stage stage = (Stage) poidsTextField.getScene().getWindow();
            stage.close();
            retourListeAction(event);

        } catch (NumberFormatException e) {
            // Gérer les erreurs de saisie
            afficherErreur("Erreur de saisie", "Le poids et la taille doivent être des nombres valides.");
        }
    }

    // Méthode pour calculer l'IMC (IMC calculé avec float)
    private Double calculerIMC( Double poids, Double taille) {
        // Formule de l'IMC : IMC = poids (kg) / taille² (m²)
        taille = taille / 100; // Convertir la taille en mètres
        return poids / (taille * taille); // Retourne un float pour correspondre au type requis
    }



    @FXML
    void retourListeAction(ActionEvent event) {
        // Naviguer vers la liste des nourritures
        try {
            // Charger la page de la liste
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Detail.fxml"));
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
        // Fermer la fenêtre de modification sans enregistrer les données
        Stage stage = (Stage) poidsTextField.getScene().getWindow();
        stage.close();
        retourListeAction(event);
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
    void updateIMC() {
        try {
            // Récupérer les valeurs des champs poids et taille
            String poidsText = poidsTextField.getText();
            String tailleText = tailleTextField.getText();

            // Vérifier si les deux champs ne sont pas vides
            if (!poidsText.isEmpty() && !tailleText.isEmpty()) {
                // Convertir les valeurs en double
                double poids = Double.parseDouble(poidsText.replace(",", "."));
                double taille = Double.parseDouble(tailleText.replace(",", "."));

                // Calculer l'IMC
                double imc = calculerIMC(poids, taille);

                // Mettre à jour l'IMC dans le label
                imcLabel.setText(String.format("%.2f", imc));
            } else {
                // Si l'un des champs est vide, afficher "Non calculé"
                imcLabel.setText("Non calculé");
            }
        } catch (NumberFormatException e) {
            // Si les valeurs saisies ne sont pas des nombres valides
            imcLabel.setText("Non calculé");
        }
    }
}
