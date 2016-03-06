package com.kaligrid.fragment.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kaligrid.R;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.util.Calendar;
import java.util.Date;

public class CalendarFragment extends CaldroidFragment {

    public CalendarFragment() {
        setArguments(getBundle());
    }

    private Bundle getBundle() {
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        return args;
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

    static class CalendarListener  extends CaldroidListener {

        private CaldroidFragment caldroidFragment;

        public CalendarListener(CaldroidFragment caldroidFragment) {
            this.caldroidFragment = caldroidFragment;
        }

        @Override
        public void onSelectDate(Date date, View view) {

        }

        @Override
        public void onCaldroidViewCreated() {
            TextView monthTitle = caldroidFragment.getMonthTitleTextView();
            monthTitle.setTextAppearance(R.style.CalendarMonthTitleText);
            caldroidFragment.setMonthTitleTextView(monthTitle);
            caldroidFragment.refreshView();
        }
    }
}
