package services;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import entities.ticket;
import entities.event;
import tools.MyConnection;
import interfaces.ITicket;

public class ticketServices implements ITicket {

    private final Connection connection;

    public ticketServices() {
        connection = MyConnection.getInstance().getConnection();
    }

    // ✅ Ajouter un ticket

    @Override
    public void addTicket(ticket t) {
        String checkEvent = "SELECT COUNT(*) FROM event WHERE idevent = ?";
        String requete = "INSERT INTO ticket (id_ticket, idEvent, prix, quantite) VALUES (?, ?, ?, ?)"; // Utiliser des paramètres

        try (PreparedStatement pstCheck = connection.prepareStatement(checkEvent);
             PreparedStatement pst = connection.prepareStatement(requete)) {

            // Debugging: Afficher les valeurs reçues
            System.out.println("Debug - Ticket object received:");
            System.out.println("Ticket ID: " + t.getIdticket());
            System.out.println("Event ID: " + t.getIdevent());
            System.out.println("Price: " + t.getPrix());
            System.out.println("Quantity: " + t.getQuantite());

            // Vérifier que l'ID de l'événement est valide (non égal à 0)
            if (t.getIdevent() <= 0) {
                System.out.println("Erreur : L'ID de l'événement doit être supérieur à 0. Valeur reçue: " + t.getIdevent());
                return;
            }

            // Vérifier l'existence de l'événement
            pstCheck.setInt(1, t.getIdevent());
            ResultSet rs = pstCheck.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1); // Nombre d'événements avec cet ID
                if (count == 0) {
                    System.out.println("Erreur : L'événement associé n'existe pas !");
                    return; // Événement inexistant
                }
            }

            // Ajouter le ticket si l'événement existe
            pst.setInt(1, t.getIdticket());   // ID du ticket
            pst.setInt(2, t.getIdevent());    // ID de l'événement
            pst.setDouble(3, t.getPrix());    // Prix du ticket
            pst.setInt(4, t.getQuantite());   // Quantité du ticket

            int rowsInserted = pst.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Ticket ajouté avec succès !");
            } else {
                System.out.println("L'insertion du ticket a échoué.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // ✅ Supprimer un ticket
    @Override
    public void removeTicket(int idTicket) {
        String req = "DELETE FROM ticket WHERE id_ticket = ?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, idTicket);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Ticket supprimé avec succès !");
            } else {
                System.out.println("Aucun ticket trouvé avec cet ID.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // ✅ Modifier un ticket
    @Override
    public void updateTicket(ticket t, int idTicket){
        String req = "UPDATE ticket SET idEvent = ?, prix = ?, quantite = ? WHERE id_ticket = ?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, t.getIdevent());
            pst.setDouble(2, t.getPrix());
            pst.setInt(3, t.getQuantite());
            pst.setInt(4, idTicket);
            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Ticket mis à jour avec succès !");
            } else {
                System.out.println("Aucun ticket trouvé avec cet ID.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // ✅ Afficher tous les tickets avec leurs événements
    @Override
    public List<ticket> displayAllTicket() {
        List<ticket> tickets = new ArrayList<>();
        String requete = "SELECT t.id_ticket, t.idEvent, t.prix, t.quantite, " +
                "e.nom, e.type, e.date_debut, e.date_fin, e.lieu " +
                "FROM ticket t " +
                "JOIN event e ON t.idEvent = e.idevent";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(requete)) {

            while (rs.next()) {
                ticket t = new ticket();
                t.setIdticket(rs.getInt("id_ticket"));
                t.setIdevent(rs.getInt("idEvent"));
                t.setPrix(rs.getInt("prix"));
                t.setQuantite(rs.getInt("quantite"));



                tickets.add(t);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return tickets;
    }
}
