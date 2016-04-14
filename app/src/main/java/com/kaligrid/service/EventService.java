package com.kaligrid.service;

import com.kaligrid.model.Event;
import com.kaligrid.model.EventType;
import com.kaligrid.util.RandomEventRecipientGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

public class EventService {

    public List<Event> getTestEvents() {
        DateTime today = DateTime.now(TimeZone.getDefault());
        today = today.minus(0, 0, 5, 0, 0, 0, 0, DateTime.DayOverflow.LastDay);
        List<Event> events = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            today = today.plusDays(1);
            long todayInMillis = today.getMilliseconds(TimeZone.getDefault());

            events.add(new Event.Builder("Yong", "My long test event/My long test event/My long test event/My long test event/My long test event.",
                    EventType.EVENT, todayInMillis).recipients(RandomEventRecipientGenerator.generate()).isSelfIncluded(true).build());

            events.add(new Event.Builder("Seula", "Seula's all day event",
                    EventType.EVENT, todayInMillis).isAllDayEvent(true).isSelfIncluded(true).recipients("Yong").build());

            events.add(new Event.Builder("Yong", "My vacation",
                    EventType.FYI, todayInMillis).isAllDayEvent(true).recipients("Daniel").build());

            events.add(new Event.Builder("Yong", "Out sick",
                    EventType.FYI, todayInMillis).isAllDayEvent(true).recipients("Daniel", "Xingy").build());

            events.add(new Event.Builder("Yong", "Traveling",
                    EventType.FYI, todayInMillis).isAllDayEvent(true).recipients("Daniel", "Seula", "Brad").build());

            events.add(new Event.Builder("Daniel", "Daniel's vacation",
                    EventType.FYI, todayInMillis).isAllDayEvent(true).recipients("Yong", "Seula", "Brad").build());

            events.add(new Event.Builder("Brad", "Brad's workout",
                    EventType.REMINDER, todayInMillis - 10).recipients("yong").build());

            events.add(new Event.Builder("Yong", "My grocery shopping reminder",
                    EventType.REMINDER, todayInMillis - 360000).isSelfIncluded(true).build());

            events.add(new Event.Builder("Yong", "My tax return",
                    EventType.REMINDER, todayInMillis - 360000).recipients("Xingy").isSelfIncluded(true).build());
        }

        return events;
    }
}
