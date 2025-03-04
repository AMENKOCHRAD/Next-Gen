package edu.pidev3a8.controllers;

import edu.pidev3a8.entities.Event;
import edu.pidev3a8.entities.Ticket;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import edu.pidev3a8.services.EventServices;
import edu.pidev3a8.services.TicketServices;

import java.io.IOException;
import java.util.List;

public class TicketController {
    public Label prixError;
    public Label quantiteError;
    public Label eventError;
    public Button btnAjouter;
    public Button btnModifier;
    public Button btnSupprimer;
    public Button btnRetour;
    public TextField searchField;
    @FXML private TextField prixField;
    @FXML private TextField quantiteField;
    @FXML private ComboBox<Event> eventComboBox;
    @FXML private TableView<Ticket> ticketTable;
    @FXML private TableColumn<Ticket, String> colPrix;
    @FXML private TableColumn<Ticket, String> colQuantite;
    @FXML private TableColumn<Ticket, String> colEventName;

    private TicketServices ticketServices = new TicketServices();
    private EventServices eventServices = new EventServices();
    private ObservableList<Ticket> ticketList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadTable();
        loadEvents();
        // Ajouter un écouteur pour la recherche en temps réel
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterEvents(newValue);
        });
    }

    private void loadTable() {
        ticketList.clear();
        ticketList.addAll(ticketServices.getAllTickets());
        ticketTable.setItems(ticketList);

        colPrix.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getPrix())));
        colQuantite.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getQuantite())));
        colEventName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEventName()));
    }

    private void loadEvents() {
        List<Event> events = eventServices.getAllEvents();
        eventComboBox.getItems().addAll(events);
        eventComboBox.setConverter(new StringConverter<Event>() {
            @Override
            public String toString(Event event) {
                return event != null ? event.getNom() : "";
            }

            @Override
            public Event fromString(String string) {
                return null;
            }
        });
    }

    private boolean validateTicketForm() {
        boolean isValid = true;

        if (prixField.getText().isEmpty()) {
            prixError.setText("Prix is required");
            isValid = false;
        } else {
            prixError.setText("");
        }

        if (quantiteField.getText().isEmpty()) {
            quantiteError.setText("Quantité is required");
            isValid = false;
        } else {
            quantiteError.setText("");
        }

        if (eventComboBox.getValue() == null) {
            eventError.setText("Événement is required");
            isValid = false;
        } else {
            eventError.setText("");
        }

        return isValid;
    }

    @FXML
    public void ajouterTicket() {
        if (validateTicketForm()) {
            double prix = Double.parseDouble(prixField.getText());
            int quantite = Integer.parseInt(quantiteField.getText());
            Event selectedEvent = eventComboBox.getValue();

            Ticket ticket = new Ticket(0, prix, quantite, selectedEvent.getNom(), selectedEvent.getIdEvent());
            ticketServices.addTicket(ticket);
            loadTable();
        }
    }

    @FXML
    public void modifierTicket() {
        if (validateTicketForm()) {
            Ticket selectedTicket = ticketTable.getSelectionModel().getSelectedItem();
            if (selectedTicket != null) {
                double prix = Double.parseDouble(prixField.getText());
                int quantite = Integer.parseInt(quantiteField.getText());
                Event selectedEvent = eventComboBox.getValue();

                selectedTicket.setPrix(prix);
                selectedTicket.setQuantite(quantite);
                selectedTicket.setEventName(selectedEvent.getNom());
                selectedTicket.setidEvent(selectedEvent.getIdEvent());
                ticketServices.updateTicket(selectedTicket);
                loadTable();
            }
        }
    }

    @FXML
    public void supprimerTicket() {
        Ticket selectedTicket = ticketTable.getSelectionModel().getSelectedItem();
        if (selectedTicket != null) {
            ticketServices.deleteTicket(selectedTicket.getIdTicket());
            loadTable();
        }
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/event_view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void filterEvents(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            ticketTable.setItems(ticketList); // Afficher tous les événements
            return;
        }

        ObservableList<Ticket> filteredList = FXCollections.observableArrayList();

        for (Ticket ticket : ticketList) {
            if (ticket.getEventName().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(ticket);
            }
        }

        ticketTable.setItems(filteredList);
    }

}