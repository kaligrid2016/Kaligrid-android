package com.kaligrid.util;

import java.util.TimeZone;

import hirondelle.date4j.DateTime;

public class DateTimeUtil {

    private static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getDefault();

    public static DateTime now() {
        return DateTime.now(DEFAULT_TIME_ZONE);
    }

    public static DateTime today() {
        return DateTime.today(DEFAULT_TIME_ZONE);
    }

    public static DateTime forInstant(long milliseconds) {
        return DateTime.forInstant(milliseconds, DEFAULT_TIME_ZONE);
    }

    public static long toMillis(DateTime dateTime) {
        return dateTime.getMilliseconds(DEFAULT_TIME_ZONE);
    }
}
