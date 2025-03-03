package controllers;

import entities.Event;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import services.EventServices;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class EventController {
    public Button btnModifier;
    public Button btnAjouter;
    public Button btnSupprimer;
    public Label dateDebutError;
    public Label dateFinError;
    public Label nomError;
    public Label typeError;
    public Label adresseError;
    public Button btngestion;
    @FXML
    private TextField nomField;
    @FXML
    private TextField typeField;
    @FXML
    private TextField adresseField;
    @FXML
    private ImageView mapIcon;
    @FXML
    private DatePicker dateDebutField;
    @FXML
    private DatePicker dateFinField;
    @FXML
    private TableView<Event> eventTable;
    @FXML
    private TableColumn<Event, String> colNom;
    @FXML
    private TableColumn<Event, String> colType;
    @FXML
    private TableColumn<Event, String> colAdresse;
    @FXML
    private TableColumn<Event, String> colDateDebut;
    @FXML
    private TableColumn<Event, String> colDateFin;

    private final EventServices eventServices = new EventServices();
    private final ObservableList<Event> eventList = FXCollections.observableArrayList();

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @FXML
    public void initialize() {
        btngestion.setOnAction(event -> openTicketView());

        colNom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        colType.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
        colAdresse.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAdresse()));
        colDateDebut.setCellValueFactory(cellData ->
                new SimpleStringProperty(dateFormat.format(cellData.getValue().getDateDebut())));

        colDateFin.setCellValueFactory(cellData ->
                new SimpleStringProperty(dateFormat.format(cellData.getValue().getDateFin())));
        loadEvents();

        // Ajouter un écouteur pour la recherche en temps réel
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterEvents(newValue);
        });

    }

    private boolean validateEventForm() {
        boolean isValid = true;

        if (nomField.getText().isEmpty()) {
            nomError.setText("Nom is required");
            isValid = false;
        } else {
            nomError.setText("");
        }

        if (typeField.getText().isEmpty()) {
            typeError.setText("Type is required");
            isValid = false;
        } else {
            typeError.setText("");
        }

        if (adresseField.getText().isEmpty()) {
            adresseError.setText("Adresse is required");
            isValid = false;
        } else {
            adresseError.setText("");
        }

        if (dateDebutField.getValue() == null) {
            dateDebutError.setText("Date Début is required");
            isValid = false;
        } else {
            dateDebutError.setText("");
        }

        if (dateFinField.getValue() == null) {
            dateFinError.setText("Date Fin is required");
            isValid = false;
        } else {
            dateFinError.setText("");
        }

        return isValid;
    }
    @FXML
    public void ajouterEvent() {
        if (validateEventForm()) {
            Event event = new Event(0, nomField.getText(), typeField.getText(), adresseField.getText(),
                    Date.valueOf(dateDebutField.getValue()), Date.valueOf(dateFinField.getValue()), null);

            eventServices.addEvent(event);
            eventList.add(event);
            eventTable.setItems(eventList);
        }
    }

    @FXML
    public void modifierEvent() {
        if (validateEventForm()) {
            Event selected = eventTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selected.setNom(nomField.getText());
                selected.setType(typeField.getText());
                selected.setAdresse(adresseField.getText());
                selected.setDateDebut(Date.valueOf(dateDebutField.getValue()));
                selected.setDateFin(Date.valueOf(dateFinField.getValue()));

                eventServices.updateEvent(selected);
                eventTable.refresh();
            }
        }
    }

    @FXML
    public void supprimerEvent() {
        Event selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            eventServices.deleteEvent(selected.getIdEvent());
            eventList.remove(selected);
        }
    }
    private void openTicketView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/TicketView.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 875, 517);
            Stage stage = (Stage) btngestion.getScene().getWindow();
            stage.setTitle("Gestion des Tickets");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void openMap() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/map_view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            Stage stage = new Stage();
            stage.setTitle("Select Location");
            stage.setScene(scene);

            // Get the controller and pass a reference to this controller
            MapController mapController = fxmlLoader.getController();
            mapController.setParentController(this);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to be called from the MapController
    public void updateLocation(String location) {
        adresseField.setText(location);
    }


    @FXML
    private TextField searchField;
    private void filterEvents(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            eventTable.setItems(eventList); // Afficher tous les événements
            return;
        }

        ObservableList<Event> filteredList = FXCollections.observableArrayList();

        for (Event event : eventList) {
            if (event.getNom().toLowerCase().contains(searchText.toLowerCase()) ||
                    event.getType().toLowerCase().contains(searchText.toLowerCase()) ||
                    event.getAdresse().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(event);
            }
        }

        eventTable.setItems(filteredList);
    }





    private void loadEvents() {
        List<Event> events = eventServices.getAllEvents();
        eventList.setAll(events);
        eventTable.setItems(eventList);
    }


}