package com.kaligrid.model.converter;

import android.content.ContentValues;

import com.kaligrid.dao.DataContract;
import com.kaligrid.model.Event;

public class EventToContentValuesConverter {

    public static ContentValues convert(Event event) {
        ContentValues values = new ContentValues();
        values.put(DataContract.EventTable.COLUMN_USER, event.getUser());
        values.put(DataContract.EventTable.COLUMN_TITLE, event.getTitle());
        values.put(DataContract.EventTable.COLUMN_TYPE, event.getType().toString());
        values.put(DataContract.EventTable.COLUMN_START_DATE_TIME, event.getStartDateTime());
        values.put(DataContract.EventTable.COLUMN_END_DATE_TIME, event.getEndDateTime());
        values.put(DataContract.EventTable.COLUMN_ALL_DAY_EVENT, event.isAllDayEvent());
        values.put(DataContract.EventTable.COLUMN_SELF_INCLUDED, event.isSelfIncluded());
        return values;
    }
}
