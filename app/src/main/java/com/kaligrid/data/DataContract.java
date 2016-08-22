package com.kaligrid.data;

import android.provider.BaseColumns;

public final class DataContract {

    public DataContract() { }

    public static abstract class Event implements BaseColumns {
        public static final String TABLE_NAME = "event";
        public static final String COLUMN_NAME_USER = "user";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_START_DATE_TIME = "startDateTime";
        public static final String COLUMN_NAME_END_DATE_TIME = "endDateTime";
        public static final String COLUMN_NAME_ALL_DAY_EVENT = "isAllDayEvent";
        public static final String COLUMN_NAME_SELF_INCLUDED = "isSelfIncluded";
    }
}
