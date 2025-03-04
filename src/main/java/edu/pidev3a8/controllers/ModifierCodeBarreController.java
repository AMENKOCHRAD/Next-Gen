package edu.pidev3a8.controllers;

import edu.pidev3a8.entities.CodeBarre;
import edu.pidev3a8.services.CodeBarreService;
import edu.pidev3a8.tools.MyConnection;
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
import java.sql.SQLException;

public class ModifierCodeBarreController {

    @FXML
    private TextField nomProduitField;

    @FXML
    private TextField ingredientsField;

    @FXML
    private TextField marqueField;

    @FXML
    private ComboBox<Integer> utilisateurComboBox;

    @FXML
    private Label idCodeLabel;

    private CodeBarreService codeBarreService = new CodeBarreService();
    private CodeBarre codeBarreToModify;

    @FXML
    void initialize() {

        loadUtilisateurs();
    }


    private void loadUtilisateurs() {
        try {
            String query = "SELECT id FROM utilisateur";
            var stmt = MyConnection.getInstance().getCnx().createStatement();
            var rs = stmt.executeQuery(query);

            while (rs.next()) {
                int idUtilisateur = rs.getInt("id");
                utilisateurComboBox.getItems().add(idUtilisateur);
            }
        } catch (SQLException e) {
            afficherErreur("Erreur", "Erreur lors du chargement des utilisateurs : " + e.getMessage());
        }
    }


    public void setCodeBarre(CodeBarre codeBarre) {
        this.codeBarreToModify = codeBarre;


        nomProduitField.setText(codeBarre.getNom_produit());
        ingredientsField.setText(codeBarre.getIngredients());
        marqueField.setText(codeBarre.getMarque());
        utilisateurComboBox.setValue(codeBarre.getFk_utilisateur());


        idCodeLabel.setText(String.valueOf(codeBarre.getId_Code()));
    }

    @FXML
    void enregistrerAction(ActionEvent event) {
        try {

            String nomProduit = nomProduitField.getText().trim();
            String ingredients = ingredientsField.getText().trim();
            String marque = marqueField.getText().trim();
            Integer fkUtilisateur = utilisateurComboBox.getValue();


            if (!validerChamp(nomProduit, "Nom du produit") || !validerChamp(ingredients, "Ingrédients") || !validerChamp(marque, "Marque")) {
                return;
            }

            if (fkUtilisateur == null) {
                afficherErreur("Erreur", "Veuillez sélectionner un utilisateur.");
                return;
            }


            codeBarreToModify.setNom_produit(nomProduit);
            codeBarreToModify.setIngredients(ingredients);
            codeBarreToModify.setMarque(marque);
            codeBarreToModify.setFk_utilisateur(fkUtilisateur);

            codeBarreService.updateCodeBarre(codeBarreToModify.getId_Code(), codeBarreToModify);


            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText("Les informations ont été mises à jour avec succès.");
            alert.setContentText("Code-barre ID : " + codeBarreToModify.getId_Code());
            alert.showAndWait();


            Stage stage = (Stage) nomProduitField.getScene().getWindow();
            stage.close();
            retourListeAction(event);

        } catch (Exception e) {
            afficherErreur("Erreur", "Une erreur s'est produite lors de la mise à jour du code-barre : " + e.getMessage());
        }
    }

    @FXML
    void retourListeAction(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/detailCode.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nomProduitField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            afficherErreur("Erreur", "Impossible de charger la liste des codes-barres.");
        }
    }

    @FXML
    void annulerAction(ActionEvent event) {

        Stage stage = (Stage) nomProduitField.getScene().getWindow();
        stage.close();
        retourListeAction(event);
    }


    private boolean validerChamp(String valeur, String champ) {
        if (valeur.isEmpty()) {
            afficherErreur("Erreur de saisie", "Le champ " + champ + " ne peut pas être vide.");
            return false;
        }


        if (!valeur.matches("^[^0-9#!?]+$")) {
            afficherErreur("Erreur de saisie", "Le champ " + champ + " ne doit pas contenir de chiffres ni les caractères #, !, ?.");
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
}