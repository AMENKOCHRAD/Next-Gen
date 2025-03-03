package edu.pidev3A8.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import edu.pidev3A8.entities.Produit;
import edu.pidev3A8.interfaces.IProduit;
import edu.pidev3A8.tools.MyConnection;
import edu.pidev3A8.entities.Etat;
import edu.pidev3A8.entities.Status;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
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
            String requete = "INSERT INTO produit(id_produit, nom_produit, type_produit, prix, etat, description, status, image) VALUES ('" + produit.getId_produit() + "','" + produit.getNom_produit() + "','" + produit.getType_produit() + "','" + produit.getPrix() + "','" + produit.getEtat() + "','" + produit.getDescription() + "','" + produit.getStatus() + "','" + produit.getImage() + "')";
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            st.executeUpdate(requete);
            System.out.println("Produit ajouté avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur SQL (ajout produit) : " + e.getMessage());
        }
    }

    @Override
    public void deleteProduit(Produit produit) {
        try {
            // Supprimer les commandes associées au produit
            String deleteCommandes = "DELETE FROM commande WHERE id_produit = " + produit.getId_produit();
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            st.executeUpdate(deleteCommandes);

            // Supprimer le produit
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
            String requete = "UPDATE produit SET nom_produit = ?, type_produit = ?, prix = ?, etat = ?, description = ?, status = ?, image = ? WHERE id_produit = ?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setString(1, produit.getNom_produit());
            pst.setString(2, produit.getType_produit());
            pst.setDouble(3, produit.getPrix());
            pst.setString(4, produit.getEtat().name());
            pst.setString(5, produit.getDescription());
            pst.setString(6, produit.getStatus().name());
            pst.setString(7, produit.getImage());
            pst.setInt(8, id);

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

    @Override
    public List<Produit> getAllData() {
        List<Produit> result = new ArrayList<>();
        String requete = "SELECT * FROM produit";

        try (Statement st = MyConnection.getInstance().getCnx().createStatement();
             ResultSet rs = st.executeQuery(requete)) {

            while (rs.next()) {
                Produit p = new Produit();
                p.setId_produit(rs.getInt("id_produit"));
                p.setNom_produit(rs.getString("nom_produit"));
                p.setType_produit(rs.getString("type_produit"));
                p.setPrix(rs.getDouble("prix"));
                p.setEtat(Etat.valueOf(rs.getString("etat")));
                p.setDescription(rs.getString("description"));
                p.setStatus(Status.valueOf(rs.getString("status")));
                p.setImage(rs.getString("image"));
                result.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des données : " + e.getMessage());
        }

        return result;
    }

    public List<Produit> searchProduitsByName(String name) {
        List<Produit> produits = new ArrayList<>();
        for (Produit produit : getAllData()) {
            if (produit.getNom_produit().toLowerCase().contains(name.toLowerCase())) {
                produits.add(produit);
            }
        }
        return produits;
    }

    // Méthode pour générer un code QR pour un produit
    public void generateQRCodeForProduct(Produit produit, String filePath) {
        String qrCodeText = "Produit ID: " + produit.getId_produit() + "\n"
                + "Nom: " + produit.getNom_produit() + "\n"
                + "Type: " + produit.getType_produit() + "\n"
                + "Prix: " + produit.getPrix() + "\n"
                + "État: " + produit.getEtat() + "\n"
                + "Description: " + produit.getDescription() + "\n"
                + "Statut: " + produit.getStatus();

        try {
            // Ensure the directory exists
            Path path = FileSystems.getDefault().getPath(filePath);
            File directory = path.getParent().toFile();
            if (!directory.exists()) {
                directory.mkdirs(); // Create the directory and any missing parent directories
            }

            // Generate the QR code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, 200, 200);

            // Save the QR code to the specified file path
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
            System.out.println("Code QR généré avec succès : " + filePath);
        } catch (WriterException | IOException e) {
            System.out.println("Erreur lors de la génération du code QR : " + e.getMessage());
        }
    }
    public void ajouterCommentaire(int produitId, String commentaire) {
        String query = "INSERT INTO commentaires (produit_id, commentaire) VALUES (?, ?)";
        try (PreparedStatement stmt = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            stmt.setInt(1, produitId);
            stmt.setString(2, commentaire);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur SQL (ajout commentaire) : " + e.getMessage());
        }
    }
    public List<String> getCommentaires(int produitId) {
        List<String> commentaires = new ArrayList<>();
        String query = "SELECT commentaire FROM commentaires WHERE produit_id = ?";
        try (PreparedStatement stmt = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            stmt.setInt(1, produitId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String commentaire = rs.getString("commentaire");
                commentaires.add(commentaire);
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL (récupération commentaires) : " + e.getMessage());
        }
        return commentaires;
    }
    public void ajouterLike(int produitId) {
        String query = "INSERT INTO likes (produit_id, likes) VALUES (?, 1) " +
                "ON DUPLICATE KEY UPDATE likes = likes + 1";
        try (PreparedStatement stmt = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            stmt.setInt(1, produitId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur SQL (ajout like) : " + e.getMessage());
        }
    }
    public int getLikes(int produitId) {
        String query = "SELECT likes FROM likes WHERE produit_id = ?";
        try (PreparedStatement stmt = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            stmt.setInt(1, produitId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("likes");
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL (récupération likes) : " + e.getMessage());
        }
        return 0;
    }
    public void supprimerCommentaire(int produitId, String commentaire) {
        String query = "DELETE FROM commentaires WHERE produit_id = ? AND commentaire = ?";
        try (PreparedStatement stmt = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            stmt.setInt(1, produitId);
            stmt.setString(2, commentaire);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur SQL (suppression commentaire) : " + e.getMessage());
        }
    }
}