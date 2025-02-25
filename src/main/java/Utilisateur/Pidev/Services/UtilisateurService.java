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

            String requete = "INSERT INTO user (email, mdp, nom, prenom, dateNai, numTel, genre, adresse, role, salaire, banned) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            ps.setBoolean(11, utilisateur.isBanned());

            ps.executeUpdate();
            System.out.println("Utilisateur ajouté");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteEntity(Utilisateur utilisateur) {
        String requete = "DELETE FROM user WHERE id = ?";
        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pst = conn.prepareStatement(requete)) {
            pst.setInt(1, utilisateur.getId());
            pst.executeUpdate();
            System.out.println("Utilisateur supprimé avec succès");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de l'utilisateur: " + e.getMessage());
        }
    }
    @Override
    public void updateEntity(int id, Utilisateur utilisateur) {
        try {
            java.sql.Date sqlDate = new java.sql.Date(utilisateur.getDateNai().getTime());

            String requete = "UPDATE user SET " +
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
                    "banned = ? " +
                    "WHERE id = ?";


            PreparedStatement ps = MyConnection.getInstance().getCnx().prepareStatement(requete);


            ps.setString(1, utilisateur.getEmail());
            ps.setString(2, utilisateur.getMdp());

            ps.setString(3, utilisateur.getNom());
            ps.setString(4, utilisateur.getPrenom());
            ps.setDate(5, sqlDate);
            ps.setInt(6, utilisateur.getNumTel());
            ps.setString(7, utilisateur.getGenre());
            ps.setString(8, utilisateur.getAdresse());
            ps.setString(9, utilisateur.getRole().name());
            ps.setFloat(10, utilisateur.getSalaire());
            ps.setBoolean(11, utilisateur.isBanned());
            ps.setInt(12, id);


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


        String requete = "SELECT * FROM user";


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
                java.sql.Date sqlDate = rs.getDate("dateNai");
                u.setDateNai(new java.util.Date(sqlDate.getTime()));// Assuming dateNai is a java.sql.Date
                u.setNumTel(rs.getInt("numTel"));
                u.setGenre(rs.getString("genre"));
                u.setAdresse(rs.getString("adresse"));
                u.setRole(Utilisateur.Role.valueOf(rs.getString("role"))); // Convertir la String en enum
                u.setSalaire(rs.getFloat("salaire"));
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
        String requete = "SELECT id, email, mdp, nom, prenom, dateNai, numTel, genre, adresse, role, banned FROM user";

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


    public static boolean isValidPhoneNumber(int phoneNumber) {

        String phoneNumberStr = String.valueOf(phoneNumber);
        String phoneRegex = "^[+]?[0-9]{8}$";
        return phoneNumberStr.matches(phoneRegex);
    }

    public void deleteEntityById(int id) {
        String requete = "DELETE FROM user WHERE id = ?";
        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pst = conn.prepareStatement(requete)) {
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Utilisateur supprimé avec succès");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de l'utilisateur: " + e.getMessage());
        }
    }
    public Utilisateur authenticateUser(String email, String password) {
        String query = "SELECT * FROM user WHERE LOWER(email) = LOWER(?) AND mdp = ?";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email.trim());
            ps.setString(2, password.trim());

            System.out.println("Executing query with email: " + email.trim() + " and password: " + password.trim()); // Debugging

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Utilisateur user = new Utilisateur();
                user.setId(rs.getInt("id"));
                user.setEmail(rs.getString("email"));
                user.setMdp(rs.getString("mdp"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setNumTel(rs.getInt("numTel"));
                user.setGenre(rs.getString("genre"));
                user.setAdresse(rs.getString("adresse"));
                user.setBanned(rs.getBoolean("banned"));
                user.setRole(Utilisateur.Role.valueOf(rs.getString("role")));

                return user;
            } else {
                System.out.println("Email or password incorrect!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


}



