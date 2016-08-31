package com.kaligrid.model.eventlist;

import android.content.Context;

import com.kaligrid.R;
import com.kaligrid.model.Event;
import com.kaligrid.model.EventType;
import com.kaligrid.util.DateTimeUtil;
import com.kaligrid.util.EventResourceHelper;
import com.kaligrid.util.EventSummaryBuilder;

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
        return DateTimeUtil.forInstant(event.getStartDateTime());
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
            DateTime eventStartTime = DateTimeUtil.forInstant(event.getStartDateTime());
            return eventStartTime.format(TIME_FORMAT, Locale.getDefault());
        }
    }

    public int getEventTypeImage() {
        return EventResourceHelper.getEventTypeIcon(event.getType());
    }

    public String getEventSummary() {
        String summary = EventSummaryBuilder.build(event);

        // If event is a timed-event that started before displaying date and ending on displaying
        // date, append text that indicates it's ending this day.
        if (!event.isAllDayEvent() && !event.isEndingSameDay()
                && DateTimeUtil.dateOnly(event.getEndDateTime()).isSameDayAs(displayingDate)) {
            String endTimeText = DateTimeUtil.forInstant(event.getEndDateTime()).format(TIME_FORMAT, Locale.getDefault());
            summary = String.format("%s (Until %s)", summary, endTimeText);
        }

        return summary;
    }
}
