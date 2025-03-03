package Utilisateur.Pidev.Controllers;

import Utilisateur.Pidev.Entites.Utilisateur;
import Utilisateur.Pidev.Services.UtilisateurService;
import Utilisateur.Pidev.Utils.CurrentUser;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import javax.activation.DataHandler;
import javax.imageio.ImageIO;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.util.Properties;

public class login_user {

    @FXML private WebView recaptchaWebView;
    @FXML private PasswordField passwordField;
    @FXML private TextField mail_field;
    @FXML private TextField mdp_field;
    @FXML private Button seconnecter_button;
    @FXML private Button sinscrire_button;
    @FXML private Button toggleButton;
    @FXML private Button sendEmailButton;



    private boolean isPasswordVisible = false;
    private final UtilisateurService utilisateurService = new UtilisateurService();

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = mail_field.getText();
        String password = isPasswordVisible ? mdp_field.getText() : passwordField.getText();

        if (!validateInput(email, password)) {
            return;
        }
        authenticateAndNavigate(email, password, event);
    }
    @FXML
    private void handleSendEmail(ActionEvent event) {
        new Thread(() -> {
            try {
                // Capture photo
                byte[] photoBytes = capturePhoto();

                // Send email
                sendEmailWithPhoto(photoBytes);

                // Show success message
                Platform.runLater(() -> {
                    showAlert("Succès", "Email envoyé avec succès.");
                });
            } catch (Exception e) {
                e.printStackTrace();
                // Show error message
                Platform.runLater(() -> {
                    showAlert("Erreur", "Erreur lors de l'envoi de l'email.");
                });
            }
        }).start();
    }

    private byte[] capturePhoto() {
        Webcam webcam = Webcam.getDefault();

        // Set the resolution BEFORE opening the webcam
        //webcam.setViewSize(WebcamResolution.VGA.getSize());

        // Open the webcam
        webcam.open();

        BufferedImage image = webcam.getImage();
        byte[] imageBytes = null;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "JPG", baos);
            baos.flush();
            imageBytes = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close the webcam when done
            webcam.close();
        }

        return imageBytes;
    }

    private void sendEmailWithPhoto(byte[] photoBytes) {
        final String username = "bboumiza01@gmail.com"; // Replace with your email
        final String password = "ppkf ddjq vrvm coor"; // Replace with your email password

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // Use TLS
        props.put("mail.smtp.host", "smtp.gmail.com"); // Replace with your SMTP server
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("badisstik@gmail.com")); // Replace with recipient email
            message.setSubject("Alerte Tentative de connexion à votre compte");

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Chere Adherent ,\n Nous avons détecté une tentative de connexion à votre compte depuis un appareil inconnu.\n" +
                    "Si vous n'êtes pas à l'origine de cette tentative, veuillez contacter l'administrateur du site pour plus d'informations.\n\n" +
                    "Cordialement,\nL'équipe de support.");

            // Create a multipart message
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // Attach the photo
            messageBodyPart = new MimeBodyPart();
            ByteArrayDataSource dataSource = new ByteArrayDataSource(photoBytes, "image/jpeg");
            messageBodyPart.setDataHandler(new DataHandler(dataSource));
            messageBodyPart.setFileName("photo.jpg");
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            message.setContent(multipart);

            Transport.send(message);

            System.out.println("Email sent successfully.");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    private boolean validateInput(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur de saisie", "Veuillez remplir tous les champs.");
            return false;
        }
        return true;
    }

    private void authenticateAndNavigate(String email, String password, ActionEvent event) {
        Utilisateur user = utilisateurService.authenticateUser(email, password);

        if (user == null) {
            showAlert("Erreur de connexion", "Email ou mot de passe incorrect.");
            return;
        }

        CurrentUser.getInstance().setCurrentUser(user);
        navigateToProfile(user, event);
    }

    private void navigateToProfile(Utilisateur user, ActionEvent event) {
        try {
            String fxmlPath = switch (user.getRole()) {
                case Admin, Coach -> "/profil.fxml";
                case Adherent -> "/profil.fxml";
                default -> throw new IllegalStateException("Rôle non supporté: " + user.getRole());
            };

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de la page de profil.");
        }
    }

    @FXML
    private void handleSignUp(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/inscription.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de la page d'inscription.");
        }
    }

    @FXML
    private void handleTogglePasswordVisibility() {
        passwordField.setVisible(!isPasswordVisible);
        mdp_field.setVisible(isPasswordVisible);

        if (!isPasswordVisible) {
            passwordField.setText(mdp_field.getText());
        } else {
            mdp_field.setText(passwordField.getText());
        }

        isPasswordVisible = !isPasswordVisible;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}