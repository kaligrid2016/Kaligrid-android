package com.kaligrid.calendar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.kaligrid.R;
import com.kaligrid.calendar.adapter.CaldroidGridAdapter;
import com.kaligrid.calendar.adapter.MonthPagerAdapter;
import com.kaligrid.calendar.adapter.WeekdayArrayAdapter;
import com.kaligrid.calendar.view.CalendarLayoutView;
import com.kaligrid.calendar.view.InfinitePagerAdapter;
import com.kaligrid.calendar.view.InfiniteViewPager;
import com.kaligrid.util.DateTimeUtil;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

/**
 * Caldroid is a fragment that display calendar with dates in a month. Caldroid
 * can be used as embedded fragment, or as dialog fragment. <br/>
 * <br/>
 * Caldroid fragment includes 4 main parts:<br/>
 * <br/>
 * 1) Month title view: show the month and year (e.g MARCH, 2013) <br/>
 * <br/>
 * 2) Navigation arrows: to navigate to next month or previous month <br/>
 * <br/>
 * 3) Weekday gridview: contains only 1 row and 7 columns. To display
 * "SUN, MON, TUE, WED, THU, FRI, SAT" <br/>
 * <br/>
 * 4) An infinite view pager that allow user to swipe left/right to change
 * month. This library is taken from
 * https://github.com/antonyt/InfiniteViewPager <br/>
 * <br/>
 * This infinite view pager recycles 4 fragment, each fragment contains a grid
 * view with 7 columns to display the dates in month. Whenever user swipes
 * different screen, the date grid views are updated. <br/>
 * <br/>
 * Caldroid fragment supports setting min/max date, selecting dates in a range,
 * setting disabled dates, highlighting today. It includes convenient methods to
 * work with date and string, enable or disable the navigation arrows. User can
 * also swipe left/right to change months.<br/>
 * <br/>
 * Caldroid code is simple and clean partly because of powerful Date4J DateTime
 * library!
 *
 * @author thomasdao
 * @author blueyoj
 */

@SuppressLint("DefaultLocale")
public class CaldroidFragment extends DialogFragment {

    public enum ViewMode { WEEK_VIEW, MONTH_VIEW }

    /**
     * Weekday conventions
     */
    public static int SUNDAY = 1;
//            MONDAY = 2,
//            TUESDAY = 3,
//            WEDNESDAY = 4,
//            THURSDAY = 5,
//            FRIDAY = 6,
//            SATURDAY = 7;

    /**
     * Flags to display month
     */
    private static final int MONTH_YEAR_FLAG = DateUtils.FORMAT_SHOW_DATE
            | DateUtils.FORMAT_NO_MONTH_DAY | DateUtils.FORMAT_SHOW_YEAR;

    /**
     * First day of month time
     */
    @SuppressWarnings("deprecation")
    private Time firstMonthTime = new Time();

    /**
     * Reuse formatter to print "MMMM yyyy" format
     */
    private final StringBuilder monthYearStringBuilder = new StringBuilder(50);
    private Formatter monthYearFormatter = new Formatter(
            monthYearStringBuilder, Locale.getDefault());

    public final static int NUMBER_OF_PAGES = 4;

    /**
     * To customize the disabled background drawable and text color
     */
    public static int disabledBackgroundDrawable = -1;
    public static int disabledTextColor = Color.GRAY;

    /**
     * Caldroid view components
     */
    private TextView monthTitleTextView;
    private GridView weekdayGridView;
    private InfiniteViewPager dateViewPager;
    private DatePageChangeListener pageChangeListener;
    private ArrayList<DateGridFragment> fragments;

    private int themeResource = R.style.CaldroidDefault;

    /**
     * Initial params key
     */
    public final static String
            DIALOG_TITLE = "dialogTitle",
            MONTH = "month",
            YEAR = "year",
            DISABLE_DATES = "disableDates",
            SELECTED_DATE = "selectedDate",
            MIN_DATE = "minDate",
            MAX_DATE = "maxDate",
            ENABLE_SWIPE = "enableSwipe",
            START_DAY_OF_WEEK = "startDayOfWeek",
            SIX_WEEKS_IN_CALENDAR = "sixWeeksInCalendar",
            ENABLE_CLICK_ON_DISABLED_DATES = "enableClickOnDisabledDates",
            SQUARE_TEXT_VIEW_CELL = "squareTextViewCell",
            THEME_RESOURCE = "themeResource";

    /**
     * For internal use
     */
    public final static String
            _MIN_DATE_TIME = "_minDateTime",
            _MAX_DATE_TIME = "_maxDateTime",
            _BACKGROUND_FOR_DATETIME_MAP = "_backgroundForDateTimeMap",
            _TEXT_COLOR_FOR_DATETIME_MAP = "_textColorForDateTimeMap";

    /**
     * Initial data
     */
    protected String dialogTitle;
    protected int month = -1;
    protected int year = -1;
    protected List<DateTime> disableDates = new ArrayList<>();
    protected DateTime selectedDate;
    protected DateTime minDateTime;
    protected DateTime maxDateTime;
    protected List<DateTime> dateInMonthsList;

