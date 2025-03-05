package edu.pidev3a8.interfaces;

import edu.pidev3a8.entities.Utilisateur;
import edu.pidev3a8.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public interface IService<T>
{
    public void addEntity(T t);
    public void deleteEntity(T t);
    public void updateEntity(int id,T t);
    public List<T> getAllData();

    default List<Utilisateur> getAllData2() {
        List<Utilisateur> result = new ArrayList<>();
        String requete = "SELECT id, email, mdp, nom, prenom, dateNai, numTel, genre, adresse, role, banned, image_user FROM user";

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
                u.setImage_user(rs.getBlob("image_user"));
                result.add(u);
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des données: " + e.getMessage());
        }

        return result;
    }

    default Utilisateur authenticateUser(String email, String password) {
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
                user.setImage_user(rs.getBlob("image_user")); // Set the image_user attribute

                return user;
            } else {
                System.out.println("Email or password incorrect!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    default Utilisateur findByEmail(String email) {
        String query = "SELECT * FROM user WHERE email = ?";
        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Utilisateur user = new Utilisateur();
                user.setId(rs.getInt("id"));
                user.setEmail(rs.getString("email"));
                user.setMdp(rs.getString("mdp"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setDateNai(rs.getDate("dateNai"));
                user.setNumTel(rs.getInt("numTel"));
                user.setGenre(rs.getString("genre"));
                user.setAdresse(rs.getString("adresse"));
                user.setRole(Utilisateur.Role.valueOf(rs.getString("role")));
                user.setSalaire(rs.getFloat("salaire"));
                user.setBanned(rs.getBoolean("banned"));
                user.setImage_user(rs.getBlob("image_user"));
                user.setAge(rs.getInt("age"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
