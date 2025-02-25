package Utilisateur.Pidev.Controllers;


import Utilisateur.Pidev.Entites.Utilisateur;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class JFXCellFactory implements Callback<TableColumn<Utilisateur, String>, TableCell<Utilisateur, String>> {
    @Override
    public TableCell<Utilisateur, String> call(TableColumn<Utilisateur, String> param) {
        return new TableCell<Utilisateur, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("••••••••");
                }
            }
        };
    }
}