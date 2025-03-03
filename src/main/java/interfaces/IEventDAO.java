package interfaces;

import entities.Event;
import java.util.List;

public interface IEventDAO {
    void addEvent(Event event);
    List<Event> getAllEvents();
    void updateEvent(Event event);
    void deleteEvent(int idEvent);
}
