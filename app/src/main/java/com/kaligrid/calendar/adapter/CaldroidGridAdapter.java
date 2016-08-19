package com.kaligrid.calendar.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kaligrid.R;
import com.kaligrid.calendar.CaldroidFragment;
import com.kaligrid.calendar.CalendarHelper;
import com.kaligrid.util.ViewHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hirondelle.date4j.DateTime;

/**
 * The CaldroidGridAdapter provides customized view for the dates gridview
 *
 * @author thomasdao
 */
public class CaldroidGridAdapter extends BaseAdapter {

    protected List<DateTime> datetimeList;
    protected int month;
    protected int year;
    protected Context context;
    protected List<DateTime> disableDates;
    protected DateTime selectedDate;

    // Use internally, to make the search for date faster instead of using
    // indexOf methods on ArrayList
    protected Map<DateTime, Integer> disableDatesMap = new HashMap<>();

    protected DateTime minDateTime;
    protected DateTime maxDateTime;
    protected DateTime today;
    protected int startDayOfWeek;
    protected boolean sixWeeksInCalendar;
    protected boolean squareTextViewCell;
    protected int themeResource;
    protected Resources resources;

    protected int defaultCellBackgroundRes = -1;
    protected ColorStateList defaultTextColorRes;

    /**
     * caldroidData belongs to Caldroid
     */
    protected Map<String, Object> caldroidData;
    /**
     * extraData belongs to client
     */
    protected Map<String, Object> extraData;

	protected LayoutInflater localInflater;

    public void setAdapterDateTime(DateTime dateTime) {
        this.month = dateTime.getMonth();
        this.year = dateTime.getYear();
        this.datetimeList = CalendarHelper.getFullWeeks(this.month, this.year,
                startDayOfWeek, sixWeeksInCalendar);
    }

    // GETTERS AND SETTERS
    public List<DateTime> getDatetimeList() {
        return datetimeList;
    }

    public DateTime getMinDateTime() {
        return minDateTime;
    }

    public void setMinDateTime(DateTime minDateTime) {
        this.minDateTime = minDateTime;
    }

    public DateTime getMaxDateTime() {
        return maxDateTime;
    }

    public void setMaxDateTime(DateTime maxDateTime) {
        this.maxDateTime = maxDateTime;
    }

    public List<DateTime> getDisableDates() {
        return disableDates;
    }

    public void setDisableDates(ArrayList<DateTime> disableDates) {
        this.disableDates = disableDates;
    }

    public DateTime getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(DateTime selectedDate) {
        this.selectedDate = selectedDate;
    }

    public int getThemeResource() {
        return themeResource;
    }

    public Map<String, Object> getCaldroidData() {
        return caldroidData;
    }

    public void setCaldroidData(Map<String, Object> caldroidData, CaldroidFragment.ViewMode viewMode) {
        this.caldroidData = caldroidData;

        // Reset parameters
        populateFromCaldroidData(viewMode);
    }

    public Map<String, Object> getExtraData() {
        return extraData;
    }

    public void setExtraData(Map<String, Object> extraData) {
        this.extraData = extraData;
    }

    public CaldroidGridAdapter(Context context, int month, int year,
                               Map<String, Object> caldroidData,
                               Map<String, Object> extraData) {
        super();
        this.month = month;
        this.year = year;
        this.context = context;
        this.caldroidData = caldroidData;
        this.extraData = extraData;
        this.resources = context.getResources();

        // Get data from caldroidData
        populateFromCaldroidData(CaldroidFragment.ViewMode.MONTH_VIEW);

	    LayoutInflater inflater = (LayoutInflater) context
			    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    localInflater = CaldroidFragment.getThemeInflater(context, inflater, themeResource);
    }

