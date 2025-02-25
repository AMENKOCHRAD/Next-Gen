package gestion.pidev.controllers;

import gestion.pidev.entities.Cours;
import gestion.pidev.services.CoursService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.stream.Collectors;

public class ListCoursController {

    @FXML
    private FlowPane coursContainer;

    @FXML
    private TextField searchField;

    private CoursService coursService = new CoursService();

    @FXML
    public void initialize() {
        loadCours();
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        loadCours(searchText);
    }

    @FXML
    private void handleExercicesGuides() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CoursFront.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) coursContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePlanEntrainement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CoursPlans.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) coursContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadCours() {
        loadCours("");
    }

    private void loadCours(String searchText) {
        List<Cours> coursList = coursService.getAllData();
        if (!searchText.isEmpty()) {
            coursList = coursList.stream()
                    .filter(cours -> cours.getNom_cours().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());
        }
        coursContainer.getChildren().clear();
        for (Cours cours : coursList) {
            VBox coursBox = new VBox(10);
            coursBox.setAlignment(Pos.CENTER);

            // Cadre blanc avec bordure et padding
            coursBox.setStyle("-fx-border-color: white; -fx-border-width: 2; -fx-border-radius: 5; -fx-padding: 10;");

            Image image = new Image(new ByteArrayInputStream(cours.getImage()));
            ImageView imageView = new ImageView(image);
            double imageWidth = image.getWidth();
            double imageHeight = image.getHeight();
            double squareSize = Math.min(imageWidth, imageHeight);
            double x = (imageWidth - squareSize) / 2;
            double y = (imageHeight - squareSize) / 2;
            imageView.setViewport(new Rectangle2D(x, y, squareSize, squareSize));
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            imageView.setPreserveRatio(true);

            Label nameLabel = new Label(cours.getNom_cours());
            nameLabel.setStyle("-fx-text-fill: white;"); // Texte en blanc

            coursBox.getChildren().addAll(imageView, nameLabel);
            coursContainer.getChildren().add(coursBox);
        }
    }
}
