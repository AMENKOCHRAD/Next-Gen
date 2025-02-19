package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import entities.event;
import services.eventServices;

import java.time.LocalDate;
import java.util.Date;

public class ModificationEventController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField typeField;

    @FXML
    private DatePicker dateDebutPicker;

    @FXML
    private DatePicker dateFinPicker;

    @FXML
    private TextField lieuField;

    private event selectedEvent;
    private eventServices eventServices;

    public void setSelectedEvent(event selectedEvent) {
        this.selectedEvent = selectedEvent;
        populateFields();
    }

    public void setEventServices(eventServices eventServices) {
        this.eventServices = eventServices;
    }

    private void populateFields() {
        if (selectedEvent != null) {
            nomField.setText(selectedEvent.getNom());
            typeField.setText(selectedEvent.getType());

            // Vérifier si les dates ne sont pas null avant de les convertir
            if (selectedEvent.getDateDebut() != null) {
                dateDebutPicker.setValue(convertToLocalDate(selectedEvent.getDateDebut()));
            }
            if (selectedEvent.getDateFin() != null) {
                dateFinPicker.setValue(convertToLocalDate(selectedEvent.getDateFin()));
            }

            lieuField.setText(selectedEvent.getLieu());
        }
    }

    private LocalDate convertToLocalDate(Date date) {
        if (date instanceof java.sql.Date) {
            // Convertir java.sql.Date en java.util.Date
            return new java.util.Date(date.getTime()).toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
        }
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }

    private java.sql.Date convertToDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return java.sql.Date.valueOf(localDate);
    }

    @FXML
    private void handleModifyButton() {
        if (selectedEvent != null) {
            // Mettre à jour les propriétés de l'événement
            selectedEvent.setNom(nomField.getText());
            selectedEvent.setType(typeField.getText());

            // Vérifier si les valeurs des DatePicker ne sont pas null
            if (dateDebutPicker.getValue() != null) {
                selectedEvent.setDateDebut(convertToDate(dateDebutPicker.getValue()));
            }
            if (dateFinPicker.getValue() != null) {
                selectedEvent.setDateFin(convertToDate(dateFinPicker.getValue()));
            }

            selectedEvent.setLieu(lieuField.getText());

            // Mettre à jour l'événement dans la base de données
            eventServices.UpDateEvent(selectedEvent, selectedEvent.getIdEvent());

            // Fermer la fenêtre de modification
            nomField.getScene().getWindow().hide();
        }
    }
}