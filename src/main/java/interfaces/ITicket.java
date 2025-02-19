package interfaces;

import java.util.ArrayList;
import java.util.List;
import entities.ticket;

public interface ITicket {
    public void addTicket(ticket t);
    public void removeTicket(int idTicket);
    public void updateTicket(ticket t, int idTicket);
    public List<ticket> displayAllTicket();
}
