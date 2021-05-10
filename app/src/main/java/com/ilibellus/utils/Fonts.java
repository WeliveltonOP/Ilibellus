package com.ilibellus.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;

import it.feio.android.checklistview.utils.DensityUtil;
import com.ilibellus.R;
import com.ilibellus.helpers.LogDelegate;


public class Fonts {

    /**
     * Overrides all the fonts set to TextView class descendants found in the
     * view passed as parameter
     */
    public static void overrideTextSize(Context context, SharedPreferences prefs, View v) {
        Context privateContext = context.getApplicationContext();
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideTextSize(privateContext, prefs, child);
                }
            } else if (v instanceof TextView) {
                float currentSize = DensityUtil.pxToDp(((TextView) v).getTextSize(), privateContext);
                int index = Arrays
                        .asList(privateContext.getResources().getStringArray(
                                R.array.text_size_values))
                        .indexOf(
                                prefs.getString("settings_text_size", "default"));
                float offset = privateContext.getResources().getIntArray(
                        R.array.text_size_offset)[index == -1 ? 0 : index];
                ((TextView) v).setTextSize(currentSize + offset);
            }
        } catch (Exception e) {
            LogDelegate.e("Error setting font size", e);
        }
    }
}
