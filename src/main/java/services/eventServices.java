package services;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import entities.event;
import tools.MyConnection;
import interfaces.IEvent;

public class eventServices implements IEvent {

    private final Connection connection;

    public eventServices() {
        connection = MyConnection.getInstance().getConnection();
    }

    @Override
    public void addEvent(event e) {
        String req = "INSERT INTO event (nom, type, date_debut, date_fin, lieu) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getNom());
            ps.setString(2, e.getType());
            ps.setDate(3, new java.sql.Date(e.getDateDebut().getTime()));
            ps.setDate(4, new java.sql.Date(e.getDateFin().getTime()));
            ps.setString(5, e.getLieu());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                e.setIdEvent(rs.getInt(1));
            }
            System.out.println("Événement ajouté avec ID : " + e.getIdEvent());
        } catch (SQLException ex) {
            System.out.println("Erreur d'ajout de l'événement : " + ex.getMessage());
        }
    }

    @Override
    public void removeEvent(int idEvent) {
        String req = "DELETE FROM event WHERE idEvent = ?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, idEvent);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Event supprimé avec succès !");
            } else {
                System.out.println("Attention !! Aucun event avec cet ID.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void UpDateEvent(event e, int idEvent) {
        String req = "UPDATE event SET nom = ?, type = ?, date_debut = ?, date_fin = ?, lieu = ? WHERE idEvent = ?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setString(1, e.getNom());
            pst.setString(2, e.getType());
            pst.setDate(3, new java.sql.Date(e.getDateDebut().getTime()));
            pst.setDate(4, new java.sql.Date(e.getDateFin().getTime()));
            pst.setString(5, e.getLieu());
            pst.setInt(6, idEvent);
            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Event mis à jour avec succès !");
            } else {
                System.out.println("Aucun event trouvé avec cet ID.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public List<event> displayAllEvent() {
        List<event> events = new ArrayList<>();
        String query = "SELECT * FROM event";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                event e = new event();
                e.setIdEvent(rs.getInt("idevent"));
                e.setNom(rs.getString("nom"));
                e.setType(rs.getString("type"));

                // Gestion des dates invalides
                Date dateDebut = rs.getDate("date_debut");
                if (rs.wasNull()) {
                    dateDebut = null; // ou une date par défaut, par exemple : new Date(0)
                }
                e.setDateDebut(dateDebut);

                Date dateFin = rs.getDate("date_fin");
                if (rs.wasNull()) {
                    dateFin = null; // ou une date par défaut, par exemple : new Date(0)
                }
                e.setDateFin(dateFin);

                e.setLieu(rs.getString("lieu"));

                events.add(e);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return events;
    }
}