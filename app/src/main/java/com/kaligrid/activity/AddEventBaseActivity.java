package com.kaligrid.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.kaligrid.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public abstract class AddEventBaseActivity extends AppCompatActivity {

    @Bind(R.id.text_title) TextView titleText;

    abstract protected int getContentView();
    abstract protected String getActivityTitle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        addTopToolbar();
        ButterKnife.bind(this);
        titleText.setText(getActivityTitle());
    }

    private void addTopToolbar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_add_event_top);
    }

    @OnClick(R.id.button_cancel)
    public void onCancelButtonClick(View v) {
        finish();
    }
}
