package com.kaligrid.model;

import com.kaligrid.util.DateTimeUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import hirondelle.date4j.DateTime;

public class Event {

    private Long id;
    private final String user;
    private final String title;
    private final EventType type;
    private final long startDateTime;
    private final long endDateTime;
    private final boolean isAllDayEvent;
    private final boolean isSelfIncluded;
    private final List<String> recipients;

    private Event(Builder builder) {
        this.id = builder.id;
        this.user = builder.user;
        this.title = builder.title;
        this.type = builder.type;
        this.startDateTime = builder.startDateTime;
        this.endDateTime = builder.endDateTime;
        this.isAllDayEvent = builder.isAllDayEvent;
        this.isSelfIncluded = builder.isSelfIncluded;
        this.recipients = builder.recipients;
    }

    public boolean isEndingSameDay() {
        return DateTimeUtil.dateOnly(startDateTime).isSameDayAs(DateTimeUtil.dateOnly(endDateTime));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

        private Long id;
        private String user;
        private String title;
        private EventType type;
        private long startDateTime;
        private long endDateTime;
        private boolean isAllDayEvent;
        private boolean isSelfIncluded;
        private List<String> recipients;

        public Builder() {
            this.recipients = Collections.emptyList();
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder user(String user) {
            this.user = user;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder type(EventType type) {
            this.type = type;
            return this;
        }

        public Builder startDateTime(long startDateTime) {
            this.startDateTime = startDateTime;
            return this;
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
            return new Event(this);
        }
    }
}
