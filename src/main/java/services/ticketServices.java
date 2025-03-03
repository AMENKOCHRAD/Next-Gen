package services;

import entities.Ticket;
import interfaces.ITicketDAO;
import tools.MyConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketServices implements ITicketDAO {
    private Connection connection;

    public TicketServices() {
        connection = MyConnection.getInstance().getConnection();
    }

    @Override
    public void addTicket(Ticket ticket) {
        String sql = "INSERT INTO ticket (prix, quantite, eventName,idEvent) VALUES (?, ?, ?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDouble(1, ticket.getPrix());
            stmt.setInt(2, ticket.getQuantite());
            stmt.setString(3, ticket.getEventName()); // Utiliser le nom de l'événement
            stmt.setInt(4, ticket.getidEvent());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                ticket.setIdTicket(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du ticket : " + e.getMessage());
        }
    }

    @Override
    public void updateTicket(Ticket ticket) {
        String sql = "UPDATE ticket SET prix=?, quantite=?, eventName=? WHERE idTicket=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, ticket.getPrix());
            stmt.setInt(2, ticket.getQuantite());
            stmt.setString(3, ticket.getEventName());
            stmt.setInt(4, ticket.getIdTicket());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du ticket : " + e.getMessage());
        }
    }

    @Override
    public void deleteTicket(int idTicket) {
        String sql = "DELETE FROM ticket WHERE idTicket=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idTicket);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du ticket : " + e.getMessage());
        }
    }

    @Override
    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM ticket";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Ticket ticket = new Ticket(
                        rs.getInt("idTicket"),
                        rs.getDouble("prix"),
                        rs.getInt("quantite"),
                        rs.getString("eventName"), // Récupérer le nom de l'événement
                        rs.getInt("idEvent") // Récupérer l'ID de l'événement
                );
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des tickets : " + e.getMessage());
        }
        return tickets;
    }

    //-----client
    public int getAvailableTickets(int idEvent) {
        String query = "SELECT SUM(quantite) FROM ticket WHERE idEvent = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, idEvent);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    public void updateTicketQuantity(int idEvent, int quantityChange) {
        String sql = "UPDATE ticket SET quantite = GREATEST(0, quantite + ?) WHERE idEvent = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quantityChange);
            stmt.setInt(2, idEvent);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour de la quantité de tickets : " + e.getMessage());
        }
    }

}
