package edu.pidev3a8.services;

import edu.pidev3a8.entities.Reclamation;
import edu.pidev3a8.entities.Categorie;
import edu.pidev3a8.interfaces.IService;
import edu.pidev3a8.tools.MyConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReclamtionService implements IService<Reclamation> {
    @Override
    public void addEntity(Reclamation reclamation) {
        try {
            // Vérifier si l'utilisateur a déjà déposé 3 réclamations aujourd'hui
            String countQuery = "SELECT COUNT(*) FROM reclamation WHERE user = ? AND DATE(date) = CURDATE()";
            PreparedStatement countStmt = MyConnection.getInstance().getCnx().prepareStatement(countQuery);
            countStmt.setInt(1, reclamation.getUser());
            ResultSet rs = countStmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                if (count >= 3) {
                    throw new SQLException("Vous avez  déjà déposé 3 réclamations aujourd'hui.");
                }
            }

            // Ajouter la réclamation si la limite n'est pas atteinte
            String requete = "INSERT INTO reclamation (sujet, description, statut, date, user, categorie, pieces_jointes) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setString(1, reclamation.getSujet());
            pst.setString(2, reclamation.getDescription());
            pst.setString(3, reclamation.getStatut());
            pst.setTimestamp(4, java.sql.Timestamp.valueOf(reclamation.getDate()));
            pst.setInt(5, reclamation.getUser());
            pst.setString(6, reclamation.getCategorie().name());
            pst.setString(7, String.join(",", reclamation.getPiecesJointes()));
            pst.executeUpdate();
            System.out.println("Réclamation ajoutée avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la réclamation : " + e.getMessage());
            throw new RuntimeException(e); // Propager l'exception pour la gérer dans le contrôleur
        }
    }
    @Override
    public void deleteEntity(Reclamation reclamation) {
        try {
            String requete = "DELETE FROM reclamation WHERE id = ?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setInt(1, reclamation.getId());
            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Réclamation supprimée avec succès !");
            } else {
                System.out.println("Aucune réclamation trouvée avec l'ID : " + reclamation.getId());
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de la réclamation : " + e.getMessage());
        }
    }

    @Override
    public void updateEntity(int id, Reclamation reclamation) {
        try {
            String requete = "UPDATE reclamation SET sujet = ?, description = ?, statut = ?, date = ?, user = ?, categorie = ?, pieces_jointes = ? WHERE id = ?";

            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setString(1, reclamation.getSujet());
            pst.setString(2, reclamation.getDescription());
            pst.setString(3, reclamation.getStatut());

            // Vérifier si la date est null et l'initialiser si nécessaire
            if (reclamation.getDate() == null) {
                reclamation.setDate(LocalDateTime.now()); // Initialiser la date avec la date et l'heure actuelles
            }
            pst.setTimestamp(4, java.sql.Timestamp.valueOf(reclamation.getDate())); // Utiliser la date de l'objet Reclamation

            pst.setInt(5, reclamation.getUser());
            pst.setString(6, reclamation.getCategorie().name()); // Convertir l'enum en String
            pst.setString(7, String.join(",", reclamation.getPiecesJointes())); // Convertir la liste en chaîne séparée par des virgules
            pst.setInt(8, id); // ID de la réclamation à mettre à jour

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Réclamation mise à jour avec succès !");
            } else {
                System.out.println("Aucune réclamation trouvée avec l'ID : " + id);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour de la réclamation : " + e.getMessage());
        }
    }

    @Override
    public List<Reclamation> getAllData() {
        List<Reclamation> result = new ArrayList<>();
        try {
            String requete = "SELECT * FROM reclamation";
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            ResultSet rs = st.executeQuery(requete);
            while (rs.next()) {
                Reclamation reclamation = new Reclamation();
                reclamation.setId(rs.getInt("id"));
                reclamation.setSujet(rs.getString("sujet"));
                reclamation.setDescription(rs.getString("description"));
                reclamation.setStatut(rs.getString("statut"));
                reclamation.setDate(rs.getTimestamp("date").toLocalDateTime());
                reclamation.setUser(rs.getInt("user"));

                // Gestion de la catégorie
                String categorieStr = rs.getString("categorie");
                if (categorieStr != null && !categorieStr.isEmpty()) {
                    try {
                        reclamation.setCategorie(Categorie.valueOf(categorieStr)); // Convertir la chaîne en enum
                    } catch (IllegalArgumentException e) {
                        // Si la valeur ne correspond à aucune valeur de l'enum, utiliser une valeur par défaut
                        reclamation.setCategorie(Categorie.AUTRE); // Remplacez AUTRE par une valeur par défaut appropriée
                    }
                } else {
                    // Si la valeur est null ou vide, utiliser une valeur par défaut
                    reclamation.setCategorie(Categorie.AUTRE); // Remplacez AUTRE par une valeur par défaut appropriée
                }

                // Gestion des pièces jointes
                String piecesJointesStr = rs.getString("pieces_jointes");
                if (piecesJointesStr != null && !piecesJointesStr.isEmpty()) {
                    reclamation.setPiecesJointes(Arrays.asList(piecesJointesStr.split(","))); // Convertir la chaîne en liste
                } else {
                    reclamation.setPiecesJointes(new ArrayList<>()); // Liste vide si null ou vide
                }

                result.add(reclamation);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des réclamations : " + e.getMessage());
        }
        return result;
    }
    public Reclamation getReclamationById(int id) {
        Reclamation reclamation = null;
        try {
            String requete = "SELECT * FROM reclamation WHERE id = ?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                reclamation = new Reclamation();
                reclamation.setId(rs.getInt("id"));
                reclamation.setSujet(rs.getString("sujet"));
                reclamation.setDescription(rs.getString("description"));
                reclamation.setStatut(rs.getString("statut"));
                reclamation.setDate(rs.getTimestamp("date").toLocalDateTime());
                reclamation.setUser(rs.getInt("user"));

                // Gestion de la catégorie
                String categorieStr = rs.getString("categorie");
                if (categorieStr != null && !categorieStr.isEmpty()) {
                    try {
                        reclamation.setCategorie(Categorie.valueOf(categorieStr));
                    } catch (IllegalArgumentException e) {
                        reclamation.setCategorie(Categorie.AUTRE);
                    }
                } else {
                    reclamation.setCategorie(Categorie.AUTRE);
                }

                // Gestion des pièces jointes
                String piecesJointesStr = rs.getString("pieces_jointes");
                if (piecesJointesStr != null && !piecesJointesStr.isEmpty()) {
                    reclamation.setPiecesJointes(Arrays.asList(piecesJointesStr.split(",")));
                } else {
                    reclamation.setPiecesJointes(new ArrayList<>());
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de la réclamation : " + e.getMessage());
        }
        return reclamation;
    }

}