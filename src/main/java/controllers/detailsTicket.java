package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import services.ticketServices; // Assurez-vous que le service TicketServices existe
import entities.ticket; // Assurez-vous que la classe ticket est correctement importée

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class detailsTicket implements Initializable {

    @FXML
    private TableView<ticket> ticketTable;

    @FXML
    private TableColumn<ticket, Integer> idTicketColumn;

    @FXML
    private TableColumn<ticket, Integer> idEventColumn;

    @FXML
    private TableColumn<ticket, Integer> prixColumn;

    @FXML
    private TableColumn<ticket, Integer> quantiteColumn;

    @FXML
    private Button ajoutButton;
    @FXML
    private Button deleteButton;

    private ticketServices ticketServices;
    private ObservableList<ticket> tickets;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initialisation du contrôleur detailsTicket...");

        ticketServices = new ticketServices();

        // Lier les colonnes aux propriétés de l'objet ticket
        idTicketColumn.setCellValueFactory(new PropertyValueFactory<>("idticket"));
        idEventColumn.setCellValueFactory(new PropertyValueFactory<>("idevent"));
        prixColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
        quantiteColumn.setCellValueFactory(new PropertyValueFactory<>("quantite"));

        // Charger les tickets dans la TableView
        loadTickets();
        System.out.println("Contrôleur initialisé avec succès !");
    }

    private void loadTickets() {
        System.out.println("Chargement des tickets depuis la base de données...");

        // Récupérer la liste des tickets depuis la base de données
        tickets = FXCollections.observableArrayList(ticketServices.displayAllTicket());

        // Afficher les tickets dans les logs pour vérification
        System.out.println("Tickets récupérés : " + tickets);

        // Ajouter les tickets à la TableView
        ticketTable.setItems(tickets);
        System.out.println("Tickets chargés dans la TableView !");
    }

    @FXML
    private void handleDeleteButton() {
        ticket selectedTicket = ticketTable.getSelectionModel().getSelectedItem();

        if (selectedTicket != null) {
            ticketServices.removeTicket(selectedTicket.getIdticket());
            loadTickets();
            System.out.println("Ticket supprimé avec succès !");
        } else {
            System.out.println("Aucun ticket sélectionné !");

            // Afficher une alerte
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Suppression impossible");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un ticket à supprimer.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleModifyTicket() {
        ticket selectedTicket = ticketTable.getSelectionModel().getSelectedItem();

        if (selectedTicket != null) {
            try {
                // Vérifiez le chemin du fichier FXML
                URL fxmlLocation = getClass().getResource("/modificationTicket.fxml");
                if (fxmlLocation == null) {
                    throw new IOException("Le fichier FXML n'a pas été trouvé à l'emplacement spécifié.");
                }

                // Charger l'interface de modification
                FXMLLoader loader = new FXMLLoader(fxmlLocation);

                // Charger l'interface dans un conteneur
                AnchorPane root = loader.load();

                // Récupérer le contrôleur de modification
                ModificationTicketController controller = loader.getController();
                controller.setSelectedTicket(selectedTicket);
                controller.setTicketServices(ticketServices);

                // Afficher la fenêtre de modification
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Modifier le Ticket");
                stage.showAndWait(); // Attendre que la fenêtre se ferme

                // Actualiser la TableView après modification
                loadTickets();
            } catch (IOException e) {
                e.printStackTrace();
                // Afficher une alerte en cas d'erreur
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Impossible d'ouvrir la modification.");
                alert.setContentText("Vérifiez le fichier FXML et le contrôleur. Détail de l'erreur: " + e.getMessage());
                alert.showAndWait();
            }
        } else {
            // Afficher un message si aucun ticket n'est sélectionné
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun ticket sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un ticket à modifier.");
            alert.showAndWait();
        }
    }



    @FXML
    private void handleAddTicket() {
        System.out.println("Clic sur le bouton Ajouter...");

        try {
            // Charger l'interface d'ajout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterTicket.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur d'ajout
            ajouterTicket controller = loader.getController();

            // Afficher la fenêtre d'ajout
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un ticket");
            stage.showAndWait(); // Attendre que la fenêtre d'ajout se ferme

            // Actualiser la TableView après l'ajout
            loadTickets();
            System.out.println("Ticket ajouté avec succès !");
        } catch (IOException e) {
            e.printStackTrace();
            // Afficher une alerte en cas d'erreur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible de charger l'interface d'ajout.");
            alert.setContentText("Veuillez vérifier que le fichier FXML est correctement configuré.");
            alert.showAndWait();
        }
    }
}