    /**
     * Retrieve internal parameters from caldroid data
     */
    @SuppressWarnings("unchecked")
    private void populateFromCaldroidData(CaldroidFragment.ViewMode viewMode) {
        disableDates = (ArrayList<DateTime>) caldroidData
                .get(CaldroidFragment.DISABLE_DATES);
        if (disableDates != null) {
            disableDatesMap.clear();
            for (DateTime dateTime : disableDates) {
                disableDatesMap.put(dateTime, 1);
            }
        }

        selectedDate = (DateTime) caldroidData.get(CaldroidFragment.SELECTED_DATE);

        minDateTime = (DateTime) caldroidData
                .get(CaldroidFragment._MIN_DATE_TIME);
        maxDateTime = (DateTime) caldroidData
                .get(CaldroidFragment._MAX_DATE_TIME);
        startDayOfWeek = (Integer) caldroidData
                .get(CaldroidFragment.START_DAY_OF_WEEK);
        sixWeeksInCalendar = (Boolean) caldroidData
                .get(CaldroidFragment.SIX_WEEKS_IN_CALENDAR);
        squareTextViewCell = (Boolean) caldroidData
                .get(CaldroidFragment.SQUARE_TEXT_VIEW_CELL);

        // Get theme
        themeResource = (Integer) caldroidData
                .get(CaldroidFragment.THEME_RESOURCE);

        if (viewMode == CaldroidFragment.ViewMode.WEEK_VIEW) {
            this.datetimeList = CalendarHelper.getSingleWeek(selectedDate);
        } else {
            this.datetimeList = CalendarHelper.getFullWeeks(this.month, this.year, startDayOfWeek, sixWeeksInCalendar);
        }

        getDefaultResources();
    }

    // This method retrieve default resources for background and text color,
    // based on the Caldroid theme
    private void getDefaultResources() {
        Context wrapped = new ContextThemeWrapper(context, themeResource);

        // Get style of normal cell or square cell in the theme
        Resources.Theme theme = wrapped.getTheme();
        TypedValue styleCellVal = new TypedValue();
        if (squareTextViewCell) {
            theme.resolveAttribute(R.attr.styleCaldroidSquareCell, styleCellVal, true);
        } else {
            theme.resolveAttribute(R.attr.styleCaldroidNormalCell, styleCellVal, true);
        }

        // Get default background of cell
        TypedArray typedArray = wrapped.obtainStyledAttributes(styleCellVal.data, R.styleable.Cell);
        defaultCellBackgroundRes = typedArray.getResourceId(R.styleable.Cell_android_background, -1);
        defaultTextColorRes = typedArray.getColorStateList(R.styleable.Cell_android_textColor);
        typedArray.recycle();
    }

    public void updateToday() {
        today = CalendarHelper.convertDateToDateTime(new Date());
    }

    protected DateTime getToday() {
        if (today == null) {
            today = CalendarHelper.convertDateToDateTime(new Date());
        }
        return today;
    }

    public int getMonth() {
        return month;
    }

    @SuppressWarnings("unchecked")
    protected void setCustomResources(DateTime dateTime, View backgroundView,
                                      TextView textView) {
        // Set custom background resource
        Map<DateTime, Drawable> backgroundForDateTimeMap = (Map<DateTime, Drawable>) caldroidData
                .get(CaldroidFragment._BACKGROUND_FOR_DATETIME_MAP);
        if (backgroundForDateTimeMap != null) {
            // Get background resource for the dateTime
            Drawable drawable = backgroundForDateTimeMap.get(dateTime);

            // Set it
            if (drawable != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    backgroundView.setBackground(drawable);
                } else {
                    backgroundView.setBackgroundDrawable(drawable);
                }
            }
        }

