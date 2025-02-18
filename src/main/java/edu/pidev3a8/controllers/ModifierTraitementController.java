package edu.pidev3a8.controllers;

import edu.pidev3a8.entities.TraitementReclamation;
import edu.pidev3a8.services.TraitementService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ModifierTraitementController {

    @FXML
    private ComboBox<String> statutComboBox;
    @FXML
    private ComboBox<String> prioriteComboBox;
    @FXML
    private TextArea commentaireTextArea;

    private TraitementReclamation traitement;
    private final TraitementService traitementService = new TraitementService();

    private Runnable onTraitementUpdated; // Callback pour notifier la mise à jour

    @FXML
    public void initialize() {
        statutComboBox.getItems().addAll("EN_COURS", "RESOLU", "REJETE");
        prioriteComboBox.getItems().addAll("Haute", "Moyenne", "Basse");
    }

    public void setTraitement(TraitementReclamation traitement) {
        this.traitement = traitement;
        if (traitement != null) {
            statutComboBox.setValue(traitement.getStatut().toString());
            prioriteComboBox.setValue(traitement.getPriorite().toString());
            commentaireTextArea.setText(traitement.getCommentaire());
        }
    }

    public void setOnTraitementUpdated(Runnable onTraitementUpdated) {
        this.onTraitementUpdated = onTraitementUpdated;
    }

    @FXML
    private void handleEnregistrer() {
        if (traitement != null) {
            try {
                // Vérifier si la sélection de statut et priorité n'est pas nulle
                String statutValue = statutComboBox.getValue();
                String prioriteValue = prioriteComboBox.getValue(); // Supposons que tu as un ComboBox pour la priorité
                String commentaire = commentaireTextArea.getText().trim();

                if (statutValue == null || prioriteValue == null) {
                    afficherAlerte("Erreur", "Statut ou priorité non sélectionné", Alert.AlertType.ERROR);
                    return;
                }

                // Vérifier que le commentaire n'est pas vide
                if (commentaire.isEmpty()) {
                    afficherAlerte("Erreur", "Le champ commentaire ne peut pas être vide.", Alert.AlertType.ERROR);
                    return;
                }

                // Vérifier la longueur du commentaire
                if (commentaire.length() > 255) {
                    afficherAlerte("Erreur", "Le commentaire ne doit pas dépasser 255 caractères.", Alert.AlertType.ERROR);
                    return;
                }

                // Convertir en enum en s'assurant que la casse est correcte
                TraitementReclamation.StatutTraitement statut = TraitementReclamation.StatutTraitement.valueOf(statutValue.toUpperCase());
                TraitementReclamation.Priorite priorite = TraitementReclamation.Priorite.valueOf(prioriteValue.toUpperCase());

                // Mise à jour des données du traitement
                traitement.setStatut(statut);
                traitement.setPriorite(priorite);
                traitement.setCommentaire(commentaire);

                // Mise à jour dans la base de données
                traitementService.updateEntityTraitement(traitement.getId_traitement(), traitement);

                // Rafraîchir la liste après modification
                if (onTraitementUpdated != null) {
                    onTraitementUpdated.run();
                }

                // Fermer la fenêtre
                Stage stage = (Stage) statutComboBox.getScene().getWindow();
                stage.close();

            } catch (IllegalArgumentException e) {
                afficherAlerte("Erreur", "Erreur lors de la conversion en enum : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Affiche une alerte avec un message donné.
     */
    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
