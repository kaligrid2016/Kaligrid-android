package com.kaligrid.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaligrid.R;
import com.kaligrid.adapter.CalendarGridAdapter;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

import java.util.Date;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

public class CalendarFragment extends CaldroidFragment {

    public CalendarFragment() {
        Bundle args = new Bundle();
        DateTime today = DateTime.today(TimeZone.getDefault());
        args.putInt(CaldroidFragment.MONTH, today.getMonth());
        args.putInt(CaldroidFragment.YEAR, today.getYear());
        args.putInt(CaldroidFragment.THEME_RESOURCE, R.style.Calendar);

        setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        setEnableSwipe(false);
        setShowNavigationArrows(false);
        setSelectedDate(new Date());
//        setCaldroidListener(new CalendarListener(this));

        return view;
    }

    @Override
    public CalendarGridAdapter getNewDatesGridAdapter(int month, int year) {
        return new CalendarGridAdapter(getActivity(), month, year, getCaldroidData(), extraData);
    }

    public void showMonthView(DateTime selectedDate) {
        clearSelectedDates();
        setSelectedDate(new Date(selectedDate.getMilliseconds(TimeZone.getDefault())));
        refreshView();
    }

    public void showWeekView(DateTime selectedDate) {
        clearSelectedDates();
        setSelectedDate(new Date(selectedDate.getMilliseconds(TimeZone.getDefault())));

        refreshMonthTitleTextView();

        // Refresh the date grid views
        for (CaldroidGridAdapter adapter : datePagerAdapters) {
            if (!(adapter instanceof CalendarGridAdapter)) {
                continue;
            }

            CalendarGridAdapter calendarGridAdapter = ((CalendarGridAdapter) adapter);

            if (selectedDate.getMonth() == calendarGridAdapter.getMonth()) {
                calendarGridAdapter.resetDatetimeListToCurrentWeek(selectedDate);

                // Reset extra data
                adapter.setExtraData(extraData);

                // Update today variable
                adapter.updateToday();

                // Refresh view
                adapter.notifyDataSetChanged();
            }
        }
    }

//    static class CalendarListener extends CaldroidListener {
//
//        private CaldroidFragment caldroidFragment;
//
//        public CalendarListener(CaldroidFragment caldroidFragment) {
//            this.caldroidFragment = caldroidFragment;
//        }
//
//        @Override
//        public void onSelectDate(Date date, View view) {
//            Toast.makeText(caldroidFragment.getContext(), "Hello from listener!", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onCaldroidViewCreated() {
//        }
//    }
}
