package com.kaligrid.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import hirondelle.date4j.DateTime;

public class EventListViewSource {

    private List<EventListItem> eventListItems;
    private Map<DateTime, Integer> eventListItemDateMap;

    public EventListViewSource() {
        this.eventListItems = new ArrayList<>();
        this.eventListItemDateMap = new LinkedHashMap<>();
    }

    public void addDateHeaderItem(EventListDateHeaderItem item) {
        eventListItems.add(item);
        eventListItemDateMap.put(item.getDate(), eventListItems.size() - 1);
    }

    public void addEventItem(EventListEventItem item) {
        eventListItems.add(item);
    }

    public EventListItem get(int index) {
        return eventListItems.get(index);
    }

    public List<EventListItem> getAll() {
        return eventListItems;
    }

    public Integer getPositionByDate(DateTime date) {
        return eventListItemDateMap.get(date);
    }

    public int size() {
        return eventListItems.size();
    }
}
