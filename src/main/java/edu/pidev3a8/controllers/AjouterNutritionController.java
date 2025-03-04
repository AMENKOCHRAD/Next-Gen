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
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
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

    private NutritionService nutritionService = new NutritionService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialiser la ComboBox avec les options "Femme" et "Homme"
        ObservableList<String> options = FXCollections.observableArrayList("Femme", "Homme");
        sexeComboBox.setItems(options);
        sexeComboBox.getSelectionModel().selectFirst(); // Sélectionner "Femme" par défaut

    }

    @FXML
    void updateIMC() {
        try {
            // Récupérer les valeurs des champs texte
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
    @FXML
    void ajouterNutritionAction(ActionEvent event) {
        try {
            // Récupérer les valeurs des champs texte
            String poidsText = poidsTextField.getText();
            String tailleText = tailleTextField.getText();
            String sexe = sexeComboBox.getValue();

            // Valider le poids
            if (!validerNombre(poidsText, "poids")) {
                return; // Arrêter si la validation échoue
            }

            // Valider la taille
            if (!validerNombre(tailleText, "taille")) {
                return; // Arrêter si la validation échoue
            }

            // Convertir les valeurs en double
            double poids = Double.parseDouble(poidsText.replace(",", "."));
            double taille = Double.parseDouble(tailleText.replace(",", "."));

            // Créer un nouvel objet Nutrition
            Nutrition nutrition = new Nutrition(poids, taille, sexe);

            // Ajouter la nutrition via le service
            nutritionService.addNutrition(nutrition);

            // Afficher un message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText("Nutrition ajoutée avec succès !");
            alert.setContentText("IMC calculé : " + String.format("%.2f", nutrition.getImc()));
            alert.showAndWait();

            // Rediriger vers la page Home
            goToHome();

        } catch (NumberFormatException e) {
            // Gérer les erreurs de saisie
            afficherErreur("Erreur de saisie", "Le poids et la taille doivent être des nombres valides.");
        } catch (Exception e) {
            // Gérer les autres erreurs
            System.out.println(e);
            afficherErreur("Erreur", "Une erreur s'est produite lors de l'ajout de la nutrition.");
        }
    }

    // Méthode pour valider un nombre (avec virgule ou point)
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

    // Méthode pour afficher une erreur
    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour naviguer vers la page Home
    @FXML
    private void goToHome() {
        try {
            // Charger la page Home
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Detail.fxml"));
            Parent root = loader.load();

            // Afficher la nouvelle scène
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
