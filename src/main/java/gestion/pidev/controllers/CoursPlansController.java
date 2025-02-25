package gestion.pidev.controllers;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

public class CoursPlansController {

    @FXML
    private ImageView image1;

    @FXML
    private ImageView image2;

    @FXML
    private ImageView image3;

    private Voice voice;
    private Thread speechThread;

    @FXML
    public void initialize() {
        // Chargement des images statiques
        try {
            image1.setImage(new Image(getClass().getResource("/Images/abs_wk.png").toExternalForm()));
            image2.setImage(new Image(getClass().getResource("/Images/beginner.png").toExternalForm()));
            image3.setImage(new Image(getClass().getResource("/Images/advanced.png").toExternalForm()));
        } catch (NullPointerException e) {
            System.err.println("Erreur : Impossible de charger une ou plusieurs images !");
            e.printStackTrace();
        }

        // Initialisation de la voix FreeTTS
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        VoiceManager voiceManager = VoiceManager.getInstance();
        voice = voiceManager.getVoice("kevin16");

        if (voice != null) {
            voice.allocate();
        } else {
            System.err.println("Erreur : Impossible de charger la voix !");
        }
    }

    @FXML
    private void showImagePopup(Image image, String text) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Aperçu du plan d'entraînement");

        ImageView enlargedImageView = new ImageView(image);
        enlargedImageView.setFitWidth(600);
        enlargedImageView.setFitHeight(400);
        enlargedImageView.setPreserveRatio(true);

        VBox layout = new VBox(20, enlargedImageView);
        layout.setStyle("-fx-background-color: black; -fx-padding: 20; -fx-alignment: center;");

        Scene scene = new Scene(layout);
        popupStage.setScene(scene);

        // Lancer la lecture du texte
        speakText(text);

        // Fermer la fenêtre si on clique dessus
        layout.setOnMouseClicked(event -> {
            popupStage.close();
        });

        popupStage.showAndWait();
    }

    private void speakText(String text) {
        if (voice == null) {
            System.err.println("Erreur : La voix n'est pas initialisée !");
            return;
        }

        speechThread = new Thread(() -> {
            voice.speak(text); // Prononcer le texte en une seule fois
        });
        speechThread.setDaemon(true); // Permet au thread de s'arrêter lorsque l'application se ferme
        speechThread.start();
    }

    @FXML
    private void showImage1() {
        showImagePopup(image1.getImage(), "This program is an abdominal workout.");
    }

    @FXML
    private void showImage2() {
        showImagePopup(image2.getImage(), "This program is dedicated to beginners!.");
    }

    @FXML
    private void showImage3() {
        showImagePopup(image3.getImage(), "This program is dedicated to people who are advanced in sports!.");
    }
}