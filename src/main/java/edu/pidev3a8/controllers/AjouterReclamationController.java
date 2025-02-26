package edu.pidev3a8.controllers;

import edu.pidev3a8.entities.Categorie;
import edu.pidev3a8.entities.Reclamation;
import edu.pidev3a8.services.ReclamtionService;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AjouterReclamationController {

    @FXML
    private Button button;

    @FXML
    private TextField description_textfield;

    @FXML
    private TextField sujet_textfield;

    @FXML
    private ComboBox<Categorie> categorieComboBox;

    @FXML
    private ListView<String> piecesJointesListView; // ListView pour afficher les pièces jointes


    private ObservableList<String> piecesJointes = FXCollections.observableArrayList(); // Liste des chemins des fichiers

    // Constantes pour les contrôles de saisie
    private static final int NOMBRE_MAX_PIECES_JOINTES = 5;
    private static final List<String> TYPES_FICHIERS_AUTORISES = List.of(".pdf", ".jpg", ".jpeg", ".png");

    private DetailController detailController; // Référence à DetailController

    public void setDetailController(DetailController detailController) {
        this.detailController = detailController;
    }

    @FXML
    public void initialize() {
        // Initialiser le ComboBox avec les valeurs de l'enum Categorie
        ObservableList<Categorie> categories = FXCollections.observableArrayList(Categorie.values());
        categorieComboBox.setItems(categories);
        categorieComboBox.getSelectionModel().selectFirst(); // Sélectionner la première valeur par défaut

        // Lier la ListView à la liste des pièces jointes
        piecesJointesListView.setItems(piecesJointes);

        // Ajouter un listener pour mettre à jour l'état du bouton Valider
        piecesJointes.addListener((ListChangeListener<String>) change -> {
            if (piecesJointes.size() > NOMBRE_MAX_PIECES_JOINTES) {
                button.setDisable(true); // Désactiver le bouton Valider
                afficherErreur("Vous ne pouvez ajouter que " + NOMBRE_MAX_PIECES_JOINTES + " pièces jointes maximum.");
            } else {
                button.setDisable(false); // Activer le bouton Valider
            }
        });
    }

    @FXML
    void AjouterReclamationAction(ActionEvent event) {
        String sujet = sujet_textfield.getText().trim();
        String description = description_textfield.getText().trim();
        Categorie categorie = categorieComboBox.getValue();
        List<String> piecesJointesList = new ArrayList<>(piecesJointes);

        // Validation du sujet
        if (!validerSujet(sujet)) {
            afficherErreur("Le sujet est obligatoire et doit contenir entre 5 et 100 caractères.");
            return;
        }

        // Validation de la description (mots interdits)
        if (contientMotsInterdits(description)) {
            afficherErreur("La description contient des mots interdits : 'nul', 'raté', 'idiot', 'stupide', 'imbécile'. Veuillez la modifier.");
            return;
        }

        // Créer une réclamation
        Reclamation r = new Reclamation(sujet, description, categorie, piecesJointesList);
        ReclamtionService rs = new ReclamtionService();

        try {
            // Ajouter la réclamation
            rs.addEntity(r);

            // Ajouter la réclamation à la liste dans DetailController
            if (detailController != null) {
                detailController.addReclamation(r);
            }

            // Fermer la fenêtre d'ajout
            ((Stage) button.getScene().getWindow()).close();

            // Rediriger vers la liste des réclamations (Detail.fxml)
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Detail.fxml"));
                Parent root = loader.load();

                // Obtenir le contrôleur de la vue Detail
                DetailController detailController = loader.getController();

                // Rafraîchir la liste des réclamations
                detailController.loadReclamationsFromDatabase();

                // Afficher la vue Detail
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Liste des Réclamations");
                stage.show();
            } catch (IOException e) {
                System.err.println("Erreur lors du chargement de la page Detail : " + e.getMessage());
                e.printStackTrace();
            }
        } catch (RuntimeException e) {
            // Gérer l'exception si la limite de réclamations est atteinte
            afficherErreur(e.getMessage());
        }
    }
    @FXML
    void handleAjouterPieceJointe(ActionEvent event) {
        // Vérifier si le nombre maximum de pièces jointes est atteint
        if (piecesJointes.size() >= NOMBRE_MAX_PIECES_JOINTES) {
            afficherErreur("Vous ne pouvez ajouter que " + NOMBRE_MAX_PIECES_JOINTES + " pièces jointes maximum.");
            return;
        }

        // Ouvrir un FileChooser pour sélectionner des fichiers
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une pièce jointe");

        // Définir les extensions de fichiers autorisées
        FileChooser.ExtensionFilter filtre = new FileChooser.ExtensionFilter(
                "Fichiers autorisés (*.pdf, *.jpg, *.jpeg, *.png)", "*.pdf", "*.jpg", "*.jpeg", "*.png"
        );
        fileChooser.getExtensionFilters().add(filtre);

        List<File> fichiers = fileChooser.showOpenMultipleDialog(null);

        if (fichiers != null) {
            for (File fichier : fichiers) {
                // Vérifier le type de fichier
                if (!estFichierAutorise(fichier)) {
                    afficherErreur("Le fichier '" + fichier.getName() + "' n'est pas autorisé. Seuls les fichiers PDF, JPG et PNG sont acceptés.");
                    continue; // Ignorer ce fichier et passer au suivant
                }

                // Vérifier si le nombre maximum de pièces jointes est atteint
                if (piecesJointes.size() >= NOMBRE_MAX_PIECES_JOINTES) {
                    afficherErreur("Vous ne pouvez ajouter que " + NOMBRE_MAX_PIECES_JOINTES + " pièces jointes maximum.");
                    break; // Arrêter l'ajout de fichiers
                }

                // Ajouter le chemin du fichier à la liste des pièces jointes
                piecesJointes.add(fichier.getAbsolutePath());
            }
        }
    }

    // Méthode pour valider le sujet
    private boolean validerSujet(String sujet) {
        return sujet != null && sujet.length() >= 5 && sujet.length() <= 100;
    }

    // Méthode pour vérifier les mots interdits dans la description
    private boolean contientMotsInterdits(String description) {
        // Liste des mots interdits
        List<String> motsInterdits = List.of("nul", "raté", "idiot", "stupide", "imbécile");

        // Vérifier si la description contient un mot interdit
        for (String mot : motsInterdits) {
            if (description.toLowerCase().contains(mot.toLowerCase())) {
                return true; // Un mot interdit a été trouvé
            }
        }
        return false; // Aucun mot interdit trouvé
    }

    // Méthode pour vérifier si un fichier est autorisé
    private boolean estFichierAutorise(File fichier) {
        String nomFichier = fichier.getName().toLowerCase();
        for (String extension : TYPES_FICHIERS_AUTORISES) {
            if (nomFichier.endsWith(extension)) {
                return true; // Le fichier est autorisé
            }
        }
        return false; // Le fichier n'est pas autorisé
    }
    private void afficherErreur(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}