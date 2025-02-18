package gestion.pidev.controllers;

import gestion.pidev.entities.Cours_type;
import gestion.pidev.services.Cours_typeService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Ajouter_cours_typeController {

    @FXML
    private TextField typeCoursField;

    @FXML
    private TextField descriptionField;

    private Cours_typeCRUDController coursTypeCRUDController;

    public void setCoursTypeCRUDController(Cours_typeCRUDController coursTypeCRUDController) {
        this.coursTypeCRUDController = coursTypeCRUDController;
    }

    @FXML
    void handleAdd(ActionEvent event) {
        String typeCours = typeCoursField.getText();
        String description = descriptionField.getText();

        if (!isValidTypeCours(typeCours) || description.isEmpty()) {
            showAlert("Invalid Input", "Please ensure the type is only letters and all fields are filled.");
            return;
        }

        Cours_type coursType = new Cours_type(typeCours, description);
        Cours_typeService coursTypeService = new Cours_typeService();
        coursTypeService.addEntity(coursType);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Type de cours ajouté avec succès");
        alert.showAndWait();

        // Close the current stage
        Stage stage = (Stage) typeCoursField.getScene().getWindow();
        stage.close();

        // Refresh the existing CRUD interface
        if (coursTypeCRUDController != null) {
            coursTypeCRUDController.loadCoursTypeData();
        }
    }

    private boolean isValidTypeCours(String typeCours) {
        return typeCours.matches("[a-zA-Z]+");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}