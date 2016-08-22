package com.kaligrid.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Kaligrid.db";

    private static final String SQL_CREATE_EVENT_TABLE = String.format(
            "CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER)",
            DataContract.Event.TABLE_NAME,
            DataContract.Event._ID,
            DataContract.Event.COLUMN_NAME_USER,
            DataContract.Event.COLUMN_NAME_TYPE,
            DataContract.Event.COLUMN_NAME_START_DATE_TIME,
            DataContract.Event.COLUMN_NAME_END_DATE_TIME,
            DataContract.Event.COLUMN_NAME_ALL_DAY_EVENT,
            DataContract.Event.COLUMN_NAME_SELF_INCLUDED);

    private static final String SQL_DROP_EVENT_TABLE = String.format("DROP TABLE IF EXISTS %s", DataContract.Event.TABLE_NAME);

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
}
