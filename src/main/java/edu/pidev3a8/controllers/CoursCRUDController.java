package edu.pidev3a8.controllers;

import edu.pidev3a8.entities.Cours;
import edu.pidev3a8.services.CoursService;
import org.vosk.Model;
import org.vosk.Recognizer;
import javafx.application.Platform;
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
import javafx.scene.chart.PieChart;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private Recognizer recognizer;

    @FXML
    public void initialize() {
        // Initialisation de la table et autres éléments
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

        // Initialisation de la reconnaissance vocale
        initVoiceRecognition();
    }

    public void loadCoursData() {
        coursList = FXCollections.observableArrayList(coursService.getAllData());
        coursTable.setItems(coursList);
    }

    private void initVoiceRecognition() {
        new Thread(() -> {
            try {
                // Charger le modèle de langue Vosk
                Model model = new Model("src/main/resources/models/vosk-model-small-fr-0.22");
                recognizer = new Recognizer(model, 16000);

                // Configuration du microphone
                AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

                if (!AudioSystem.isLineSupported(info)) {
                    System.err.println("Microphone non supporté");
                    return;
                }

                TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
                microphone.open(format);
                microphone.start();

                byte[] buffer = new byte[4096];
                System.out.println("Enregistrement en cours...");

                // Enregistrement et reconnaissance en temps réel
                while (true) {
                    int bytesRead = microphone.read(buffer, 0, buffer.length);
                    if (bytesRead > 0) {
                        if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                            String result = recognizer.getResult();
                            handleVoiceCommand(result);
                        } else {
                            String partialResult = recognizer.getPartialResult();
                            System.out.println("Reconnaissance partielle : " + partialResult);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleVoiceCommand(String command) {
        Platform.runLater(() -> {
            if (command.toLowerCase().contains("liste")) {
                handleListCours();
            } else if (command.toLowerCase().contains("ajouter")) {
                handleAdd();
            } else if (command.toLowerCase().contains("supprimer")) {
                handleDelete();
            } else if (command.toLowerCase().contains("modifier")) {
                handleUpdate();
            } else if (command.toLowerCase().contains("statistiques")) {
                handleStats();
            } else {
                System.out.println("Commande non reconnue : " + command);
            }
        });
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
            loadCoursData(); // Rafraîchir la table après l'ajout d'un cours
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
            showAlert("Aucune sélection", "Aucun cours sélectionné", "Veuillez sélectionner un cours dans le tableau.");
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
                loadCoursData(); // Rafraîchir la table après la modification d'un cours
            } catch (IOException e) {
                showAlert("Erreur", "Erreur de chargement", "Impossible de charger l'interface de modification.");
            }
        } else {
            showAlert("Aucune sélection", "Aucun cours sélectionné", "Veuillez sélectionner un cours dans le tableau.");
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

            // Fermer la fenêtre actuelle
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

    @FXML
    private void handleListCours() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListCours.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) coursTable.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur de chargement", "Impossible de charger l'interface de la liste des cours.");
        }
    }

    @FXML
    private void handleStats() {
        // Récupérer tous les cours et les grouper par type
        List<Cours> allCours = coursService.getAllData();
        Map<String, Long> stats = allCours.stream()
                .collect(Collectors.groupingBy(Cours::getType, Collectors.counting()));

        // Créer un PieChart et ajouter les données
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Pourcentage de cours par type");
        stats.forEach((type, count) -> {
            PieChart.Data slice = new PieChart.Data(type, count);
            pieChart.getData().add(slice);
        });

        // Afficher le graphique dans une nouvelle fenêtre
        Stage stage = new Stage();
        Scene scene = new Scene(pieChart, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Statistiques des cours");
        stage.show();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}