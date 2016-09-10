package com.kaligrid.fragment.component;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import com.kaligrid.util.DateTimeUtil;

import hirondelle.date4j.DateTime;

public class TimePickerFragment extends DialogFragment {

    public static final String FIELD_INITIAL_TIME = "initialTime";

    private TimePickerDialog.OnTimeSetListener onTimeSetListener;

    public static TimePickerFragment newInstance(TimePickerDialog.OnTimeSetListener listener) {
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setOnTimeSetListener(listener);
        return fragment;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        long initialTimeRaw = getArguments().getLong(FIELD_INITIAL_TIME, System.currentTimeMillis());
        DateTime initialTime = DateTimeUtil.forInstant(initialTimeRaw);

        return new TimePickerDialog(getActivity(), onTimeSetListener, initialTime.getHour(),
                initialTime.getMinute(), DateFormat.is24HourFormat(getActivity()));
    }

    public void setOnTimeSetListener(TimePickerDialog.OnTimeSetListener onTimeSetListener) {
        this.onTimeSetListener = onTimeSetListener;
    }
}