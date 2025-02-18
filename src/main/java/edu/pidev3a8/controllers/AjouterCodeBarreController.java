package edu.pidev3a8.controllers;

import edu.pidev3a8.entities.CodeBarre;
import edu.pidev3a8.services.CodeBarreService;
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
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class AjouterCodeBarreController implements Initializable {

    @FXML
    private TextField nomProduitField;
    @FXML
    private TextField ingredientsField;
    @FXML
    private TextField marqueField;
    @FXML
    private ComboBox<Integer> utilisateurComboBox;

    private final CodeBarreService codeBarreService = new CodeBarreService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadUtilisateurs();
    }

    private void loadUtilisateurs() {
        ObservableList<Integer> utilisateursList = FXCollections.observableArrayList();

        try {
            Connection conn = MyConnection.getInstance().getCnx();
            String query = "SELECT id FROM utilisateur";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

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
    void AjouterCodeBarreAction(ActionEvent event) {
        try {
            String nomProduit = nomProduitField.getText().trim();
            String ingredients = ingredientsField.getText().trim();
            String marque = marqueField.getText().trim();
            Integer fk_utilisateur = utilisateurComboBox.getValue();

            if (!validerChamp(nomProduit, "Nom du produit") || !validerChamp(ingredients, "Ingrédients") || !validerChamp(marque, "Marque")) {
                return;
            }

            if (fk_utilisateur == null) {
                afficherErreur("Erreur", "Veuillez sélectionner un utilisateur.");
                return;
            }

            CodeBarre codeBarre = new CodeBarre(nomProduit, ingredients, marque, fk_utilisateur);
            codeBarreService.addCodeBarre(codeBarre);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText("Code-barre ajouté avec succès !");
            //alert.setContentText("ID du Code-barre : " + codeBarre.getId_Code());
            alert.showAndWait();

            goToHome();

        } catch (Exception e) {
            afficherErreur("Erreur", "Une erreur s'est produite lors de l'ajout du code-barre : " + e.getMessage());
        }
    }

    private boolean validerChamp(String valeur, String champ) {
        if (valeur.isEmpty()) {
            afficherErreur("Erreur de saisie", "Le champ " + champ + " ne peut pas être vide.");
            return false;
        }

        // Vérifie si la valeur contient un chiffre ou un des caractères interdits
        if (!valeur.matches("^[^0-9#!?]+$")) {
            afficherErreur("Erreur de saisie", "Le champ " + champ + " ne doit pas contenir de chiffres ni les caractères #, !, ?.");
            return false;
        }

        return true;
    }

    @FXML
    private void goToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/detailCode.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nomProduitField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            afficherErreur("Erreur", "Impossible de charger la page d'accueil.");
        }
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
