package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class MapController {
    @FXML
    private Label locationLabel;

    @FXML
    private WebView mapView;

    private EventController parentController;
    private double latitude;
    private double longitude;
    private String address;

    @FXML
    public void initialize() {
        WebEngine engine = mapView.getEngine();
        engine.load(getClass().getResource("/views/map.html").toExternalForm());

        // Add the controller as a member of the JavaScript window object
        engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == javafx.concurrent.Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) engine.executeScript("window");
                window.setMember("javaController", this);
            }
        });
    }

    public void setParentController(EventController controller) {
        this.parentController = controller;
    }

    // This method will be called from JavaScript

    public void updateLocationWithAddress(double lat, double lng, String address) {
        this.latitude = lat;
        this.longitude = lng;
        this.address = address;
        locationLabel.setText(String.format("Location: %.6f, %.6f%nAddress: %s", lat, lng, address));
    }

    @FXML
    protected void onAcceptButtonClick() {
        if (parentController != null && address != null) {
            String location = String.format("%.6f, %.6f - %s", latitude, longitude, address);
            parentController.updateLocation(location);

            // Close the window
            Stage stage = (Stage) mapView.getScene().getWindow();
            stage.close();
        }
    }
}