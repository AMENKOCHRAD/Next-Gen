package edu.pidev3a8.controllers;

import edu.pidev3a8.services.ReclamtionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.image.PixelFormat;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

public class CamembertStatutController {

    @FXML
    private PieChart pieChart;

    private ReclamtionService reclamtionService = new ReclamtionService();

    @FXML
    public void initialize() {
        // Récupérer les données des réclamations par statut
        Map<String, Integer> reclamationsByStatut = reclamtionService.getReclamationsByStatut();

        // Créer une liste de données pour le graphique
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        // Ajouter les données à la liste
        for (Map.Entry<String, Integer> entry : reclamationsByStatut.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        // Ajouter les données au graphique
        pieChart.setData(pieChartData);

        // Personnaliser les couleurs des sections
        applyCustomColors();
    }

    @FXML
    private void handleExporterCamembert() {
        // Capturer une image du graphique
        WritableImage image = pieChart.snapshot(new SnapshotParameters(), null);

        // Ouvrir une boîte de dialogue pour choisir l'emplacement de sauvegarde
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter le Camembert");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File file = fileChooser.showSaveDialog(pieChart.getScene().getWindow());

        if (file != null) {
            try (PDDocument document = new PDDocument()) {
                // Créer une page PDF
                PDPage page = new PDPage();
                document.addPage(page);

                // Convertir l'image JavaFX en une image compatible PDFBox
                PDImageXObject pdImage = PDImageXObject.createFromFile(imageToFile(image).getAbsolutePath(), document);

                // Ajouter l'image à la page PDF
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.drawImage(pdImage, 50, 500, pdImage.getWidth() / 2, pdImage.getHeight() / 2);
                }

                // Sauvegarder le PDF
                document.save(file);

                // Ouvrir automatiquement le fichier PDF après l'enregistrement
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                }

                // Afficher une alerte de succès
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Exportation réussie");
                alert.setHeaderText(null);
                alert.setContentText("Le camembert a été exporté en PDF avec succès !");
                alert.showAndWait();

                System.out.println("Le camembert a été exporté en PDF avec succès !");
            } catch (IOException e) {
                // Afficher une alerte d'erreur en cas d'échec
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur d'exportation");
                alert.setHeaderText(null);
                alert.setContentText("Erreur lors de l'exportation du camembert : " + e.getMessage());
                alert.showAndWait();

                System.err.println("Erreur lors de l'exportation du camembert : " + e.getMessage());
            }

        }
    }

    // Nouvelle méthode pour convertir une WritableImage en fichier temporaire
    private File imageToFile(WritableImage image) throws IOException {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        // Créer un buffer pour stocker les pixels de l'image
        byte[] buffer = new byte[width * height * 4]; // 4 canaux (ARGB)
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);

        // Lire les pixels de l'image dans le buffer
        image.getPixelReader().getPixels(0, 0, width, height,
                WritablePixelFormat.getByteBgraInstance(), byteBuffer, width * 4);

        // Convertir le buffer en une image Java AWT
        java.awt.image.BufferedImage awtImage = new java.awt.image.BufferedImage(
                width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = (y * width + x) * 4;
                int argb = ((buffer[index + 3] & 0xFF) << 24) | // Alpha
                        ((buffer[index] & 0xFF) << 16) |     // Rouge
                        ((buffer[index + 1] & 0xFF) << 8) |  // Vert
                        (buffer[index + 2] & 0xFF);          // Bleu
                awtImage.setRGB(x, y, argb);
            }
        }

        // Sauvegarder l'image AWT en fichier PNG
        File tempFile = File.createTempFile("chart", ".png");
        ImageIO.write(awtImage, "png", tempFile);

        return tempFile;
    }

    private void applyCustomColors() {
        // Définir des couleurs personnalisées pour chaque section
        int colorIndex = 0;
        for (PieChart.Data data : pieChart.getData()) {
            switch (colorIndex) {
                case 0:
                    data.getNode().setStyle("-fx-pie-color: #FFA500;"); // Orange pour "En cours"
                    break;
                case 1:
                    data.getNode().setStyle("-fx-pie-color: #32CD32;"); // Vert pour "Résolu"
                    break;
                case 2:
                    data.getNode().setStyle("-fx-pie-color: #FF0000;"); // Rouge pour "Rejeté"
                    break;
            }
            colorIndex++;
        }
    }
}