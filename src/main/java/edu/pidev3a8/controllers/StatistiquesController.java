package edu.pidev3a8.controllers;

import edu.pidev3a8.services.ReclamtionService;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.Map;

public class StatistiquesController {

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    private ReclamtionService reclamtionService = new ReclamtionService();

    @FXML
    public void initialize() {
        // Récupérer les données des réclamations par catégorie
        Map<String, Integer> reclamationsByCategory = reclamtionService.getReclamationsByCategory();

        // Créer une série de données pour le graphique
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Réclamations par Catégorie");

        // Ajouter les données à la série
        for (Map.Entry<String, Integer> entry : reclamationsByCategory.entrySet()) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(entry.getKey(), entry.getValue());
            series.getData().add(data);
        }

        // Ajouter la série au graphique
        barChart.getData().add(series);

        // Configurer les axes
        xAxis.setLabel("Catégorie");
        yAxis.setLabel("Nombre de réclamations");

        // Appliquer des styles personnalisés aux barres
        applyCustomStyles();
    }

    private void applyCustomStyles() {
        // Définir les couleurs pour chaque catégorie
        String[] colors = {"#87CEEB", "#4682B4", "#00008B"}; // Bleu clair, moyen, foncé

        // Appliquer les styles aux barres
        for (XYChart.Series<String, Number> series : barChart.getData()) {
            int colorIndex = 0;
            for (XYChart.Data<String, Number> data : series.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    // Appliquer la couleur et réduire la largeur de la barre
                    node.setStyle(
                            "-fx-bar-fill: " + colors[colorIndex % colors.length] + ";" + // Couleur
                                    "-fx-background-radius: 0;" + // Pas de bord arrondi
                                    "-fx-pref-width: 5px;" // Largeur des barres
                    );
                }
                colorIndex++;
            }
        }

        // Réduire l'espacement entre les barres
        barChart.setCategoryGap(10); // Ajuste cette valeur pour réduire l'espacement
    }
}