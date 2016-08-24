package com.kaligrid.model.eventlist;

import android.content.Context;

import com.kaligrid.R;
import com.kaligrid.util.DateTimeUtil;

import java.util.Locale;

import hirondelle.date4j.DateTime;

public class EventListDateHeaderItem implements EventListItem {

    private static final String DATE_FORMAT = "WWW, MMM D";

    private final DateTime date;
    private final Context context;

    public EventListDateHeaderItem(DateTime date, Context context) {
        this.date = date;
        this.context = context;
    }

    @Override
    public DateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        DateTime today = DateTimeUtil.today();
        StringBuilder dateString = new StringBuilder(date.format(DATE_FORMAT, Locale.getDefault()));

        if (date.isSameDayAs(today)) {
            dateString.append(" (").append(context.getResources().getString(R.string.date_today)).append(")");
        } else if (date.isSameDayAs(today.plusDays(1))) {
            dateString.append(" (").append(context.getResources().getString(R.string.date_tomorrow)).append(")");
        }

        return dateString.toString();
    }
}
