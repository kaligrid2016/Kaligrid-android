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
    protected String getSaveButtonText() {
        return getResources().getString(R.string.new_fyi_save_button_text);
    }

    @Override
    protected String getEventTitleHint() {
        return getResources().getString(R.string.new_fyi_title_hint);
    }

    @Override
    protected int getPictureButtonImage() {
        return R.drawable.icon_pic_fyi;
    }

    @Override
    protected int getLocationButtonImage() {
        return R.drawable.icon_location_fyi;
    }
}
