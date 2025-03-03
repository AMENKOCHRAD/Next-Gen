package edu.pidev3A8.controllers;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
public class ModifierCommandeController {

    @FXML
    private ComboBox<Produit> produitComboBox; // ComboBox pour les produits

    @FXML
    private TextField idClientField; // Champ pour l'ID du client

    @FXML
    private TextField quantiteField; // Champ pour la quantité

    @FXML
    private TextField prixTotalField; // Champ pour le prix total

    @FXML
    private DatePicker dateCommandePicker; // DatePicker pour la date de la commande

    @FXML
    private ComboBox<StatutCommande> statutComboBox; // ComboBox pour le statut de la commande

    private Commande commandeToModify; // Commande à modifier
    private Commandeservice commandeservice = new Commandeservice();
    private Produitservice produitservice = new Produitservice();
    private DetailsCommandeController detailsController; // Référence à DetailsCommandeController

    // Méthode pour définir la commande à modifier
    public void setCommandeToModify(Commande commande) {
        this.commandeToModify = commande;

        // Remplir les champs avec les données de la commande
        int idProduit = commande.getId_produit(); // ID du produit associé à la commande

        // Parcourir la liste des produits dans la ComboBox pour trouver celui qui correspond à l'ID
        for (Produit produit : produitComboBox.getItems()) {
            if (produit.getId_produit() == idProduit) {
                produitComboBox.getSelectionModel().select(produit); // Sélectionner le produit dans la ComboBox
                break; // Sortir de la boucle une fois le produit trouvé
            }
        }

        idClientField.setText(String.valueOf(commande.getId_client()));
        quantiteField.setText(String.valueOf(commande.getQuantite()));
        prixTotalField.setText(String.valueOf(commande.getPrix_total()));

        // Convertir java.util.Date en java.sql.Date
        java.util.Date utilDate = commande.getDate_commande();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        LocalDate localDate = sqlDate.toLocalDate();

        // Définir la valeur dans le DatePicker
        dateCommandePicker.setValue(localDate);

        statutComboBox.setValue(commande.getStatut());
    }

    // Méthode pour définir la référence à DetailsCommandeController
    public void setDetailsController(DetailsCommandeController detailsController) {
        this.detailsController = detailsController;
    }

    @FXML
    public void initialize() {
        // Remplir la ComboBox des produits
        List<Produit> produits = produitservice.getAllData();
        produitComboBox.getItems().setAll(produits);

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

    // Méthode pour gérer la modification d'une commande
    @FXML
    private void handleModifierCommande() {
        try {
            // Récupérer les nouvelles valeurs des champs
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

            // Mettre à jour la commande
            commandeToModify.setId_produit(produit.getId_produit());
            commandeToModify.setId_client(idClient);
            commandeToModify.setQuantite(quantite);
            commandeToModify.setPrix_total(prixTotal);
            commandeToModify.setDate_commande(dateCommande);
            commandeToModify.setStatut(statut);

            // Modifier la commande via le service
            commandeservice.updateCommande(commandeToModify.getId_commande(), commandeToModify);

            // Rafraîchir la liste des commandes dans DetailsCommandeController
            if (detailsController != null) {
                detailsController.loadCommandes();
            }

            // Afficher une alerte de succès
            showAlert(AlertType.INFORMATION, "Succès", "Commande modifiée", "La commande a été modifiée avec succès.");

            // Fermer la fenêtre de modification
            Stage stage = (Stage) produitComboBox.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Erreur de saisie", "Format invalide", "Veuillez saisir des nombres valides pour l'ID client, la quantité et le prix total.");
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors de la modification de la commande", e.getMessage());
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