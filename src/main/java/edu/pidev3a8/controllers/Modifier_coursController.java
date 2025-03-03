package edu.pidev3a8.controllers;

import edu.pidev3a8.entities.Cours;
import edu.pidev3a8.services.CoursService;
import edu.pidev3a8.entities.Cours_type;
import edu.pidev3a8.services.Cours_typeService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class Modifier_coursController {

    @FXML
    private TextField addresse_coach;

    @FXML
    private DatePicker date;

    @FXML
    private TextField nom_cours;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private ImageView imageView;

    private byte[] image;

    private Cours selectedCours;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public void setCours(Cours cours) {
        this.selectedCours = cours;
        this.nom_cours.setText(cours.getNom_cours());
        this.addresse_coach.setText(cours.getAdresse_mail_coach());

        Cours_typeService coursTypeService = new Cours_typeService();
        List<Cours_type> coursTypes = coursTypeService.getAllData();
        for (Cours_type coursType : coursTypes) {
            typeComboBox.getItems().add(coursType.getType_cours());
        }
        typeComboBox.setValue(cours.getType());

        this.image = cours.getImage();
        if (image != null) {
            Image img = new Image(new ByteArrayInputStream(image));
            imageView.setImage(img);
        }
    }

    @FXML
    void modifierCours(ActionEvent event) {
        String nom_courss = nom_cours.getText();
        String addresse_coachs = addresse_coach.getText();

        if (!Pattern.matches("^[a-zA-Z].*", nom_courss)) {
            showAlert("Invalid Input", "Nom Cours Error", "Le nom du cours doit commencer par une lettre.");
            return;
        }

        if (!Pattern.matches(".*@.*", addresse_coachs)) {
            showAlert("Invalid Input", "Adresse Mail Coach Error", "L'adresse mail du coach doit contenir '@'.");
            return;
        }

        selectedCours.setType(typeComboBox.getValue());
        selectedCours.setNom_cours(nom_courss);
        try {
            // Convert DatePicker value to java.util.Date
            Date dates = dateFormat.parse(date.getValue().toString());
            selectedCours.setDate(new java.sql.Date(dates.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        selectedCours.setAdresse_mail_coach(addresse_coachs);
        selectedCours.setImage(image);

        CoursService coursService = new CoursService();
        coursService.updateEntity(selectedCours.getId(), selectedCours);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Cours modifié avec succès");
        alert.showAndWait();

        // Close the current stage
        Stage stage = (Stage) nom_cours.getScene().getWindow();
        stage.close();
    }

    @FXML
    void handleImageUpload(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                image = Files.readAllBytes(file.toPath());
                Image img = new Image(new ByteArrayInputStream(image));
                imageView.setImage(img);
            } catch (IOException e) {
                showAlert("File Error", "Could not read file", "An error occurred while reading the file.");
            }
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}