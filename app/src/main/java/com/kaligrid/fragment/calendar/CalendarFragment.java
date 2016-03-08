package com.kaligrid.fragment.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kaligrid.R;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;
import com.roomorama.caldroid.CalendarHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import hirondelle.date4j.DateTime;

public class CalendarFragment extends CaldroidFragment {

    public CalendarFragment() {
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putInt(CaldroidFragment.THEME_RESOURCE, R.style.Calendar);

        setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        setShowNavigationArrows(false);
        setCaldroidListener(new CalendarListener(this));

        return view;
    }

    @Override
    public CalendarGridAdapter getNewDatesGridAdapter(int month, int year) {
        return new CalendarGridAdapter(getActivity(), month, year, getCaldroidData(), extraData);
    }

    @Override
    protected ArrayList<String> getDaysOfWeek() {
        ArrayList<String> list = new ArrayList<>();

        SimpleDateFormat fmt = new SimpleDateFormat("EEE", Locale.getDefault());

        // 17 Feb 2013 is Sunday
        DateTime sunday = new DateTime(2013, 2, 17, 0, 0, 0, 0);
        DateTime nextDay = sunday.plusDays(startDayOfWeek - SUNDAY);

        for (int i = 0; i < 7; i++) {
            Date date = CalendarHelper.convertDateTimeToDate(nextDay);
            list.add(fmt.format(date).substring(0, 1).toUpperCase());
            nextDay = nextDay.plusDays(1);
        }

        return list;
    }

    static class CalendarListener extends CaldroidListener {

        private CaldroidFragment caldroidFragment;

        public CalendarListener(CaldroidFragment caldroidFragment) {
            this.caldroidFragment = caldroidFragment;
        }

        @Override
        public void onSelectDate(Date date, View view) {

        }

        @Override
        public void onCaldroidViewCreated() {
        }
    }
}
