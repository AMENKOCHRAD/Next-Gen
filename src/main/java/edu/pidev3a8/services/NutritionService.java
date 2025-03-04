package edu.pidev3a8.services;

import edu.pidev3a8.entities.Nutrition;
import edu.pidev3a8.interfaces.INutrition;
import edu.pidev3a8.tools.MyConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class NutritionService implements INutrition<Nutrition> {

    @Override
    public void addNutrition(Nutrition nutrition) {
        try {
            String requete = "INSERT INTO nutrition( poids, taille, sexe, imc ,id_utilisateur) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setDouble(1, nutrition.getPoids());
            pst.setDouble(2, nutrition.getTaille());
            pst.setString(3, nutrition.getSexe());
            pst.setDouble(4, nutrition.getImc()); // Ajout de l'IMC
            pst.setInt(5,nutrition.getId_utilisateur());
            pst.executeUpdate();
            System.out.println("Plan de nutrition ajouté avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur SQL (ajout) : " + e.getMessage());
        }
    }

    @Override
    public void deleteNutrition(Nutrition nutrition) {
        try {
            String requete = "DELETE FROM nutrition WHERE id_nut = ?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setInt(1, nutrition.getId_nut());

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Plan de nutrition supprimé avec succès !");
            } else {
                System.out.println("Aucune suppression effectuée. ID introuvable.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL (suppression) : " + e.getMessage());
        }
    }
    @Override
    public void updateNutrition(int id_nut, Nutrition nutrition) {
        try {
            // Requête SQL pour mettre à jour la nutrition, y compris id_utilisateur
            String requete = "UPDATE nutrition SET poids = ?, taille = ?, sexe = ?, imc = ?, id_utilisateur = ? WHERE id_nut = ?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);

            // Définir les valeurs des paramètres
            pst.setDouble(1, nutrition.getPoids());
            pst.setDouble(2, nutrition.getTaille());
            pst.setString(3, nutrition.getSexe());
            pst.setDouble(4, nutrition.getImc()); // Ajout de l'IMC
            pst.setInt(5, nutrition.getId_utilisateur()); // Ajout de l'ID utilisateur (clé étrangère)
            pst.setInt(6, id_nut); // ID de la nutrition à mettre à jour

            // Exécuter la mise à jour
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Plan de nutrition mis à jour avec succès pour l'ID " + id_nut);
            } else {
                System.out.println("Aucune mise à jour effectuée. ID introuvable.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL (mise à jour) : " + e.getMessage());
        }
    }

    @Override
    public List<Nutrition> getAllData() {
        List<Nutrition> result = new ArrayList<>();
        try {
            String requete = "SELECT * FROM nutrition";
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            ResultSet rs = st.executeQuery(requete);

            while (rs.next()) {
                Nutrition nutrition = new Nutrition();
                nutrition.setId_nut(rs.getInt("id_nut"));
                nutrition.setPoids(rs.getDouble("poids"));
                nutrition.setTaille(rs.getDouble("taille"));
                nutrition.setSexe(rs.getString("sexe"));
                nutrition.setImc(rs.getDouble("imc")); // Récupération de l'IMC
                nutrition.setId_utilisateur(rs.getInt("id_utilisateur"));
                result.add(nutrition);
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
        return result;
    }

    public List<Nutrition> getDataByUserId(int userId) {
        List<Nutrition> result = new ArrayList<>();
        try {
            String requete = "SELECT * FROM nutrition WHERE id_utilisateur = ?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Nutrition nutrition = new Nutrition();
                nutrition.setId_nut(rs.getInt("id_nut"));
                nutrition.setPoids(rs.getDouble("poids"));
                nutrition.setTaille(rs.getDouble("taille"));
                nutrition.setSexe(rs.getString("sexe"));
                nutrition.setImc(rs.getDouble("imc"));
                nutrition.setId_utilisateur(rs.getInt("id_utilisateur"));
                result.add(nutrition);
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL (getDataByUserId) : " + e.getMessage());
        }
        return result;
    }

}