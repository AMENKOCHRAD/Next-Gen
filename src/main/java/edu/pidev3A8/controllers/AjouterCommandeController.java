package edu.pidev3A8.controllers;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import edu.pidev3A8.entities.Commande;
import edu.pidev3A8.entities.Produit;
import edu.pidev3A8.entities.StatutCommande;
import edu.pidev3A8.services.Commandeservice;
import edu.pidev3A8.services.Produitservice;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

public class AjouterCommandeController {

    @FXML
    private ComboBox<Produit> produitComboBox; // ComboBox pour les produits

    @FXML
    private TextField idClientField;

    @FXML
    private TextField quantiteField;

    @FXML
    private TextField prixTotalField;

    @FXML
    private DatePicker dateCommandePicker;

    @FXML
    private ComboBox<StatutCommande> statutComboBox;

    private Commandeservice commandeservice = new Commandeservice();
    private Produitservice produitservice = new Produitservice();

    private Consumer<Commande> onCommandeAddedCallback; // Callback pour mettre à jour la liste des commandes

    /**
     * Définit un callback pour mettre à jour la liste des commandes après l'ajout.
     *
     * @param callback Le callback à exécuter après l'ajout d'une commande.
     */
    public void setOnCommandeAddedCallback(Consumer<Commande> callback) {
        this.onCommandeAddedCallback = callback;
    }

    @FXML
    public void initialize() {
        // Remplir la ComboBox des produits
        List<Produit> produits = produitservice.getAllData();
        produitComboBox.getItems().setAll(produits); // Ajouter les produits à la ComboBox

        // Définir un CellFactory pour afficher uniquement l'id_produit dans la ComboBox
        produitComboBox.setCellFactory(listView -> new ListCell<Produit>() {
            @Override
            protected void updateItem(Produit produit, boolean empty) {
                super.updateItem(produit, empty);
                if (empty || produit == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(produit.getId_produit())); // Afficher l'id_produit
                }
            }
        });

        // Définir un ButtonCell pour afficher l'id_produit dans le bouton de la ComboBox
        produitComboBox.setButtonCell(new ListCell<Produit>() {
            @Override
            protected void updateItem(Produit produit, boolean empty) {
                super.updateItem(produit, empty);
                if (empty || produit == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(produit.getId_produit())); // Afficher l'id_produit
                }
            }
        });

        // Remplir la ComboBox des statuts
        statutComboBox.getItems().setAll(StatutCommande.values());
    }

    @FXML
    private void handleAjouter() {
        try {
            // Récupérer les valeurs des champs
            Produit produit = produitComboBox.getValue(); // Produit sélectionné
            int idClient = Integer.parseInt(idClientField.getText());
            int quantite = Integer.parseInt(quantiteField.getText());
            double prixTotal = Double.parseDouble(prixTotalField.getText());
            LocalDate localDate = dateCommandePicker.getValue();
            Date dateCommande = Date.valueOf(localDate);
            StatutCommande statut = statutComboBox.getValue();

            // Contrôle de saisie pour idClient
            if (idClient <= 0) {
                showAlert(AlertType.ERROR, "Erreur de saisie", "ID client invalide", "L'ID client doit être supérieur à 0.");
                return;
            }

            // Contrôle de saisie pour la quantité
            if (quantite < 1 || quantite > 20) {
                showAlert(AlertType.ERROR, "Erreur de saisie", "Quantité invalide", "La quantité doit être comprise entre 1 et 20.");
                return;
            }

            // Contrôle de saisie pour le prix total
            if (prixTotal < 0) {
                showAlert(AlertType.ERROR, "Erreur de saisie", "Prix total invalide", "Le prix total ne peut pas être négatif.");
                return;
            }

            // Contrôle de saisie pour la date de commande
            if (localDate.getYear() != 2025) { // Vérifier que l'année est exactement 2025
                showAlert(AlertType.ERROR, "Erreur de saisie", "Date invalide", "La date de commande doit être en 2025.");
                return;
            }

            // Vérifier que tous les champs sont valides
            if (produit == null || dateCommande == null || statut == null) {
                showAlert(AlertType.ERROR, "Erreur de saisie", "Champs manquants", "Veuillez remplir tous les champs correctement.");
                return;
            }

            // Créer un nouvel objet Commande
            Commande commande = new Commande(produit.getId_produit(), idClient, quantite, prixTotal, dateCommande, statut);

            // Ajouter la commande via le service
            commandeservice.addCommande(commande);

            // Appeler le callback pour mettre à jour la liste des commandes
            if (onCommandeAddedCallback != null) {
                onCommandeAddedCallback.accept(commande);
            }

            // Afficher une alerte de succès
            showAlert(AlertType.INFORMATION, "Succès", "Commande ajoutée", "La commande a été ajoutée avec succès.");

            // Fermer la fenêtre d'ajout
            Stage stage = (Stage) produitComboBox.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Erreur de saisie", "Format invalide", "Veuillez saisir des nombres valides pour l'ID client, la quantité et le prix total.");
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors de l'ajout de la commande", e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();

    }
}