package edu.pidev3a8.controllers;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import edu.pidev3a8.entities.Utilisateur;
import edu.pidev3a8.services.UtilisateurService;
import edu.pidev3a8.tools.MyConnection;
import edu.pidev3a8.utils.EmailValidator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.Blob;
import java.io.InputStream;
import java.nio.file.StandardOpenOption;

public class inscription {

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
    private ImageView countryFlagImageView;

    @FXML
    private Label countryCodeLabel;

    private PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    @FXML
    private ComboBox<String> genre_combobox;

    @FXML
    private TextField adresse_textfield;

    @FXML
    private Button sinscrire2Button;

    @FXML
    private Label label_message;

    @FXML
    private Button uploadButton;

    @FXML
    private ImageView imageView;

    private File selectedFile;
    private UtilisateurService utilisateurService = new UtilisateurService();
    private EmailValidator emailValidator;

    public void initialize() {
        genre_combobox.getItems().addAll("Homme", "Femme");
        genre_combobox.setValue("Homme");
        emailValidator = new EmailValidator(MyConnection.getInstance().getCnx());

        numTel_textfield.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateCountryInfo(newValue);
            }
        });
    }



    @FXML
    void handleUploadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        Stage stage = (Stage) uploadButton.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            imageView.setImage(image);
        }
    }
    private void updateCountryInfo(String phoneNumber) {
        try {
            // Assume the phone number is from Tunisia if no country code is provided
            Phonenumber.PhoneNumber number = phoneNumberUtil.parse(phoneNumber, "ZZ");
            String regionCode = phoneNumberUtil.getRegionCodeForNumber(number);
            String countryCode = "+" + number.getCountryCode();

            // Update the country code label
            countryCodeLabel.setText(countryCode);

            // Update the country flag image view
            String flagImagePath = "/flags/" + regionCode.toLowerCase() + ".png";
            Image flagImage = new Image(getClass().getResourceAsStream(flagImagePath));
            countryFlagImageView.setImage(flagImage);

        } catch (NumberParseException e) {
            countryCodeLabel.setText("");
            countryFlagImageView.setImage(null);
        }
    }

    @FXML
    void AjouterUtilisateurAction(ActionEvent event) {
        try {
            String email = mail_textfield.getText();
            String mdp = mdp_textfield.getText();
            String nom = nom_textfield.getText();
            String prenom = prenom_textfield.getText();
            LocalDate localDate = dateNai_datepicker.getValue();

            if (localDate == null) {
                showAlert("Erreur", "Veuillez sélectionner une date de naissance.");
                return;
            }
            Date dateNai = Date.valueOf(localDate);
            int numTel = Integer.parseInt(numTel_textfield.getText());
            String genre = genre_combobox.getValue();
            String adresse = adresse_textfield.getText();

            if (emailValidator.emailExists(email)) {
                showAlert("Erreur", "Cette adresse email est déjà utilisée.");
                return;
            }

            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setEmail(email);
            utilisateur.setMdp(mdp);
            utilisateur.setNom(nom);
            utilisateur.setPrenom(prenom);
            utilisateur.setDateNai(dateNai);
            utilisateur.setNumTel(numTel);
            utilisateur.setGenre(genre);
            utilisateur.setAdresse(adresse);
            utilisateur.setRole(Utilisateur.Role.Adherent);
            utilisateur.setBanned(false);

            if (selectedFile != null) {
                try (InputStream inputStream = Files.newInputStream(selectedFile.toPath(), StandardOpenOption.READ)) {
                    byte[] imageBytes = inputStream.readAllBytes();
                    Blob imageBlob = MyConnection.getInstance().getCnx().createBlob();
                    imageBlob.setBytes(1, imageBytes);
                    utilisateur.setImage_user(imageBlob);
                } catch (IOException | SQLException e) {
                    showAlert("Erreur", "Erreur lors de la lecture de l'image.");
                    e.printStackTrace();
                    return;
                }
            }

            utilisateurService.addEntity(utilisateur);

            label_message.setText("Bienvenue, vous êtes inscrit à Sportify !");
            showAlert("Succès", "Inscription réussie !");

            // Close the current window
            Stage stage = (Stage) sinscrire2Button.getScene().getWindow();
            stage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin.fxml"));
            Parent root = loader.load();
            Stage adminStage = new Stage();
            adminStage.setScene(new Scene(root));
            adminStage.show();

        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer un numéro de téléphone valide.");
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de l'inscription.");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
