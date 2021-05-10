package com.ilibellus.utils.date;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.ilibellus.R;

import java.util.Calendar;


public class TimePickerFragment extends DialogFragment {

    public static final String DEFAULT_TIME = "default_time";

    TextView timer_label;
    private Activity mActivity;
    private OnTimeSetListener mListener;
    private Long defaultTime = null;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        if (getArguments().containsKey(DEFAULT_TIME)) {
            this.defaultTime = getArguments().getLong(DEFAULT_TIME);
        }

        try {
            mListener = (OnTimeSetListener) mActivity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTimeSetListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Calendar cal = DateUtils.getCalendar(defaultTime);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        boolean is24HourMode = DateUtils.is24HourMode(mActivity);
        TimePickerDialog tpd = new TimePickerDialog(mActivity, R.style.Theme_AppCompat_Dialog_NoBackgroundOrDim, mListener, hour, minute, is24HourMode);
        tpd.setTitle("");
        return tpd;
    }

}