        // Set custom text color
        Map<DateTime, Integer> textColorForDateTimeMap = (Map<DateTime, Integer>) caldroidData
                .get(CaldroidFragment._TEXT_COLOR_FOR_DATETIME_MAP);
        if (textColorForDateTimeMap != null) {
            // Get textColor for the dateTime
            Integer textColorResource = textColorForDateTimeMap.get(dateTime);

            // Set it
            if (textColorResource != null) {
                textView.setTextColor(resources.getColor(textColorResource));
            }
        }
    }

    private void resetCustomResources(com.kaligrid.calendar.view.CellView cellView) {
        cellView.setBackgroundResource(defaultCellBackgroundRes);
        cellView.setTextColor(defaultTextColorRes);
    }

    /**
     * Customize colors of text and background based on states of the cell
     * (disabled, active, selected, etc)
     * <p/>
     * To be used only in getView method
     *
     * @param position
     * @param cellView
     */
    protected void customizeTextView(int position, com.kaligrid.calendar.view.CellView cellView) {
        // Get the padding of cell so that it can be restored later
        int topPadding = cellView.getPaddingTop();
        int leftPadding = cellView.getPaddingLeft();
        int bottomPadding = cellView.getPaddingBottom();
        int rightPadding = cellView.getPaddingRight();

        // Get dateTime of this cell
        DateTime dateTime = this.datetimeList.get(position);

        cellView.resetCustomStates();
        resetCustomResources(cellView);

        if (dateTime.equals(getToday())) {
            cellView.addCustomState(com.kaligrid.calendar.view.CellView.STATE_TODAY);
        }

        // Set color of the dates in previous / next month
        if (dateTime.getMonth() != month) {
            cellView.addCustomState(com.kaligrid.calendar.view.CellView.STATE_PREV_NEXT_MONTH);
        }

        // Customize for disabled dates and date outside min/max dates
        if ((minDateTime != null && dateTime.lt(minDateTime))
                || (maxDateTime != null && dateTime.gt(maxDateTime))
                || (disableDates != null && disableDatesMap
                .containsKey(dateTime))) {

            cellView.addCustomState(com.kaligrid.calendar.view.CellView.STATE_DISABLED);
        }

        // Customize for selected dates
        if (selectedDate != null) {
            cellView.addCustomState(com.kaligrid.calendar.view.CellView.STATE_SELECTED);
        }

        cellView.refreshDrawableState();

        // Set text
        cellView.setText(String.valueOf(dateTime.getDay()));

        // Set custom color if required
        setCustomResources(dateTime, cellView, cellView);

        // Somehow after setBackgroundResource, the padding collapse.
        // This is to recover the padding
        cellView.setPadding(leftPadding, topPadding, rightPadding,
                bottomPadding);
    }

    @Override
    public int getCount() {
        return this.datetimeList.size();
    }

    @Override
    public Object getItem(int position) {
        return datetimeList.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // For reuse
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_calendar_cell, parent, false);
        }

        TextView dateText = (TextView) convertView.findViewById(R.id.calendar_date_text);

        // Get dateTime of this cell
        DateTime dateTime = this.datetimeList.get(position);

        // Set color of the dates in previous / next month
        if (dateTime.getMonth() != month) {
            ViewHelper.setTextAppearance(context, dateText, R.style.CalendarDateInactiveText);
        } else {
            ViewHelper.setTextAppearance(context, dateText, R.style.CalendarDateActiveText);
        }

        if (isDateSelected(dateTime)) {
            ViewHelper.setTextAppearance(context, dateText, R.style.CalendarDateTodayText);
            dateText.setBackgroundResource(R.drawable.background_calendar_cell_selected);
        } else {
            // Customize for today
            if (dateTime.isSameDayAs(getToday())) {
                ViewHelper.setTextAppearance(context, dateText, R.style.CalendarDateTodayText);
                dateText.setBackgroundResource(R.drawable.background_calendar_cell_today);
            } else {
                // Remove the background.
                dateText.setBackgroundResource(0);
            }
        }

        dateText.setText(dateTime.getDay().toString());

        // Set custom color if required
        setCustomResources(dateTime, convertView, dateText);

        return convertView;
    }

    private boolean isDateSelected(DateTime dateTime) {
        return (selectedDate != null) && dateTime.isSameDayAs(selectedDate);
    }
}
