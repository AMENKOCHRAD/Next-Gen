package edu.pidev3A8.controllers;

import edu.pidev3A8.entities.Commande;
import edu.pidev3A8.entities.Produit;
import edu.pidev3A8.services.Commandeservice;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class DetailsCommandeController {

    @FXML
    private TableView<Commande> commandeTable;

    @FXML
    private TableColumn<Commande, Integer> idCommandeColumn;

    @FXML
    private TableColumn<Commande, Integer> idProduitColumn;

    @FXML
    private TableColumn<Commande, Integer> idClientColumn;

    @FXML
    private TableColumn<Commande, Integer> quantiteColumn;

    @FXML
    private TableColumn<Commande, Double> prixTotalColumn;

    @FXML
    private TableColumn<Commande, String> dateCommandeColumn;

    @FXML
    private TableColumn<Commande, String> statutColumn;

    private Commandeservice commandeservice = new Commandeservice();

    @FXML
    public void initialize() {
        // Lier les colonnes aux propriétés de la classe Commande
        idCommandeColumn.setCellValueFactory(new PropertyValueFactory<>("id_commande"));
        idProduitColumn.setCellValueFactory(new PropertyValueFactory<>("id_produit"));
        idClientColumn.setCellValueFactory(new PropertyValueFactory<>("id_client"));
        quantiteColumn.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        prixTotalColumn.setCellValueFactory(new PropertyValueFactory<>("prix_total"));
        dateCommandeColumn.setCellValueFactory(new PropertyValueFactory<>("date_commande"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Charger les commandes dans la TableView
        loadCommandes();
    }

    // Méthode pour charger les commandes depuis la base de données
    public void loadCommandes() {
        ObservableList<Commande> commandes = FXCollections.observableArrayList(commandeservice.getAllCommandes());
        commandeTable.setItems(commandes);
    }
    @FXML
    private void handleAjouterCommande() {
        try {
            // Charger le fichier FXML de l'interface d'ajout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouter_commande.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur de la fenêtre d'ajout
            AjouterCommandeController ajouterCommandeController = loader.getController();

            // Définir le callback pour rafraîchir la liste des commandes après l'ajout
            ajouterCommandeController.setOnCommandeAddedCallback(commande -> {
                loadCommandes(); // Rafraîchir la liste des commandes
            });

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Créer une nouvelle fenêtre (stage)
            Stage stage = new Stage();
            stage.setTitle("Ajouter une commande");
            stage.setScene(scene);

            // Afficher la fenêtre
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur lors de l'ouverture de l'interface d'ajout : " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    private void handleModifierCommande() {
        // Récupérer la commande sélectionnée dans la TableView
        Commande selectedCommande = commandeTable.getSelectionModel().getSelectedItem();

        if (selectedCommande != null) {
            try {
                // Charger le fichier FXML pour la modification d'une commande
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierCommande.fxml"));
                Parent root = loader.load();

                // Obtenir le contrôleur de la fenêtre de modification
                ModifierCommandeController modifierCommandeController = loader.getController();

                // Passer la commande sélectionnée à ModifierCommandeController
                modifierCommandeController.setCommandeToModify(selectedCommande);

                // Passer une référence de DetailsCommandeController à ModifierCommandeController
                modifierCommandeController.setDetailsController(this);

                // Créer une nouvelle scène
                Scene scene = new Scene(root);

                // Créer une nouvelle fenêtre
                Stage stage = new Stage();
                stage.setTitle("Modifier une commande");
                stage.setScene(scene);

                // Afficher la fenêtre
                stage.show();
            } catch (IOException e) {
                System.out.println("Erreur de chargement de ModifierCommande.fxml : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // Afficher un message d'erreur si aucune commande n'est sélectionnée
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune commande sélectionnée");
            alert.setHeaderText("Aucune commande sélectionnée");
            alert.setContentText("Veuillez sélectionner une commande à modifier.");
            alert.showAndWait();
        }
    }
    @FXML
    private void handleSupprimerCommande() {
        // Récupérer la commande sélectionnée
        Commande selectedCommande = commandeTable.getSelectionModel().getSelectedItem();

        if (selectedCommande != null) {
            // Afficher une boîte de dialogue de confirmation
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Supprimer la commande");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer cette commande?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Supprimer la commande via le service
                commandeservice.deleteCommande(selectedCommande.getId_commande()); // Appel non statique

                // Rafraîchir la liste des commandes
                loadCommandes(); // Utiliser loadCommandes() au lieu de loadProduits()
            }
        } else {
            // Afficher un message d'erreur si aucune commande n'est sélectionnée
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune commande sélectionnée");
            alert.setHeaderText("Aucune commande sélectionnée");
            alert.setContentText("Veuillez sélectionner une commande à supprimer.");
            alert.showAndWait();
        }
    }
}
