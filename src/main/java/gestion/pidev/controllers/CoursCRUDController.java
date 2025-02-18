package gestion.pidev.controllers;

import gestion.pidev.entities.Cours;
import gestion.pidev.services.CoursService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;

public class CoursCRUDController {

    @FXML
    private TableView<Cours> coursTable;

    @FXML
    private TableColumn<Cours, Integer> idColumn;

    @FXML
    private TableColumn<Cours, String> typeColumn;

    @FXML
    private TableColumn<Cours, String> nomCoursColumn;

    @FXML
    private TableColumn<Cours, String> dateColumn;

    @FXML
    private TableColumn<Cours, String> adresseMailCoachColumn;

    @FXML
    private TableColumn<Cours, ImageView> imageColumn;

    private CoursService coursService = new CoursService();
    private ObservableList<Cours> coursList;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        nomCoursColumn.setCellValueFactory(new PropertyValueFactory<>("nom_cours"));
        dateColumn.setCellValueFactory(cellData -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            return new javafx.beans.property.SimpleStringProperty(dateFormat.format(cellData.getValue().getDate()));
        });
        adresseMailCoachColumn.setCellValueFactory(new PropertyValueFactory<>("adresse_mail_coach"));
        imageColumn.setCellValueFactory(cellData -> {
            byte[] imageBytes = cellData.getValue().getImage();
            if (imageBytes != null) {
                Image image = new Image(new ByteArrayInputStream(imageBytes));
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);
                return new javafx.beans.property.SimpleObjectProperty<>(imageView);
            } else {
                return null;
            }
        });

        imageColumn.setCellFactory(column -> new TableCell<Cours, ImageView>() {
            @Override
            protected void updateItem(ImageView item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    HBox hbox = new HBox(item);
                    hbox.setAlignment(Pos.CENTER);
                    setGraphic(hbox);
                } else {
                    setGraphic(null);
                }
            }
        });

        loadCoursData();
    }

    public void loadCoursData() {
        coursList = FXCollections.observableArrayList(coursService.getAllData());
        coursTable.setItems(coursList);
    }

    @FXML
    void handleAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Ajouter_cours.fxml"));
            Parent root = loader.load();

            Ajouter_coursController controller = loader.getController();
            controller.setCoursCRUDController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadCoursData(); // Refresh the table after adding a new course
        } catch (IOException e) {
            showAlert("Erreur", "Erreur de chargement", "Impossible de charger l'interface d'ajout.");
        }
    }

    @FXML
    void handleDelete() {
        Cours selectedCours = coursTable.getSelectionModel().getSelectedItem();
        if (selectedCours != null) {
            coursService.deleteEntity(selectedCours);
            loadCoursData();
        } else {
            showAlert("No Selection", "No Cours Selected", "Please select a cours in the table.");
        }
    }

    @FXML
    void handleUpdate() {
        Cours selectedCours = coursTable.getSelectionModel().getSelectedItem();
        if (selectedCours != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifier_cours.fxml"));
                Parent root = loader.load();

                Modifier_coursController controller = loader.getController();
                controller.setCours(selectedCours);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.showAndWait();
                loadCoursData(); // Refresh the table after updating the course
            } catch (IOException e) {
                showAlert("Erreur", "Erreur de chargement", "Impossible de charger l'interface de modification.");
            }
        } else {
            showAlert("No Selection", "No Cours Selected", "Please select a cours in the table.");
        }
    }

    @FXML
    void handleCoursTypeCRUD() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Cours_typeCRUD.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current stage
            Stage currentStage = (Stage) coursTable.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur de chargement", "Impossible de charger l'interface de gestion des types de cours.");
        }
    }
    @FXML
    private void handleCoursFront() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CoursFront.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) coursTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur de chargement", "Impossible de charger l'interface des cours.");
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