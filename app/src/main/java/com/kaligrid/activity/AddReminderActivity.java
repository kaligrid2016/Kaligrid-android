package com.kaligrid.activity;

import com.kaligrid.R;

public class AddReminderActivity extends AddEventBaseActivity {

    @Override
    protected int getContentView() {
        return R.layout.activity_add_reminder;
    }

    @Override
    protected String getActivityTitle() {
        return "New Reminder";
    }
}
