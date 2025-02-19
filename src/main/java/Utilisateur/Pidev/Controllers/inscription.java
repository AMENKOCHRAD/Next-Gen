package Utilisateur.Pidev.Controllers;

import Utilisateur.Pidev.Entites.Utilisateur;
import Utilisateur.Pidev.Services.UtilisateurService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.sql.Date;
import java.time.LocalDate;

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
    private ComboBox<String> genre_combobox;

    @FXML
    private TextField adresse_textfield;

    @FXML
    private Button imageButton;

    @FXML
    private Button sinscrire2Button;

    @FXML
    private ImageView image_view;

    @FXML
    private Label label_message; // Ensure this is correctly linked in FXML

    private String imagePath;

    public void initialize() {
        genre_combobox.getItems().addAll("Homme", "Femme");
        genre_combobox.setValue("Homme");
    }

    @FXML
    public void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            imagePath = selectedFile.toURI().toString();
            image_view.setImage(new Image(imagePath));
        }
    }

    @FXML
    void AjouterUtilisateurAction(ActionEvent event) {
        String email = mail_textfield.getText();
        String mdp = mdp_textfield.getText();
        String nom = nom_textfield.getText();
        String prenom = prenom_textfield.getText();
        LocalDate localDate = dateNai_datepicker.getValue();
        Date dateNai = Date.valueOf(localDate);
        int numTel = Integer.parseInt(numTel_textfield.getText());
        String genre = genre_combobox.getValue();
        String adresse = adresse_textfield.getText();

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
        utilisateur.setImage(imagePath);

        UtilisateurService utilisateurService = new UtilisateurService();
        utilisateurService.addEntity(utilisateur);

        label_message.setText("Bienvenue,Vous inscrit à Sportify!");
    }
}