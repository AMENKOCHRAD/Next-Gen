package Utilisateur.Pidev.Controllers;

import Utilisateur.Pidev.Entites.Utilisateur;
import Utilisateur.Pidev.Services.UtilisateurService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Date;
import java.time.LocalDate;

public class UpdateController {

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
    private ComboBox<String> genre_combobox;

    @FXML
    private TextField adresse_textfield;

    @FXML
    private Button update_button;

    private UtilisateurService utilisateurService = new UtilisateurService();
    private Utilisateur selectedUser;
    private admin adminController;

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