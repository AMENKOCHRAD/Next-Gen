package edu.pidev3a8.entities;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import edu.pidev3a8.services.GoogleCalendarService;

import java.util.Date;

public class CalendarManager {
    public static void createEvent(String summary, String description, Date startDate, Date endDate) throws Exception {
        Calendar service = GoogleCalendarService.getCalendarService();

        Event event = new Event()
                .setSummary(summary) // Titre de l'événement
                .setDescription(description); // Description de l'événement

        // Dates de début et de fin
        EventDateTime start = new EventDateTime().setDateTime(new DateTime(startDate));
        EventDateTime end = new EventDateTime().setDateTime(new DateTime(endDate));
        event.setStart(start);
        event.setEnd(end);

        // Ajouter l'événement au calendrier principal
        String calendarId = "primary";
        service.events().insert(calendarId, event).execute();
    }
}