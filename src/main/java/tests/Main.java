package tests;

import entities.event;
import entities.ticket;
import services.eventServices;
import services.ticketServices;
import tools.MyConnection;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {

        MyConnection conn1 = MyConnection.getInstance();
        eventServices es = new eventServices();
        ticketServices ts = new ticketServices();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Menu:");
            System.out.println("1. Ajouter Event");
            System.out.println("2. Afficher Event");
            System.out.println("3. Modifier Event");
            System.out.println("4. Supprimer Event");
            System.out.println("5. Ajouter Ticket");
            System.out.println("6. Afficher Ticket");
            System.out.println("7. Modifier Ticket");
            System.out.println("8. Supprimer Ticket");
            System.out.println("9. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    // Add Event
                    event event = getEventInput();
                    es.addEvent(event);
                    System.out.println("Event added successfully!");
                    break;
                case 2:
                    // Display Events
                    System.out.println(es.displayAllEvent());
                    break;
                case 3:
                    // Update Event
                    System.out.print("Enter Event ID to update: ");
                    int eventToUpdate = scanner.nextInt();
                    event event1 = getEventInput();
                    es.UpDateEvent(event1, eventToUpdate);
                    System.out.println("Event updated successfully!");
                    break;
                case 4:
                    // Delete Event
                    System.out.print("Enter Event ID to delete: ");
                    int eventIdToDelete = scanner.nextInt();
                    es.removeEvent(eventIdToDelete);
                    System.out.println("Event deleted successfully!");
                    break;
                case 5:
                    // Add Ticket
                    ticket ticket = getTicketInput();
                    ts.addTicket(ticket);
                    System.out.println("Ticket added successfully!");
                    break;
                case 6:
                    // Display Tickets
                    System.out.println(ts.displayAllTicket());
                    break;
                case 7:
                    // Update Ticket
                    System.out.print("Enter Ticket ID to update: ");
                    int ticketToUpdate = scanner.nextInt();
                    ticket ticket1 = getTicketInput();
                    ts.updateTicket(ticket1, ticketToUpdate);
                    System.out.println("Ticket updated successfully!");
                    break;
                case 8:
                    // Delete Ticket
                    System.out.print("Enter Ticket ID to delete: ");
                    int ticketIdToDelete = scanner.nextInt();
                    ts.removeTicket(ticketIdToDelete);
                    System.out.println("Ticket deleted successfully!");
                    break;
                case 9:
                    // Exit
                    System.out.println("Exiting program.");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 9.");
            }
        }
    }

    private static event getEventInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter event ID: ");
        int event_id = scanner.nextInt();
        System.out.print("Enter event name: ");
        String nomevent = scanner.next();
        System.out.print("Enter event type: ");
        String typeevent = scanner.next();
        System.out.print("Enter event start date: ");
        java.sql.Date dateDebut = Date.valueOf(scanner.next());
        System.out.print("Enter event end date: ");
        java.sql.Date dateFin = Date.valueOf(scanner.next());
        System.out.print("Enter event location: ");
        String lieu = scanner.next();

        return new event(event_id, nomevent, typeevent, dateDebut, dateFin, lieu);
    }

    private static ticket getTicketInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter ticket ID: ");
        int idticket = scanner.nextInt();
        System.out.print("Enter event ID for this ticket: ");
        int idevent = scanner.nextInt();
        System.out.print("Enter ticket price: ");
        int prix = scanner.nextInt();
        System.out.print("Enter ticket quantity: ");
        int quantite = scanner.nextInt();


        System.out.println("Debug - Ticket ID: " + idticket);
        System.out.println("Debug - Event ID: " + idevent);
        System.out.println("Debug - Price: " + prix);
        System.out.println("Debug - Quantity: " + quantite);

        return new ticket(idticket, idevent, prix, quantite);
    }
}
