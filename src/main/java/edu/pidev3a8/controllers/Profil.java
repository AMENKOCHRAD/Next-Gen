package edu.pidev3a8.controllers;

import edu.pidev3a8.entities.Utilisateur;
import edu.pidev3a8.utils.CurrentUser;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Profil {
    @FXML
    private Label emailLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label genderLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label salaryLabel;
    @FXML
    private Label bannedLabel;
    @FXML
    private ImageView profileImageView;

    @FXML
    public void initialize() {
        CurrentUser currentUserInstance = CurrentUser.getInstance();
        Utilisateur currentUser = currentUserInstance.getCurrentUser();
        if (currentUser != null) {
            System.out.println("Salary: " + currentUser.getSalaire()); // Debug statement

            emailLabel.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "Non spécifié");
            nameLabel.setText((currentUser.getNom() != null ? currentUser.getNom() : "Non spécifié") + " " + (currentUser.getPrenom() != null ? currentUser.getPrenom() : "Non spécifié"));
            roleLabel.setText(currentUser.getRole() != null ? currentUser.getRole().toString() : "Non spécifié");
            phoneLabel.setText(currentUser.getNumTel() != 0 ? String.valueOf(currentUser.getNumTel()) : "Non spécifié");
            genderLabel.setText(currentUser.getGenre() != null ? currentUser.getGenre() : "Non spécifié");
            addressLabel.setText(currentUser.getAdresse() != null ? currentUser.getAdresse() : "Non spécifié");
            salaryLabel.setText(currentUser.getSalaire() >= 0.0f ? String.valueOf(currentUser.getSalaire()) : "Non spécifié");
            bannedLabel.setText(currentUser.isBanned() ? "Yes" : "No");

            // Assuming getImage() returns a valid Image object
            Image profileImage = currentUser.getImage();
            if (profileImage != null) {
                profileImageView.setImage(profileImage);
            }
        }
    }
}