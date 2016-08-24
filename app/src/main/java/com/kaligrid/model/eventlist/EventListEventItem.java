package com.kaligrid.model.eventlist;

import android.content.Context;

import com.kaligrid.R;
import com.kaligrid.model.Event;
import com.kaligrid.util.DateTimeUtil;
import com.kaligrid.util.EventResourceHelper;
import com.kaligrid.util.EventSummaryBuilder;

import java.util.Locale;

import hirondelle.date4j.DateTime;

public class EventListEventItem implements EventListItem {

    private static final String TIME_FORMAT = "h12:mm a";

    private final Event event;
    private final Context context;
    private final boolean showEventTime;

    public EventListEventItem(Event event, Context context, boolean showEventTime) {
        this.event = event;
        this.context = context;
        this.showEventTime = showEventTime;
    }

    @Override
    public DateTime getDate() {
        return DateTimeUtil.forInstant(event.getStartDateTime());
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
        return EventSummaryBuilder.build(event);
    }
}
