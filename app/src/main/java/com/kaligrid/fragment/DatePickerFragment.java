package com.kaligrid.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.kaligrid.util.DateTimeUtil;

import hirondelle.date4j.DateTime;

public class DatePickerFragment extends DialogFragment {

    public static final String FIELD_INITIAL_DATE = "initialDate";

    private DatePickerDialog.OnDateSetListener onDateSetListener;

    public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener listener) {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setOnDateSetListener(listener);
        return fragment;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use passed date or the current date.
        long initialDateRaw = getArguments().getLong(FIELD_INITIAL_DATE, System.currentTimeMillis());
        DateTime initialDate = DateTimeUtil.forInstant(initialDateRaw);

        // Android month starts from 0 but Date4J starts from 1.
        return new DatePickerDialog(getActivity(), onDateSetListener,
                initialDate.getYear(), initialDate.getMonth() - 1, initialDate.getDay());
    }

    public void setOnDateSetListener(DatePickerDialog.OnDateSetListener onDateSetListener) {
        this.onDateSetListener = onDateSetListener;
    }
}
