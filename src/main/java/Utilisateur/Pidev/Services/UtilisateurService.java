package Utilisateur.Pidev.Services;

import Utilisateur.Pidev.Entites.Utilisateur;
import Utilisateur.Pidev.Interfaces.IService;
import Utilisateur.Pidev.Tools.MyConnection;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurService implements IService<Utilisateur> {


    @Override
    public void addEntity(Utilisateur utilisateur) {
        try {
            if (!isValidEmail(utilisateur.getEmail())) {
                System.out.println("Invalid email format");
                return;
            }

            if (!isValidPhoneNumber(utilisateur.getNumTel())) {
                System.out.println("Invalid phone number format");
                return;
            }

            String requete = "INSERT INTO utilisateur (email, mdp, nom, prenom, dateNai, numTel, genre, adresse, role, salaire, image, banned) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = MyConnection.getInstance().getCnx().prepareStatement(requete);
            ps.setString(1, utilisateur.getEmail());
            ps.setString(2, utilisateur.getMdp());
            ps.setString(3, utilisateur.getNom());
            ps.setString(4, utilisateur.getPrenom());
            ps.setDate(5, new java.sql.Date(utilisateur.getDateNai().getTime()));
            ps.setInt(6, utilisateur.getNumTel());
            ps.setString(7, utilisateur.getGenre());
            ps.setString(8, utilisateur.getAdresse());
            ps.setString(9, utilisateur.getRole().name());
            ps.setFloat(10, utilisateur.getSalaire());
            ps.setString(11, utilisateur.getImage());
            ps.setBoolean(12, utilisateur.isBanned());

            ps.executeUpdate();
            System.out.println("Utilisateur ajouté");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteEntity(Utilisateur utilisateur) {
        try {
            String requete = "DELETE FROM utilisateur WHERE id = " + utilisateur.getId();
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            st.executeUpdate(requete);
            System.out.println("Utilisateur supprimé avec succès");

        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de l'utilisateur: " + e.getMessage());
        }
    }

    @Override
    public void updateEntity(int id, Utilisateur utilisateur) {
        try {
            java.sql.Date sqlDate = new java.sql.Date(utilisateur.getDateNai().getTime());
            // Create the SQL UPDATE query with parameters
            String requete = "UPDATE utilisateur SET " +
                    "email = ?, " +
                    "mdp = ?, " +
                    "nom = ?, " +
                    "prenom = ?, " +
                    "dateNai = ?, " +
                    "numTel = ?, " +
                    "genre = ?, " +
                    "adresse = ?, " +
                    "role = ?, " +
                    "salaire = ?, " +
                    "image = ?, " +
                    "banned = ? " +
                    "WHERE id = ?";

            // Create a PreparedStatement
            PreparedStatement ps = MyConnection.getInstance().getCnx().prepareStatement(requete);

            // Set parameters for the PreparedStatement
            ps.setString(1, utilisateur.getEmail());
            ps.setString(2, utilisateur.getMdp());

            ps.setString(3, utilisateur.getNom());
            ps.setString(4, utilisateur.getPrenom());
            ps.setDate(5, sqlDate);
            ps.setInt(6, utilisateur.getNumTel());
            ps.setString(7, utilisateur.getGenre());
            ps.setString(8, utilisateur.getAdresse());
            ps.setString(9, utilisateur.getRole().name()); // Convertir l'enum en String
            ps.setFloat(10, utilisateur.getSalaire());
            ps.setString(11, utilisateur.getImage());
            ps.setBoolean(12, utilisateur.isBanned());
            ps.setInt(13, id);


            // Execute the query
            ps.executeUpdate();
            System.out.println("Utilisateur mis à jour avec succès");

        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage());
        }

    }

    @Override
    public List<Utilisateur> getAllData() {

        List<Utilisateur> result = new ArrayList<>();

        // SQL query to select all data from the 'utilisateur' table
        String requete = "SELECT * FROM utilisateur";

        // Use try-with-resources to ensure the Statement and ResultSet are closed automatically
        try (Connection conn = MyConnection.getInstance().getCnx();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(requete)) {

            // Iterate through the ResultSet and populate the list
            while (rs.next()) {
                Utilisateur u = new Utilisateur();
                u.setId(rs.getInt("id")); // Use column name for clarity
                u.setEmail(rs.getString("email"));
                u.setMdp(rs.getString("mdp"));
                u.setNom(rs.getString("nom"));
                u.setPrenom(rs.getString("prenom"));
                java.sql.Date sqlDate = rs.getDate("dateNai");
                u.setDateNai(new java.util.Date(sqlDate.getTime()));// Assuming dateNai is a java.sql.Date
                u.setNumTel(rs.getInt("numTel"));
                u.setGenre(rs.getString("genre"));
                u.setAdresse(rs.getString("adresse"));
                u.setRole(Utilisateur.Role.valueOf(rs.getString("role"))); // Convertir la String en enum
                u.setSalaire(rs.getFloat("salaire"));
                u.setImage(rs.getString("image"));
                u.setBanned(rs.getBoolean("banned"));
                result.add(u);
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des données: " + e.getMessage());
        }

        return result;
    }

    @Override
    public List<Utilisateur> getAllData2() {
        List<Utilisateur> result = new ArrayList<>();
        String requete = "SELECT id, email, mdp, nom, prenom, dateNai, numTel, genre, adresse, role, image, banned FROM utilisateur";

        try (Connection conn = MyConnection.getInstance().getCnx();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(requete)) {

            while (rs.next()) {
                Utilisateur u = new Utilisateur();
                u.setId(rs.getInt("id"));
                u.setEmail(rs.getString("email"));
                u.setMdp(rs.getString("mdp"));
                u.setNom(rs.getString("nom"));
                u.setPrenom(rs.getString("prenom"));

                // Gestion des dates invalides
                java.sql.Date sqlDate = rs.getDate("dateNai");
                if (sqlDate != null && sqlDate.toString().equals("0000-00-00")) {
                    u.setDateNai(null); // ou définir une date par défaut
                } else {
                    u.setDateNai(sqlDate != null ? new java.util.Date(sqlDate.getTime()) : null);
                }

                u.setNumTel(rs.getInt("numTel"));
                u.setGenre(rs.getString("genre"));
                u.setAdresse(rs.getString("adresse"));
                u.setRole(Utilisateur.Role.valueOf(rs.getString("role")));

                String imageUrl = rs.getString("image");
                System.out.println("Loading image URL from database: " + imageUrl); // Debug line
                u.setImage(imageUrl);
                u.setBanned(rs.getBoolean("banned"));
                result.add(u);
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des données: " + e.getMessage());
        }

        return result;
    }





    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    // Phone number validation method
    public static boolean isValidPhoneNumber(int phoneNumber) {
        // Convert the phone number to a string for regex validation
        String phoneNumberStr = String.valueOf(phoneNumber);
        String phoneRegex = "^[+]?[0-9]{8}$"; // Adjust the regex based on your requirements
        return phoneNumberStr.matches(phoneRegex);
    }

}



