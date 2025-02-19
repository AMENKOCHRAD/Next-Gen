package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import services.eventServices;
import entities.event;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class details implements Initializable {

    public Button GestionButton;
    @FXML
    private TableView<event> eventTable;

    @FXML
    private TableColumn<event, Integer> idColumn;

    @FXML
    private TableColumn<event, String> nomColumn;

    @FXML
    private TableColumn<event, String> typeColumn;

    @FXML
    private TableColumn<event, Date> dateDebutColumn;

    @FXML
    private TableColumn<event, Date> dateFinColumn;

    @FXML
    private TableColumn<event, String> lieuColumn;

    @FXML
    private Button ajoutButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button modifyButton;

    private eventServices eventServices;
    private ObservableList<event> events;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        eventServices = new eventServices();

        // Lier les colonnes aux propriétés de l'objet event
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idEvent"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        dateDebutColumn.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        dateFinColumn.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        lieuColumn.setCellValueFactory(new PropertyValueFactory<>("lieu"));

        // Charger les événements dans la TableView
        loadEvents();
    }

    private void loadEvents() {
        // Récupérer la liste des événements depuis la base de données
        events = FXCollections.observableArrayList(eventServices.displayAllEvent());

        // Afficher les événements dans les logs pour vérification
        System.out.println("Événements récupérés : " + events);

        // Ajouter les événements à la TableView
        eventTable.setItems(events);
    }

    @FXML
    private void handleDeleteButton() {
        event selectedEvent = eventTable.getSelectionModel().getSelectedItem();

        if (selectedEvent != null) {
            eventServices.removeEvent(selectedEvent.getIdEvent());
            loadEvents();
            System.out.println("Événement supprimé avec succès !");
        } else {
            System.out.println("Aucun événement sélectionné !");
        }
    }

    @FXML
    private void handleModifyButton() {
        event selectedEvent = eventTable.getSelectionModel().getSelectedItem();

        if (selectedEvent != null) {
            try {
                // Charger l'interface de modification
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/modificationEvent.fxml"));
                AnchorPane root = loader.load();

                // Récupérer le contrôleur de modification
                ModificationEventController controller = loader.getController();
                controller.setSelectedEvent(selectedEvent);
                controller.setEventServices(eventServices);

                // Afficher la fenêtre de modification
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Modifier l'événement");
                stage.showAndWait(); // Attendre que la fenêtre de modification se ferme

                // Actualiser la TableView après la modification
                loadEvents();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Aucun événement sélectionné !");
        }

    }

    @FXML
    private void handleAddEvent() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterEvent.fxml"));
        Parent root = null;
        try {
            root = loader.load();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ajouterEvent controller = loader.getController();
        ajouterEvent event = new ajouterEvent();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("ajouter event");
        stage.setScene(scene);
        stage.show();
    }

    public void GoToGestionTicket(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML correspondant à la gestion des codes à barre
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/detailsTicket .fxml"));
            Parent root = loader.load();

            // Obtenir la scène de la fenêtre actuelle et la remplacer par la nouvelle scène
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}