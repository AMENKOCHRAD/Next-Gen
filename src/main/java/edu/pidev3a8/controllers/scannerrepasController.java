package edu.pidev3a8.controllers;

import edu.pidev3a8.entities.Nutrition;
import edu.pidev3a8.services.NutritionService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.opencv.core.*;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class scannerrepasController {

    @FXML
    private Button startButton; // Bouton pour démarrer le scanner

    @FXML
    private Label statusLabel; // Label pour afficher le statut

    // Load OpenCV native library
    static {
        System.load("C:\\Pidev3A8(entite1,2)\\lib\\opencv\\build\\java\\x64\\opencv_java490.dll");
    }

    // YOLO model paths
    private static final String MODEL_PATH = "C:\\Users\\asus\\Downloads\\yolov3.weights";
    private static final String CONFIG_PATH = "C:\\Users\\asus\\Downloads\\yolov3.cfg";
    private static final String CLASSES_PATH = "C:\\Users\\asus\\Downloads\\coco.names";

    // List of classes (e.g., "apple", "banana", etc.)
    private static List<String> CLASSES = new ArrayList<>();

    // Nutrition service
    private NutritionService nutritionService = new NutritionService();

    // Variable pour stocker l'IMC
    private double imc;

    // Méthode pour définir l'IMC
    public void setImc(double imc) {
        this.imc = imc;
        statusLabel.setText("Status: IMC set to " + imc);
    }

    @FXML
    public void startScanning(ActionEvent actionEvent) {
        // Logique pour démarrer le scanner
        statusLabel.setText("Scanning started...");

        // Démarrer le scanner avec l'IMC récupéré
        startScanning(imc);
    }

    private void startScanning(double imc) {
        // Load class names
        try (BufferedReader br = new BufferedReader(new FileReader(CLASSES_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                CLASSES.add(line.trim());
            }
        } catch (IOException e) {
            System.out.println("Error: Failed to load class names.");
            e.printStackTrace();
            return;
        }

        // Initialize video capture
        VideoCapture camera = new VideoCapture(0); // 0 for default camera
        if (!camera.isOpened()) {
            System.out.println("Error: Camera not found or could not be opened!");
            statusLabel.setText("Status: Camera not found!");
            return;
        } else {
            System.out.println("Camera opened successfully.");
            statusLabel.setText("Status: Camera opened successfully.");
            System.out.println("Camera resolution: " + camera.get(Videoio.CAP_PROP_FRAME_WIDTH) + "x" + camera.get(Videoio.CAP_PROP_FRAME_HEIGHT));
        }

        // Load the YOLO model
        Net net = Dnn.readNetFromDarknet(CONFIG_PATH, MODEL_PATH);
        if (net.empty()) {
            System.out.println("Error: Failed to load the YOLO model.");
            statusLabel.setText("Status: Failed to load YOLO model.");
            return;
        } else {
            System.out.println("YOLO model loaded successfully.");
            statusLabel.setText("Status: YOLO model loaded.");
        }

        // Get nutritional limits based on IMC
        double[] limitesNutrition = getLimitesNutritionPourIMC(imc);
        System.out.println("Nutritional limits for IMC " + imc + ": " + Arrays.toString(limitesNutrition));

        // Main loop to process camera frames
        Mat frame = new Mat();
        List<String> detectedFoods = new ArrayList<>();
        while (true) {
            if (camera.read(frame)) {
                System.out.println("Frame captured successfully.");

                // Perform food recognition
                String detectedFood = detectFood(frame, net);

                if (detectedFood != null) {
                    detectedFoods.add(detectedFood);
                    System.out.println("Detected food: " + detectedFood);

                    // Get nutritional info for the detected food
                    double[] nutritionInfo = getNutritionInfo(detectedFood);
                    if (nutritionInfo != null) {
                        System.out.println("Nutrition info for " + detectedFood + ": " + Arrays.toString(nutritionInfo));
                    } else {
                        System.out.println("No nutrition info found for " + detectedFood);
                    }
                } else {
                    System.out.println("No food detected.");
                }

                // Display the frame with detected food label
                Imgproc.putText(frame, "Detected: " + (detectedFood != null ? detectedFood : "None"),
                        new Point(10, 30), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 255, 0), 2);
                HighGui.imshow("Food Scanner", frame);
            } else {
                System.out.println("Failed to capture frame from camera.");
            }

            // Break the loop if 'ESC' is pressed
            if (HighGui.waitKey(1) == 27) {
                break;
            }
        }

        // Release resources
        camera.release();
        HighGui.destroyAllWindows();
        System.out.println("Camera released and windows closed.");

        // Calculate total nutrition for the meal
        double[] totalNutrition = calculateTotalNutrition(detectedFoods);
        System.out.println("Total nutrition for the meal: " + Arrays.toString(totalNutrition));

        // Compare with user's nutritional limits
        compareNutritionWithLimits(totalNutrition, limitesNutrition);
    }

    private String detectFood(Mat frame, Net net) {
        // Preprocess the frame for the model
        Mat blob = Dnn.blobFromImage(frame, 1.0 / 255.0, new Size(416, 416), new Scalar(0, 0, 0), true, false);
        net.setInput(blob);

        // Perform inference
        List<String> outLayerNames = net.getUnconnectedOutLayersNames();
        List<Mat> detections = new ArrayList<>();
        net.forward(detections, outLayerNames);

        // Parse the results
        for (Mat detection : detections) {
            for (int i = 0; i < detection.rows(); i++) {
                double[] data = detection.get(i, 0);

                // Log the structure of the detection
                System.out.println("Detection data: " + Arrays.toString(data));

                // Ensure the data array has enough elements
                if (data.length < 6) {
                    System.out.println("Skipping invalid detection (length = " + data.length + ")");
                    continue; // Skip invalid detections
                }

                double confidence = data[4]; // Confidence score
                if (confidence > 0.3) { // Confidence threshold (réduit à 0.3)
                    int classId = (int) data[5]; // Class ID
                    if (classId >= 0 && classId < CLASSES.size()) {
                        String detectedClass = CLASSES.get(classId);
                        System.out.println("Detected class: " + detectedClass + " with confidence: " + confidence);
                        return detectedClass;
                    }
                }
            }
        }

        return null; // No food detected
    }

    private double[] getNutritionInfo(String foodName) {
        // Simulate fetching nutrition info from a database or API
        // Replace this with actual API calls or database queries
        switch (foodName.toLowerCase()) {
            case "apple":
                return new double[]{52, 0.3, 0.2, 14}; // Calories, Proteins, Fats, Carbohydrates
            case "banana":
                return new double[]{89, 1.1, 0.3, 23};
            case "chicken":
                return new double[]{165, 31, 3.6, 0};
            case "rice":
                return new double[]{130, 2.7, 0.3, 28};
            case "pasta":
                return new double[]{131, 5, 1.1, 25};
            default:
                return null; // No info found
        }
    }

    private double[] calculateTotalNutrition(List<String> foods) {
        double totalCalories = 0;
        double totalProteins = 0;
        double totalFats = 0;
        double totalCarbohydrates = 0;

        for (String food : foods) {
            double[] nutritionInfo = getNutritionInfo(food);
            if (nutritionInfo != null) {
                totalCalories += nutritionInfo[0];
                totalProteins += nutritionInfo[1];
                totalFats += nutritionInfo[2];
                totalCarbohydrates += nutritionInfo[3];
            }
        }

        return new double[]{totalCalories, totalProteins, totalFats, totalCarbohydrates};
    }

    private void compareNutritionWithLimits(double[] totalNutrition, double[] limitesNutrition) {
        System.out.println("Comparing nutrition with limits...");
        System.out.println("Total Calories: " + totalNutrition[0] + " / Limit: " + limitesNutrition[0]);
        System.out.println("Total Proteins: " + totalNutrition[1] + " / Limit: " + limitesNutrition[1]);
        System.out.println("Total Fats: " + totalNutrition[2] + " / Limit: " + limitesNutrition[2]);
        System.out.println("Total Carbohydrates: " + totalNutrition[3] + " / Limit: " + limitesNutrition[3]);

        if (totalNutrition[0] > limitesNutrition[0]) {
            System.out.println("Warning: Calories exceed the limit!");
        }
        if (totalNutrition[1] > limitesNutrition[1]) {
            System.out.println("Warning: Proteins exceed the limit!");
        }
        if (totalNutrition[2] > limitesNutrition[2]) {
            System.out.println("Warning: Fats exceed the limit!");
        }
        if (totalNutrition[3] > limitesNutrition[3]) {
            System.out.println("Warning: Carbohydrates exceed the limit!");
        }
    }

    private double[] getLimitesNutritionPourIMC(double imc) {
        double[] limites = new double[4]; // [calories, proteines, glucides, lipides]

        if (imc >= 24) {
            // Plan pour IMC >= 24 (surpoids)
            limites[0] = 200; // Limite de calories
            limites[1] = 15;  // Limite de protéines
            limites[2] = 25;  // Limite de glucides
            limites[3] = 10;  // Limite de lipides
        } else if (imc >= 18.5 && imc < 24) {
            // Plan pour 18.5 <= IMC < 24 (poids normal)
            limites[0] = 250; // Limite de calories
            limites[1] = 20;  // Limite de protéines
            limites[2] = 30;  // Limite de glucides
            limites[3] = 15;  // Limite de lipides
        } else if (imc < 18.5) {
            // Plan pour IMC < 18.5 (insuffisance pondérale)
            limites[0] = 300; // Limite de calories
            limites[1] = 25;  // Limite de protéines
            limites[2] = 35;  // Limite de glucides
            limites[3] = 20;  // Limite de lipides
        }

        return limites;
    }
}