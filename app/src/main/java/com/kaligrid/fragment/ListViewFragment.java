package com.kaligrid.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.kaligrid.R;
import com.kaligrid.fragment.calendar.CalendarFragment;

import java.util.List;

public class ListViewFragment extends TypedBaseFragment {

    private Context context;

    public static ListViewFragment newInstance(Context context) {
        ListViewFragment fragment = new ListViewFragment();
        fragment.setContext(context);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);

        initializeCalendar();

        return view;
    }

    @Override
    public ContentViewType getType() {
        return ContentViewType.LIST;
    }

    private void initializeCalendar() {
        FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.calendar, new CalendarFragment());
        transaction.commit();
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
