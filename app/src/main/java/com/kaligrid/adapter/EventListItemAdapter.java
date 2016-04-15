package com.kaligrid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.kaligrid.R;
import com.kaligrid.model.EventListItem;

import java.util.List;

public class EventListItemAdapter extends ArrayAdapter<EventListItem> {

    private Context context;

    public EventListItemAdapter(Context context, int resource, List<EventListItem> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // For reuse
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_calendar_cell, parent, false);
        }

        return null;
    }
}
