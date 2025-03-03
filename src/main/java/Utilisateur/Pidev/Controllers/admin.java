package Utilisateur.Pidev.Controllers;

import Utilisateur.Pidev.Entites.Utilisateur;
import Utilisateur.Pidev.Services.UtilisateurService;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
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
    private TableColumn<Utilisateur, Boolean> column_banned;

    @FXML
    private TableColumn<Utilisateur, Image> column_image;

    private UtilisateurService utilisateurService = new UtilisateurService();
    private ExecutorService executorService = Executors.newCachedThreadPool();

    @FXML
    public void initialize() {
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
        column_mdp.setCellFactory(new JFXCellFactory());

        column_image.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getImage()));
        column_image.setCellFactory(new Callback<TableColumn<Utilisateur, Image>, TableCell<Utilisateur, Image>>() {
            @Override
            public TableCell<Utilisateur, Image> call(TableColumn<Utilisateur, Image> param) {
                return new TableCell<Utilisateur, Image>() {
                    private final ImageView imageView = new ImageView();

                    @Override
                    protected void updateItem(Image item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            imageView.setImage(item);
                            imageView.setFitHeight(50);
                            imageView.setFitWidth(50);
                            setGraphic(imageView);
                        }
                    }
                };
            }
        });

        loadUserData();
    }

    private void loadUserData() {
        List<Utilisateur> utilisateurs = utilisateurService.getAllData2();
        table_adherents.getItems().setAll(utilisateurs);
    }

    public void shutdown() {
        executorService.shutdown();
    }

    @FXML
    private void handleAddAdherent(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/inscription.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteAdherent(ActionEvent event) {
        Utilisateur selectedUser = table_adherents.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            int userId = selectedUser.getId();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete the selected user?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    utilisateurService.deleteEntityById(userId);
                    loadUserData();
                    System.out.println("User deleted successfully.");
                } catch (Exception e) {
                    System.err.println("Error deleting user: " + e.getMessage());
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Vous devez Selectionner un utilisateur pour le supprimer.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleUpdateAdherent(ActionEvent event) {
        Utilisateur selectedUser = table_adherents.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/update_user.fxml"));
                Parent root = loader.load();

                UpdateController controller = loader.getController();
                controller.setUserData(selectedUser);
                controller.setAdminController(this);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();

                Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                currentStage.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Vous devez Selectionner un utilisateur pour le modifier.");
            alert.showAndWait();
        }
    }

    public void refreshTable() {
        loadUserData();
    }
}