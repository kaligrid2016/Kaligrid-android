package com.kaligrid.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.kaligrid.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewEventMenuActivity extends Activity {

    @Bind(R.id.new_event_menu_layout) RelativeLayout newEventMenuLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event_menu);
        ButterKnife.bind(this);

        newEventMenuLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

    @OnClick({R.id.cancel_button, R.id.cancel_button_text})
    public void onAddButtonClick(View v) {
        finishActivity();
    }

    @OnClick({R.id.new_fyi_button, R.id.new_fyi_button_text})
    public void onAddFyiButtonClick(View v) {
        finishActivity();
        startActivity(new Intent(this, NewFyiActivity.class));
    }

    @OnClick({R.id.new_reminder_button, R.id.new_reminder_button_text})
    public void onAddReminderButtonClick(View v) {
        finishActivity();
        startActivity(new Intent(this, NewReminderActivity.class));
    }

    @OnClick({R.id.new_event_button, R.id.new_event_button_text})
    public void onAddEventButtonClick(View v) {
        finishActivity();
        startActivity(new Intent(this, NewEventActivity.class));
    }

    private void finishActivity() {
        finish();
        overridePendingTransition(0, 0);
    }
}
