package interfaces;
import entities.event;
import java.util.ArrayList;
import java.util.List;


public interface IEvent<T> {

    public  void addEvent(event e);
    public  void removeEvent(int idEvent);
    public  void UpDateEvent(event e, int idEvent);
    public  List<T> displayAllEvent();
}
