package com.kaligrid.activity;

import com.kaligrid.R;
import com.kaligrid.model.Event;
import com.kaligrid.model.EventType;
import com.kaligrid.util.DateTimeUtil;

public class NewReminderActivity extends NewEventBaseActivity {

    @Override
    protected int getContentView() {
        return R.layout.activity_new_reminder;
    }

    @Override
    protected String getActivityTitle() {
        return getResources().getString((mode == Mode.CREATE) ?
                R.string.new_reminder_activity_title : R.string.new_reminder_activity_title_edit);
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
                .startDateTime(DateTimeUtil.toMillis(readDateTime(fromDateText, fromTimeText)))
                .endDateTime(DateTimeUtil.toMillis(readDateTime(toDateText, toTimeText)))
                .isAllDayEvent(allDaySwitch.isChecked())
                .isSelfIncluded(true)
                .build();
    }
}
