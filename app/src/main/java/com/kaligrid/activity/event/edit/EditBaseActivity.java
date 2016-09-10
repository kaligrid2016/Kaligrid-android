package com.kaligrid.activity.event.edit;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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
import com.kaligrid.app.App;
import com.kaligrid.fragment.component.DatePickerFragment;
import com.kaligrid.fragment.component.TimePickerFragment;
import com.kaligrid.model.Event;
import com.kaligrid.service.EventService;
import com.kaligrid.util.DateTimeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hirondelle.date4j.DateTime;

public abstract class EditBaseActivity extends AppCompatActivity {

    public static final String EXTRA_KEY_EVENT_ID = "eventId";

    private static final String TAG = EditBaseActivity.class.getSimpleName();
    private static final String DATE_WRITE_FORMAT = "WWW, MMMM D, YYYY";
    private static final String TIME_WRITE_FORMAT = "h12:mm a";
    private static final SimpleDateFormat DATE_READ_FORMAT = new SimpleDateFormat("EEE, MMMM d, yyyy", Locale.getDefault());
    private static final SimpleDateFormat TIME_READ_FORMAT = new SimpleDateFormat("h:mm a", Locale.getDefault());

    protected enum Mode { CREATE, EDIT }

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
    @Bind(R.id.delete_text) TextView deleteText;

    @Inject EventService eventService;

    protected Mode mode = Mode.CREATE;
    private long selectedEventId;

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
    abstract protected Event createEventFromFields();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());

        ButterKnife.bind(this);
        App.getObjectGraph().inject(this);

        if ((getIntent().getExtras() != null) && getIntent().getExtras().containsKey(EXTRA_KEY_EVENT_ID)) {
            selectedEventId = getIntent().getExtras().getLong(EXTRA_KEY_EVENT_ID);
            mode = Mode.EDIT;
            // TODO: Handle error - selected event not found
        }

        initializeViews();
    }

    @OnClick(R.id.button_cancel)
    public void onCancelButtonClick(View v) {
        finish();
    }

    @OnClick(R.id.save_button)
    public void onSaveButtonClick(View v) {
        switch (mode) {
            case CREATE:
                eventService.addEvent(createEventFromFields());
                break;
            case EDIT:
                eventService.updateEvent(createEventFromFields(selectedEventId));
                break;
        }

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
        bundle.putLong(DatePickerFragment.FIELD_INITIAL_DATE, DateTimeUtil.toMillis(readDate(v)));

        DialogFragment newFragment = DatePickerFragment.newInstance(
                (v.getId() == R.id.from_date_text) ? FROM_DATE_SET_LISTENER : TO_DATE_SET_LISTENER);
        newFragment.setArguments(bundle);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @OnClick({ R.id.from_time_text, R.id.to_time_text})
    public void onFromTimeTextClick(TextView v) {
        Bundle bundle = new Bundle();
        bundle.putLong(TimePickerFragment.FIELD_INITIAL_TIME, DateTimeUtil.toMillis(readTime(v)));

        DialogFragment newFragment = TimePickerFragment.newInstance(
                (v.getId() == R.id.from_time_text) ? FROM_TIME_SET_LISTENER : TO_TIME_SET_LISTENER);
        newFragment.setArguments(bundle);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @OnClick(R.id.delete_text)
    public void onDeleteTextClick(TextView v) {
        new AlertDialog.Builder(this)
                .setMessage("Do you really want to delete this?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        eventService.deleteEvent(selectedEventId);
                        finish();
                    }})
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void initializeViews() {
        initializeTopToolbar();

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

        if (mode == Mode.CREATE) {
            // Create initial date without minutes and seconds.
            DateTime now = DateTimeUtil.clearMinutesAndSeconds(DateTimeUtil.now());
            initializeDateTimeTextViews(now, DateTimeUtil.addHours(now, 1));

            deleteText.setVisibility(View.GONE);
        } else {
            Event event = eventService.getEvent(selectedEventId);

            initializeDateTimeTextViews(
                    DateTimeUtil.forInstant(event.getStartDateTime()),
                    DateTimeUtil.forInstant(event.getEndDateTime()));

            eventTitleText.setText(event.getTitle());
            allDaySwitch.setChecked(event.isAllDayEvent());
            deleteText.setVisibility(View.VISIBLE);
        }
    }

    private void initializeTopToolbar() {
        Toolbar actionBar = (Toolbar) findViewById(R.id.toolbar_new_event_top);
        setSupportActionBar(actionBar);

        titleText.setText(getActivityTitle());
        saveButton.setText(getSaveButtonText());
    }

    private void initializeDateTimeTextViews(DateTime from, DateTime to) {
        fromDateText.setText(DateTimeUtil.format(from, DATE_WRITE_FORMAT));
        fromTimeText.setText(DateTimeUtil.format(from, TIME_WRITE_FORMAT));

        toDateText.setText(DateTimeUtil.format(to, DATE_WRITE_FORMAT));
        toTimeText.setText(DateTimeUtil.format(to, TIME_WRITE_FORMAT));
    }

    private void handleOnDateSet(TextView dateText, int year, int month, int day) {
        // Android month starts from 0 but Date4J starts from 1.
        dateText.setText(DateTime.forDateOnly(year, month + 1, day).format(DATE_WRITE_FORMAT, Locale.getDefault()));
        if (!isDateRangeValid()) {
            showDateRangeError();
        }
    }

    private void handleOnTimeSet(TextView timeText, int hourOfDay, int minute) {
        timeText.setText(DateTime.forTimeOnly(hourOfDay, minute, 0, 0).format(TIME_WRITE_FORMAT, Locale.getDefault()));
        if (!isDateRangeValid()) {
            showDateRangeError();
        }
    }

    private Event createEventFromFields(long eventId) {
        Event event = createEventFromFields();
        event.setId(eventId);
        return event;
    }

    protected static DateTime readDateTime(TextView dateText, TextView timeTextView) {
        DateTime date = readDate(dateText);
        DateTime time = readTime(timeTextView);
        return new DateTime(date.getYear(), date.getMonth(), date.getDay(), time.getHour(), time.getMinute(), 0, 0);
    }

    private static DateTime readDate(TextView dateText) {
        try {
            Date dateRead = DATE_READ_FORMAT.parse(dateText.getText().toString());
            return DateTimeUtil.forInstant(dateRead.getTime());
        } catch (ParseException e) {
            Log.w(TAG, "Failed to parse date text: " + dateText.getText());
            return DateTimeUtil.today();
        }
    }

    private static DateTime readTime(TextView timeTextView) {
        try {
            Date readTime = TIME_READ_FORMAT.parse(timeTextView.getText().toString());
            return DateTimeUtil.forInstant(readTime.getTime());
        } catch (ParseException e) {
            Log.w(TAG, "Failed to parse time text: " + timeTextView.getText());
            return DateTimeUtil.now();
        }
    }

    private boolean isDateRangeValid() {
        DateTime from = readDateTime(fromDateText, fromTimeText);
        DateTime to = readDateTime(toDateText, toTimeText);
        return from.lteq(to);
    }

    private void showDateRangeError() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.error_invalid_date_range_title))
                .setMessage(getResources().getString(R.string.error_invalid_date_range_message))
                .setPositiveButton(getResources().getString(R.string.dialog_button_ok), null)
                .create().show();
    }
}
