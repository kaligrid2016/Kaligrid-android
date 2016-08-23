package com.kaligrid.dao;

import android.provider.BaseColumns;

public final class DataContract {

    public DataContract() { }

    public static abstract class EventTable implements BaseColumns {
        public static final String TABLE_NAME = "event";
        public static final String COLUMN_USER = "user";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_START_DATE_TIME = "startDateTime";
        public static final String COLUMN_END_DATE_TIME = "endDateTime";
        public static final String COLUMN_ALL_DAY_EVENT = "isAllDayEvent";
        public static final String COLUMN_SELF_INCLUDED = "isSelfIncluded";
    }
}
