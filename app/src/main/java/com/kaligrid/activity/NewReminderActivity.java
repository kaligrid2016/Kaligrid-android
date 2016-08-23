package com.kaligrid.activity;

import com.kaligrid.R;
import com.kaligrid.model.Event;
import com.kaligrid.model.EventType;

import java.util.TimeZone;

public class NewReminderActivity extends NewEventBaseActivity {

    @Override
    protected int getContentView() {
        return R.layout.activity_new_reminder;
    }

    @Override
    protected String getActivityTitle() {
        return getResources().getString(R.string.new_reminder_activity_title);
    }

    @Override
    protected String getSaveButtonText() {
        return getResources().getString(R.string.new_reminder_save_button_text);
    }

    @Override
    protected String getEventTitleHint() {
        return getResources().getString(R.string.new_reminder_title_hint);
    }

    @Override
    protected int getPictureButtonImage() {
        return R.drawable.icon_pic_reminder;
    }

    @Override
    protected int getLocationButtonImage() {
        return R.drawable.icon_location_reminder;
    }

    @Override
    protected Event createEventFromFields() {
        return new Event.Builder()
                .user("Me")
                .title(eventTitleText.getText().toString())
                .type(EventType.REMINDER)
                .startDateTime(readDateTime(fromDateText, fromTimeText).getMilliseconds(TimeZone.getDefault()))
                .endDateTime(readDateTime(toDateText, toTimeText).getMilliseconds(TimeZone.getDefault()))
                .isAllDayEvent(allDaySwitch.isChecked())
                .isSelfIncluded(true)
                .build();
    }
}
