package edu.pidev3a8.controllers;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import edu.pidev3a8.entities.Utilisateur;
import edu.pidev3a8.services.UtilisateurService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.itextpdf.layout.Document;

public class UserStatisticsController {

    @FXML
    private BarChart<String, Number> ageBarChart;

    private UtilisateurService utilisateurService = new UtilisateurService();

    @FXML
    public void initialize() {
        loadAgeStatistics();
    }

    private void loadAgeStatistics() {
        List<Utilisateur> adherents = utilisateurService.getAllData().stream()
                .filter(user -> user.getRole() == Utilisateur.Role.Adherent)
                .collect(Collectors.toList());

        Map<Integer, Long> ageDistribution = adherents.stream()
                .collect(Collectors.groupingBy(Utilisateur::getAge, Collectors.counting()));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Age Distribution");

        ageDistribution.forEach((age, count) -> {
            series.getData().add(new XYChart.Data<>(String.valueOf(age), count));
        });

        ageBarChart.getData().add(series);
    }
    @FXML
    private void handleGeneratePdf(ActionEvent event) {
        try {
            String userHome = System.getProperty("user.home");
            String dest = userHome + "/Desktop/user_statistics.pdf";
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Title
            Paragraph title = new Paragraph("Age Distribution of Adherents")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20)
                    .setBold()
                    .setFontColor(ColorConstants.BLUE);
            document.add(title);

            // Table
            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                    .useAllAvailableWidth()
                    .setMarginTop(20);

            // Table Header
            table.addHeaderCell(new Cell().add(new Paragraph("Age"))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Number of Adherents"))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold());

            // Table Data
            Map<Integer, Long> ageDistribution = utilisateurService.getAllData().stream()
                    .filter(user -> user.getRole() == Utilisateur.Role.Adherent)
                    .collect(Collectors.groupingBy(Utilisateur::getAge, Collectors.counting()));

            ageDistribution.forEach((age, count) -> {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(age)))
                        .setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(count)))
                        .setTextAlignment(TextAlignment.CENTER));
            });

            document.add(table);
            document.close();

            System.out.println("PDF generated successfully on the desktop.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}