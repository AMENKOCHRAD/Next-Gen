package gestion.pidev.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;

import java.io.IOException;

public class CoursFrontController {

    @FXML
    private VBox vbox1;

    @FXML
    private VBox vbox2;

    @FXML
    private VBox vbox3;

    @FXML
    private ImageView imageView1;

    @FXML
    private ImageView imageView2;

    @FXML
    private ImageView imageView3;

    @FXML
    public void initialize() {

    }


    @FXML
    private void handleSquatsAction(ActionEvent event) {
        runPythonScript("C:/Users/Fedy_/Desktop/exos_python/squats.py");
    }

    @FXML
    private void handleSautsAmericainsAction(ActionEvent event) {
        runPythonScript("C:/Users/Fedy_/Desktop/exos_python/sauts_americains.py");
    }

    @FXML
    private void handleElevationDeGenouxAction(ActionEvent event) {
        runPythonScript("C:/Users/Fedy_/Desktop/exos_python/elevation_de_genoux.py");
    }

    private void runPythonScript(String scriptPath) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python", scriptPath);
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}