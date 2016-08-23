package com.kaligrid.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.kaligrid.dao.DataContract.EventTable;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Kaligrid.db";

    private static final String SQL_CREATE_EVENT_TABLE = String.format(
            "CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER)",
            EventTable.TABLE_NAME,
            EventTable._ID,
            EventTable.COLUMN_USER,
            EventTable.COLUMN_TITLE,
            EventTable.COLUMN_TYPE,
            EventTable.COLUMN_START_DATE_TIME,
            EventTable.COLUMN_END_DATE_TIME,
            EventTable.COLUMN_ALL_DAY_EVENT,
            EventTable.COLUMN_SELF_INCLUDED);

    private static final String SQL_DROP_EVENT_TABLE = String.format("DROP TABLE IF EXISTS %s", EventTable.TABLE_NAME);

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_EVENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_EVENT_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
