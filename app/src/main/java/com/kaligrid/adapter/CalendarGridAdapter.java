package com.kaligrid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kaligrid.R;
import com.kaligrid.calendar.adapter.CaldroidGridAdapter;
import com.kaligrid.util.ViewHelper;

import java.util.ArrayList;
import java.util.Map;

import hirondelle.date4j.DateTime;

public class CalendarGridAdapter extends CaldroidGridAdapter {

    public CalendarGridAdapter(Context context, int month, int year,
                               Map<String, Object> caldroidData, Map<String, Object> extraData) {
        super(context, month, year, caldroidData, extraData);
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

    public void resetDatetimeListToCurrentWeek(DateTime selectedDate) {
        this.datetimeList = new ArrayList<>();

        for (int i = 1; i <= 7; i++) {
            DateTime dateTime = selectedDate.minusDays(selectedDate.getWeekDay() - i);
            datetimeList.add(dateTime);
        }
    }

    public int getMonth() {
        return month;
    }

    private boolean isDateSelected(DateTime dateTime) {
        if (selectedDates == null) {
            return false;
        }

        for (DateTime date : selectedDates) {
            if (dateTime.isSameDayAs(date)) {
                return true;
            }
        }

        return false;
    }
}
