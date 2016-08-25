package com.kaligrid.util;

import java.util.Locale;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

public class DateTimeUtil {

    private static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getDefault();
    private static final Locale DEFAULT_LOCALE = Locale.getDefault();

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

    public static String format(DateTime dateTime, String format) {
        return dateTime.format(format, DEFAULT_LOCALE);
    }

    public static DateTime addHours(DateTime dateTime, int hours) {
        return dateTime.plus(0, 0, 0, hours, 0, 0, 0, DateTime.DayOverflow.FirstDay);
    }

    public static DateTime clearMinutesAndSeconds(DateTime dateTime) {
        return dateTime.minus(0, 0, 0, 0, dateTime.getMinute(), dateTime.getSecond(), dateTime.getNanoseconds(), DateTime.DayOverflow.LastDay);
    }
}
