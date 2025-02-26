package edu.pidev3a8.controllers;

import edu.pidev3a8.entities.Categorie;
import edu.pidev3a8.entities.Reclamation;
import edu.pidev3a8.services.ReclamtionService;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ModifierReclamationController {

    @FXML
    private TextField sujetTextField;

    @FXML
    private TextField descriptionTextField;

    @FXML
    private ComboBox<Categorie> categorieComboBox;

    @FXML
    private ListView<String> piecesJointesListView; // ListView pour afficher les pièces jointes

    @FXML
    private Button enregistrerButton; // Bouton Enregistrer

    private ObservableList<String> piecesJointes = FXCollections.observableArrayList(); // Liste des chemins des fichiers

    // Constantes pour les contrôles de saisie
    private static final int NOMBRE_MAX_PIECES_JOINTES = 5;
    private static final List<String> TYPES_FICHIERS_AUTORISES = List.of(".pdf", ".jpg", ".jpeg", ".png");

    private Reclamation reclamation;
    private ReclamtionService reclamtionService = new ReclamtionService();

    private Runnable onReclamationUpdated; // Callback pour notifier la mise à jour

    @FXML
    public void initialize() {
        // Initialiser le ComboBox avec les valeurs de l'enum Categorie
        ObservableList<Categorie> categories = FXCollections.observableArrayList(Categorie.values());
        categorieComboBox.setItems(categories);

        // Lier la ListView à la liste des pièces jointes
        piecesJointesListView.setItems(piecesJointes);

        // Ajouter un listener pour mettre à jour l'état du bouton Enregistrer
        piecesJointes.addListener((ListChangeListener<String>) change -> {
            if (piecesJointes.size() > NOMBRE_MAX_PIECES_JOINTES) {
                enregistrerButton.setDisable(true); // Désactiver le bouton Enregistrer
                afficherErreur("Vous ne pouvez ajouter que " + NOMBRE_MAX_PIECES_JOINTES + " pièces jointes maximum.");
            } else {
                enregistrerButton.setDisable(false); // Activer le bouton Enregistrer
            }
        });
    }

    /**
     * Définit la réclamation à modifier et remplit les champs avec ses données.
     *
     * @param reclamation La réclamation à modifier.
     */
    public void setReclamation(Reclamation reclamation) {
        this.reclamation = reclamation;
        if (reclamation != null) {
            sujetTextField.setText(reclamation.getSujet());
            descriptionTextField.setText(reclamation.getDescription());
            categorieComboBox.setValue(reclamation.getCategorie()); // Sélectionner la catégorie actuelle
            piecesJointes.setAll(reclamation.getPiecesJointes()); // Afficher les pièces jointes existantes
        }
    }

    /**
     * Définit un callback pour notifier la mise à jour de la réclamation.
     *
     * @param onReclamationUpdated Le callback à exécuter après la mise à jour.
     */
    public void setOnReclamationUpdated(Runnable onReclamationUpdated) {
        this.onReclamationUpdated = onReclamationUpdated;
    }

    /**
     * Gère l'action du bouton "Enregistrer".
     */
    @FXML
    private void handleEnregistrer() {
        if (reclamation != null) {
            String sujet = sujetTextField.getText().trim(); // Supprimer les espaces inutiles
            String description = descriptionTextField.getText().trim(); // Supprimer les espaces inutiles

            // Validation du sujet
            if (!validerSujet(sujet)) {
                afficherErreur("Le sujet est obligatoire et doit contenir entre 5 et 100 caractères.");
                return; // Arrêter l'exécution si la validation échoue
            }

            // Validation de la description (mots interdits)
            if (contientMotsInterdits(description)) {
                afficherErreur("La description contient des mots interdits : 'nul', 'raté', 'idiot', 'stupide', 'imbécile'. Veuillez la modifier.");
                return; // Arrêter l'exécution si la validation échoue
            }

            // Mettre à jour la réclamation
            reclamation.setSujet(sujet);
            reclamation.setDescription(description);
            reclamation.setCategorie(categorieComboBox.getValue()); // Mettre à jour la catégorie

            // Mettre à jour les pièces jointes
            reclamation.setPiecesJointes(new ArrayList<>(piecesJointes)); // Utiliser la nouvelle liste de pièces jointes

            // Enregistrer les modifications dans la base de données
            reclamtionService.updateEntity(reclamation.getId(), reclamation);

            // Notifier DetailController que la réclamation a été mise à jour
            if (onReclamationUpdated != null) {
                onReclamationUpdated.run();
            }

            // Fermer la fenêtre de modification
            Stage stage = (Stage) sujetTextField.getScene().getWindow();
            stage.close();
        } else {
            System.err.println("Erreur : Aucune réclamation à modifier.");
        }
    }

    /**
     * Gère l'action du bouton "Ajouter une pièce jointe".
     */
    @FXML
    private void handleAjouterPieceJointe() {
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
            // Vider la liste des pièces jointes avant d'ajouter les nouvelles
            piecesJointes.clear();

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

    // Méthode pour afficher un message d'erreur
    private void afficherErreur(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur de saisie");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}