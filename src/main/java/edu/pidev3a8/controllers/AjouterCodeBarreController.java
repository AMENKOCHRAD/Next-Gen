package edu.pidev3a8.controllers;

import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import edu.pidev3a8.entities.CodeBarre;
import edu.pidev3a8.entities.Nutrition;
import edu.pidev3a8.services.CodeBarreService;
import edu.pidev3a8.tools.MyConnection;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.util.Arrays;
import java.util.ResourceBundle;

public class AjouterCodeBarreController implements Initializable {

    @FXML
    private TextField nomProduitField;
    @FXML
    private TextField ingredientsField;
    @FXML
    private TextField marqueField;
    @FXML
    private TextField RefProduitField;
    @FXML
    private TextField caloriesField;
    @FXML
    private TextField proteinesField;
    @FXML
    private TextField glucidesField;
    @FXML
    private TextField lipidesField;
    @FXML
    private ComboBox<Integer> utilisateurComboBox;
    @FXML
    private ImageView cameraView;
    @FXML
    private Button trouverProduitCompatibleButton;

    private final CodeBarreService codeBarreService = new CodeBarreService();
    private VideoCapture camera;
    private boolean isScanning = false;
    private Thread cameraThread;

    static {
        System.load("C:\\Pidev3A8(entite1,2)\\lib\\opencv\\build\\java\\x64\\opencv_java490.dll");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadUtilisateurs();
        if (!utilisateurComboBox.getItems().isEmpty()) {
            utilisateurComboBox.getSelectionModel().selectFirst(); // Sélectionner le premier utilisateur par défaut
        }
        startCamera();

    }

