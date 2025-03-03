package edu.pidev3A8.services;

import edu.pidev3A8.entities.Produit;
import edu.pidev3A8.entities.StatutCommande;
import edu.pidev3A8.interfaces.IProduit;
import edu.pidev3A8.tools.MyConnection;
import edu.pidev3A8.entities.Etat;
import edu.pidev3A8.entities.Status;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Produitservice implements IProduit<Produit> {

    @Override
    public void addProduit(Produit produit) {
        try {
            String requete = "INSERT INTO produit(id_produit, nom_produit, type_produit, prix, etat, description, status, image) VALUES ('" + produit.getId_produit() + "','" + produit.getNom_produit() + "','" + produit.getType_produit() + "','" + produit.getPrix() + "','" + produit.getEtat() + "','" + produit.getDescription() + "','" + produit.getStatus() + "','" +produit.getImage()+"')";
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            st.executeUpdate(requete);
            System.out.println("produitajoutée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteProduit(Produit produit) {
        try {

           String deleteCommandes = "DELETE FROM commande WHERE id_produit = " + produit.getId_produit();
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            st.executeUpdate(deleteCommandes);


            String deleteProduit = "DELETE FROM produit WHERE id_produit = " + produit.getId_produit();
            int rowsAffected = st.executeUpdate(deleteProduit);

            if (rowsAffected > 0) {
                System.out.println("Produit supprimé avec succès !");
            } else {
                System.out.println("Aucune suppression effectuée. ID introuvable.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL (suppression) : " + e.getMessage());
        }
    }





    @Override
    public void updateProduit(int id, Produit produit) {
        try {
            // Correction : ajout d'un espace avant WHERE
            String requete = "UPDATE produit SET nom_produit = ?, type_produit = ?, prix = ?, etat = ?, description = ?, status = ?, image = ? WHERE id_produit = ?";

            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setString(1, produit.getNom_produit());  // Mise à jour du nom du produit
            pst.setString(2, produit.getType_produit()); // Mise à jour du type du produit
            pst.setDouble(3, produit.getPrix());         // Mise à jour du prix
            pst.setString(4, produit.getEtat().name());  // Mise à jour de l'état du produit (en tant qu'Enum)
            pst.setString(5, produit.getDescription());  // Mise à jour de la description
            pst.setString(6, produit.getStatus().name()); // Mise à jour du statut (en tant qu'Enum)
            pst.setString(7, produit.getImage()); // Mise à jour de l'image

            pst.setInt(8, id);  // Utilisation de l'ID pour spécifier quel produit mettre à jour

            // Exécution de la mise à jour
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Produit mis à jour avec succès pour l'ID " + id);
            } else {
                System.out.println("Aucune mise à jour effectuée. ID introuvable.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL (mise à jour produit) : " + e.getMessage());
        }
    }

    public List<Produit> getAllData() {
        List<Produit> result = new ArrayList<>();
        String requete = "SELECT * FROM produit";

        try (Statement st = MyConnection.getInstance().getCnx().createStatement();
             ResultSet rs = st.executeQuery(requete)) {

            while (rs.next()) {
                Produit p = new Produit();
                p.setId_produit(rs.getInt("id_produit")); // Utilisation du nom de la colonne
                p.setNom_produit(rs.getString("nom_produit"));
                p.setType_produit(rs.getString("type_produit"));
                p.setPrix(rs.getDouble("prix"));
                p.setEtat(Etat.valueOf(rs.getString("etat")));
                p.setDescription(rs.getString("description"));

                p.setStatus(Status.valueOf(rs.getString("status"))); // Statut comme Enum
p.setImage(rs.getString("image"));
                result.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des données : " + e.getMessage());
        }

        return result;
    }


}



