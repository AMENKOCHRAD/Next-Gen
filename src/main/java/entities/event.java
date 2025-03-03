package entities;

import java.util.Date;
import java.util.List;

public class Event {
    private int idEvent;
    private String nom;
    private String type;
    private String adresse;
    private Date dateDebut;
    private Date dateFin;
    private List<Ticket> tickets; // Relation OneToMany

    public Event(int idEvent, String nom, String type,String adresse, Date dateDebut, Date dateFin, List<Ticket> tickets) {
        this.idEvent = idEvent;
        this.nom = nom;
        this.type = type;
        this.adresse = adresse;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.tickets = tickets;
    }

    // Getters & Setters
    public int getIdEvent() { return idEvent; }
    public void setIdEvent(int idEvent) { this.idEvent = idEvent; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public Date getDateDebut() { return dateDebut; }
    public void setDateDebut(Date dateDebut) { this.dateDebut = dateDebut; }

    public Date getDateFin() { return dateFin; }
    public void setDateFin(Date dateFin) { this.dateFin = dateFin; }

    public List<Ticket> getTickets() { return tickets; }
    public void setTickets(List<Ticket> tickets) { this.tickets = tickets; }

    public void addTicket(Ticket ticket) {
        this.tickets.add(ticket);
    }
}
