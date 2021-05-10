package com.ilibellus.utils.date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerDialogFragment extends DialogFragment {

    public static final String DEFAULT_DATE = "default_date";

    private OnDateSetListener mListener;
    private Long defaultDate;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getArguments().containsKey(DEFAULT_DATE)) {
            this.defaultDate = getArguments().getLong(DEFAULT_DATE);
        }

        try {
            mListener = (OnDateSetListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDateSetListener");
        }
    }


    @Override
    public void onDetach() {
        this.mListener = null;
        super.onDetach();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        Calendar cal = DateUtils.getCalendar(defaultDate);
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH);
        int d = cal.get(Calendar.DAY_OF_MONTH);

        // Jelly Bean introduced a bug in DatePickerDialog (and possibly 
        // TimePickerDialog as well), and one of the possible solutions is 
        // to postpone the creation of both the listener and the BUTTON_* .
        // 
        // Passing a null here won't harm because DatePickerDialog checks for a null
        // whenever it reads the listener that was passed here. >>> This seems to be 
        // true down to 1.5 / API 3, up to 4.1.1 / API 16. <<< No worries. For now.
        //
        // See my own question and answer, and details I included for the issue:
        //
        // http://stackoverflow.com/a/11493752/489607
        // http://code.google.com/p/android/issues/detail?id=34833
        //
        // Of course, suggestions welcome.

        final DatePickerDialog picker = new DatePickerDialog(getActivity(), DatePickerDialog.THEME_HOLO_LIGHT,
                mListener, y, m, d);
        picker.setTitle("");

		picker.setButton(DialogInterface.BUTTON_POSITIVE,
				getActivity().getString(android.R.string.ok),
				(dialog, which) -> {
					DatePicker dp = picker.getDatePicker();
					mListener.onDateSet(dp,
							dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
				});
		picker.setButton(DialogInterface.BUTTON_NEGATIVE,
				getActivity().getString(android.R.string.cancel),
				(dialog, which) -> {});

        return picker;
    }

}
