package com.kaligrid.service;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kaligrid.dao.DBHelper;
import com.kaligrid.model.Event;
import com.kaligrid.model.EventType;
import com.kaligrid.model.converter.EventToContentValuesConverter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.kaligrid.dao.DataContract.EventTable;

public class EventService {

    private static final String[] COLUMNS = {
            EventTable._ID,
            EventTable.COLUMN_USER,
            EventTable.COLUMN_TITLE,
            EventTable.COLUMN_TYPE,
            EventTable.COLUMN_START_DATE_TIME,
            EventTable.COLUMN_END_DATE_TIME,
            EventTable.COLUMN_ALL_DAY_EVENT,
            EventTable.COLUMN_SELF_INCLUDED
    };

    private final DBHelper dbHelper;
    private Map<Long, Event> events;

    public EventService(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public Event getEvent(long id) {
        if (events == null) {
            events = loadEventsFromDB();
        }
        return events.get(id);
    }

    public List<Event> getEvents() {
        if (events == null) {
            events = loadEventsFromDB();
        }
        return new ArrayList<>(events.values());
    }

    public void addEvent(Event event) {
        event.setId(Event.UNASSIGNED_ID);

        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        long id = db.insert(EventTable.TABLE_NAME, null, EventToContentValuesConverter.convert(event));
        db.close();

        event.setId(id);
        events.put(id, event);
    }

    public void updateEvent(Event event) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        long id = db.replace(EventTable.TABLE_NAME, null, EventToContentValuesConverter.convert(event));
        db.close();

        event.setId(id);
        events.put(id, event);
    }

    public void deleteEvent(long eventId) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        db.delete(EventTable.TABLE_NAME, EventTable._ID + " = ?", new String[] { String.valueOf(eventId) });
        db.close();

        events.remove(eventId);
    }

    private Map<Long, Event> loadEventsFromDB() {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        Cursor cursor = db.query(EventTable.TABLE_NAME, COLUMNS, null, null, null, null, null);
        Map<Long, Event> events = new LinkedHashMap<>();

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(EventTable._ID));
                events.put(id, new Event.Builder()
                        .id(id)
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
        cursor.close();

        return events;
    }
}
