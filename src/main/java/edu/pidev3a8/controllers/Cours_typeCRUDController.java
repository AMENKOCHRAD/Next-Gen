package edu.pidev3a8.controllers;

import edu.pidev3a8.entities.Cours_type;
import edu.pidev3a8.services.Cours_typeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class Cours_typeCRUDController {

    @FXML
    private TableView<Cours_type> coursTypeTable;

    @FXML
    private TableColumn<Cours_type, Integer> idColumn;

    @FXML
    private TableColumn<Cours_type, String> typeCoursColumn;

    @FXML
    private TableColumn<Cours_type, String> descriptionColumn;

    @FXML
    private TextField typeCoursField;

    @FXML
    private TextField descriptionField;

    private Cours_typeService coursTypeService = new Cours_typeService();
    private ObservableList<Cours_type> coursTypeList;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        typeCoursColumn.setCellValueFactory(new PropertyValueFactory<>("type_cours"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        loadCoursTypeData();
    }

    public void loadCoursTypeData() {
        coursTypeList = FXCollections.observableArrayList(coursTypeService.getAllData());
        coursTypeTable.setItems(coursTypeList);
    }

    @FXML
    void handleAdd(ActionEvent event) {
        String typeCours = typeCoursField.getText();
        String description = descriptionField.getText();

        Cours_type coursType = new Cours_type(typeCours, description);
        coursTypeService.addEntity(coursType);
        loadCoursTypeData();
    }


    @FXML
    void handleDelete(ActionEvent event) {
        Cours_type selectedCoursType = coursTypeTable.getSelectionModel().getSelectedItem();
        if (selectedCoursType != null) {
            coursTypeService.deleteEntity(selectedCoursType);
            loadCoursTypeData();
        } else {
            showAlert("No Selection", "No Cours Type Selected", "Please select a cours type in the table.");
        }
    }
    @FXML
    void handleAddNewCoursType(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Ajouter_cours_type.fxml"));
            Parent root = loader.load();

            Ajouter_cours_typeController controller = loader.getController();
            controller.setCoursTypeCRUDController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur de chargement", "Impossible de charger l'interface d'ajout de type de cours.");
        }
    }
    @FXML
    void handleUpdate(ActionEvent event) {
        Cours_type selectedCoursType = coursTypeTable.getSelectionModel().getSelectedItem();
        if (selectedCoursType != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Modifier_cours_type.fxml"));
                Parent root = loader.load();

                Modifier_cours_typeController controller = loader.getController();
                controller.setCoursType(selectedCoursType);
                controller.setCoursTypeCRUDController(this);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.showAndWait();
            } catch (IOException e) {
                showAlert("Erreur", "Erreur de chargement", "Impossible de charger l'interface de modification de type de cours.");
            }
        } else {
            showAlert("No Selection", "No Cours Type Selected", "Please select a cours type in the table.");
        }
    }
    @FXML
    void handleBackToCoursCRUD(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CoursCRUD.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current stage
            Stage currentStage = (Stage) coursTypeTable.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur de chargement", "Impossible de charger l'interface de gestion des cours.");
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}