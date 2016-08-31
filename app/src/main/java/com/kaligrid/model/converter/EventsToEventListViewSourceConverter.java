package com.kaligrid.model.converter;

import android.content.Context;

import com.kaligrid.model.Event;
import com.kaligrid.model.eventlist.EventListDateHeaderItem;
import com.kaligrid.model.eventlist.EventListEventItem;
import com.kaligrid.model.eventlist.EventListViewSource;
import com.kaligrid.util.DateTimeUtil;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import hirondelle.date4j.DateTime;

public class EventsToEventListViewSourceConverter {

    public static EventListViewSource convert(List<Event> events, Context context) {
        Map<DateTime, CategorizedEvents> categorizedEvents = getCategorizedEvents(events);
        return convertToEventListViewSource(categorizedEvents, context);
    }

    private static Map<DateTime, CategorizedEvents> getCategorizedEvents(List<Event> events) {
        Map<DateTime, CategorizedEvents> categorizedEvents = new TreeMap<>();

        for(Event event: events) {
            DateTime eventStartDate = DateTimeUtil.dateOnly(event.getStartDateTime());
            DateTime eventEndDate = DateTimeUtil.dateOnly(event.getEndDateTime());
            List<DateTime> datesBetween = DateTimeUtil.daysBetween(eventStartDate, eventEndDate);

            for (DateTime date : datesBetween) {
                if (!categorizedEvents.containsKey(date)) {
                    categorizedEvents.put(date, new CategorizedEvents());
                }

                if (event.isAllDayEvent()) {
                    categorizedEvents.get(date).allDayEvents.add(event);
                } else if (event.isEndingSameDay()) {
                    categorizedEvents.get(date).timedEvents.add(event);
                } else {
                    categorizedEvents.get(date).continuedTimedEvents.add(event);
                }
            }
        }

        return categorizedEvents;
    }

    private static EventListViewSource convertToEventListViewSource(Map<DateTime, CategorizedEvents> categorizedEvents, Context context) {
        EventListViewSource eventListViewSource = new EventListViewSource();
        int firstItemIndex = -1;
        DateTime today = DateTimeUtil.today();

        for (Map.Entry<DateTime, CategorizedEvents> entry : categorizedEvents.entrySet()) {
            DateTime date = entry.getKey();

            // First event that's today or later should be the first event shown.
            if ((firstItemIndex < 0) && date.gteq(today)) {
                firstItemIndex = eventListViewSource.size();
            }

            eventListViewSource.addDateHeaderItem(new EventListDateHeaderItem(date, context));
            addAllDayEventListItems(eventListViewSource, date, entry.getValue().allDayEvents, context);
            addContinuedTimedEventListItems(eventListViewSource, date, entry.getValue().continuedTimedEvents, context);
            addTimedEventListItems(eventListViewSource, date, entry.getValue().timedEvents, context);
        }

        eventListViewSource.setFirstVisibleItem(firstItemIndex);

        return eventListViewSource;
    }

    private static void addAllDayEventListItems(EventListViewSource source, DateTime date, Set<Event> events, Context context) {
        boolean showAllDayText = true;
        for (Event event : events) {
            source.addEventItem(new EventListEventItem(event, context, date, showAllDayText));
            showAllDayText = false;
        }
    }

    private static void addContinuedTimedEventListItems(EventListViewSource eventListViewSource, DateTime date, Set<Event> events, Context context) {
        for (Event event : events) {
            eventListViewSource.addEventItem(new EventListEventItem(event, context, date, false));
        }
    }

    private static void addTimedEventListItems(EventListViewSource source, DateTime date, Set<Event> events, Context context) {
        boolean showEventTimeText = true;
        DateTime oldEventTime = new DateTime(1, 1, 1, 0, 0, 0, 0);

        for (Event event : events) {
            DateTime newEventTime = DateTimeUtil.forInstant(event.getStartDateTime());

            // Show event time text is event time is different.
            if (!oldEventTime.getHour().equals(newEventTime.getHour()) ||
                    !oldEventTime.getMinute().equals(newEventTime.getMinute())) {
                showEventTimeText = true;
            }

            source.addEventItem(new EventListEventItem(event, context, date, showEventTimeText));

            showEventTimeText = false;
            oldEventTime = newEventTime;
        }
    }

    private static class CategorizedEvents {

        private Set<Event> allDayEvents;
        private Set<Event> continuedTimedEvents;
        private Set<Event> timedEvents;

        public CategorizedEvents() {
            // All-day events are sorted by event title.
            allDayEvents = new TreeSet<>(new Comparator<Event>() {
                @Override
                public int compare(Event lhs, Event rhs) {
                    return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
                }
            });

            // Continued timed-events are sorted by their end time.
            continuedTimedEvents = new TreeSet<>(new Comparator<Event>() {
                @Override
                public int compare(Event lhs, Event rhs) {
                    return Long.compare(lhs.getEndDateTime(), rhs.getEndDateTime());
                }
            });

            // Timed events are sorted by their start time.
            timedEvents = new TreeSet<>(new Comparator<Event>() {
                @Override
                public int compare(Event lhs, Event rhs) {
                    return Long.compare(lhs.getStartDateTime(), rhs.getStartDateTime());
                }
            });
        }
    }
}
