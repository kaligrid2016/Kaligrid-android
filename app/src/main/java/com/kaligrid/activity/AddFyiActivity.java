package com.kaligrid.activity;

import com.kaligrid.R;

public class AddFyiActivity extends AddEventBaseActivity {

    @Override
    protected int getContentView() {
        return R.layout.activity_add_fyi;
    }

    @Override
    protected String getActivityTitle() {
        return "New FYI";
    }
}
