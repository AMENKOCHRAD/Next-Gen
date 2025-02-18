package gestion.pidev.controllers;

import gestion.pidev.entities.Cours;
import gestion.pidev.entities.Cours_type;
import gestion.pidev.services.CoursService;
import gestion.pidev.services.Cours_typeService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class Ajouter_coursController {

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

    private byte[] image; // New attribute

    private CoursCRUDController coursCRUDController;

    public void setCoursCRUDController(CoursCRUDController coursCRUDController) {
        this.coursCRUDController = coursCRUDController;
    }

    @FXML
    public void initialize() {
        Cours_typeService coursTypeService = new Cours_typeService();
        List<Cours_type> coursTypes = coursTypeService.getAllData();
        for (Cours_type coursType : coursTypes) {
            typeComboBox.getItems().add(coursType.getType_cours());
        }
    }

    @FXML
    void Ajouter_cours(ActionEvent event) {
        String types = typeComboBox.getValue();
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

        LocalDate localDate = date.getValue();
        Date dates = java.sql.Date.valueOf(localDate);

        Cours c = new Cours(types, nom_courss, dates, addresse_coachs, image);
        CoursService coursService = new CoursService();
        coursService.addEntity(c);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Cours ajouté avec succès");
        alert.showAndWait();




        // Close the current stage
        Stage stage = (Stage) nom_cours.getScene().getWindow();
        stage.close();

        // Refresh the existing CRUD interface
        if (coursCRUDController != null) {
            coursCRUDController.loadCoursData();
        }
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