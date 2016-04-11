package com.kaligrid.model;

import java.util.Comparator;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

public class Event {

    private final String title;
    private final EventType type;
    private final long startDateTime;
    private final long endDateTime;
    private final boolean isAllDayEvent;

    private Event(String title, EventType type, long eventStartDateTime, long eventEndDateTime, boolean isAllDayEvent) {
        this.title = title;
        this.type = type;
        this.startDateTime = eventStartDateTime;
        this.endDateTime = eventEndDateTime;
        this.isAllDayEvent = isAllDayEvent;
    }

    public String getTitle() {
        return title;
    }

    public EventType getType() {
        return type;
    }

    public long getStartDateTime() {
        return startDateTime;
    }

    public long getEndDateTime() {
        return endDateTime;
    }

    public boolean isAllDayEvent() {
        return isAllDayEvent;
    }

    public static class Builder {

        private final String title;
        private final EventType type;
        private final long startDateTime;
        private long endDateTime;
        private boolean isAllDayEvent;

        public Builder(String title, EventType type, long startDateTime) {
            this.title = title;
            this.type = type;
            this.startDateTime = startDateTime;
        }

        public Builder endDateTime(long endDateTime) {
            this.endDateTime = endDateTime;
            return this;
        }

        public Builder isAllDayEvent(boolean isAllDayEvent) {
            this.isAllDayEvent = isAllDayEvent;
            return this;
        }

        public Event build() {
            return new Event(title, type, startDateTime, endDateTime, isAllDayEvent);
        }
    }

    public static class EventStartDateComparator implements Comparator<Event> {

        @Override
        public int compare(Event lhs, Event rhs) {
            DateTime lhsDateTime = DateTime.forInstant(lhs.getStartDateTime(), TimeZone.getDefault());
            DateTime rhsDateTime = DateTime.forInstant(rhs.getStartDateTime(), TimeZone.getDefault());

            if (lhsDateTime.isSameDayAs(rhsDateTime)) {
                if (lhs.isAllDayEvent() && rhs.isAllDayEvent()) {
                    return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
                } else if (lhs.isAllDayEvent()) {
                    return -1;
                } else if (rhs.isAllDayEvent()) {
                    return 1;
                } else {
                    // Copy of Long.compare() method
                    return lhs.getStartDateTime() < rhs.getStartDateTime() ?
                            -1 : (lhs.getStartDateTime() == rhs.getStartDateTime() ? 0 : 1);
                }
            } else {
                // Copy of Long.compare() method
                return lhs.getStartDateTime() < rhs.getStartDateTime() ?
                        -1 : (lhs.getStartDateTime() == rhs.getStartDateTime() ? 0 : 1);
            }
        }
    }
}
