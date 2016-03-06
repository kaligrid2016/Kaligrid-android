package com.kaligrid.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaligrid.R;
import com.kaligrid.fragment.calendar.CalendarFragment;

public class ListViewFragment extends TypedBaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_view_fragment, container, false);

        initializeCalendar();

        return view;
    }

    private void initializeCalendar() {
        FragmentTransaction t = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar, new CalendarFragment());
        t.commit();
    }

    @Override
    public ContentViewType getType() {
        return ContentViewType.LIST;
    }
}
