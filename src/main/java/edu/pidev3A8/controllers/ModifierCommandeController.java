package edu.pidev3A8.controllers;

import edu.pidev3A8.entities.Commande;
import edu.pidev3A8.entities.Produit;
import edu.pidev3A8.services.Commandeservice;
import edu.pidev3A8.services.Produitservice;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
    private  TextField AdresseField;

    @FXML
    private TextField mailField;



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
        AdresseField.setText(commande.getAdresse());
        mailField.setText(commande.getAdresseEmail());




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


    }

    // Méthode pour gérer la modification d'une commande
    @FXML
    private void handleModifierCommande() {
        try {
            // Récupérer les nouvelles valeurs des champs
            Produit produit = produitComboBox.getValue(); // Produit sélectionné
            int idClient = Integer.parseInt(idClientField.getText());
            int quantite = Integer.parseInt(quantiteField.getText());
            String adresse = AdresseField.getText();
            String adresseEmail=mailField.getText();





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
            // Contrôle de saisie pour l'adresse (doit commencer par une lettre)
            if (!adresse.matches("^[A-Za-z].*")) {
                showAlert(AlertType.ERROR, "Erreur de saisie", "Adresse invalide", "L'adresse doit commencer par une lettre.");
                return;
            }

            // Contrôle de saisie pour l'email (doit contenir '@')
            if (!adresseEmail.contains("@")) {
                showAlert(AlertType.ERROR, "Erreur de saisie", "Adresse e-mail invalide", "L'adresse e-mail doit contenir '@'.");
                return;
            }



            // Mettre à jour la commande
            commandeToModify.setId_produit(produit.getId_produit());
            commandeToModify.setId_client(idClient);
            commandeToModify.setQuantite(quantite);
            commandeToModify.setAdresse(adresse);
            commandeToModify.setAdresseEmail(adresseEmail);

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