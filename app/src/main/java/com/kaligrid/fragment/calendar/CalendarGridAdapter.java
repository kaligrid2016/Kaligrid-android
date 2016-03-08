package com.kaligrid.fragment.calendar;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kaligrid.R;
import com.roomorama.caldroid.CaldroidGridAdapter;

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
            convertView = inflater.inflate(R.layout.calendar_cell, null);
        }

        TextView dateText = (TextView) convertView.findViewById(R.id.calendar_date_text);

        // Get dateTime of this cell
        DateTime dateTime = this.datetimeList.get(position);
        Resources resources = context.getResources();

        // Set color of the dates in previous / next month
        if (dateTime.getMonth() != month) {
            dateText.setTextAppearance(R.style.CalendarDateInactiveText);
        }

        if (isDateSelected(dateTime)) {
            convertView.setBackgroundColor(resources.getColor(com.caldroid.R.color.caldroid_sky_blue));
        } else {
            // Customize for today
            if (dateTime.equals(getToday())) {
                dateText.setTextAppearance(R.style.CalendarDateTodayText);
                convertView.setBackgroundResource(R.drawable.calendar_cell_today_background);
            }
        }

        dateText.setText(dateTime.getDay().toString());

        // Set custom color if required
        setCustomResources(dateTime, convertView, dateText);

        return convertView;
    }

    private boolean isDateSelected(DateTime dateTime) {
        return (selectedDates != null) && (selectedDates.contains(dateTime));
    }
}
