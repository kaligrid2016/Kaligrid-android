package com.kaligrid.activity;

import com.kaligrid.R;

public class AddEventActivity extends AddEventBaseActivity {

    @Override
    protected int getContentView() {
        return R.layout.activity_add_event;
    }

    @Override
    protected String getActivityTitle() {
        return "New Event";
    }
}
