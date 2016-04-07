package com.kaligrid.model;

public class Event {

    private String title;
    private EventType type;
    private long eventDateTime;

    public Event(String title, EventType type, long eventDateTime) {
        this.title = title;
        this.type = type;
        this.eventDateTime = eventDateTime;
    }

    public String getTitle() {
        return title;
    }

    public EventType getType() {
        return type;
    }

    public long getEventDateTime() {
        return eventDateTime;
    }
}
