package Utilisateur.Pidev.Controllers;

import Utilisateur.Pidev.Entites.Utilisateur;
import Utilisateur.Pidev.Services.UtilisateurService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class admin {

    @FXML
    private TableView<Utilisateur> table_adherents;

    @FXML
    private TableColumn<Utilisateur, Integer> column_id;

    @FXML
    private TableColumn<Utilisateur, String> column_email;

    @FXML
    private TableColumn<Utilisateur, String> column_mdp;

    @FXML
    private TableColumn<Utilisateur, String> column_nom;

    @FXML
    private TableColumn<Utilisateur, String> column_prenom;

    @FXML
    private TableColumn<Utilisateur, String> column_date;

    @FXML
    private TableColumn<Utilisateur, Integer> column_numTel;

    @FXML
    private TableColumn<Utilisateur, String> column_genre;

    @FXML
    private TableColumn<Utilisateur, String> column_adresse;

    @FXML
    private TableColumn<Utilisateur, String> column_role;

    @FXML
    private TableColumn<Utilisateur, String> column_image;

    @FXML
    private TableColumn<Utilisateur, Boolean> column_banned;

    private UtilisateurService utilisateurService = new UtilisateurService();

    // Thread pool for asynchronous image loading
    private ExecutorService executorService = Executors.newCachedThreadPool();

    @FXML
    public void initialize() {
        // Initialize table columns
        column_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        column_email.setCellValueFactory(new PropertyValueFactory<>("email"));
        column_mdp.setCellValueFactory(new PropertyValueFactory<>("mdp"));
        column_nom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        column_prenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        column_date.setCellValueFactory(new PropertyValueFactory<>("dateNai"));
        column_numTel.setCellValueFactory(new PropertyValueFactory<>("numTel"));
        column_genre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        column_adresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        column_role.setCellValueFactory(new PropertyValueFactory<>("role"));
        column_banned.setCellValueFactory(new PropertyValueFactory<>("banned"));

        // Add a custom cell for displaying images
        column_image.setCellFactory(param -> new TableCell<Utilisateur, String>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);
                imageView.setPreserveRatio(true);
                setGraphic(imageView);
            }

            @Override
            protected void updateItem(String imageUrl, boolean empty) {
                super.updateItem(imageUrl, empty);

                if (empty || imageUrl == null || imageUrl.isEmpty()) {
                    imageView.setImage(null);
                    return;
                }

                System.out.println("Loading image from URL: " + imageUrl); // Debug line

                try {
                    executorService.submit(() -> {
                        try {
                            // Encode the URL to handle spaces and special characters
                            String encodedUrl = imageUrl.replace(" ", "%20");
                            System.out.println("Encoded URL: " + encodedUrl); // Debug line

                            // Load image directly from the URL in the database
                            Image image = new Image(encodedUrl, true);
                            image.errorProperty().addListener((obs, oldError, newError) -> {
                                if (newError) {
                                    System.out.println("Error loading image from URL: " + encodedUrl);
                                    javafx.application.Platform.runLater(() -> imageView.setImage(null));
                                }
                            });
                            javafx.application.Platform.runLater(() -> imageView.setImage(image));
                        } catch (Exception e) {
                            System.out.println("Exception loading image from URL " + imageUrl + ": " + e.getMessage());
                            javafx.application.Platform.runLater(() -> imageView.setImage(null));
                        }
                    });
                } catch (Exception e) {
                    System.out.println("Error submitting image loading task: " + e.getMessage());
                    imageView.setImage(null);
                }
            }
        });

// Load data into the table
        loadUserData();

    }

    // Method to load data into the table
    private void loadUserData() {
        List<Utilisateur> utilisateurs = utilisateurService.getAllData2();
        table_adherents.getItems().setAll(utilisateurs);
    }

    // Clean up the executor service when the controller is no longer needed
    public void shutdown() {
        executorService.shutdown();
    }


    // Event handler for adding a new adherent
    @FXML
    private void handleAddAdherent(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/inscription.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleDeleteAdherent(ActionEvent event) {
        Utilisateur selectedUser = table_adherents.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            utilisateurService.deleteEntity(selectedUser);
            loadUserData(); // Refresh the table
        } else {
            System.out.println("No user selected for deletion.");
        }
    }

}