package com.kaligrid.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

public class Event {

    private final String user;
    private final String title;
    private final EventType type;
    private final long startDateTime;
    private final long endDateTime;
    private final boolean isAllDayEvent;
    private final boolean isSelfIncluded;
    private final List<String> recipients;

    private Event(String user, String title, EventType type, long eventStartDateTime, long eventEndDateTime,
                  boolean isAllDayEvent, boolean isSelfIncluded, List<String> recipients) {
        this.user = user;
        this.title = title;
        this.type = type;
        this.startDateTime = eventStartDateTime;
        this.endDateTime = eventEndDateTime;
        this.isAllDayEvent = isAllDayEvent;
        this.isSelfIncluded = isSelfIncluded;
        this.recipients = recipients;
    }

    public String getUser() {
        return user;
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

    public boolean isSelfIncluded() {
        return isSelfIncluded;
    }

    public List<String> getRecipients() {
        return Collections.unmodifiableList(recipients);
    }

    public static class Builder {

        private final String user;
        private final String title;
        private final EventType type;
        private final long startDateTime;
        private long endDateTime;
        private boolean isAllDayEvent;
        private boolean isSelfIncluded;
        private List<String> recipients;

        public Builder(String user, String title, EventType type, long startDateTime) {
            this.user = user;
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

        public Builder isSelfIncluded(boolean isSelfIncluded) {
            this.isSelfIncluded = isSelfIncluded;
            return this;
        }

        public Builder recipients(List<String> recipients) {
            this.recipients = recipients;
            return this;
        }

        public Builder recipients(String... recipients) {
            this.recipients = Arrays.asList(recipients);
            return this;
        }

        public Event build() {
            if (recipients == null) {
                recipients = Collections.emptyList();
            }
            return new Event(user, title, type, startDateTime, endDateTime, isAllDayEvent, isSelfIncluded, recipients);
        }
    }

    public static class EventStartDateComparator implements Comparator<Event> {

        /**
         * Sort order: all day events by title and then events by start time.
         */
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
