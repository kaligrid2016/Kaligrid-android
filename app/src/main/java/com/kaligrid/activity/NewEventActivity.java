package com.kaligrid.activity;

import com.kaligrid.R;

public class NewEventActivity extends NewEventBaseActivity {

    @Override
    protected int getContentView() {
        return R.layout.activity_new_event;
    }

    @Override
    protected String getActivityTitle() {
        return "New Event";
    }

    @Override
    protected String getEventTitleHint() {
        return getResources().getString(R.string.new_event_title_hint);
    }
}