    /**
     * caldroidData belongs to Caldroid
     */
    protected Map<String, Object> caldroidData = new HashMap<>();

    /**
     * extraData belongs to client
     */
    protected Map<String, Object> extraData = new HashMap<>();

    /**
     * backgroundForDateMap holds background resource for each date
     */
    protected Map<DateTime, Drawable> backgroundForDateTimeMap = new HashMap<>();

    /**
     * textColorForDateMap holds color for text for each date
     */
    protected Map<DateTime, Integer> textColorForDateTimeMap = new HashMap<>();

    /**
     * First column of calendar is Sunday
     */
    protected int startDayOfWeek = SUNDAY;

    /**
     * A calendar height is not fixed, it may have 5 or 6 rows. Set fitAllMonths
     * to true so that the calendar will always have 6 rows
     */
    private boolean sixWeeksInCalendar = true;

    /**
     * datePagerAdapters hold 4 adapters, meant to be reused
     */
    protected ArrayList<CaldroidGridAdapter> datePagerAdapters = new ArrayList<>();

    /**
     * To control the navigation
     */
    protected boolean enableSwipe = false;
    protected boolean enableClickOnDisabledDates = true;

    /**
     * To use SquareTextView to display Date cell.By default, it is true,
     * however in many cases with compact screen, it can be collapsed to save space
     */
    protected boolean squareTextViewCell;

    /**
     * dateItemClickListener is fired when user click on the date cell
     */
    private OnItemClickListener dateItemClickListener;

    /**
     * dateItemLongClickListener is fired when user does a longclick on the date
     * cell
     */
    private OnItemLongClickListener dateItemLongClickListener;

    /**
     * caldroidListener inform library client of the event happens inside Caldroid
     */
    private CaldroidListener caldroidListener;

    private ViewMode currentViewMode;

    private CalendarGestureListener calendarGestureListener;

    public void setCalendarGestureListener(CalendarGestureListener calendarGestureListener) {
        this.calendarGestureListener = calendarGestureListener;
    }

    /**
     * Retrieve current month
     */
    public int getMonth() {
        return month;
    }

    /**
     * Retrieve current year
     */
    public int getYear() {
        return year;
    }

    public CaldroidListener getCaldroidListener() {
        return caldroidListener;
    }

