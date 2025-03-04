package edu.pidev3a8.controllers;

import edu.pidev3a8.entities.CodeBarre;
import edu.pidev3a8.services.CodeBarreService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import javax.swing.JOptionPane;

public class DetailCodeController {

    @FXML
    private TableView<CodeBarre> codeBarreTable;
    @FXML
    private TableColumn<CodeBarre, Integer> idColumn;
    @FXML
    private TableColumn<CodeBarre, String> nomProduitColumn;
    @FXML
    private TableColumn<CodeBarre, String> ingredientsColumn;
    @FXML
    private TableColumn<CodeBarre, String> marqueColumn;
    @FXML
    private TableColumn<CodeBarre, Integer> fkUtilisateurColumn;
    @FXML
    private TableColumn<CodeBarre, String> refproduitColumn;
    @FXML
    private TableColumn<CodeBarre, Double> caloriesColumn;

    @FXML
    private TableColumn<CodeBarre, Double> proteinesColumn;

    @FXML
    private TableColumn<CodeBarre, Double> glucidesColumn;

    @FXML
    private TableColumn<CodeBarre, Double> lipidesColumn;
    @FXML
    private TableColumn<CodeBarre, Void> actionsColumn;

    private final CodeBarreService codeBarreService = new CodeBarreService();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id_Code"));
        nomProduitColumn.setCellValueFactory(new PropertyValueFactory<>("Nom_produit"));
        refproduitColumn.setCellValueFactory(new PropertyValueFactory<>("Ref_produit"));
        ingredientsColumn.setCellValueFactory(new PropertyValueFactory<>("Ingredients"));
        marqueColumn.setCellValueFactory(new PropertyValueFactory<>("Marque"));
        fkUtilisateurColumn.setCellValueFactory(new PropertyValueFactory<>("fk_utilisateur"));
        caloriesColumn.setCellValueFactory(new PropertyValueFactory<>("calories"));
        proteinesColumn.setCellValueFactory(new PropertyValueFactory<>("proteines"));
        glucidesColumn.setCellValueFactory(new PropertyValueFactory<>("glucides"));
        lipidesColumn.setCellValueFactory(new PropertyValueFactory<>("lipides"));


        actionsColumn.setCellFactory(new Callback<TableColumn<CodeBarre, Void>, TableCell<CodeBarre, Void>>() {
            @Override
            public TableCell<CodeBarre, Void> call(TableColumn<CodeBarre, Void> param) {
                return new TableCell<CodeBarre, Void>() {
                    private final Button modifyBtn = new Button("Modifier");
                    private final Button deleteBtn = new Button("Supprimer");
                    private final HBox buttonContainer = new HBox(10, modifyBtn, deleteBtn);

                    {
                        modifyBtn.setOnAction(e -> {
                            CodeBarre selectedCodeBarre = getTableView().getItems().get(getIndex());
                            goToModifierCodeBarre(selectedCodeBarre);
                        });

                        deleteBtn.setOnAction(e -> {
                            CodeBarre selectedCodeBarre = getTableView().getItems().get(getIndex());
                            deleteCodeBarre(selectedCodeBarre);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : buttonContainer);
                    }
                };
            }
        });

        loadCodeBarreData();
    }

    public void loadCodeBarreData() {
        ObservableList<CodeBarre> codeBarreList = FXCollections.observableArrayList(codeBarreService.getAllData());
        codeBarreTable.setItems(codeBarreList);
    }

    private void deleteCodeBarre(CodeBarre codeBarre) {
        // Afficher une boîte de dialogue de confirmation
        int response = JOptionPane.showConfirmDialog(
                null,
                "Êtes-vous sûr de vouloir supprimer ce code-barres ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        // Vérifier la réponse de l'utilisateur
        if (response == JOptionPane.YES_OPTION) {
            // Si l'utilisateur confirme, procéder à la suppression
            System.out.println("Suppression du code-barres : " + codeBarre);
            codeBarreService.deleteCodeBarre(codeBarre);
            loadCodeBarreData(); // Rafraîchir la liste après suppression
        } else {
            // Si l'utilisateur annule, ne rien faire
            System.out.println("Suppression du code-barres annulée.");
        }
    }

    @FXML
    private void goToAjouterCodeBarre() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterCodeBarre.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) codeBarreTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void goToModifierCodeBarre(CodeBarre selectedCodeBarre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierCodeBarre.fxml"));
            Parent root = loader.load();

            ModifierCodeBarreController controller = loader.getController();
            controller.setCodeBarre(selectedCodeBarre);  // Passer les détails du code-barre sélectionné

            Stage stage = (Stage) codeBarreTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToListedesNutrition(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML correspondant à la liste des codes à barre
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailNutrition.fxml"));
            Parent root = loader.load();

            // Obtenir la scène de la fenêtre actuelle et la remplacer par la nouvelle scène
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}