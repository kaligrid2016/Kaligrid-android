package com.kaligrid.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.DateTimeKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.kaligrid.R;
import com.kaligrid.fragment.DatePickerFragment;
import com.kaligrid.fragment.TimePickerFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public abstract class NewEventBaseActivity extends AppCompatActivity {

    private static final String TAG = NewEventBaseActivity.class.getSimpleName();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, MMM d, yyyy");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("h:mm a");

    @Bind(R.id.text_title) TextView titleText;
    @Bind(R.id.edit_text_event_title) EditText editTextEventTitle;
    @Bind(R.id.text_from_date) TextView textFromDate;
    @Bind(R.id.text_from_time) TextView textFromTime;
    @Bind(R.id.text_to_date) TextView textToDate;
    @Bind(R.id.text_to_time) TextView textToTime;

    private final DatePickerDialog.OnDateSetListener FROM_DATE_SET_LISTENER = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            handleOnDateSet(textFromDate, year, month, day);
        }
    };

    private final TimePickerDialog.OnTimeSetListener FROM_TIME_SET_LISTENER = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            handleOnTimeSet(textFromTime, hourOfDay, minute);
        }
    };

    private final DatePickerDialog.OnDateSetListener TO_DATE_SET_LISTENER = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            handleOnDateSet(textToDate, year, month, day);
        }
    };

    private final TimePickerDialog.OnTimeSetListener TO_TIME_SET_LISTENER = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            handleOnTimeSet(textToTime, hourOfDay, minute);
        }
    };

    abstract protected int getContentView();
    abstract protected String getActivityTitle();
    abstract protected String getEventTitleHint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        addTopToolbar();
        ButterKnife.bind(this);
        initializeDateTimeTextViews(Calendar.getInstance());
        titleText.setText(getActivityTitle());
        editTextEventTitle.setHint(getEventTitleHint());
    }

    @OnClick(R.id.button_cancel)
    public void onCancelButtonClick(View v) {
        finish();
    }

    @OnClick({ R.id.text_from_date, R.id.text_to_date })
    public void onFromDateTextClick(TextView v) {
        Bundle bundle = new Bundle();
        bundle.putLong(DatePickerFragment.FIELD_INITIAL_DATE, readDate(v).getTime());

        DialogFragment newFragment = DatePickerFragment.newInstance(
                (v.getId() == R.id.text_from_date) ? FROM_DATE_SET_LISTENER : TO_DATE_SET_LISTENER);
        newFragment.setArguments(bundle);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @OnClick({ R.id.text_from_time, R.id.text_to_time })
    public void onFromTimeTextClick(TextView v) {
        Bundle bundle = new Bundle();
        bundle.putLong(TimePickerFragment.FIELD_INITIAL_TIME, readTime(v).getTime());

        DialogFragment newFragment = TimePickerFragment.newInstance(
                (v.getId() == R.id.text_from_time) ? FROM_TIME_SET_LISTENER : TO_TIME_SET_LISTENER);
        newFragment.setArguments(bundle);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private void addTopToolbar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_new_event_top);
    }

    private void initializeDateTimeTextViews(Calendar calendar) {
        calendar.set(Calendar.MINUTE, 0);

        Date today = calendar.getTime();
        textFromDate.setText(DATE_FORMAT.format(today));
        textFromTime.setText(TIME_FORMAT.format(today));
        textToDate.setText(DATE_FORMAT.format(today));

        calendar.add(Calendar.HOUR, 1);
        textToTime.setText(TIME_FORMAT.format(calendar.getTime()));
    }

    private void handleOnDateSet(TextView dateTextView, int year, int month, int day) {
        CharSequence oldDateText = dateTextView.getText();
        dateTextView.setText(DATE_FORMAT.format(createDate(year, month, day)));
        if (!isDateRangeValid()) {
            showDateRangeError();
            // Restore the old date text if the new date is invalid.
            dateTextView.setText(oldDateText);
        }
    }

    private void handleOnTimeSet(TextView timeTextView, int hourOfDay, int minute) {
        CharSequence oldDateText = timeTextView.getText();
        timeTextView.setText(TIME_FORMAT.format(createTime(hourOfDay, minute)));
        if (!isDateRangeValid()) {
            showDateRangeError();
            // Restore the old date text if the new date is invalid.
            timeTextView.setText(oldDateText);
        }
    }

    private static Date createDate(int year, int month, int day) {
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(year, month, day);
        return selectedDate.getTime();
    }

    private static Date createTime(int hourOfDay, int minute) {
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(Calendar.HOUR, hourOfDay);
        selectedDate.set(Calendar.MINUTE, minute);
        return selectedDate.getTime();
    }

    private static Date readDate(TextView dateTextView) {
        try {
            return DATE_FORMAT.parse(dateTextView.getText().toString());
        } catch (ParseException e) {
            Log.w(TAG, "Failed to parse date text: " + dateTextView.getText());
            return new Date();
        }
    }

    private static Date readTime(TextView timeTextView) {
        try {
            return TIME_FORMAT.parse(timeTextView.getText().toString());
        } catch (ParseException e) {
            Log.w(TAG, "Failed to parse time text: " + timeTextView.getText());
            return new Date();
        }
    }

    private boolean isDateRangeValid() {
        return true;
    }

    private void showDateRangeError() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.error_invalid_date_range_title))
                .setMessage(getResources().getString(R.string.error_invalid_date_range_message))
                .setPositiveButton(getResources().getString(R.string.dialog_button_ok), null)
                .create().show();
    }
}
