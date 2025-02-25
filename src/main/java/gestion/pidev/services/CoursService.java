package gestion.pidev.services;

import gestion.pidev.entities.Cours;
import gestion.pidev.interfaces.IService;
import gestion.pidev.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoursService implements IService<Cours> {
    @Override
    public void addEntity(Cours cours) {
        try {
            String requete = "INSERT INTO cours(type, nom_cours, date, adresse_mail_coach, image) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setString(1, cours.getType());
            pst.setString(2, cours.getNom_cours());
            pst.setDate(3, new java.sql.Date(cours.getDate().getTime()));
            pst.setString(4, cours.getAdresse_mail_coach());
            pst.setBytes(5, cours.getImage());
            pst.executeUpdate();
            System.out.println("Cours ajouté");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteEntity(Cours cours) {
        try {
            String requete = "DELETE FROM cours WHERE id = ?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setInt(1, cours.getId());
            pst.executeUpdate();
            System.out.println("Cours supprimé avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du cours : " + e.getMessage());
        }
    }

    @Override
    public void updateEntity(int id, Cours cours) {
        try {
            String requete = "UPDATE cours SET type = ?, nom_cours = ?, date = ?, adresse_mail_coach = ?, image = ? WHERE id = ?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setString(1, cours.getType());
            pst.setString(2, cours.getNom_cours());
            pst.setDate(3, new java.sql.Date(cours.getDate().getTime()));
            pst.setString(4, cours.getAdresse_mail_coach());
            pst.setBytes(5, cours.getImage());
            pst.setInt(6, id);
            pst.executeUpdate();
            System.out.println("Cours mis à jour avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du cours : " + e.getMessage());
        }
    }

    @Override
    public List<Cours> getAllData() {
        List<Cours> result = new ArrayList<>();
        try {
            String requete = "SELECT * FROM cours";
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            ResultSet rs = st.executeQuery(requete);
            while (rs.next()) {
                Cours c = new Cours();
                c.setId(rs.getInt("id"));
                c.setType(rs.getString("type"));
                c.setNom_cours(rs.getString("nom_cours"));
                c.setDate(rs.getDate("date"));
                c.setAdresse_mail_coach(rs.getString("adresse_mail_coach"));
                c.setImage(rs.getBytes("image"));
                result.add(c);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }




}