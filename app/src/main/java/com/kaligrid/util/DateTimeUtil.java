package com.kaligrid.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    public static DateTime dateOnly(long milliseconds) {
        return DateTime.forInstant(milliseconds, DEFAULT_TIME_ZONE).truncate(DateTime.Unit.DAY);
    }

    public static DateTime dateOnly(DateTime dateTime) {
        return dateTime.truncate(DateTime.Unit.DAY);
    }

    public static long toMillis(DateTime dateTime) {
        return dateTime.getMilliseconds(DEFAULT_TIME_ZONE);
    }

    public static String format(DateTime dateTime, String format) {
        return dateTime.format(format, DEFAULT_LOCALE);
    }

    public static String format(long milliseconds, String format) {
        return format(forInstant(milliseconds), format);
    }

    public static DateTime addHours(DateTime dateTime, int hours) {
        return dateTime.plus(0, 0, 0, hours, 0, 0, 0, DateTime.DayOverflow.FirstDay);
    }

    public static DateTime clearMinutesAndSeconds(DateTime dateTime) {
        return dateTime.minus(0, 0, 0, 0, dateTime.getMinute(), dateTime.getSecond(), dateTime.getNanoseconds(), DateTime.DayOverflow.LastDay);
    }

    public static List<DateTime> daysBetween(long from, long to) {
        return daysBetween(forInstant(from), forInstant(to));
    }

    public static List<DateTime> daysBetween(DateTime from, DateTime to) {
        if (from.isSameDayAs(to)) {
            return Collections.singletonList(from);
        }

        List<DateTime> dates = new ArrayList<>();
        DateTime date = dateOnly(from);

        while(date.lteq(dateOnly(to))) {
            dates.add(date);
            date = date.plusDays(1);
        }

        return dates;
    }
}