    private void loadUtilisateurs() {
        ObservableList<Integer> utilisateursList = FXCollections.observableArrayList();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = MyConnection.getInstance().getCnx();
            String query = "SELECT id FROM utilisateur";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                int idUtilisateur = rs.getInt("id");
                utilisateursList.add(idUtilisateur);
            }
            utilisateurComboBox.setItems(utilisateursList);
        } catch (SQLException e) {
            e.printStackTrace();
            afficherErreur("Erreur", "Erreur lors du chargement des utilisateurs : " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void startCamera() {
        // Initialize the camera
        camera = new VideoCapture(0); // Use the default camera
        if (!camera.isOpened()) {
            afficherErreur("Erreur", "Impossible d'accéder à la caméra.");
            return;
        }

        isScanning = true;
        cameraThread = new Thread(() -> {
            Mat frame = new Mat();
            while (isScanning) {
                // Read a frame from the camera
                if (camera.read(frame)) {
                    if (!frame.empty()) {
                        // Convert the frame to an Image for JavaFX
                        Image fxImage = convertirMatToImageView(frame);

                        // Update the JavaFX UI on the main thread
                        Platform.runLater(() -> cameraView.setImage(fxImage));

                        // Detect barcode in the frame
                        String codeBarre = detecterCodeBarre(frame);
                        if (codeBarre != null) {
                            // Stop the camera and update the UI on the main thread
                            Platform.runLater(() -> {
                                RefProduitField.setText(codeBarre);
                                stopCamera();
                            });
                        }
                    }
                } else {
                    // Handle the case where the frame could not be read
                    System.err.println("Erreur: Impossible de lire une frame de la caméra.");
                    break;
                }

                // Add a small delay to avoid overloading the CPU
                try {
                    Thread.sleep(30); // 30ms delay (~33 FPS)
                } catch (InterruptedException e) {
                    System.err.println("Thread interrupted: " + e.getMessage());
                    break;
                }
            }

            // Release the camera when done
            camera.release();
        });

        // Set the thread as a daemon so it doesn't prevent the application from exiting
        cameraThread.setDaemon(true);
        cameraThread.start();
    }

    private void remplirChampsDepuisOpenFoodFacts(String codeBarre) {
        String apiUrl = "https://world.openfoodfacts.org/api/v0/product/" + codeBarre + ".json";

        try {
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                InputStream inputStream = connection.getInputStream();
                java.util.Scanner scanner = new java.util.Scanner(inputStream).useDelimiter("\\A");
                String jsonResponse = scanner.hasNext() ? scanner.next() : "";
                scanner.close();

                org.json.JSONObject jsonObject = new org.json.JSONObject(jsonResponse);

                if (jsonObject.has("product")) {
                    org.json.JSONObject product = jsonObject.getJSONObject("product");

                    final String nomProduit = product.optString("product_name", "N/A");
                    final String marque = product.optString("brands", "N/A");
                    String ingredients = product.optString("ingredients_text", "N/A");
                    String[] ingredientsList = ingredients.split(",");
                    StringBuilder limitedIngredients = new StringBuilder();

                    for (int i = 0; i < Math.min(4, ingredientsList.length); i++) {
                        if (i > 0) {
                            limitedIngredients.append(", ");
                        }
                        limitedIngredients.append(ingredientsList[i].trim());
                    }

                    final double calories;
                    final double proteines;
                    final double glucides;
                    final double lipides;

                    if (product.has("nutriments")) {
                        org.json.JSONObject nutriments = product.getJSONObject("nutriments");
                        calories = nutriments.optDouble("energy-kcal_100g", 0.0);
                        proteines = nutriments.optDouble("proteins_100g", 0.0);
                        glucides = nutriments.optDouble("carbohydrates_100g", 0.0);
                        lipides = nutriments.optDouble("fat_100g", 0.0);
                    } else {
                        calories = 0.0;
                        proteines = 0.0;
                        glucides = 0.0;
                        lipides = 0.0;
                    }

                    Platform.runLater(() -> {
                        nomProduitField.setText(nomProduit);
                        marqueField.setText(marque);
                        ingredientsField.setText(limitedIngredients.toString());
                        caloriesField.setText(String.valueOf(calories));
                        proteinesField.setText(String.valueOf(proteines));
                        glucidesField.setText(String.valueOf(glucides));
                        lipidesField.setText(String.valueOf(lipides));

                        Integer fk_utilisateur = utilisateurComboBox.getValue();
                        if (fk_utilisateur != null) {
                            boolean estCompatible = verifierCompatibiliteProduit(calories, proteines, glucides, lipides, fk_utilisateur);
                            System.out.println("Produit compatible: " + estCompatible);

                            if (!estCompatible) {
                                // Afficher une alerte si le produit n'est pas compatible
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                alert.setTitle("Produit Non Compatible");
                                alert.setHeaderText("Ce produit n'est pas compatible avec votre régime alimentaire.");
                                alert.setContentText("Voulez-vous trouver un produit compatible ?");

                                // Ajouter des boutons Oui/Non
                                ButtonType buttonTypeOui = new ButtonType("Oui");
                                ButtonType buttonTypeNon = new ButtonType("Non");
                                alert.getButtonTypes().setAll(buttonTypeOui, buttonTypeNon);

                                // Gérer la réponse de l'utilisateur
                                alert.showAndWait().ifPresent(response -> {
                                    if (response == buttonTypeOui) {
                                        // Rechercher un produit compatible
                                        double[] limites = getLimitesNutritionPourIMC(getIMCDeUtilisateur(fk_utilisateur));
                                        CodeBarre produitCompatible = trouverProduitCompatibleViaAPI(limites);
                                        if (produitCompatible != null) {
                                            afficherProduitCompatible(produitCompatible);
                                        } else {
                                            afficherErreur("Aucun produit compatible", "Aucun produit compatible n'a été trouvé.");
                                        }
                                    } else {
                                        // Retourner à la liste des codes-barres
                                        goToHome();
                                    }
                                });
                            }
                        } else {
                            System.out.println("Aucun utilisateur sélectionné.");
                        }
                    });
                } else {
                    System.err.println("Aucun produit trouvé pour ce code-barres.");
                }
            } else {
                System.err.println("Erreur API OpenFoodFacts : Code de réponse " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            afficherErreur("Erreur API", "Impossible de récupérer les informations du produit : " + e.getMessage());
        }
    }

    private String detecterCodeBarre(Mat frame) {
        BufferedImage image = convertirMatToBufferedImage(frame);
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
            Result result = new MultiFormatReader().decode(bitmap);
            String codeBarre = result.getText();

            // Appel de l'API OpenFoodFacts pour remplir les champs
            remplirChampsDepuisOpenFoodFacts(codeBarre);

            return codeBarre;
        } catch (NotFoundException e) {
            // Aucun code-barres trouvé, continuer à scanner
        }
        return null;
    }

    private void stopCamera() {
        isScanning = false;
        if (camera != null && camera.isOpened()) {
            camera.release();
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

    private double getIMCDeUtilisateur(int id_utilisateur) {
        double imc = 0.0;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = MyConnection.getInstance().getCnx();
            String query = "SELECT imc FROM nutrition WHERE id_utilisateur = ?"; // Remplacez 'id' par le bon nom de colonne
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, id_utilisateur); // Utilisation d'un PreparedStatement pour éviter les injections SQL
            rs = stmt.executeQuery();

            if (rs.next()) {
                imc = rs.getDouble("imc");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return imc;
    }

    private boolean verifierCompatibiliteProduit(double calories, double proteines, double glucides, double lipides, int idUtilisateur) {
        double imc = getIMCDeUtilisateur(idUtilisateur);
        System.out.println("IMC: " + imc); // Afficher l'IMC de l'utilisateur

        if (imc <= 0) {
            return false; // IMC invalide
        }

        double[] limites = getLimitesNutritionPourIMC(imc);
        System.out.println("Limites: " + Arrays.toString(limites)); // Afficher les limites nutritionnelles

        boolean estCompatible = estProduitCompatible(calories, proteines, glucides, lipides, limites);
        System.out.println("Produit compatible: " + estCompatible); // Afficher si le produit est compatible

        return estCompatible;
    }

    private boolean estProduitCompatible(double calories, double proteines, double glucides, double lipides, double[] limites) {
        return calories <= limites[0] &&
                proteines <= limites[1] &&
                glucides <= limites[2] &&
                lipides <= limites[3];
    }

    @FXML
    void AjouterCodeBarreAction(ActionEvent event) {
        // Récupération des valeurs des champs
        String nomProduit = nomProduitField.getText().trim();
        String ingredients = ingredientsField.getText().trim();
        String marque = marqueField.getText().trim();
        Integer fk_utilisateur = utilisateurComboBox.getValue();
        String ref_produit = RefProduitField.getText().trim();
        String caloriesText = caloriesField.getText().trim();
        String proteinesText = proteinesField.getText().trim();
        String glucidesText = glucidesField.getText().trim();
        String lipidesText = lipidesField.getText().trim();

        // Validation des champs obligatoires
        if (!validerChamp(nomProduit, "Nom du produit") ||
                !validerChamp(ingredients, "Ingrédients") ||
                !validerChamp(marque, "Marque") ||
                !validerChamp(ref_produit, "Référence du produit") ||
                !validerChamp(caloriesText, "Calories") ||
                !validerChamp(proteinesText, "Protéines") ||
                !validerChamp(glucidesText, "Glucides") ||
                !validerChamp(lipidesText, "Lipides")) {
            return; // Arrêter si un champ est vide
        }

        // Validation de l'utilisateur
        if (fk_utilisateur == null) {
            afficherErreur("Erreur", "Veuillez sélectionner un utilisateur.");
            return;
        }

        // Conversion des valeurs numériques
        double calories = Double.parseDouble(caloriesText);
        double proteines = Double.parseDouble(proteinesText);
        double glucides = Double.parseDouble(glucidesText);
        double lipides = Double.parseDouble(lipidesText);

        // Vérification de la compatibilité du produit avec l'IMC de l'utilisateur
        if (!verifierCompatibiliteProduit(calories, proteines, glucides, lipides, fk_utilisateur)) {
            afficherErreur("Incompatibilité", "Ce produit n'est pas compatible avec votre régime alimentaire.");
            return;
        }

        // Création et ajout du code-barres
        CodeBarre codeBarre = new CodeBarre(nomProduit, ingredients, marque, fk_utilisateur, ref_produit, calories, proteines, glucides, lipides);
        codeBarreService.addCodeBarre(codeBarre);

        // Affichage d'un message de succès
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText("Code-barre ajouté avec succès !");
        alert.showAndWait();

        // Retour à la page d'accueil
        goToHome();
    }

    private boolean validerChamp(String valeur, String champ) {
        if (valeur.isEmpty()) {
            afficherErreur("Erreur de saisie", "Le champ " + champ + " ne peut pas être vide.");
            return false;
        }

        return true;
    }

    @FXML
    private void goToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/detailCode.fxml"));
            Parent root = loader.load();
            DetailCodeController controller = loader.getController();
            Stage stage = (Stage) nomProduitField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            afficherErreur("Erreur", "Impossible de charger la page d'accueil.");
        }
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private BufferedImage convertirMatToBufferedImage(Mat mat) {
        MatOfByte byteMat = new MatOfByte();
        Imgcodecs.imencode(".png", mat, byteMat);
        byte[] byteArray = byteMat.toArray();
        BufferedImage image = null;

        try (InputStream in = new ByteArrayInputStream(byteArray)) {
            image = ImageIO.read(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    private Image convertirMatToImageView(Mat mat) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", mat, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }



    private CodeBarre trouverProduitCompatibleViaAPI(double[] limites) {
        String apiUrl = "https://world.openfoodfacts.org/cgi/search.pl?" +
                "search_terms=produit" + // Vous pouvez ajuster les termes de recherche
                "&sort_by=nutriscore_score" + // Trier par Nutri-Score pour trouver des produits plus sains
                "&json=1";

        try {
            // Envoyer une requête HTTP GET à l'API
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                // Lire la réponse JSON
                InputStream inputStream = connection.getInputStream();
                java.util.Scanner scanner = new java.util.Scanner(inputStream).useDelimiter("\\A");
                String jsonResponse = scanner.hasNext() ? scanner.next() : "";
                scanner.close();

                // Parser la réponse JSON
                org.json.JSONObject jsonObject = new org.json.JSONObject(jsonResponse);
                org.json.JSONArray products = jsonObject.getJSONArray("products");

                // Parcourir les produits pour en trouver un compatible
                for (int i = 0; i < products.length(); i++) {
                    org.json.JSONObject product = products.getJSONObject(i);

                    // Récupérer les informations nutritionnelles du produit
                    if (product.has("nutriments")) {
                        org.json.JSONObject nutriments = product.getJSONObject("nutriments");

                        double productCalories = nutriments.optDouble("energy-kcal_100g", 0.0);
                        double productProteines = nutriments.optDouble("proteins_100g", 0.0);
                        double productGlucides = nutriments.optDouble("carbohydrates_100g", 0.0);
                        double productLipides = nutriments.optDouble("fat_100g", 0.0);

                        // Vérifier si le produit est compatible avec les limites
                        if (productCalories <= limites[0] &&
                                productProteines <= limites[1] &&
                                productGlucides <= limites[2] &&
                                productLipides <= limites[3]) {
                            // Retourner le produit compatible
                            return new CodeBarre(
                                    product.optString("product_name", "N/A"),
                                    product.optString("ingredients_text", "N/A"),
                                    product.optString("brands", "N/A"),
                                    0, // ID utilisateur (à remplacer si nécessaire)
                                    product.optString("code", "N/A"),
                                    productCalories,
                                    productProteines,
                                    productGlucides,
                                    productLipides
                            );
                        }
                    }
                }
            } else {
                System.err.println("Erreur API Open Food Facts : Code de réponse " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            afficherErreur("Erreur API", "Impossible de récupérer les informations du produit : " + e.getMessage());
        }

        return null; // Aucun produit compatible trouvé
    }
    private int obtenirIdUtilisateur() {
        // Retourne l'ID de l'utilisateur sélectionné dans le ComboBox (ou autre composant)
        Integer idUtilisateur = utilisateurComboBox.getValue();  // Assurez-vous que 'utilisateurComboBox' est bien défini
        if (idUtilisateur != null) {
            return idUtilisateur;
        } else {
            // Si aucun utilisateur n'est sélectionné, afficher un message d'erreur ou une valeur par défaut
            afficherErreur("Erreur", "Aucun utilisateur sélectionné.");
            return -1;  // Retourne une valeur invalide (ou vous pouvez lancer une exception si nécessaire)
        }
    }

    private void afficherProduitCompatible(CodeBarre produitCompatible) {
        // Afficher les informations du produit compatible dans une boîte de dialogue
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Produit Compatible Trouvé");
        alert.setHeaderText("Un produit compatible a été trouvé :");

        // Ajouter les informations à afficher dans l'alerte
        alert.setContentText("Nom: " + produitCompatible.getNom_produit() + "\n" +
                "Marque: " + produitCompatible.getMarque() + "\n" +
                "Calories: " + produitCompatible.getCalories() + "\n" +
                "Protéines: " + produitCompatible.getProteines() + "\n" +
                "Glucides: " + produitCompatible.getGlucides() + "\n" +
                "Lipides: " + produitCompatible.getLipides() + "\n" +
                "Code-barre: " + produitCompatible.getRef_produit() + "\n" +
                "Ingrédients: " + produitCompatible.getIngredients());

        // Ajouter un bouton pour enregistrer le produit dans la base de données
        ButtonType addButton = new ButtonType("Ajouter ");
        alert.getButtonTypes().setAll(ButtonType.CANCEL, addButton);

        // Récupérer l'ID de l'utilisateur actuel
        int idUtilisateur = obtenirIdUtilisateur();  // Vous devez implémenter cette méthode pour récupérer l'ID de l'utilisateur

        // Gestion de l'ajout dans la base de données
        alert.showAndWait().ifPresent(response -> {
            if (response == addButton) {
                // Créer un nouvel objet CodeBarre avec les informations et l'ID de l'utilisateur
                CodeBarre codeBarre = new CodeBarre(
                        produitCompatible.getNom_produit(),
                        produitCompatible.getIngredients(),
                        produitCompatible.getMarque(),
                        idUtilisateur,  // L'ID de l'utilisateur qui a scanné le produit
                        produitCompatible.getRef_produit(),
                        produitCompatible.getCalories(),
                        produitCompatible.getProteines(),
                        produitCompatible.getGlucides(),
                        produitCompatible.getLipides()
                );

                // Ajouter le code-barre dans la base de données
                codeBarreService.addCodeBarre(codeBarre);

                // Retour à l'écran principal ou autre action après l'ajout
                goToHome();
            }
        });
    }

}