package com.kaligrid.activity.event.edit;

import android.view.View;
import android.widget.Toast;

import com.kaligrid.R;
import com.kaligrid.model.Event;
import com.kaligrid.model.EventType;
import com.kaligrid.util.DateTimeUtil;

import butterknife.OnClick;

public class EditFyiActivity extends EditBaseActivity {

    @Override
    protected int getContentView() {
        return R.layout.activity_edit_fyi;
    }

    @Override
    protected String getActivityTitle() {
        return getResources().getString((mode == Mode.CREATE) ?
                R.string.new_fyi_activity_title : R.string.new_fyi_activity_title_edit);
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

    @Override
    protected Event createEventFromFields() {
        return new Event.Builder()
                .user("Me")
                .title(eventTitleText.getText().toString())
                .type(EventType.FYI)
                .startDateTime(DateTimeUtil.toMillis(readDateTime(fromDateText, fromTimeText)))
                .endDateTime(DateTimeUtil.toMillis(readDateTime(toDateText, toTimeText)))
                .isAllDayEvent(allDaySwitch.isChecked())
                .isSelfIncluded(true)
                .build();
    }

    @OnClick(R.id.recipients_button)
    public void onRecipientsButtonClick(View v) {
        Toast.makeText(this, "This feature is not implemented yet...", Toast.LENGTH_LONG).show();
    }
}
