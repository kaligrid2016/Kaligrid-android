package com.kaligrid.activity;

import com.kaligrid.R;

public class NewEventActivity extends NewEventBaseActivity {

    @Override
    protected int getContentView() {
        return R.layout.activity_new_event;
    }

    @Override
    protected String getActivityTitle() {
        return getResources().getString(R.string.new_event_activity_title);
    }

    @Override
    protected String getSaveButtonText() {
        return getResources().getString(R.string.new_event_save_button_text);
    }

    @Override
    protected String getEventTitleHint() {
        return getResources().getString(R.string.new_event_title_hint);
    }

    @Override
    protected int getPictureButtonImage() {
        return R.drawable.icon_pic_event;
    }

    @Override
    protected int getLocationButtonImage() {
        return R.drawable.icon_location_event;
    }
}