    /**
     * Meant to be subclassed. User who wants to provide custom view, need to
     * provide custom adapter here
     */
    public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year) {
        return new CaldroidGridAdapter(getActivity(), month, year, getCaldroidData(), extraData);
    }

    /**
     * Meant to be subclassed. User who wants to provide custom view, need to
     * provide custom adapter here
     */
    public WeekdayArrayAdapter getNewWeekdayAdapter(int themeResource) {
        return new WeekdayArrayAdapter(
                getActivity(), android.R.layout.simple_list_item_1,
                getDaysOfWeek(), themeResource);
    }

    /**
     * caldroidData return data belong to Caldroid
     */
    public Map<String, Object> getCaldroidData() {
        caldroidData.clear();
        caldroidData.put(DISABLE_DATES, disableDates);
        caldroidData.put(SELECTED_DATE, selectedDate);
        caldroidData.put(_MIN_DATE_TIME, minDateTime);
        caldroidData.put(_MAX_DATE_TIME, maxDateTime);
        caldroidData.put(START_DAY_OF_WEEK, startDayOfWeek);
        caldroidData.put(SIX_WEEKS_IN_CALENDAR, sixWeeksInCalendar);
        caldroidData.put(SQUARE_TEXT_VIEW_CELL, squareTextViewCell);
        caldroidData.put(THEME_RESOURCE, themeResource);


        // For internal use
        caldroidData.put(_BACKGROUND_FOR_DATETIME_MAP, backgroundForDateTimeMap);
        caldroidData.put(_TEXT_COLOR_FOR_DATETIME_MAP, textColorForDateTimeMap);

        return caldroidData;
    }

    /**
     * Set backgroundForDateMap
     */
    public void setBackgroundDrawableForDates(
            Map<Date, Drawable> backgroundForDateMap) {
        if (backgroundForDateMap == null || backgroundForDateMap.size() == 0) {
            return;
        }

        backgroundForDateTimeMap.clear();

        for (Date date : backgroundForDateMap.keySet()) {
            Drawable drawable = backgroundForDateMap.get(date);
            DateTime dateTime = CalendarHelper.convertDateToDateTime(date);
            backgroundForDateTimeMap.put(dateTime, drawable);
        }
    }

    public void clearBackgroundDrawableForDates(List<Date> dates) {
        if (dates == null || dates.size() == 0) {
            return;
        }

        for (Date date : dates) {
            clearBackgroundDrawableForDate(date);
        }
    }

    public void setBackgroundDrawableForDateTimes(
            Map<DateTime, Drawable> backgroundForDateTimeMap) {
        this.backgroundForDateTimeMap.putAll(backgroundForDateTimeMap);
    }

    public void clearBackgroundDrawableForDateTimes(List<DateTime> dateTimes) {
        if (dateTimes == null || dateTimes.size() == 0) return;

        for (DateTime dateTime : dateTimes) {
            backgroundForDateTimeMap.remove(dateTime);
        }
    }

    public void setBackgroundDrawableForDate(Drawable drawable, Date date) {
        DateTime dateTime = CalendarHelper.convertDateToDateTime(date);
        backgroundForDateTimeMap.put(dateTime, drawable);
    }

    public void clearBackgroundDrawableForDate(Date date) {
        DateTime dateTime = CalendarHelper.convertDateToDateTime(date);
        backgroundForDateTimeMap.remove(dateTime);
    }

    public void setBackgroundDrawableForDateTime(Drawable drawable,
                                                 DateTime dateTime) {
        backgroundForDateTimeMap.put(dateTime, drawable);
    }

    public void clearBackgroundDrawableForDateTime(DateTime dateTime) {
        backgroundForDateTimeMap.remove(dateTime);
    }

    /**
     * Set textColorForDateMap
     */
    public void setTextColorForDates(Map<Date, Integer> textColorForDateMap) {
        if (textColorForDateMap == null || textColorForDateMap.size() == 0) {
            return;
        }

        textColorForDateTimeMap.clear();

        for (Date date : textColorForDateMap.keySet()) {
            Integer resource = textColorForDateMap.get(date);
            DateTime dateTime = CalendarHelper.convertDateToDateTime(date);
            textColorForDateTimeMap.put(dateTime, resource);
        }
    }

    public void clearTextColorForDates(List<Date> dates) {
        if (dates == null || dates.size() == 0) return;

        for (Date date : dates) {
            clearTextColorForDate(date);
        }
    }

    public void setTextColorForDateTimes(
            Map<DateTime, Integer> textColorForDateTimeMap) {
        this.textColorForDateTimeMap.putAll(textColorForDateTimeMap);
    }

    public void setTextColorForDate(int textColorRes, Date date) {
        DateTime dateTime = CalendarHelper.convertDateToDateTime(date);
        textColorForDateTimeMap.put(dateTime, textColorRes);
    }

    public void clearTextColorForDate(Date date) {
        DateTime dateTime = CalendarHelper.convertDateToDateTime(date);
        textColorForDateTimeMap.remove(dateTime);
    }

    public void setTextColorForDateTime(int textColorRes, DateTime dateTime) {
        textColorForDateTimeMap.put(dateTime, textColorRes);
    }

    /**
     * Get current saved sates of the Caldroid. Useful for handling rotation.
     * It does not need to save state of SQUARE_TEXT_VIEW_CELL because this
     * may change on orientation change
     */
    public Bundle getSavedStates() {
        Bundle bundle = new Bundle();
        bundle.putInt(MONTH, month);
        bundle.putInt(YEAR, year);

        if (dialogTitle != null) {
            bundle.putString(DIALOG_TITLE, dialogTitle);
        }

        if (selectedDate != null) {
            bundle.putString(SELECTED_DATE, CalendarHelper.convertToString(selectedDate));
        }

        if (disableDates != null && disableDates.size() > 0) {
            bundle.putStringArrayList(DISABLE_DATES, CalendarHelper.convertToStringList(disableDates));
        }

        if (minDateTime != null) {
            bundle.putString(MIN_DATE, minDateTime.format("YYYY-MM-DD"));
        }

        if (maxDateTime != null) {
            bundle.putString(MAX_DATE, maxDateTime.format("YYYY-MM-DD"));
        }

        bundle.putBoolean(ENABLE_SWIPE, enableSwipe);
        bundle.putInt(START_DAY_OF_WEEK, startDayOfWeek);
        bundle.putBoolean(SIX_WEEKS_IN_CALENDAR, sixWeeksInCalendar);
        bundle.putInt(THEME_RESOURCE, themeResource);

        Bundle args = getArguments();
        if (args != null && args.containsKey(SQUARE_TEXT_VIEW_CELL)) {
            bundle.putBoolean(SQUARE_TEXT_VIEW_CELL, args.getBoolean(SQUARE_TEXT_VIEW_CELL));
        }

        return bundle;
    }

    /**
     * Save current state to bundle outState
     */
    public void saveStatesToKey(Bundle outState, String key) {
        outState.putBundle(key, getSavedStates());
    }

    /**
     * Restore current states from savedInstanceState
     */
    public void restoreStatesFromKey(Bundle savedInstanceState, String key) {
        if (savedInstanceState != null && savedInstanceState.containsKey(key)) {
            Bundle caldroidSavedState = savedInstanceState.getBundle(key);
            setArguments(caldroidSavedState);
        }
    }

    /**
     * Restore state for dialog
     */
    public void restoreDialogStatesFromKey(FragmentManager manager,
                                           Bundle savedInstanceState, String key, String dialogTag) {
        restoreStatesFromKey(savedInstanceState, key);

        CaldroidFragment existingDialog = (CaldroidFragment) manager
                .findFragmentByTag(dialogTag);
        if (existingDialog != null) {
            existingDialog.dismiss();
            show(manager, dialogTag);
        }
    }

    /**
     * Get current virtual position of the month being viewed
     */
    public int getCurrentVirtualPosition() {
        int currentPage = dateViewPager.getCurrentItem();
        return pageChangeListener.getCurrent(currentPage);
    }

    /**
     * Move calendar to the specified date
     */
    public void moveToDate(Date date) {
        moveToDateTime(CalendarHelper.convertDateToDateTime(date));
    }

    /**
     * Move calendar to specified dateTime, with animation
     */
    public void moveToDateTime(DateTime dateTime) {

        DateTime firstOfMonth = new DateTime(year, month, 1, 0, 0, 0, 0);
        DateTime lastOfMonth = firstOfMonth.getEndOfMonth();

        // To create a swipe effect
        // Do nothing if the dateTime is in current month

        // Calendar swipe left when dateTime is in the past
        if (dateTime.lt(firstOfMonth)) {
            // Get next month of dateTime. When swipe left, month will
            // decrease
            DateTime firstDayNextMonth = dateTime.plus(0, 1, 0, 0, 0, 0, 0,
                    DateTime.DayOverflow.LastDay);

            // Refresh adapters
            pageChangeListener.setCurrentDateTime(firstDayNextMonth);
            int currentItem = dateViewPager.getCurrentItem();
            pageChangeListener.refreshAdapters(currentItem);

            // Swipe left
            dateViewPager.setCurrentItem(currentItem - 1);
        }

        // Calendar swipe right when dateTime is in the future
        else if (dateTime.gt(lastOfMonth)) {
            // Get last month of dateTime. When swipe right, the month will
            // increase
            DateTime firstDayLastMonth = dateTime.minus(0, 1, 0, 0, 0, 0, 0,
                    DateTime.DayOverflow.LastDay);

            // Refresh adapters
            pageChangeListener.setCurrentDateTime(firstDayLastMonth);
            int currentItem = dateViewPager.getCurrentItem();
            pageChangeListener.refreshAdapters(currentItem);

            // Swipe right
            dateViewPager.setCurrentItem(currentItem + 1);
        }

    }

    /**
     * Set month and year for the calendar. This is to avoid naive
     * implementation of manipulating month and year. All dates within same
     * month/year give same result
     */
    public void setCalendarDate(Date date) {
        setCalendarDateTime(CalendarHelper.convertDateToDateTime(date));
    }

    public void setCalendarDateTime(DateTime dateTime) {
        month = dateTime.getMonth();
        year = dateTime.getYear();

        // Notify listener
        if (caldroidListener != null) {
            caldroidListener.onChangeMonth(month, year);
        }

        refreshView(currentViewMode);
    }

    /**
     * Set calendar to previous month
     */
    public void prevMonth() {
        dateViewPager.setCurrentItem(pageChangeListener.getCurrentPage() - 1);
    }

    /**
     * Set calendar to next month
     */
    public void nextMonth() {
        dateViewPager.setCurrentItem(pageChangeListener.getCurrentPage() + 1);
    }

    /**
     * Clear all disable dates. Notice this does not refresh the calendar, need
     * to explicitly call refreshView()
     */
    public void clearDisableDates() {
        disableDates.clear();
    }

    /**
     * Set disableDates from ArrayList of Date
     */
    public void setDisableDates(ArrayList<Date> disableDateList) {
        if (disableDateList == null || disableDateList.size() == 0) {
            return;
        }

        disableDates.clear();

        for (Date date : disableDateList) {
            DateTime dateTime = CalendarHelper.convertDateToDateTime(date);
            disableDates.add(dateTime);
        }

    }

    /**
     * Set disableDates from ArrayList of String. By default, the date formatter
     * is yyyy-MM-dd. For e.g 2013-12-24
     */
    public void setDisableDatesFromString(ArrayList<String> disableDateStrings) {
        setDisableDatesFromString(disableDateStrings, null);
    }

    /**
     * Set disableDates from ArrayList of String with custom date format. For
     * example, if the date string is 06-Jan-2013, use date format dd-MMM-yyyy.
     * This method will refresh the calendar, it's not necessary to call
     * refreshView()
     */
    public void setDisableDatesFromString(ArrayList<String> disableDateStrings,
                                          String dateFormat) {
        if (disableDateStrings == null) {
            return;
        }

        disableDates.clear();

        for (String dateString : disableDateStrings) {
            DateTime dateTime = CalendarHelper.getDateTimeFromString(
                    dateString, dateFormat);
            disableDates.add(dateTime);
        }
    }

    public DateTime getSelectedDate() {
        return this.selectedDate;
    }

    /**
     * Select single date
     */
    public void setSelectedDate(DateTime date) {
        this.selectedDate = date;
    }

    /**
     * Clear selection of the specified date
     */
    public void clearSelectedDate(Date date) {
        if (date == null) {
            return;
        }
        this.selectedDate = null;
    }

    /**
     * Checks whether the specified date is selected
     */
    public boolean isSelectedDate(Date date) {
        if (date == null) {
            return false;
        }
        DateTime dateTime = CalendarHelper.convertDateToDateTime(date);
        return dateTime.equals(this.selectedDate);
    }

    /**
     * Set min date. This method does not refresh view
     */
    public void setMinDate(Date minDate) {
        if (minDate == null) {
            minDateTime = null;
        } else {
            minDateTime = CalendarHelper.convertDateToDateTime(minDate);
        }
    }

    /**
     * Convenient method to set min date from String. If dateFormat is null,
     * default format is yyyy-MM-dd
     */
    public void setMinDateFromString(String minDateString, String dateFormat) {
        if (minDateString == null) {
            setMinDate(null);
        } else {
            minDateTime = CalendarHelper.getDateTimeFromString(minDateString,
                    dateFormat);
        }
    }

    /**
     * Set max date. This method does not refresh view
     */
    public void setMaxDate(Date maxDate) {
        if (maxDate == null) {
            maxDateTime = null;
        } else {
            maxDateTime = CalendarHelper.convertDateToDateTime(maxDate);
        }
    }

    /**
     * Convenient method to set max date from String. If dateFormat is null,
     * default format is yyyy-MM-dd
     */
    public void setMaxDateFromString(String maxDateString, String dateFormat) {
        if (maxDateString == null) {
            setMaxDate(null);
        } else {
            maxDateTime = CalendarHelper.getDateTimeFromString(maxDateString,
                    dateFormat);
        }
    }

    public ViewMode getCurrentViewMode() {
        return this.currentViewMode;
    }

    /**
     * Set caldroid listener when user click on a date
     */
    public void setCaldroidListener(CaldroidListener caldroidListener) {
        this.caldroidListener = caldroidListener;
    }

    /**
     * Callback to listener when date is valid (not disable, not outside of min/max date)
     */
    public OnItemClickListener getDateItemClickListener() {
        if (dateItemClickListener == null) {
            dateItemClickListener = new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    DateTime dateTime = dateInMonthsList.get(position);

                    if (caldroidListener != null) {
                        if (!enableClickOnDisabledDates) {
                            if ((minDateTime != null && dateTime
                                    .lt(minDateTime))
                                    || (maxDateTime != null && dateTime
                                    .gt(maxDateTime))
                                    || (disableDates != null && disableDates
                                    .indexOf(dateTime) != -1)) {
                                return;
                            }
                        }

                        Date date = CalendarHelper
                                .convertDateTimeToDate(dateTime);
                        caldroidListener.onSelectDate(date, view);
                    }
                }
            };
        }

        return dateItemClickListener;
    }

    /**
     * Callback to listener when date is valid (not disable, not outside of min/max date)
     */
    public OnItemLongClickListener getDateItemLongClickListener() {
        if (dateItemLongClickListener == null) {
            dateItemLongClickListener = new OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent,
                                               View view, int position, long id) {

                    DateTime dateTime = dateInMonthsList.get(position);

                    if (caldroidListener != null) {
                        if (!enableClickOnDisabledDates) {
                            if ((minDateTime != null && dateTime
                                    .lt(minDateTime))
                                    || (maxDateTime != null && dateTime
                                    .gt(maxDateTime))
                                    || (disableDates != null && disableDates
                                    .indexOf(dateTime) != -1)) {
                                return false;
                            }
                        }
                        Date date = CalendarHelper
                                .convertDateTimeToDate(dateTime);
                        caldroidListener.onLongClickDate(date, view);
                    }

                    return true;
                }
            };
        }

        return dateItemLongClickListener;
    }

    /**
     * Refresh month title text view when user swipe
     */
    protected void refreshMonthTitleTextView() {
        // Refresh title view
        firstMonthTime.year = year;
        firstMonthTime.month = month - 1;
        firstMonthTime.monthDay = 15;
        long millis = firstMonthTime.toMillis(true);

        // This is the method used by the platform Calendar app to get a
        // correctly localized month name for display on a wall calendar
        monthYearStringBuilder.setLength(0);
        String monthTitle = DateUtils.formatDateRange(getActivity(),
                monthYearFormatter, millis, millis, MONTH_YEAR_FLAG).toString();

        monthTitleTextView.setText(monthTitle.toUpperCase(Locale.getDefault()));
    }

    /**
     * Refresh view when parameter changes. You should always change all
     * parameters first, then call this method.
     */
    public void refreshView(ViewMode viewMode) {
        // If month and year is not yet initialized, refreshView doesn't do
        // anything
        if (month == -1 || year == -1) {
            return;
        }

        refreshMonthTitleTextView();

        // Refresh the date grid views
        for (CaldroidGridAdapter adapter : datePagerAdapters) {
            if ((viewMode == ViewMode.WEEK_VIEW) && (selectedDate.getMonth() != adapter.getMonth())) {
                continue;
            }

            adapter.setCaldroidData(getCaldroidData(), viewMode);

            // Reset extra data
            adapter.setExtraData(extraData);

            // Update today variable
            adapter.updateToday();

            // Refresh view
            adapter.notifyDataSetChanged();
        }

        currentViewMode = viewMode;
    }

    public void refreshView(ViewMode viewMode, DateTime selectedDate) {
        setSelectedDate(selectedDate);
        refreshView(viewMode);
        moveToDateTime(selectedDate);
    }

    /**
     * Retrieve initial arguments to the fragment Data can include: month, year,
     * dialogTitle, showNavigationArrows,(String) disableDates, selectedDates,
     * minDate, maxDate, squareTextViewCell
     */
    protected void retrieveInitialArgs() {
        // Get arguments
        Bundle args = getArguments();

        CalendarHelper.setup();

        if (args != null) {
            // Get month, year
            month = args.getInt(MONTH, -1);
            year = args.getInt(YEAR, -1);
            dialogTitle = args.getString(DIALOG_TITLE);
            Dialog dialog = getDialog();
            if (dialog != null) {
                if (dialogTitle != null) {
                    dialog.setTitle(dialogTitle);
                } else {
                    // Don't display title bar if user did not supply
                    // dialogTitle
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                }
            }

            // Get start day of Week. Default calendar first column is SUNDAY
            startDayOfWeek = args.getInt(START_DAY_OF_WEEK, 1);
            if (startDayOfWeek > 7) {
                startDayOfWeek = startDayOfWeek % 7;
            }

            // Should enable swipe to change month
            enableSwipe = args.getBoolean(ENABLE_SWIPE, true);

            // Get sixWeeksInCalendar
            sixWeeksInCalendar = args.getBoolean(SIX_WEEKS_IN_CALENDAR, true);

            // Get squareTextViewCell, by default, use square cell in portrait mode
            // and using normal cell in landscape mode
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                squareTextViewCell = args.getBoolean(SQUARE_TEXT_VIEW_CELL, true);
            } else {
                squareTextViewCell = args.getBoolean(SQUARE_TEXT_VIEW_CELL, false);
            }

            // Get clickable setting
            enableClickOnDisabledDates = args.getBoolean(
                    ENABLE_CLICK_ON_DISABLED_DATES, false);

            // Get disable dates
            ArrayList<String> disableDateStrings = args
                    .getStringArrayList(DISABLE_DATES);
            if (disableDateStrings != null && disableDateStrings.size() > 0) {
                disableDates.clear();
                for (String dateString : disableDateStrings) {
                    DateTime dt = CalendarHelper.getDateTimeFromString(
                            dateString, null);
                    disableDates.add(dt);
                }
            }

            // Get selected dates
            String selectedDateString = args.getString(SELECTED_DATE);
            if (selectedDateString == null) {
                selectedDate = DateTimeUtil.today();
            } else {
                selectedDate = CalendarHelper.getDateTimeFromString(selectedDateString, null);
            }

            // Get min date and max date
            String minDateTimeString = args.getString(MIN_DATE);
            if (minDateTimeString != null) {
                minDateTime = CalendarHelper.getDateTimeFromString(
                        minDateTimeString, null);
            }

            String maxDateTimeString = args.getString(MAX_DATE);
            if (maxDateTimeString != null) {
                maxDateTime = CalendarHelper.getDateTimeFromString(
                        maxDateTimeString, null);
            }

            // Get theme
            themeResource = args.getInt(THEME_RESOURCE, R.style.CaldroidDefault);
        }
        if (month == -1 || year == -1) {
            DateTime dateTime = DateTimeUtil.today();
            month = dateTime.getMonth();
            year = dateTime.getYear();
            selectedDate = DateTimeUtil.today();
        }
    }

    /**
     * Below code fixed the issue viewpager disappears in dialog mode on
     * orientation change
     * <p/>
     * Code taken from Andy Dennie and Zsombor Erdody-Nagy
     * http://stackoverflow.com/questions/8235080/fragments-dialogfragment
     * -and-screen-rotation
     */
    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    public static LayoutInflater getThemeInflater(Context context, LayoutInflater origInflater, int themeResource) {
        Context wrapped = new ContextThemeWrapper(context, themeResource);
        return origInflater.cloneInContext(wrapped);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        retrieveInitialArgs();

        // To support keeping instance for dialog
        if (getDialog() != null) {
            try {
                setRetainInstance(true);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

        LayoutInflater localInflater = getThemeInflater(getActivity(), inflater, themeResource);

        // This is a hack to fix issue localInflater doesn't use the themeResource, make Android
        // complain about layout_width and layout_height missing. I'm unsure about its impact
        // for app that wants to change theme dynamically.
        getActivity().setTheme(themeResource);

        View view = localInflater.inflate(R.layout.calendar_view, container, false);
        if (view instanceof CalendarLayoutView) {
            ((CalendarLayoutView) view).setCalendarGestureListener(calendarGestureListener);
        }

        // For the monthTitleTextView
        monthTitleTextView = (TextView) view
                .findViewById(R.id.calendar_month_year_textview);

        // For the weekday gridview ("SUN, MON, TUE, WED, THU, FRI, SAT")
        weekdayGridView = (GridView) view.findViewById(R.id.weekday_gridview);
        WeekdayArrayAdapter weekdaysAdapter = getNewWeekdayAdapter(themeResource);
        weekdayGridView.setAdapter(weekdaysAdapter);

        // Setup all the pages of date grid views. These pages are recycled
        setupDateGridPages(view);

        // Refresh view
        refreshView(ViewMode.MONTH_VIEW);

        return view;
    }

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Inform client that all views are created and not null
		// Client should perform customization for buttons and textviews here
		if (caldroidListener != null) {
			caldroidListener.onCaldroidViewCreated();
		}
	}

	/**
     * This method can be used to provide different gridview.
     */
    protected int getGridViewRes() {
        return R.layout.date_grid_fragment;
    }

    /**
     * Setup 4 pages contain date grid views. These pages are recycled to use
     * memory efficient
     */
    private void setupDateGridPages(View view) {
        // Get current date time
        DateTime currentDateTime = new DateTime(year, month, 1, 0, 0, 0, 0);

        // Set to pageChangeListener
        pageChangeListener = new DatePageChangeListener();
        pageChangeListener.setCurrentDateTime(currentDateTime);

        // Setup adapters for the grid views
        // Current month
        CaldroidGridAdapter adapter0 = getNewDatesGridAdapter(
                currentDateTime.getMonth(), currentDateTime.getYear());

        // Setup dateInMonthsList
        dateInMonthsList = adapter0.getDatetimeList();

        // Next month
        DateTime nextDateTime = currentDateTime.plus(0, 1, 0, 0, 0, 0, 0,
                DateTime.DayOverflow.LastDay);
        CaldroidGridAdapter adapter1 = getNewDatesGridAdapter(
                nextDateTime.getMonth(), nextDateTime.getYear());

        // Next 2 month
        DateTime next2DateTime = nextDateTime.plus(0, 1, 0, 0, 0, 0, 0,
                DateTime.DayOverflow.LastDay);
        CaldroidGridAdapter adapter2 = getNewDatesGridAdapter(
                next2DateTime.getMonth(), next2DateTime.getYear());

        // Previous month
        DateTime prevDateTime = currentDateTime.minus(0, 1, 0, 0, 0, 0, 0,
                DateTime.DayOverflow.LastDay);
        CaldroidGridAdapter adapter3 = getNewDatesGridAdapter(
                prevDateTime.getMonth(), prevDateTime.getYear());

        // Add to the array of adapters
        datePagerAdapters.add(adapter0);
        datePagerAdapters.add(adapter1);
        datePagerAdapters.add(adapter2);
        datePagerAdapters.add(adapter3);

        // Set adapters to the pageChangeListener so it can refresh the adapter
        // when page change
        pageChangeListener.setCaldroidGridAdapters(datePagerAdapters);

        // Setup InfiniteViewPager and InfinitePagerAdapter. The
        // InfinitePagerAdapter is responsible
        // for reuse the fragments
        dateViewPager = (InfiniteViewPager) view
                .findViewById(R.id.months_infinite_pager);

        // Set enable swipe
        dateViewPager.setEnabled(enableSwipe);

        // Set if viewpager wrap around particular month or all months (6 rows)
        dateViewPager.setSixWeeksInCalendar(sixWeeksInCalendar);

        // Set the numberOfDaysInMonth to dateViewPager so it can calculate the
        // height correctly
        dateViewPager.setDatesInMonth(dateInMonthsList);

        // MonthPagerAdapter actually provides 4 real fragments. The
        // InfinitePagerAdapter only recycles fragment provided by this
        // MonthPagerAdapter
        final MonthPagerAdapter pagerAdapter = new MonthPagerAdapter(
                getChildFragmentManager());

        // Provide initial data to the fragments, before they are attached to
        // view.
        fragments = pagerAdapter.getFragments();

        for (int i = 0; i < NUMBER_OF_PAGES; i++) {
            DateGridFragment dateGridFragment = fragments.get(i);
            CaldroidGridAdapter adapter = datePagerAdapters.get(i);
            dateGridFragment.setGridViewRes(getGridViewRes());
            dateGridFragment.setGridAdapter(adapter);
            dateGridFragment.setOnItemClickListener(getDateItemClickListener());
            dateGridFragment.setOnItemLongClickListener(getDateItemLongClickListener());
        }

        // Setup InfinitePagerAdapter to wrap around MonthPagerAdapter
        InfinitePagerAdapter infinitePagerAdapter = new InfinitePagerAdapter(
                pagerAdapter);

        // Use the infinitePagerAdapter to provide data for dateViewPager
        dateViewPager.setAdapter(infinitePagerAdapter);

        // Setup pageChangeListener
        dateViewPager.setOnPageChangeListener(pageChangeListener);
    }

    /**
     * To display the week day title
     *
     * @return "SUN, MON, TUE, WED, THU, FRI, SAT"
     */
    protected ArrayList<String> getDaysOfWeek() {
        ArrayList<String> list = new ArrayList<String>();

        SimpleDateFormat fmt = new SimpleDateFormat("EEE", Locale.getDefault());

        // 17 Feb 2013 is Sunday
        DateTime sunday = new DateTime(2013, 2, 17, 0, 0, 0, 0);
        DateTime nextDay = sunday.plusDays(startDayOfWeek - SUNDAY);

        for (int i = 0; i < 7; i++) {
            Date date = CalendarHelper.convertDateTimeToDate(nextDay);
            list.add(fmt.format(date).toUpperCase());
            nextDay = nextDay.plusDays(1);
        }

        return list;
    }

    /**
     * DatePageChangeListener refresh the date grid views when user swipe the
     * calendar
     *
     * @author thomasdao
     */
    public class DatePageChangeListener implements OnPageChangeListener {
        private int currentPage = InfiniteViewPager.OFFSET;
        private DateTime currentDateTime;
        private ArrayList<CaldroidGridAdapter> caldroidGridAdapters;

        /**
         * Return currentPage of the dateViewPager
         */
        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        /**
         * Return currentDateTime of the selected page
         */
        public DateTime getCurrentDateTime() {
            return currentDateTime;
        }

        public void setCurrentDateTime(DateTime dateTime) {
            this.currentDateTime = dateTime;
            setCalendarDateTime(currentDateTime);
        }

        /**
         * Return 4 adapters
         */
        public ArrayList<CaldroidGridAdapter> getCaldroidGridAdapters() {
            return caldroidGridAdapters;
        }

        public void setCaldroidGridAdapters(
                ArrayList<CaldroidGridAdapter> caldroidGridAdapters) {
            this.caldroidGridAdapters = caldroidGridAdapters;
        }

        /**
         * Return virtual next position
         */
        private int getNext(int position) {
            return (position + 1) % CaldroidFragment.NUMBER_OF_PAGES;
        }

        /**
         * Return virtual previous position
         */
        private int getPrevious(int position) {
            return (position + 3) % CaldroidFragment.NUMBER_OF_PAGES;
        }

        /**
         * Return virtual current position
         */
        public int getCurrent(int position) {
            return position % CaldroidFragment.NUMBER_OF_PAGES;
        }

        @Override
        public void onPageScrollStateChanged(int position) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        public void refreshAdapters(int position) {
            // Get adapters to refresh
            CaldroidGridAdapter currentAdapter = caldroidGridAdapters
                    .get(getCurrent(position));
            CaldroidGridAdapter prevAdapter = caldroidGridAdapters
                    .get(getPrevious(position));
            CaldroidGridAdapter nextAdapter = caldroidGridAdapters
                    .get(getNext(position));

            if (position == currentPage) {
                // Refresh current adapter

                currentAdapter.setAdapterDateTime(currentDateTime);
                currentAdapter.notifyDataSetChanged();

                // Refresh previous adapter
                prevAdapter.setAdapterDateTime(currentDateTime.minus(0, 1, 0,
                        0, 0, 0, 0, DateTime.DayOverflow.LastDay));
                prevAdapter.notifyDataSetChanged();

                // Refresh next adapter
                nextAdapter.setAdapterDateTime(currentDateTime.plus(0, 1, 0, 0,
                        0, 0, 0, DateTime.DayOverflow.LastDay));
                nextAdapter.notifyDataSetChanged();
            }
            // Detect if swipe right or swipe left
            // Swipe right
            else if (position > currentPage) {
                // Update current date time to next month
                currentDateTime = currentDateTime.plus(0, 1, 0, 0, 0, 0, 0,
                        DateTime.DayOverflow.LastDay);

                // Refresh the adapter of next gridview
                nextAdapter.setAdapterDateTime(currentDateTime.plus(0, 1, 0, 0,
                        0, 0, 0, DateTime.DayOverflow.LastDay));
                nextAdapter.notifyDataSetChanged();

            }
            // Swipe left
            else {
                // Update current date time to previous month
                currentDateTime = currentDateTime.minus(0, 1, 0, 0, 0, 0, 0,
                        DateTime.DayOverflow.LastDay);

                // Refresh the adapter of previous gridview
                prevAdapter.setAdapterDateTime(currentDateTime.minus(0, 1, 0,
                        0, 0, 0, 0, DateTime.DayOverflow.LastDay));
                prevAdapter.notifyDataSetChanged();
            }

            // Update current page
            currentPage = position;
        }

        /**
         * Refresh the fragments
         */
        @Override
        public void onPageSelected(int position) {
            refreshAdapters(position);

            // Update current date time of the selected page
            setCalendarDateTime(currentDateTime);

            // Update all the dates inside current month
            CaldroidGridAdapter currentAdapter = caldroidGridAdapters
                    .get(position % CaldroidFragment.NUMBER_OF_PAGES);

            // Refresh dateInMonthsList
            dateInMonthsList.clear();
            dateInMonthsList.addAll(currentAdapter.getDatetimeList());
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
