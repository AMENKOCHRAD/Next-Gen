package edu.pidev3A8.services;

import edu.pidev3A8.entities.Commande;
import edu.pidev3A8.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Commandeservice {

    public void addCommande(Commande commande) {
        try {
            String requete = "INSERT INTO commande (id_produit, id_client, quantite, adresse, adresseEmail, prixTotal) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setInt(1, commande.getId_produit());       // id_produit
            pst.setInt(2, commande.getId_client());        // id_client
            pst.setInt(3, commande.getQuantite());         // quantite
            pst.setString(4, commande.getAdresse());       // adresse
            pst.setString(5, commande.getAdresseEmail());  // adresseEmail
            pst.setDouble(6, commande.getPrixTotal());

            pst.executeUpdate();
            System.out.println("Commande ajoutée !");

            // Mettre à jour le statut du produit si nécessaire
            updateProduitStatus(commande.getId_produit());
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la commande : " + e.getMessage());
        }
    }

    public void deleteCommande(int id_commande) {
        try {
            // Récupérer l'ID du produit associé à la commande avant de la supprimer
            int idProduit = getProduitIdFromCommande(id_commande);

            // Supprimer la commande
            String requete = "DELETE FROM commande WHERE id_commande = ?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setInt(1, id_commande);

            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Commande supprimée !");

                // Mettre à jour le statut du produit si nécessaire
                if (idProduit > 0) {
                    updateProduitStatus(idProduit);
                }
            } else {
                System.out.println("Commande introuvable !");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    public void updateCommande(int id_commande, Commande commande) {
        try {
            // Requête SQL pour mettre à jour les informations de la commande
            String requete = "UPDATE commande SET id_produit = ?, id_client = ?, quantite = ?, adresse = ?, adresseEmail = ?, prixTotal = ? WHERE id_commande = ?";

            // Préparation de la requête avec les valeurs fournies par l'objet commande
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setInt(1, commande.getId_produit());  // Mise à jour de l'id_produit
            pst.setInt(2, commande.getId_client());   // Mise à jour de l'id_client
            pst.setInt(3, commande.getQuantite());
            pst.setString(4, commande.getAdresse());
            pst.setString(5, commande.getAdresseEmail());
            pst.setDouble(6, commande.getPrixTotal());
            pst.setInt(7, id_commande);  // Utilisation de l'ID pour spécifier quelle commande mettre à jour

            // Exécution de la mise à jour
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Commande mise à jour avec succès pour l'ID " + id_commande);

                // Mettre à jour le statut du produit si nécessaire
                updateProduitStatus(commande.getId_produit());
            } else {
                System.out.println("Aucune mise à jour effectuée. ID introuvable.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL (mise à jour commande) : " + e.getMessage());
        }
    }

    public List<Commande> getAllCommandes() {
        List<Commande> result = new ArrayList<>();
        String requete = "SELECT * FROM commande";

        try (Statement st = MyConnection.getInstance().getCnx().createStatement();
             ResultSet rs = st.executeQuery(requete)) {

            while (rs.next()) {
                // Récupération des valeurs depuis ResultSet
                int id_commande = rs.getInt("id_commande");
                int id_produit = rs.getInt("id_produit");
                int id_client = rs.getInt("id_client");
                int quantite = rs.getInt("quantite");
                String adresse = rs.getString("adresse");
                String adresseEmail = rs.getString("adresseEmail");
                Double prixTotal = rs.getDouble("prixTotal");

                // Création de l'objet Commande et ajout dans la liste
                Commande c = new Commande(id_produit, id_client, quantite, adresse, adresseEmail, prixTotal);
                c.setId_commande(id_commande);
                result.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des commandes : " + e.getMessage());
        }

        return result;
    }

    public Commande getProduitLePlusVendu() {
        Commande commandePlusVendue = null;
        String requete = "SELECT id_produit, SUM(quantite) AS total_quantite, SUM(prixTotal) AS total_prix " +
                "FROM commande " +
                "GROUP BY id_produit " +
                "ORDER BY total_quantite DESC " +
                "LIMIT 1";

        try (Statement st = MyConnection.getInstance().getCnx().createStatement();
             ResultSet rs = st.executeQuery(requete)) {

            if (rs.next()) {
                int id_produit = rs.getInt("id_produit");
                int total_quantite = rs.getInt("total_quantite");
                double total_prix = rs.getDouble("total_prix");

                // Créer un objet Commande pour représenter le produit le plus vendu
                commandePlusVendue = new Commande();
                commandePlusVendue.setId_produit(id_produit);
                commandePlusVendue.setQuantite(total_quantite);
                commandePlusVendue.setPrixTotal(total_prix);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du produit le plus vendu : " + e.getMessage());
        }

        return commandePlusVendue;
    }

    private void updateProduitStatus(int idProduit) {
        String countQuery = "SELECT COUNT(*) AS total FROM commande WHERE id_produit = ?";
        String updateQuery = "UPDATE produit SET status = 'VENDU' WHERE id_produit = ?";

        try (PreparedStatement countStmt = MyConnection.getInstance().getCnx().prepareStatement(countQuery);
             PreparedStatement updateStmt = MyConnection.getInstance().getCnx().prepareStatement(updateQuery)) {

            // Compter le nombre de commandes pour ce produit
            countStmt.setInt(1, idProduit);
            ResultSet rs = countStmt.executeQuery();

            if (rs.next()) {
                int totalCommandes = rs.getInt("total");
                if (totalCommandes >= 20) {
                    // Mettre à jour le statut du produit à VENDU
                    updateStmt.setInt(1, idProduit);
                    updateStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du statut du produit : " + e.getMessage());
        }
    }

    private int getProduitIdFromCommande(int idCommande) {
        String query = "SELECT id_produit FROM commande WHERE id_commande = ?";
        try (PreparedStatement stmt = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            stmt.setInt(1, idCommande);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_produit");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de l'ID du produit : " + e.getMessage());
        }
        return -1; // Retourner -1 si l'ID du produit n'est pas trouvé
    }
    public List<Commande> searchCommandesByQuantite(int quantite) {
        List<Commande> result = new ArrayList<>();
        String query = "SELECT * FROM commande WHERE quantite = ?";

        try (PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            pst.setInt(1, quantite);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                // Récupération des valeurs depuis ResultSet
                int id_commande = rs.getInt("id_commande");
                int id_produit = rs.getInt("id_produit");
                int id_client = rs.getInt("id_client");
                int quantiteResult = rs.getInt("quantite");
                String adresse = rs.getString("adresse");
                String adresseEmail = rs.getString("adresseEmail");
                Double prixTotal = rs.getDouble("prixTotal");

                // Création de l'objet Commande et ajout dans la liste
                Commande c = new Commande(id_produit, id_client, quantiteResult, adresse, adresseEmail, prixTotal);
                c.setId_commande(id_commande);
                result.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche des commandes par quantité : " + e.getMessage());
        }

        return result;
    }
    public List<Commande> sortCommandesByQuantite(boolean ascending) {
        List<Commande> result = new ArrayList<>();
        String query = "SELECT * FROM commande ORDER BY quantite " + (ascending ? "ASC" : "DESC");

        try (Statement st = MyConnection.getInstance().getCnx().createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                // Récupération des valeurs depuis ResultSet
                int id_commande = rs.getInt("id_commande");
                int id_produit = rs.getInt("id_produit");
                int id_client = rs.getInt("id_client");
                int quantite = rs.getInt("quantite");
                String adresse = rs.getString("adresse");
                String adresseEmail = rs.getString("adresseEmail");
                Double prixTotal = rs.getDouble("prixTotal");

                // Création de l'objet Commande et ajout dans la liste
                Commande c = new Commande(id_produit, id_client, quantite, adresse, adresseEmail, prixTotal);
                c.setId_commande(id_commande);
                result.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du tri des commandes par quantité : " + e.getMessage());
        }

        return result;
    }
}