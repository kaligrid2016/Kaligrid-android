package com.kaligrid.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.kaligrid.R;
import com.kaligrid.fragment.DatePickerFragment;
import com.kaligrid.fragment.TimePickerFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hirondelle.date4j.DateTime;

public abstract class NewEventBaseActivity extends AppCompatActivity {

    private static final String TAG = NewEventBaseActivity.class.getSimpleName();
    private static final String DATE_WRITE_FORMAT = "WWW, MMMM D, YYYY";
    private static final String TIME_WRITE_FORMAT = "h12:mm a";
    private static final SimpleDateFormat DATE_READ_FORMAT = new SimpleDateFormat("EEE, MMMM d, yyyy", Locale.getDefault());
    private static final SimpleDateFormat TIME_READ_FORMAT = new SimpleDateFormat("h:mm a", Locale.getDefault());

    @Bind(R.id.title_text) TextView titleText;
    @Bind(R.id.save_button) TextView saveButton;
    @Bind(R.id.event_title_text) EditText eventTitleText;
    @Bind(R.id.picture_button) ImageView pictureButton;
    @Bind(R.id.location_button) ImageView locationButton;
    @Bind(R.id.all_day_switch) SwitchCompat allDaySwitch;
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
        initializeTopToolbar();
        initializeDateTimeTextViews();

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

    @OnClick(R.id.picture_button)
    public void onPictureButtonClick(View v) {
        Toast.makeText(this, "This feature is not implemented yet...", Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.location_button)
    public void onLocationButtonClick(View v) {
        Toast.makeText(this, "This feature is not implemented yet...", Toast.LENGTH_LONG).show();
    }

    @OnClick({ R.id.from_date_text, R.id.to_date_text})
    public void onFromDateTextClick(TextView v) {
        Bundle bundle = new Bundle();
        bundle.putLong(DatePickerFragment.FIELD_INITIAL_DATE, readDate(v).getMilliseconds(TimeZone.getDefault()));

        DialogFragment newFragment = DatePickerFragment.newInstance(
                (v.getId() == R.id.from_date_text) ? FROM_DATE_SET_LISTENER : TO_DATE_SET_LISTENER);
        newFragment.setArguments(bundle);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @OnClick({ R.id.from_time_text, R.id.to_time_text})
    public void onFromTimeTextClick(TextView v) {
        Bundle bundle = new Bundle();
        bundle.putLong(TimePickerFragment.FIELD_INITIAL_TIME, readTime(v).getMilliseconds(TimeZone.getDefault()));

        DialogFragment newFragment = TimePickerFragment.newInstance(
                (v.getId() == R.id.from_time_text) ? FROM_TIME_SET_LISTENER : TO_TIME_SET_LISTENER);
        newFragment.setArguments(bundle);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private void initializeTopToolbar() {
        Toolbar actionBar = (Toolbar) findViewById(R.id.toolbar_new_event_top);
        setSupportActionBar(actionBar);

        titleText.setText(getActivityTitle());
        saveButton.setText(getSaveButtonText());
    }

    private void initializeDateTimeTextViews() {
        // Create initial date without minutes and seconds.
        DateTime now = DateTime.now(TimeZone.getDefault());
        DateTime initialDate = new DateTime(now.getYear(), now.getMonth(), now.getDay(), now.getHour(), 0, 0, 0);

        fromDateText.setText(initialDate.format(DATE_WRITE_FORMAT, Locale.getDefault()));
        fromTimeText.setText(initialDate.format(TIME_WRITE_FORMAT, Locale.getDefault()));

        toDateText.setText(initialDate.format(DATE_WRITE_FORMAT, Locale.getDefault()));
        toTimeText.setText(initialDate.plus(0, 0, 0, 1, 0, 0, 0, DateTime.DayOverflow.FirstDay).format(TIME_WRITE_FORMAT, Locale.getDefault()));
    }

    private void handleOnDateSet(TextView dateText, int year, int month, int day) {
        // Android month starts from 0 but Date4J starts from 1.
        dateText.setText(DateTime.forDateOnly(year, month + 1, day).format(DATE_WRITE_FORMAT, Locale.getDefault()));
    }

    private void handleOnTimeSet(TextView timeText, int hourOfDay, int minute) {
        timeText.setText(DateTime.forTimeOnly(hourOfDay, minute, 0, 0).format(TIME_WRITE_FORMAT, Locale.getDefault()));
    }

    private static DateTime readDate(TextView dateText) {
        try {
            Date dateRead = DATE_READ_FORMAT.parse(dateText.getText().toString());
            return DateTime.forInstant(dateRead.getTime(), TimeZone.getDefault());
        } catch (ParseException e) {
            Log.w(TAG, "Failed to parse date text: " + dateText.getText());
            return DateTime.today(TimeZone.getDefault());
        }
    }

    private static DateTime readTime(TextView timeTextView) {
        try {
            Date readTime = TIME_READ_FORMAT.parse(timeTextView.getText().toString());
            return DateTime.forInstant(readTime.getTime(), TimeZone.getDefault());
        } catch (ParseException e) {
            Log.w(TAG, "Failed to parse time text: " + timeTextView.getText());
            return DateTime.now(TimeZone.getDefault());
        }
    }

    private boolean isDateRangeValid() {
        DateTime fromDate = readDate(fromDateText);
        DateTime fromTime = readTime(fromTimeText);
        DateTime fromDateTime = new DateTime(fromDate.getYear(), fromDate.getMonth(), fromDate.getDay(),
                fromTime.getHour(), fromTime.getMinute(), 0, 0);

        DateTime toDate = readDate(toDateText);
        DateTime toTime = readTime(toTimeText);
        DateTime toDateTime = new DateTime(toDate.getYear(), toDate.getMonth(), toDate.getDay(),
                toTime.getHour(), toTime.getMinute(), 0, 0);

        return fromDateTime.lteq(toDateTime);
    }

    private void showDateRangeError() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.error_invalid_date_range_title))
                .setMessage(getResources().getString(R.string.error_invalid_date_range_message))
                .setPositiveButton(getResources().getString(R.string.dialog_button_ok), null)
                .create().show();
    }
}
