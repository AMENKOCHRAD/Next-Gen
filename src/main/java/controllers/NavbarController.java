package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class NavbarController {

    @FXML
    private Button eventButton;

    @FXML
    private void handleEventButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/event_view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) eventButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
