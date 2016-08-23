package com.kaligrid.model.converter;

import android.content.Context;

import com.kaligrid.adapter.EventListItemAdapter;
import com.kaligrid.model.Event;
import com.kaligrid.model.eventlist.EventListDateHeaderItem;
import com.kaligrid.model.eventlist.EventListEventItem;
import com.kaligrid.model.eventlist.EventListViewSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

public class EventsToEventListViewSourceConverter {

    public static EventListViewSource convert(List<Event> events, Context context) {
        Map<DateTime, AllDayAndTimedEvents> eventsByDateAndType = getEventsByDateAndType(events);
        EventListViewSource eventListViewSource = new EventListViewSource();
        int firstItemIndex = -1;

        for (Map.Entry<DateTime, AllDayAndTimedEvents> entry : eventsByDateAndType.entrySet()) {
            DateTime date = entry.getKey();

            // First event that's today or later should be the first event shown.
            if ((firstItemIndex < 0) && date.gteq(DateTime.today(TimeZone.getDefault()))) {
                firstItemIndex = eventListViewSource.size();
            }

            eventListViewSource.addDateHeaderItem(new EventListDateHeaderItem(date, context));
            addAllDayEventListItems(eventListViewSource, entry.getValue().allDayEvents, context);
            addTimedEventListItems(eventListViewSource, entry.getValue().timedEvents, context);
        }

        eventListViewSource.setFirstVisibleItem(firstItemIndex);

        return eventListViewSource;
    }

    private static Map<DateTime, AllDayAndTimedEvents> getEventsByDateAndType(List<Event> events) {
        Map<DateTime, AllDayAndTimedEvents> eventsByDateAndType = new LinkedHashMap<>();

        for(Event event : events) {
            DateTime eventDate = DateTime.forInstant(event.getStartDateTime(), TimeZone.getDefault())
                    .truncate(DateTime.Unit.DAY);

            if (!eventsByDateAndType.containsKey(eventDate)) {
                eventsByDateAndType.put(eventDate, new AllDayAndTimedEvents());
            }
            eventsByDateAndType.get(eventDate).addEvent(event);
        }

        return eventsByDateAndType;
    }

    private static void addAllDayEventListItems(EventListViewSource eventListViewSource, List<Event> events, Context context) {
        boolean showAllDayText = true;
        for (Event event : events) {
            eventListViewSource.addEventItem(new EventListEventItem(event, context, showAllDayText));
            showAllDayText = false;
        }
    }

    private static void addTimedEventListItems(EventListViewSource eventListViewSource, List<Event> events, Context context) {
        boolean showEventTimeText = true;
        DateTime oldEventTime = new DateTime(1, 1, 1, 0, 0, 0, 0);

        for (Event event : events) {
            DateTime newEventTime = DateTime.forInstant(event.getStartDateTime(), TimeZone.getDefault());

            // Show event time text is event time is different.
            if (!oldEventTime.getHour().equals(newEventTime.getHour()) ||
                    !oldEventTime.getMinute().equals(newEventTime.getMinute())) {
                showEventTimeText = true;
            }

            eventListViewSource.addEventItem(new EventListEventItem(event, context, showEventTimeText));

            showEventTimeText = false;
            oldEventTime = newEventTime;
        }
    }

    private static class AllDayAndTimedEvents {

        private List<Event> allDayEvents;
        private List<Event> timedEvents;

        public AllDayAndTimedEvents() {
            allDayEvents = new ArrayList<>();
            timedEvents = new ArrayList<>();
        }

        public void addEvent(Event event) {
            if (event.isAllDayEvent()) {
                allDayEvents.add(event);
            } else {
                timedEvents.add(event);
            }
        }
    }
}
