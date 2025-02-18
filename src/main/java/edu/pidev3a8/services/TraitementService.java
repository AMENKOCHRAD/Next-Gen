package edu.pidev3a8.services;

import edu.pidev3a8.entities.TraitementReclamation;
import edu.pidev3a8.interfaces.ITraitement;
import edu.pidev3a8.tools.MyConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class    TraitementService implements ITraitement<TraitementReclamation> {
    @Override
    public void addEntityTraitement(TraitementReclamation traitementReclamation) {
        try {
            String requete = "INSERT INTO TraitementReclamation (id_reclamation, id_admin, date_prise_en_charge, statut, commentaire, priorite) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setInt(1, traitementReclamation.getReclamationId());
            pst.setInt(2, traitementReclamation.getAdminId());
            pst.setObject(3, traitementReclamation.getDatePriseEnCharge());
            pst.setString(4, traitementReclamation.getStatut().toString());
            pst.setString(5, traitementReclamation.getCommentaire());
            pst.setString(6, traitementReclamation.getPriorite().toString()); // Ajouter la priorité
            pst.executeUpdate();
            System.out.println("Traitement ajouté avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du traitement : " + e.getMessage());
        }
    }

    @Override
    public void deleteEntityTraitement(TraitementReclamation traitementReclamation) {
        try {
            String requete = "DELETE FROM TraitementReclamation WHERE id_traitement = ?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setInt(1, traitementReclamation.getId_traitement());
            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Traitement supprimé avec succès !");
            } else {
                System.out.println("Aucun traitement trouvé avec l'ID : " + traitementReclamation.getId());
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du traitement : " + e.getMessage());
        }

    }

    @Override
    public void updateEntityTraitement(int id_traitement, TraitementReclamation traitementReclamation) {
        try {
            // Requête SQL pour mettre à jour le statut, le commentaire et la priorité
            String requete = "UPDATE TraitementReclamation SET statut = ?, commentaire = ?, priorite = ? WHERE id_traitement = ?";

            // Préparation de la requête
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setString(1, traitementReclamation.getStatut().toString()); // Nouveau statut
            pst.setString(2, traitementReclamation.getCommentaire()); // Nouveau commentaire
            pst.setString(3, traitementReclamation.getPriorite().toString()); // Nouvelle priorité
            pst.setInt(4, id_traitement); // ID du traitement à mettre à jour

            // Exécution de la requête
            int rowsUpdated = pst.executeUpdate();

            // Vérification du succès de la mise à jour
            if (rowsUpdated > 0) {
                System.out.println("Traitement mis à jour avec succès !");
            } else {
                System.out.println("Aucun traitement trouvé avec l'ID : " + id_traitement);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du traitement : " + e.getMessage());
        }
    }


    @Override
    public List<TraitementReclamation> getAllDataTraitement() {
        List<TraitementReclamation> result = new ArrayList<>();
        try {
            String requete = "SELECT * FROM TraitementReclamation";
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            ResultSet rs = st.executeQuery(requete);
            while (rs.next()) {
                TraitementReclamation traitement = new TraitementReclamation();
                traitement.setId_traitement(rs.getInt("id_traitement"));
                traitement.setReclamationId(rs.getInt("id_reclamation"));
                traitement.setAdminId(rs.getInt("id_admin"));
                traitement.setDatePriseEnCharge(rs.getTimestamp("date_prise_en_charge").toLocalDateTime());
                traitement.setStatut(TraitementReclamation.StatutTraitement.valueOf(rs.getString("statut")));
                traitement.setCommentaire(rs.getString("commentaire"));
                traitement.setPriorite(TraitementReclamation.Priorite.valueOf(rs.getString("priorite"))); // Récupérer la priorité
                result.add(traitement);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des traitements : " + e.getMessage());
        }
        return result;
    }   }

