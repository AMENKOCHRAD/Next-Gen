package edu.pidev3a8.controllers;

import edu.pidev3a8.entities.Utilisateur;
import edu.pidev3a8.services.UtilisateurService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Date;
import java.time.LocalDate;
import java.sql.Blob;

import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import java.net.URL;
import java.util.ResourceBundle;

public class UpdateController implements Initializable {

    @FXML
    private TextField mail_textfield;

    @FXML
    private TextField mdp_textfield;

    @FXML
    private TextField nom_textfield;

    @FXML
    private TextField prenom_textfield;

    @FXML
    private DatePicker dateNai_datepicker;

    @FXML
    private TextField numTel_textfield;

    @FXML
    private TextField adresse_textfield;

    @FXML
    private Button update_button;

    @FXML
    private ImageView imageView;

    @FXML
    private File selectedFile;

    private UtilisateurService utilisateurService = new UtilisateurService();
    private Utilisateur selectedUser;
    private admin adminController;

    @FXML
    private ComboBox<String> genre_combobox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        genre_combobox.getItems().addAll("Homme", "Femme");
    }

    public void setUserData(Utilisateur user) {
        this.selectedUser = user;
        mail_textfield.setText(user.getEmail());
        mdp_textfield.setText(user.getMdp());
        nom_textfield.setText(user.getNom());
        prenom_textfield.setText(user.getPrenom());
        numTel_textfield.setText(String.valueOf(user.getNumTel()));
        genre_combobox.setValue(user.getGenre());
        adresse_textfield.setText(user.getAdresse());
    }

    public void setAdminController(admin adminController) {
        this.adminController = adminController;
    }

    @FXML
    void handleUploadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                // Just select the file, no need to set it to the ImageView
                new FileInputStream(selectedFile); // Ensure the file exists
            } catch (FileNotFoundException e) {
                showAlert("Erreur Fichier non trouvé.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleUpdateUser() {
        try {
            String email = mail_textfield.getText();
            String numTelStr = numTel_textfield.getText();
            int numTel = Integer.parseInt(numTelStr);

            // Validate email and phone number
            if (!UtilisateurService.isValidEmail(email)) {
                showAlert("Invalid email format");
                return;
            }

            if (!UtilisateurService.isValidPhoneNumber(numTel)) {
                showAlert("Invalid phone number format");
                return;
            }

            selectedUser.setEmail(mail_textfield.getText());
            selectedUser.setMdp(mdp_textfield.getText());
            selectedUser.setNom(nom_textfield.getText());
            selectedUser.setPrenom(prenom_textfield.getText());
            LocalDate localDate = dateNai_datepicker.getValue();
            if (localDate != null) {
                selectedUser.setDateNai(Date.valueOf(localDate));
            }
            selectedUser.setNumTel(Integer.parseInt(numTel_textfield.getText()));
            selectedUser.setGenre(genre_combobox.getValue());
            selectedUser.setAdresse(adresse_textfield.getText());

            // Update the image if a new file is selected
            if (selectedFile != null) {
                try (FileInputStream fis = new FileInputStream(selectedFile)) {
                    Blob blob = utilisateurService.createBlob(fis, (int) selectedFile.length());
                    selectedUser.setImage_user(blob);
                }
            }

            utilisateurService.updateEntity(selectedUser.getId(), selectedUser);

            // Refresh the table in the admin controller
            if (adminController != null) {
                adminController.refreshTable();
            }

            Stage stage = (Stage) update_button.getScene().getWindow();
            stage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin.fxml"));
            Parent root = loader.load();
            Stage adminStage = new Stage();
            adminStage.setScene(new Scene(root));
            adminStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}