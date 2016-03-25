package com.kaligrid.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
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

    @Bind(R.id.title_text) TextView titleText;
    @Bind(R.id.save_button) TextView saveButton;
    @Bind(R.id.event_title_text) EditText eventTitleText;
    @Bind(R.id.picture_button) ImageView pictureButton;
    @Bind(R.id.location_button) ImageView locationButton;
    @Bind(R.id.switch_all_day) Switch allDaySwitch;
    @Bind(R.id.from_date_text) TextView fromDateText;
    @Bind(R.id.from_time_text) TextView fromTimeText;
    @Bind(R.id.to_date_text) TextView toDateText;
    @Bind(R.id.to_time_text) TextView toTimeText;

    private final DatePickerDialog.OnDateSetListener FROM_DATE_SET_LISTENER = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            handleOnDateSet(fromDateText, year, month, day);
        }
    };

    private final TimePickerDialog.OnTimeSetListener FROM_TIME_SET_LISTENER = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            handleOnTimeSet(fromTimeText, hourOfDay, minute);
        }
    };

    private final DatePickerDialog.OnDateSetListener TO_DATE_SET_LISTENER = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            handleOnDateSet(toDateText, year, month, day);
        }
    };

    private final TimePickerDialog.OnTimeSetListener TO_TIME_SET_LISTENER = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            handleOnTimeSet(toTimeText, hourOfDay, minute);
        }
    };

    abstract protected int getContentView();
    abstract protected String getActivityTitle();
    abstract protected String getSaveButtonText();
    abstract protected String getEventTitleHint();
    abstract protected int getPictureButtonImage();
    abstract protected int getLocationButtonImage();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        ButterKnife.bind(this);
        initlaizeTopToolbar();
        initializeDateTimeTextViews(Calendar.getInstance());

        eventTitleText.setHint(getEventTitleHint());
        pictureButton.setImageResource(getPictureButtonImage());
        locationButton.setImageResource(getLocationButtonImage());
        allDaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fromTimeText.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                toTimeText.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            }
        });
    }

    @OnClick(R.id.button_cancel)
    public void onCancelButtonClick(View v) {
        finish();
    }

    @OnClick({ R.id.from_date_text, R.id.to_date_text})
    public void onFromDateTextClick(TextView v) {
        Bundle bundle = new Bundle();
        bundle.putLong(DatePickerFragment.FIELD_INITIAL_DATE, readDate(v).getTime());

        DialogFragment newFragment = DatePickerFragment.newInstance(
                (v.getId() == R.id.from_date_text) ? FROM_DATE_SET_LISTENER : TO_DATE_SET_LISTENER);
        newFragment.setArguments(bundle);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @OnClick({ R.id.from_time_text, R.id.to_time_text})
    public void onFromTimeTextClick(TextView v) {
        Bundle bundle = new Bundle();
        bundle.putLong(TimePickerFragment.FIELD_INITIAL_TIME, readTime(v).getTime());

        DialogFragment newFragment = TimePickerFragment.newInstance(
                (v.getId() == R.id.from_time_text) ? FROM_TIME_SET_LISTENER : TO_TIME_SET_LISTENER);
        newFragment.setArguments(bundle);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private void initlaizeTopToolbar() {
        Toolbar actionBar = (Toolbar) findViewById(R.id.toolbar_new_event_top);
        setSupportActionBar(actionBar);

        titleText.setText(getActivityTitle());
        saveButton.setText(getSaveButtonText());
    }

    private void initializeDateTimeTextViews(Calendar calendar) {
        calendar.set(Calendar.MINUTE, 0);

        Date today = calendar.getTime();
        fromDateText.setText(DATE_FORMAT.format(today));
        fromTimeText.setText(TIME_FORMAT.format(today));
        toDateText.setText(DATE_FORMAT.format(today));

        calendar.add(Calendar.HOUR, 1);
        toTimeText.setText(TIME_FORMAT.format(calendar.getTime()));
    }

    private void handleOnDateSet(TextView dateText, int year, int month, int day) {
        dateText.setText(DATE_FORMAT.format(createDate(year, month, day)));
    }

    private void handleOnTimeSet(TextView timeText, int hourOfDay, int minute) {
        timeText.setText(TIME_FORMAT.format(createTime(hourOfDay, minute)));
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

    private static Date readDate(TextView dateText) {
        try {
            return DATE_FORMAT.parse(dateText.getText().toString());
        } catch (ParseException e) {
            Log.w(TAG, "Failed to parse date text: " + dateText.getText());
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

    private static Date mergeDateAndTime(Date date, Date time) {
        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(time);

        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(date);
        dateCal.set(Calendar.HOUR, timeCal.get(Calendar.HOUR));
        dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));

        return dateCal.getTime();
    }

    private boolean isDateRangeValid() {
        Date fromDate = readDate(fromDateText);
        Date fromTime = readTime(fromTimeText);
        Date fromDateTime = mergeDateAndTime(fromDate, fromTime);

        Date toDate = readDate(toDateText);
        Date toTime = readTime(toTimeText);
        Date toDateTime = mergeDateAndTime(toDate, toTime);

        return fromDateTime.getTime() < toDateTime.getTime();
    }

    private void showDateRangeError() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.error_invalid_date_range_title))
                .setMessage(getResources().getString(R.string.error_invalid_date_range_message))
                .setPositiveButton(getResources().getString(R.string.dialog_button_ok), null)
                .create().show();
    }
}
