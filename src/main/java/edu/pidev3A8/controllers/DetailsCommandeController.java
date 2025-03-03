package edu.pidev3A8.controllers;

import com.itextpdf.text.pdf.PdfWriter;
import edu.pidev3A8.entities.Commande;
import edu.pidev3A8.entities.Produit;
import edu.pidev3A8.services.Commandeservice;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;


import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
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
    private  TableColumn<Commande, String> adressecolumn;

    @FXML
    private TableColumn<Commande, String>mailcolumn;
    @FXML
    private TableColumn<Commande, Double> prixtotalcolumn;


    private Commandeservice commandeservice = new Commandeservice();
    @FXML
    private TextField quantiteSearchField;
    @FXML
    private void handleExportToPDF() {
        exportToPDF();
    }
    public void exportToPDF() {
        // Créer un document PDF
        Document document = new Document();
        try {
            // Demander à l'utilisateur où enregistrer le fichier PDF
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                // Créer un fichier PDF
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                // Ajouter un titre au PDF
                document.add(new Paragraph("Liste des commandes"));

                // Créer une table pour les données
                PdfPTable table = new PdfPTable(commandeTable.getColumns().size());
                table.setWidthPercentage(100);

                // Ajouter les en-têtes de colonnes
                for (TableColumn<Commande, ?> column : commandeTable.getColumns()) {
                    table.addCell(column.getText());
                }

                // Ajouter les données de la TableView
                for (Commande commande : commandeTable.getItems()) {
                    table.addCell(String.valueOf(commande.getId_commande()));
                    table.addCell(String.valueOf(commande.getId_produit()));
                    table.addCell(String.valueOf(commande.getId_client()));
                    table.addCell(String.valueOf(commande.getQuantite()));
                    table.addCell(commande.getAdresse());
                    table.addCell(commande.getAdresseEmail());
                    table.addCell(String.valueOf(commande.getPrixTotal()));
                }

                // Ajouter la table au document
                document.add(table);

                // Fermer le document
                document.close();

                // Afficher un message de succès
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export réussi");
                alert.setHeaderText(null);
                alert.setContentText("Le PDF a été exporté avec succès !");
                alert.showAndWait();

                // Ouvrir le PDF avec l'application par défaut
                showPDF(file);
            }
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de l'exportation du PDF : " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void showPDF(File pdfFile) {
        try {
            // Ouvrir le fichier PDF avec l'application par défaut
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.OPEN)) {
                    desktop.open(pdfFile); // Ouvrir le fichier PDF
                } else {
                    throw new UnsupportedOperationException("L'action OPEN n'est pas supportée sur ce système.");
                }
            } else {
                throw new UnsupportedOperationException("Desktop n'est pas supporté sur ce système.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de l'ouverture du PDF : " + e.getMessage());
            alert.showAndWait();
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Fonctionnalité non supportée : " + e.getMessage());
            alert.showAndWait();
        }
    }
    @FXML
    public void initialize() {
        // Lier les colonnes aux propriétés de la classe Commande
        idCommandeColumn.setCellValueFactory(new PropertyValueFactory<>("id_commande"));
        idProduitColumn.setCellValueFactory(new PropertyValueFactory<>("id_produit"));
        idClientColumn.setCellValueFactory(new PropertyValueFactory<>("id_client"));
        quantiteColumn.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        adressecolumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        mailcolumn.setCellValueFactory(new PropertyValueFactory<>("adresseEmail"));
        prixtotalcolumn.setCellValueFactory(new PropertyValueFactory<>("prixTotal"));
        // Charger les commandes dans la TableView
        loadCommandes();
    }

    // Méthode pour charger les commandes depuis la base de données
    // Méthode pour charger les commandes depuis la base de données
    public void loadCommandes() {
        System.out.println("Chargement des commandes...");
        ObservableList<Commande> commandes = FXCollections.observableArrayList(commandeservice.getAllCommandes());
        System.out.println("Nombre de commandes récupérées : " + commandes.size());
        commandeTable.setItems(commandes);
    }
    @FXML
    private void handleAjouterCommande() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouter_commande.fxml"));
            Parent root = loader.load();

            AjouterCommandeController ajouterCommandeController = loader.getController();

            // Définir le callback pour rafraîchir la liste des commandes après l'ajout
            ajouterCommandeController.setOnCommandeAddedCallback(commande -> {
                System.out.println("Callback exécuté : Rafraîchir la liste des commandes");
                loadCommandes(); // Rafraîchir la liste des commandes
            });

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Ajouter une commande");
            stage.setScene(scene);
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
    private void handleProduitPlusVendu() {
        // Récupérer le produit le plus vendu via le service
        Commandeservice commandeservice = new Commandeservice();
        Commande commandePlusVendue = commandeservice.getProduitLePlusVendu();

        if (commandePlusVendue != null) {
            // Créer une boîte de dialogue
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Produit le plus vendu");
            alert.setHeaderText(null);
            alert.setContentText(
                    "ID Produit : " + commandePlusVendue.getId_produit() + "\n" +
                            "Quantité vendue : " + commandePlusVendue.getQuantite() + "\n" +
                            "Prix total : " + commandePlusVendue.getPrixTotal()
            );

            // Appliquer le style CSS
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            dialogPane.getStyleClass().add("dialog-pane");

            // Ajouter une animation de fondu
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), dialogPane);
            fadeTransition.setFromValue(0); // Transparent
            fadeTransition.setToValue(1);   // Opacité totale

            // Afficher la boîte de dialogue après l'animation
            alert.setOnShown(event -> fadeTransition.play());
            alert.showAndWait();
        } else {
            // Afficher un message d'erreur si aucun produit n'est trouvé
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun produit trouvé");
            alert.setHeaderText(null);
            alert.setContentText("Aucun produit n'a été trouvé dans les commandes.");

            // Appliquer le style CSS
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            dialogPane.getStyleClass().add("dialog-pane");

            // Ajouter une animation de fondu
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), dialogPane);
            fadeTransition.setFromValue(0); // Transparent
            fadeTransition.setToValue(1);   // Opacité totale

            // Afficher la boîte de dialogue après l'animation
            alert.setOnShown(event -> fadeTransition.play());
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
    }@FXML
    private void handleSearchByQuantite() {
        try {
            int quantite = Integer.parseInt(quantiteSearchField.getText().trim());
            List<Commande> commandes = commandeservice.searchCommandesByQuantite(quantite);
            ObservableList<Commande> observableCommandes = FXCollections.observableArrayList(commandes);
            commandeTable.setItems(observableCommandes);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez entrer une quantité valide.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleSortByQuantite() {
        boolean ascending = true; // Par défaut, tri ascendant
        List<Commande> commandes = commandeservice.sortCommandesByQuantite(ascending);
        ObservableList<Commande> observableCommandes = FXCollections.observableArrayList(commandes);
        commandeTable.setItems(observableCommandes);
    }

}