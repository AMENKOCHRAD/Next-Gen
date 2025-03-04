package edu.pidev3a8.controllers;

import edu.pidev3a8.entities.SentimentAnalyzer;
import edu.pidev3a8.entities.Reclamation;
import edu.pidev3a8.entities.TraitementReclamation;
import edu.pidev3a8.services.EmailService;
import edu.pidev3a8.services.ReclamtionService;
import edu.pidev3a8.services.TraitementService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;

public class TraitementController {

    @FXML
    private TextField sujetField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField categorieField;

    @FXML
    private ImageView pieceJointeImageView;

    @FXML
    private ComboBox<String> statutComboBox;

    @FXML
    private ComboBox<TraitementReclamation.Priorite> prioriteComboBox;

    @FXML
    private TextArea commentaireField;

    private final ReclamtionService reclamationService = new ReclamtionService();
    private final TraitementService traitementService = new TraitementService();

    private Reclamation reclamation; // Réclamation sélectionnée

    @FXML
    public void initialize() {
        // Initialiser les ComboBox
        statutComboBox.getItems().addAll("EN_COURS", "RESOLU", "REJETE");
        prioriteComboBox.getItems().addAll(TraitementReclamation.Priorite.values());
    }

    @FXML
    private void handleAjouterTraitement() {
        try {
            // Vérifications des champs
            if (reclamation == null) {
                showAlert("Erreur", "Aucune réclamation sélectionnée.", Alert.AlertType.ERROR);
                return;
            }
            String statut = statutComboBox.getValue();
            String commentaire = commentaireField.getText().trim();
            TraitementReclamation.Priorite priorite = prioriteComboBox.getValue();

            // Vérifications supplémentaires
            if (statut == null || statut.isEmpty()) {
                showAlert("Erreur", "Veuillez sélectionner un statut.", Alert.AlertType.ERROR);
                return;
            }
            if (commentaire.isEmpty()) {
                showAlert("Erreur", "Le champ commentaire est obligatoire.", Alert.AlertType.ERROR);
                return;
            }

            // Création de l'objet TraitementReclamation
            TraitementReclamation traitement = new TraitementReclamation(
                    reclamation.getId(), // Utiliser l'ID de la réclamation sélectionnée
                    "Administratif",
                    priorite,
                    commentaire,
                    TraitementReclamation.StatutTraitement.valueOf(statut),
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    1 // Supposons que l'ID admin est 1
            );

            // Ajout du traitement à la base de données
            traitementService.addEntityTraitement(traitement);

            // Mettre à jour le statut de la réclamation associée
            reclamation.setStatut(statut); // Mettre à jour le statut de la réclamation
            reclamationService.updateEntity(reclamation.getId(), reclamation);
            EmailService.sendTreatmentNotification(
                    statut, // Ex: "RESOLU"
                    commentaireField.getText() // Commentaire de l'admin
            );
            // Sauvegarder les modifications

            // Afficher une alerte de succès
            showAlert("Succès", "Traitement ajouté avec succès !", Alert.AlertType.INFORMATION);

            // Réinitialiser le formulaire
            resetForm();

            // Rediriger vers la liste des traitements après l'ajout
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailTraitement.fxml"));
                Parent root = loader.load();

                // Fermer la fenêtre actuelle
                Stage currentStage = (Stage) commentaireField.getScene().getWindow();
                currentStage.close();

                // Ouvrir la nouvelle fenêtre de la liste des traitements
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Liste des Traitements");
                stage.show();
            } catch (IOException e) {
                System.err.println("Erreur lors du chargement de la liste des traitements : " + e.getMessage());
                e.printStackTrace();
            }

        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout du traitement : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void resetForm() {
        statutComboBox.getSelectionModel().clearSelection();
        prioriteComboBox.getSelectionModel().clearSelection();
        commentaireField.clear();
        sujetField.clear();
        descriptionField.clear();
        categorieField.clear();
        pieceJointeImageView.setImage(null);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setReclamation(Reclamation reclamation) {
        this.reclamation = reclamation;

        // Pré-remplir les champs avec les données de la réclamation
        sujetField.setText(reclamation.getSujet());
        descriptionField.setText(reclamation.getDescription());
        categorieField.setText(reclamation.getCategorie().toString());

        // Désactiver les champs pour empêcher la modification
        sujetField.setDisable(true);
        descriptionField.setDisable(true);
        categorieField.setDisable(true);

        // Charger l'image de la pièce jointe si elle existe
        if (reclamation.getPiecesJointes() != null && !reclamation.getPiecesJointes().isEmpty()) {
            String imagePath = reclamation.getPiecesJointes().get(0); // Prendre la première pièce jointe
            Image image = new Image("file:" + imagePath);
            pieceJointeImageView.setImage(image);
        } else {
            pieceJointeImageView.setImage(null);
        }
    }
    @FXML
    private void handleAnalyserPriorite() {
        if (descriptionField.getText().isEmpty()) {
            showAlert("Erreur", "Aucune description à analyser.", Alert.AlertType.ERROR);
            return;
        }

        // Analyse du sentiment et classification
        String priorite = SentimentAnalyzer.analyzeSentiment(descriptionField.getText());

        // Mettre à jour la ComboBox Priorité
        prioriteComboBox.setValue(TraitementReclamation.Priorite.valueOf(priorite));

        showAlert("Analyse Terminée", "Priorité déterminée: " + priorite, Alert.AlertType.INFORMATION);
    }

}