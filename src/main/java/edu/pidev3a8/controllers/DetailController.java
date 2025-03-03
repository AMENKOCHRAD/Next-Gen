package edu.pidev3a8.controllers;



import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class DetailController {

    @FXML
    private TextField a;

    @FXML
    private TextField b;

    public TextField getA() {
        return a;
    }

        public void setA(String a) {
        this.a.setText(a);
    }

    public TextField getB() {
        return b;
    }

    public void setB(String b) {
        this.b.setText(b);
    }
}
