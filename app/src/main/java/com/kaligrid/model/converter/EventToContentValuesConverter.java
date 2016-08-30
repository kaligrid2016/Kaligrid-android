package com.kaligrid.model.converter;

import android.content.ContentValues;

import com.kaligrid.model.Event;

import static com.kaligrid.dao.DataContract.EventTable;

public class EventToContentValuesConverter {

    public static ContentValues convert(Event event) {
        ContentValues values = new ContentValues();
        if (event.getId() >= 0) {
            values.put(EventTable._ID, event.getId());
        }
        values.put(EventTable.COLUMN_USER, event.getUser());
        values.put(EventTable.COLUMN_TITLE, event.getTitle());
        values.put(EventTable.COLUMN_TYPE, event.getType().toString());
        values.put(EventTable.COLUMN_START_DATE_TIME, event.getStartDateTime());
        values.put(EventTable.COLUMN_END_DATE_TIME, event.getEndDateTime());
        values.put(EventTable.COLUMN_ALL_DAY_EVENT, event.isAllDayEvent());
        values.put(EventTable.COLUMN_SELF_INCLUDED, event.isSelfIncluded());
        return values;
    }
}
