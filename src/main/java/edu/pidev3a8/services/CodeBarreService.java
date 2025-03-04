package edu.pidev3a8.services;

import edu.pidev3a8.entities.CodeBarre;
import edu.pidev3a8.tools.MyConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CodeBarreService {

    // Ajouter un CodeBarre
    public void addCodeBarre(CodeBarre codeBarre) {
        try {
            String requete = "INSERT INTO codebarre(nom_produit, ingredients, marque, fk_utilisateur, ref_produit, calories, proteines, glucides, lipides) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);

            pst.setString(1, codeBarre.getNom_produit());
            pst.setString(2, codeBarre.getIngredients());
            pst.setString(3, codeBarre.getMarque());
            pst.setInt(4, codeBarre.getFk_utilisateur());
            pst.setString(5, codeBarre.getRef_produit());
            pst.setDouble(6, codeBarre.getCalories());
            pst.setDouble(7, codeBarre.getProteines());
            pst.setDouble(8, codeBarre.getGlucides());
            pst.setDouble(9, codeBarre.getLipides());

            pst.executeUpdate();
            System.out.println("Code-barre ajouté avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur SQL (ajout) : " + e.getMessage());
        }
    }

    // Supprimer un CodeBarre
    public void deleteCodeBarre(CodeBarre codeBarre) {
        try {
            String requete = "DELETE FROM codebarre WHERE id_Code = ?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setInt(1, codeBarre.getId_Code());
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Code-barre supprimé avec succès !");
            } else {
                System.out.println("Aucune suppression effectuée. ID introuvable.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL (suppression) : " + e.getMessage());
        }
    }

    // Mise à jour d'un CodeBarre
    public void updateCodeBarre(int id_Code, CodeBarre codeBarre) {
        try {
            String requete = "UPDATE codebarre SET nom_produit = ?, ingredients = ?, marque = ?, fk_utilisateur = ? WHERE id_Code = ?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setString(1, codeBarre.getNom_produit());
            pst.setString(2, codeBarre.getIngredients());
            pst.setString(3, codeBarre.getMarque());
            pst.setInt(4, codeBarre.getFk_utilisateur());
            pst.setInt(6, id_Code);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Code-barre mis à jour avec succès pour l'ID " + id_Code);
            } else {
                System.out.println("Aucune mise à jour effectuée. ID introuvable.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL (mise à jour) : " + e.getMessage());
        }
    }
    // Obtenir tous les CodeBarre
    public List<CodeBarre> getAllData() {
        List<CodeBarre> result = new ArrayList<>();
        String requete = "SELECT * FROM codebarre";
        try (Statement st = MyConnection.getInstance().getCnx().createStatement();
             ResultSet rs = st.executeQuery(requete)) {

            while (rs.next()) {
                CodeBarre codeBarre = new CodeBarre();
                codeBarre.setId_Code(rs.getInt("id_code"));
                codeBarre.setNom_produit(rs.getString("nom_produit"));
                codeBarre.setIngredients(rs.getString("ingredients"));
                codeBarre.setMarque(rs.getString("marque"));
                codeBarre.setFk_utilisateur(rs.getInt("fk_utilisateur"));
                codeBarre.setRef_produit(rs.getString("ref_produit"));
                codeBarre.setCalories(rs.getDouble("calories"));
                codeBarre.setProteines(rs.getDouble("proteines"));
                codeBarre.setGlucides(rs.getDouble("glucides"));
                codeBarre.setLipides(rs.getDouble("lipides"));

                result.add(codeBarre);
            }

            if (result.isEmpty()) {
                System.out.println("Aucun code-barre trouvé.");
            }

        } catch (SQLException e) {
            System.out.println("Erreur SQL lors de la récupération des données : " + e.getMessage());
        }

        return result;
    }
}
