package com.kaligrid.activity;

import com.kaligrid.R;

public class NewReminderActivity extends NewEventBaseActivity {

    @Override
    protected int getContentView() {
        return R.layout.activity_new_reminder;
    }

    @Override
    protected String getActivityTitle() {
        return "New Reminder";
    }

    @Override
    protected String getEventTitleHint() {
        return getResources().getString(R.string.new_reminder_title_hint);
    }
}
