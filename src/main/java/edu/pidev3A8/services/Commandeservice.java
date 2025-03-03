package edu.pidev3A8.services;

import edu.pidev3A8.entities.Commande;
import edu.pidev3A8.entities.StatutCommande;
import edu.pidev3A8.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Commandeservice {

    public static void addCommande(Commande commande) {
        try {
            Statement st = MyConnection.getInstance().getCnx().createStatement();

            // Conversion de la date
            java.sql.Date sqlDate = new java.sql.Date(commande.getDate_commande().getTime());

            String requete = "INSERT INTO commande (id_produit, id_client, quantite, prix_total, date_commande, statut) " +
                    "VALUES ('" + commande.getId_produit() + "', '" + commande.getId_client() + "', '" +
                    commande.getQuantite() + "', '" + commande.getPrix_total() + "', '" + sqlDate + "', '" + commande.getStatut() + "')";

            st.executeUpdate(requete);
            System.out.println("Commande ajoutée !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la commande : " + e.getMessage());
        }
    }

    public void deleteCommande(int id_commande) {
        try {
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            String requete = "DELETE FROM commande WHERE id_commande = " + id_commande;
            int rowsDeleted = st.executeUpdate(requete);
            if (rowsDeleted > 0) {
                System.out.println("Commande supprimée !");
            } else {
                System.out.println("Commande introuvable !");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    // 3. **Lister toutes les commandes avec Statement**

    // 3. **Lister toutes les commandes avec Statement**
    public List<Commande> getAllCommandes() {
        List<Commande> result = new ArrayList<>();
        String requete = "SELECT * FROM commande";

        try (Statement st = MyConnection.getInstance().getCnx().createStatement();
             ResultSet rs = st.executeQuery(requete)) {

            while (rs.next()) {
                // Récupération des valeurs depuis ResultSet
                int id_commande = rs.getInt("id_commande");
                int id_produit = rs.getInt("id_produit");  // Assurez-vous que l'id_produit est un int
                int id_client = rs.getInt("id_client");
                int quantite = rs.getInt("quantite");
                double prix_total = rs.getDouble("prix_total");
                Date date_commande = rs.getDate("date_commande");
                StatutCommande statut = StatutCommande.valueOf(rs.getString("statut")); // Statut comme Enum

                // Création de l'objet Commande et ajout dans la liste
                Commande c = new Commande(id_produit, id_client, quantite, prix_total, date_commande, statut);
                c.setId_commande(id_commande);
                result.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des commandes : " + e.getMessage());
        }

        return result;
    }



    public void updateCommande(int id_commande, Commande commande) {
        try {
            // Requête SQL pour mettre à jour les informations de la commande
            String requete = "UPDATE commande SET id_produit = ?, id_client = ?, quantite = ?, prix_total = ?, date_commande = ?, statut = ? WHERE id_commande = ?";

            // Préparation de la requête avec les valeurs fournies par l'objet commande
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setInt(1, commande.getId_produit());  // Mise à jour de l'id_produit
            pst.setInt(2, commande.getId_client());   // Mise à jour de l'id_client
            pst.setInt(3, commande.getQuantite());    // Mise à jour de la quantité
            pst.setDouble(4, commande.getPrix_total());  // Mise à jour du prix total
            pst.setDate(5, new java.sql.Date(commande.getDate_commande().getTime())); // Mise à jour de la date_commande
            pst.setString(6, commande.getStatut().name()); // Mise à jour du statut (en tant qu'Enum)
            pst.setInt(7, id_commande);  // Utilisation de l'ID pour spécifier quelle commande mettre à jour

            // Exécution de la mise à jour
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Commande mise à jour avec succès pour l'ID " + id_commande);
            } else {
                System.out.println("Aucune mise à jour effectuée. ID introuvable.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL (mise à jour commande) : " + e.getMessage());
        }
    }

}
