package edu.pidev3A8.controllers;

import edu.pidev3A8.entities.Produit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.util.List;

public class PanierController {

    @FXML
    private TableView<Produit> panierTable;

    @FXML
    private TableColumn<Produit, String> nomColumn;

    @FXML
    private TableColumn<Produit, Double> prixColumn;

    @FXML
    private Button supprimerButton;

    @FXML
    private Button viderButton;

    @FXML
    private Text totalText;

    private ObservableList<Produit> panier = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Liaison des colonnes avec les propriétés des produits
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom_produit"));
        prixColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));

        // Associer la liste observable au tableau
        panierTable.setItems(panier);

        // Désactiver les boutons si le panier est vide
        supprimerButton.setDisable(true);
        viderButton.setDisable(true);

        // Écouter les changements dans le panier
        panierTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            supprimerButton.setDisable(newSelection == null);
        });

        updateTotal();
    }

    // Ajouter un produit au panier
    public void ajouterAuPanier(Produit produit) {
        if (produit != null) {
            panier.add(produit);
            updateTotal();
            viderButton.setDisable(false);
        }
    }

    // Supprimer un produit sélectionné du panier
    @FXML
    private void handleSupprimerProduit() {
        Produit selectedProduit = panierTable.getSelectionModel().getSelectedItem();
        if (selectedProduit != null) {
            panier.remove(selectedProduit);
            updateTotal();

            // Désactiver le bouton si le panier est vide
            if (panier.isEmpty()) {
                viderButton.setDisable(true);
            }
        } else {
            showAlert("Aucun produit sélectionné", "Veuillez sélectionner un produit à supprimer.");
        }
    }

    // Vider tout le panier
    @FXML
    private void handleViderPanier() {
        panier.clear();
        updateTotal();
        viderButton.setDisable(true);
    }

    // Mettre à jour le total du panier
    private void updateTotal() {
        double total = panier.stream().mapToDouble(Produit::getPrix).sum();
        totalText.setText("Total : " + total + " €");
    }

    // Afficher une alerte
    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setProduitsPanier(List<Produit> panier) {
        this.panier.clear(); // Vider le panier actuel (si nécessaire)
        this.panier.addAll(panier); // Ajouter les produits du panier
        panierTable.setItems(this.panier); // Mettre à jour la table
        updateTotal(); // Mettre à jour le total
    }
    public void updatePanierTable(List<Produit> updatedPanier) {
        panier.clear();  // Vide la liste actuelle
        panier.addAll(updatedPanier);  // Ajoute tous les produits du panier mis à jour
        panierTable.refresh();  // Rafraîchit la table pour afficher les nouveaux produits
    }


}
