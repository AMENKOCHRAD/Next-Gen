package edu.pidev3a8.interfaces;

import edu.pidev3a8.entities.Ticket;
import java.util.List;

public interface ITicketDAO {
    void addTicket(Ticket ticket);
    void updateTicket(Ticket ticket);
    void deleteTicket(int idTicket);
    List<Ticket> getAllTickets();
}
