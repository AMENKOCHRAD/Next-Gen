package edu.pidev3a8.services;

import edu.pidev3a8.entities.Event;
import edu.pidev3a8.interfaces.IEventDAO;
import edu.pidev3a8.tools.MyConnection;

import java.util.ArrayList;
import java.util.List;


public class EventServices implements IEventDAO {
    private final Connection connection;

    public EventServices() {
        this.connection = MyConnection.getInstance().getConnection();
    }

    @Override
    public void addEvent(Event event) {
        String sql = "INSERT INTO event (nom, type, adresse, dateDebut, dateFin) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, event.getNom());
            stmt.setString(2, event.getType());
            stmt.setString(3, event.getAdresse());
            stmt.setDate(4, new java.sql.Date(event.getDateDebut().getTime()));
            stmt.setDate(5,  new java.sql.Date(event.getDateFin().getTime()));

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                event.setIdEvent(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de l'événement : " + e.getMessage());
        }
    }

    @Override
    public void updateEvent(Event event) {
        String sql = "UPDATE event SET nom=?, type=?, adresse=?, dateDebut=?, dateFin=? WHERE idEvent=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, event.getNom());
            stmt.setString(2, event.getType());
            stmt.setString(3, event.getAdresse());
            stmt.setDate(4, new java.sql.Date(event.getDateDebut().getTime()));
            stmt.setDate(5, new java.sql.Date(event.getDateFin().getTime()));
            stmt.setInt(6, event.getIdEvent());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour de l'événement : " + e.getMessage());
        }
    }

    @Override
    public void deleteEvent(int idEvent) {
        String sql = "DELETE FROM event WHERE idEvent=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idEvent);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de l'événement : " + e.getMessage());
        }
    }

    @Override
    public List<Event> getAllEvents() {
        String query = "SELECT * FROM events LEFT JOIN tickets ON events.idEvent = tickets.idEvent";

        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM event";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                events.add(new Event(
                        rs.getInt("idEvent"),
                        rs.getString("nom"),
                        rs.getString("type"),
                        rs.getString("adresse"),
                        rs.getDate("dateDebut"),
                        rs.getDate("dateFin"),
                        null
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des événements : " + e.getMessage());
        }
        return events;
    }




}
