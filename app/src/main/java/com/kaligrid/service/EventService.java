package com.kaligrid.service;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kaligrid.dao.DBHelper;
import com.kaligrid.model.Event;
import com.kaligrid.model.EventType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.kaligrid.dao.DataContract.EventTable;

public class EventService {

    private final DBHelper dbHelper;
    private List<Event> events;

    public EventService(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void addEvent(Event event) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        db.insert(EventTable.TABLE_NAME, null, event.toContentValues());
        db.close();

        events.add(event);
    }

    public void updateEvent(Event event) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        db.replace(EventTable.TABLE_NAME, null, event.toContentValues());
        db.close();

        events.add(event);
    }

    public List<Event> getEvents() {
        if (events == null) {
            String[] columns = {
                    EventTable._ID,
                    EventTable.COLUMN_USER,
                    EventTable.COLUMN_TITLE,
                    EventTable.COLUMN_TYPE,
                    EventTable.COLUMN_START_DATE_TIME,
                    EventTable.COLUMN_END_DATE_TIME,
                    EventTable.COLUMN_ALL_DAY_EVENT,
                    EventTable.COLUMN_SELF_INCLUDED
            };

            SQLiteDatabase db = this.dbHelper.getReadableDatabase();
            Cursor cursor = db.query(EventTable.TABLE_NAME, columns, null, null, null, null, null);
            events = new ArrayList<>();

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    events.add(new Event.Builder()
                            .id(cursor.getLong(cursor.getColumnIndexOrThrow(EventTable._ID)))
                            .user(cursor.getString(cursor.getColumnIndexOrThrow(EventTable.COLUMN_USER)))
                            .title(cursor.getString(cursor.getColumnIndexOrThrow(EventTable.COLUMN_TITLE)))
                            .type(EventType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(EventTable.COLUMN_TYPE))))
                            .startDateTime(cursor.getLong(cursor.getColumnIndexOrThrow(EventTable.COLUMN_START_DATE_TIME)))
                            .endDateTime(cursor.getLong(cursor.getColumnIndexOrThrow(EventTable.COLUMN_END_DATE_TIME)))
                            .isAllDayEvent(cursor.getInt(cursor.getColumnIndexOrThrow(EventTable.COLUMN_ALL_DAY_EVENT)) > 0)
                            .isSelfIncluded(cursor.getInt(cursor.getColumnIndexOrThrow(EventTable.COLUMN_SELF_INCLUDED)) > 0)
                            .build());
                } while (cursor.moveToNext());
            }
        }

        Collections.sort(events, new Event.EventStartDateComparator());
        return Collections.unmodifiableList(events);
    }
}
