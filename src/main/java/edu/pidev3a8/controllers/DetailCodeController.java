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
    private TableColumn<CodeBarre, Void> actionsColumn;

    private final CodeBarreService codeBarreService = new CodeBarreService();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id_Code"));
        nomProduitColumn.setCellValueFactory(new PropertyValueFactory<>("nom_produit"));
        ingredientsColumn.setCellValueFactory(new PropertyValueFactory<>("ingredients"));
        marqueColumn.setCellValueFactory(new PropertyValueFactory<>("marque"));
        fkUtilisateurColumn.setCellValueFactory(new PropertyValueFactory<>("fk_utilisateur"));

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
        codeBarreService.deleteCodeBarre(codeBarre);
        loadCodeBarreData();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Detail.fxml"));
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