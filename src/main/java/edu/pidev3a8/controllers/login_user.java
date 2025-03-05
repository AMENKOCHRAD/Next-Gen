package edu.pidev3a8.controllers;

import edu.pidev3a8.entities.Utilisateur;
import edu.pidev3a8.services.UtilisateurService;
import edu.pidev3a8.utils.CurrentUser;
import com.github.sarxos.webcam.Webcam;
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
import javafx.stage.Stage;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.activation.DataHandler;
import javax.imageio.ImageIO;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.util.Properties;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import java.security.SecureRandom;
import com.github.sarxos.webcam.Webcam;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class login_user {

    @FXML private PasswordField passwordField;
    @FXML private TextField mail_field;
    @FXML private TextField mdp_field;
    @FXML private Button seconnecter_button;
    @FXML private Button sinscrire_button;
    @FXML private Button toggleButton;
    @FXML private Button sendEmailButton;


    private int failedLoginAttempts = 0;
    private boolean isPasswordVisible = false;
    private final UtilisateurService utilisateurService = new UtilisateurService();

    // Twilio credentials
    private static final String ACCOUNT_SID = "AC96b8e5d3ede833e660a2e2b4bf9569b7";
    private static final String AUTH_TOKEN = "133ae77859fdcbccef880a8a2111b3d2";
    private static final String FROM_PHONE_NUMBER = "+16072846186";

    private void initializeTwilio() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = mail_field.getText();
        String password = isPasswordVisible ? mdp_field.getText() : passwordField.getText();

        if (!validateInput(email, password)) {
            return;
        }

        Utilisateur user = utilisateurService.authenticateUser(email, password);

        if (user == null) {
            failedLoginAttempts++;
            showAlert("Erreur de connexion", "Email ou mot de passe incorrect.");

            if (failedLoginAttempts >= 3) {
                handleSendEmail(email);
                failedLoginAttempts = 0; // Reset the counter after sending the email
            }
            return;
        }

        // If the user is an Admin, perform face recognition
        if (user.getRole() == Utilisateur.Role.Admin) {
            boolean faceRecognized = performFaceRecognitionWithWidget();
            CurrentUser.getInstance().setCurrentUser(user);
            navigateToProfile(user, event);
            if (!faceRecognized) {
                showAlert("Erreur de reconnaissance", "Face non reconnue. Veuillez réessayer.");
                return;
            }
        }

        failedLoginAttempts = 0; // Reset counter on successful login
        //CurrentUser.getInstance().setCurrentUser(user);
        //navigateToProfile(user, event);
    }
    @FXML
    private void handleSendEmail(String email) {
        new Thread(() -> {
            try {
                // Capture photo
                byte[] photoBytes = capturePhoto();

                // Send email
                sendEmailWithPhoto(photoBytes, email);

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

    private void sendEmailWithPhoto(byte[] photoBytes, String recipientEmail) {
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
            javax.mail.Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(recipientEmail)); // Use the recipient email
            message.setSubject("Alerte Tentative de connexion à votre compte");

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Si vous n'êtes pas à l'origine de cette tentative, veuillez contacter l'administrateur du site pour plus d'informations.\n\n" +
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


    private byte[] capturePhoto() {
        Webcam webcam = Webcam.getDefault();
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
            webcam.close();
        }
        return imageBytes;
    }

    /**
     * Opens a modal window showing the live camera feed. While running, it processes frames
     * for face detection and recognition. If a face matching the admin is recognized (label==1 and confidence<50),
     * the widget closes and the method returns true; otherwise, it times out.
     */

    private boolean performFaceRecognitionWithWidget() {
        final boolean[] recognized = {false};

        VideoCapture capture = new VideoCapture(0);
        if (!capture.isOpened()) {
            System.out.println("Camera not detected");
            return false;
        }

        // Load recognizer and cascade classifier once.
        LBPHFaceRecognizer recognizer = LBPHFaceRecognizer.create();
        recognizer.read("faceModel.xml");  // Use your updated model filename if needed
        CascadeClassifier faceDetector = new CascadeClassifier("C:\\Users\\msi\\IdeaProjects\\Tache_Utilisateur\\src\\main\\resources\\lbpcascade_frontalface.xml");


        // Create an ImageView and modal Stage to display the live camera feed.
        ImageView imageView = new ImageView();
        imageView.setFitWidth(640);
        imageView.setFitHeight(480);
        Pane pane = new Pane(imageView);
        Stage stage = new Stage();
        stage.setScene(new Scene(pane, 640, 480));
        stage.setTitle("Face Recognition - Position Your Face");
        stage.initModality(Modality.APPLICATION_MODAL);

        // AnimationTimer to update the feed and check recognition continuously.
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Mat frame = new Mat();
                if (capture.read(frame)) {
                    Mat gray = new Mat();
                    opencv_imgproc.cvtColor(frame, gray, opencv_imgproc.COLOR_BGR2GRAY);

                    // Detect faces
                    org.bytedeco.opencv.opencv_core.RectVector faces = new org.bytedeco.opencv.opencv_core.RectVector();
                    faceDetector.detectMultiScale(gray, faces);

                    if (faces.size() > 0 && !recognized[0]) {
                        // Process the first detected face
                        Rect faceRect = faces.get(0);

                        // For visual feedback, draw a rectangle around the detected face.
                        opencv_imgproc.rectangle(frame,
                                new org.bytedeco.opencv.opencv_core.Point(faceRect.x(), faceRect.y()),
                                new org.bytedeco.opencv.opencv_core.Point(faceRect.x() + faceRect.width(), faceRect.y() + faceRect.height()),
                                new org.bytedeco.opencv.opencv_core.Scalar(0, 255, 0, 0));

                        // Extract and resize the face region.
                        Mat faceROI = new Mat(gray, faceRect);
                        Mat resizedFace = new Mat();
                        opencv_imgproc.resize(faceROI, resizedFace, new Size(100, 100));

                        // Perform face recognition.
                        int[] predictedLabel = new int[1];
                        double[] confidence = new double[1];
                        recognizer.predict(resizedFace, predictedLabel, confidence);
                        System.out.println("Predicted label: " + predictedLabel[0] + ", Confidence: " + confidence[0]);

                        // Check if the recognized label is the admin label (e.g., 1)
                        // and that the confidence meets your criteria.
                        if (predictedLabel[0] == 1 && confidence[0] < 100) {
                            recognized[0] = true;
                            stop();     // Stop the timer
                            stage.close(); // Close the widget immediately
                        } else {
                            // Optionally, show feedback in the console or update the widget to indicate "Not admin"
                            System.out.println("Face detected, but not recognized as admin (label "
                                    + predictedLabel[0] + ").");
                        }
                    }
                    // Convert frame to JavaFX image and display it.
                    BufferedImage bufferedImage = matToBufferedImage(frame);
                    javafx.scene.image.Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
                    imageView.setImage(fxImage);
                }
            }
        };

        timer.start();
        stage.showAndWait(); // Blocks until widget is closed.
        capture.release();

        return recognized[0];
    }

    /**
     * Helper method to convert an OpenCV Mat to a BufferedImage.
     * This implementation assumes the Mat type is CV_8UC3 or CV_8UC1.
     */
    private BufferedImage matToBufferedImage(Mat mat) {
        int width = mat.cols();
        int height = mat.rows();
        int channels = mat.channels();
        byte[] sourcePixels = new byte[width * height * channels];
        mat.data().get(sourcePixels);

        BufferedImage image;
        if (channels > 1) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        } else {
            image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        }
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);
        return image;
    }
    private boolean validateInput(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur de saisie", "Veuillez remplir tous les champs.");
            return false;
        }
        return true;
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

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        String email = mail_field.getText();
        Utilisateur user = utilisateurService.findByEmail(email);

        if (user == null) {
            showAlert("Erreur", "Email non trouvé.");
            return;
        }

        String newPassword = generateRandomPassword(8);
        user.setMdp(newPassword); // Update the password in the user object
        utilisateurService.updateEntity(user.getId(), user); // Save the updated user to the database

        sendSms(user.getNumTel(), newPassword); // Send the new password via SMS

        showAlert("Succès", "Un nouveau mot de passe a été envoyé à votre numéro de téléphone.");
        passwordField.clear(); // Clear the password field
        mdp_field.clear(); // Clear the visible password field
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void sendSms(int phoneNumber, String newPassword) {
        initializeTwilio();
        try {
            String toPhoneNumber = "+216" + String.valueOf(phoneNumber).replaceFirst("^0+", "");
            System.out.println("Sending SMS to: " + toPhoneNumber);
            System.out.println("From: " + FROM_PHONE_NUMBER);

            Message message = Message.creator(
                    new PhoneNumber(toPhoneNumber),
                    new PhoneNumber(FROM_PHONE_NUMBER),
                    "Votre nouveau mot de passe est: " + newPassword).create();

            System.out.println("SMS sent successfully. SID: " + message.getSid());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to send SMS: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}