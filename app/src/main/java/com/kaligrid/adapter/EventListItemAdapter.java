package com.kaligrid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaligrid.R;
import com.kaligrid.model.eventlist.EventListDateHeaderItem;
import com.kaligrid.model.eventlist.EventListEventItem;
import com.kaligrid.model.eventlist.EventListItem;

import java.util.List;

public class EventListItemAdapter extends ArrayAdapter<EventListItem> {

    private Context context;

    public EventListItemAdapter(Context context, List<EventListItem> objects) {
        super(context, 0, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EventListItem item = getItem(position);

        if (item instanceof EventListEventItem) {
            convertView = getEventView((EventListEventItem) item, parent);
        } else {
            convertView = getDateHeaderView((EventListDateHeaderItem) item, parent);
        }

        return convertView;
    }

    @Override
    public EventListItem getItem(int position) {
        return super.getItem(position);
    }

    private View getEventView(EventListEventItem item, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event_list_event, parent, false);

        ((TextView) view.findViewById(R.id.event_list_event_time_text)).setText(item.getTimeText());
        ((ImageView) view.findViewById(R.id.event_list_event_type_image)).setImageResource(item.getEventTypeImage());
        ((TextView) view.findViewById(R.id.event_list_event_summary_text)).setText(item.getEventSummary());

        return view;
    }

    private View getDateHeaderView(EventListDateHeaderItem item, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event_list_date_header, parent, false);

        TextView dateText = (TextView) view.findViewById(R.id.event_list_date_header_text);
        dateText.setText(item.toString());

        return view;
    }
}
