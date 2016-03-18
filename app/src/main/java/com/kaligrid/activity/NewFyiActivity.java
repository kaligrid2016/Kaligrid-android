package com.kaligrid.activity;

import com.kaligrid.R;

public class NewFyiActivity extends NewEventBaseActivity {

    @Override
    protected int getContentView() {
        return R.layout.activity_new_fyi;
    }

    @Override
    protected String getActivityTitle() {
        return getResources().getString(R.string.new_fyi_activity_title);
    }

    @Override
    protected String getEventTitleHint() {
        return getResources().getString(R.string.new_fyi_title_hint);
    }
}
