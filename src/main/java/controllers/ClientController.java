package controllers;

import entities.Event;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import services.EventServices;
import services.TicketServices;

import java.text.SimpleDateFormat;
import java.util.List;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.io.IOException;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

public class ClientController {
    @FXML
    private TableView<Event> eventTable;
    @FXML
    private TableColumn<Event, String> colNom;
    @FXML
    private TableColumn<Event, String> colDate;
    @FXML
    private TableColumn<Event, Integer> colTickets;
    @FXML
    private Label nomLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label ticketsLabel;
    @FXML
    private Button buyTicketButton;
    @FXML
    private Stage paymentDialogStage;

    private final EventServices eventServices = new EventServices();
    private final TicketServices ticketServices = new TicketServices();
    private final ObservableList<Event> eventList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colNom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        colDate.setCellValueFactory(cellData -> new SimpleStringProperty(new SimpleDateFormat("yyyy-MM-dd").format(cellData.getValue().getDateDebut())));
        colTickets.setCellValueFactory(cellData -> new SimpleIntegerProperty(ticketServices.getAvailableTickets(cellData.getValue().getIdEvent())).asObject());

        loadEvents();

        eventTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showEventDetails(newSelection);
            }
        });
    }

    private void loadEvents() {
        List<Event> events = eventServices.getAllEvents();
        eventList.setAll(events);
        eventTable.setItems(eventList);
    }

    private void showEventDetails(Event event) {
        nomLabel.setText(event.getNom());
        dateLabel.setText(new SimpleDateFormat("yyyy-MM-dd").format(event.getDateDebut()));

        int availableTickets = ticketServices.getAvailableTickets(event.getIdEvent());
        ticketsLabel.setText(String.valueOf(availableTickets));

        buyTicketButton.setDisable(availableTickets == 0);
    }

    @FXML
    public void buyTicket() {
        Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
        if (selectedEvent != null) {
            try {
                // Stripe payment integration
                Stripe.apiKey = "sk_test_51Qy8NxLS1h5GXhF1Ecu6A7EP8eyGqyaFLPDlNm4IgXKRdmEcaTOR14Rap9zrT02iLYzyE2MNrUe2i6j0w4AGWPKx00AoXzbHN3";

                PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                        .setAmount(1000L) // Amount in cents
                        .setCurrency("usd")
                        .addPaymentMethodType("card")
                        .build();

                PaymentIntent intent = PaymentIntent.create(params);

                // Create a new dialog for Stripe payment
                paymentDialogStage = new Stage();
                paymentDialogStage.initModality(Modality.APPLICATION_MODAL);
                paymentDialogStage.setTitle("Stripe Payment");

                WebView webView = new WebView();
                WebEngine webEngine = webView.getEngine();
                webEngine.load(getClass().getResource("/views/stripe_payment.html").toExternalForm());

                // Pass the client secret to the HTML
                webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue == javafx.concurrent.Worker.State.SUCCEEDED) {
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember("client_secret_from_server", intent.getClientSecret());
                        window.setMember("javaController", this);
                        System.out.println("Client secret set in HTML: " + intent.getClientSecret());
                    }
                });

                VBox vbox = new VBox(webView);
                Scene scene = new Scene(vbox, 800, 600);
                paymentDialogStage.setScene(scene);
                paymentDialogStage.showAndWait();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void paymentSuccess() {
        Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
        if (selectedEvent != null) {
            // Send email
            sendEmail(selectedEvent);

            // Update ticket quantity
            ticketServices.updateTicketQuantity(selectedEvent.getIdEvent(), -1);
            loadEvents();
            showEventDetails(selectedEvent);

            System.out.println("Achat de ticket pour l'événement : " + selectedEvent.getNom());
        }
    }

    public void closePaymentDialog() {
        if (paymentDialogStage != null) {
            PauseTransition delay = new PauseTransition(Duration.seconds(5));
            delay.setOnFinished(event -> paymentDialogStage.close());
            delay.play();
        }
    }
    private void sendEmail(Event event) {
        String to = "maryembc14@gmail.com";
        String from = "maryembc14@gmail.com";
        String host = "smtp.gmail.com";
        String username = "maryembc14@gmail.com"; // Replace with your email username
        String password = "ouvz vquw wgln frza"; // Replace with your email password

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Ticket Purchase Confirmation");

            int ticketNumber = ticketServices.getAvailableTickets(event.getIdEvent()) + 1; // Assuming ticket number is based on remaining tickets
            String qrCodeData = "Event: " + event.getNom() + "\nDate: " + new SimpleDateFormat("yyyy-MM-dd").format(event.getDateDebut()) + "\nLocation: " + event.getAdresse() + "\nTicket Number: " + ticketNumber;
            String qrCodePath = generateQRCodeImage(qrCodeData, 300, 300); // Increase size to 300x300

            String ticketHtml = generateTicketTemplate(event, ticketNumber);

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(ticketHtml, "text/html");

            MimeBodyPart imagePart = new MimeBodyPart();
            DataSource fds = new FileDataSource(qrCodePath);
            imagePart.setDataHandler(new DataHandler(fds));
            imagePart.setHeader("Content-ID", "<qrCode>");
            imagePart.setFileName(qrCodePath);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(imagePart);

            message.setContent(multipart);

            Transport.send(message);
            System.out.println("Email sent successfully.");
        } catch (MessagingException | WriterException | IOException mex) {
            mex.printStackTrace();
        }
    }

    private String generateTicketTemplate(Event event, int ticketNumber) {
        return "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;\">" +
                "<h2 style=\"color: #4CAF50;\">Congratulations on Your Successful Payment!</h2>" +
                "<p>Thank you for your payment. Here are your ticket details:</p>" +
                "<p><strong>Event:</strong> " + event.getNom() + "</p>" +
                "<p><strong>Date:</strong> " + new SimpleDateFormat("yyyy-MM-dd").format(event.getDateDebut()) + "</p>" +
                "<p><strong>Location:</strong> " + event.getAdresse() + "</p>" +
                "<p><strong>Ticket Number:</strong> " + ticketNumber + "</p>" +
                "<div><img src=\"cid:qrCode\" alt=\"QR Code\"></div>" +
                "<p>We look forward to seeing you at the event!</p>" +
                "</div>";
    }

    private String generateQRCodeImage(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        String filePath = "qrCode.png";
        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

        return filePath;
    }
}
