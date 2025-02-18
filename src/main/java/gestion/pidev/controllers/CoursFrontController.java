package gestion.pidev.controllers;

import gestion.pidev.entities.Cours;
import gestion.pidev.services.CoursService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class CoursFrontController {

    @FXML
    private FlowPane coursesFlowPane;

    private CoursService coursService = new CoursService();

    @FXML
    public void initialize() {
        loadCourses();
    }

    public void reloadData() {
        coursesFlowPane.getChildren().clear();
        loadCourses();
    }

    private void loadCourses() {
        List<Cours> courses = coursService.getAllData();
        for (Cours course : courses) {
            VBox courseBox = new VBox(10);
            ImageView courseImage = new ImageView();
            if (course.getImage() != null) {
                Image image = new Image(new ByteArrayInputStream(course.getImage()));
                courseImage.setImage(image);
            }
            courseImage.setFitHeight(100);
            courseImage.setFitWidth(100);

            Text courseType = new Text("Type: " + course.getType());
            Text courseName = new Text("Nom: " + course.getNom_cours());
            Text courseDate = new Text("Date: " + new SimpleDateFormat("dd/MM/yyyy").format(course.getDate()));
            Text coachEmail = new Text("Email Coach: " + course.getAdresse_mail_coach());

            courseBox.getChildren().addAll(courseImage, courseType, courseName, courseDate, coachEmail);
            coursesFlowPane.getChildren().add(courseBox);
        }
    }

    @FXML
    private void handleBackToCoursCRUD(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CoursCRUD.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}