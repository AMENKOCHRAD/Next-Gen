package edu.pidev3a8.controllers;

import edu.pidev3a8.entities.TraitementReclamation;
import edu.pidev3a8.services.TraitementService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class DetailTraitementController {

    @FXML
    private TableView<TraitementReclamation> traitementTable;
    @FXML
    private TableColumn<TraitementReclamation, Integer> idColumn;
    @FXML
    private TableColumn<TraitementReclamation, Integer> reclamationIdColumn;
    @FXML
    private TableColumn<TraitementReclamation, Integer> adminIdColumn;
    @FXML
    private TableColumn<TraitementReclamation, String> datePriseChargeColumn;
    @FXML
    private TableColumn<TraitementReclamation, String> statutColumn;
    @FXML
    private TableColumn<TraitementReclamation, String> commentaireColumn;
    @FXML
    private TableColumn<TraitementReclamation, String> prioriteColumn;
    @FXML
    private TableColumn<TraitementReclamation, Void> actionColumn;

    private final TraitementService traitementService = new TraitementService();
    private final ObservableList<TraitementReclamation> traitements = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id_traitement"));
        reclamationIdColumn.setCellValueFactory(new PropertyValueFactory<>("reclamationId"));
        adminIdColumn.setCellValueFactory(new PropertyValueFactory<>("adminId"));
        datePriseChargeColumn.setCellValueFactory(new PropertyValueFactory<>("datePriseEnCharge"));
        commentaireColumn.setCellValueFactory(new PropertyValueFactory<>("commentaire"));

        // ✅ Convertir Enum en String avant affichage
        statutColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatut().toString()));
        prioriteColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPriorite().toString()));

        // Charger les traitements
        loadTraitements();

        // Ajouter les boutons Modifier / Supprimer
        addActionButtons();

        // Appliquer le style en fonction du statut
        applyRowStyling();
        applyPriorityStyling();


        System.out.println("actionColumn: " + actionColumn);
    }

    private void loadTraitements() {
        List<TraitementReclamation> traitementList = traitementService.getAllDataTraitement();
        traitements.clear();
        traitements.addAll(traitementList);
        traitementTable.setItems(traitements);

        System.out.println("Nombre de traitements chargés: " + traitements.size());
    }

    private void addActionButtons() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button btnModifier = new Button("Modifier");
            private final Button btnSupprimer = new Button("Supprimer");

            {
                btnModifier.setOnAction(event -> {
                    TraitementReclamation traitement = getTableView().getItems().get(getIndex());
                    if (traitement != null) {
                        ouvrirFenetreModification(traitement);
                    }
                });

                btnSupprimer.setOnAction(event -> {
                    TraitementReclamation traitement = getTableView().getItems().get(getIndex());
                    if (traitement != null) {
                        supprimerTraitement(traitement);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, btnModifier, btnSupprimer);
                    setGraphic(buttons);
                }
            }
        });
    }

    private void ouvrirFenetreModification(TraitementReclamation traitement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierTraitement.fxml"));
            if (loader.getLocation() == null) {
                System.err.println("❌ ERREUR : Fichier ModifierTraitement.fxml introuvable !");
                return;
            }

            Parent root = loader.load();
            ModifierTraitementController controller = loader.getController();
            if (controller == null) {
                System.err.println("❌ ERREUR : Impossible de récupérer le contrôleur ModifierTraitementController !");
                return;
            }

            controller.setTraitement(traitement);
            controller.setOnTraitementUpdated(this::loadTraitements); // Rafraîchir la table après modif

            Stage stage = new Stage();
            stage.setTitle("Modifier Traitement");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ Exception lors du chargement de ModifierTraitement.fxml");
        }
    }


    private void supprimerTraitement(TraitementReclamation traitement) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer ce traitement ?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                traitementService.deleteEntityTraitement(traitement);
                loadTraitements();
            }
        });
    }

    private void applyRowStyling() {
        traitementTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(TraitementReclamation traitement, boolean empty) {
                super.updateItem(traitement, empty);
                if (traitement == null || empty) {
                    setStyle(""); // Réinitialiser le style
                } else {
                    // ✅ Assurer que statut est bien affiché sous forme de String
                    String statut = traitement.getStatut().toString();
                    switch (statut) {
                        case "REJETE":
                            setStyle("-fx-background-color: #ff6666;"); // Rouge clair
                            break;
                        case "RESOLU":
                            setStyle("-fx-background-color: #66ff66;"); // Vert clair
                            break;
                        case "EN_COURS":
                            setStyle("-fx-background-color: #ffff99;"); // Jaune clair
                            break;
                        default:
                            setStyle(""); // Réinitialiser pour les autres cas
                            break;
                    }
                }
            }
        });
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) traitementTable.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void handleAjouterTraitement() {
        try {
            // Charger la page TraitementReclamation.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TraitementReclamation.fxml"));
            Parent root = loader.load();

            // Passer le contrôleur actuel au contrôleur de la page TraitementReclamation
            TraitementController traitementController = loader.getController();

            // Afficher la page TraitementReclamation
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un Traitement");
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la page TraitementReclamation : " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void applyPriorityStyling() {
        prioriteColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "HAUTE":
                            setStyle("-fx-background-color: #ff4c4c; -fx-text-fill: white;"); // Rouge
                            break;
                        case "MOYENNE":
                            setStyle("-fx-background-color: #ffcc00; -fx-text-fill: black;"); // Jaune
                            break;
                        case "BASSE":
                            setStyle("-fx-background-color: #66cc66; -fx-text-fill: white;"); // Vert
                            break;
                        default:
                            setStyle("");
                            break;
                    }
                }
            }
        });
    }

}
