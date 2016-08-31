package com.kaligrid.model.eventlist;

import android.content.Context;

import com.kaligrid.R;
import com.kaligrid.model.Event;
import com.kaligrid.model.EventType;
import com.kaligrid.util.DateTimeUtil;
import com.kaligrid.util.EventResourceHelper;
import com.kaligrid.util.EventSummaryBuilder;

import java.util.List;
import java.util.Locale;

import hirondelle.date4j.DateTime;

public class EventListEventItem implements EventListItem {

    private static final String TIME_FORMAT = "h12:mm a";

    private final Event event;
    private final Context context;
    private final DateTime displayingDate;
    private final boolean showEventTime;

    public EventListEventItem(Event event, Context context, DateTime displayingDate, boolean showEventTime) {
        this.event = event;
        this.context = context;
        this.displayingDate = displayingDate;
        this.showEventTime = showEventTime;
    }

    @Override
    public DateTime getDate() {
        return displayingDate;
    }

    public long getEventId() {
        return event.getId();
    }

    public EventType getEventType() {
        return event.getType();
    }

    public String getTimeText() {
        if (!showEventTime) {
            return "";
        }

        if (event.isAllDayEvent()) {
            return context.getResources().getText(R.string.event_list_text_all_day).toString();
        } else {
            return DateTimeUtil.format(event.getStartDateTime(), TIME_FORMAT);
        }
    }

    public int getEventTypeImage() {
        return EventResourceHelper.getEventTypeIcon(event.getType());
    }

    public String getEventSummary() {
        String summary = EventSummaryBuilder.build(event);

        // If event is a timed-event that started before displaying date and ending on displaying
        // date, append text that indicates it's ending this day.
        if (!event.isAllDayEvent() && !event.isEndingSameDay() && isEndingOnDisplayingDate()) {
            String endTimeText = DateTimeUtil.format(event.getEndDateTime(), TIME_FORMAT);
            summary = String.format("%s (Until %s)", summary, endTimeText);
        } else if (!event.isEndingSameDay()) {
            List<DateTime> daysBetween = DateTimeUtil.daysBetween(event.getStartDateTime(), event.getEndDateTime());
            String dayCountText = String.format("Day %d of %d", getIndexOfDisplayingDate(daysBetween), daysBetween.size());
            summary = String.format("%s (%s)", summary, dayCountText);
        }

        return summary;
    }

    private boolean isEndingOnDisplayingDate() {
        return DateTimeUtil.dateOnly(event.getEndDateTime()).isSameDayAs(displayingDate);
    }

    private int getIndexOfDisplayingDate(List<DateTime> days) {
        int i = 1;
        for (DateTime d : days) {
            if (d.isSameDayAs(displayingDate)) {
                break;
            }
            i++;
        }

        return i;
    }
}
