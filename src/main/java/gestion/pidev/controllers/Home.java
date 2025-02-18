package gestion.pidev.controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

import javax.imageio.IIOException;

public class Home extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {


        try{
        Parent root= FXMLLoader.load(getClass().getResource("/CoursCRUD.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
     }catch(IIOException e){

            System.out.println("Erreur de lecture"+e.getMessage());

        }

    }
}
