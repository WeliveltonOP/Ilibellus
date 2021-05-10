package com.ilibellus;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceViewHolder;

import it.feio.android.checklistview.utils.DensityUtil;
import com.ilibellus.utils.Fonts;

public class FontSizeListPreference extends ListPreference {

    private int clickedDialogEntryIndex;


    public FontSizeListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        TextView summary = (TextView) holder.findViewById(android.R.id.summary);

        Fonts.overrideTextSize(getContext(), getSharedPreferences(), summary);
    }

    @Override
    protected void onClick() {
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getContext(),
                R.layout.settings_font_size_dialog_item, getEntries()) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                CheckedTextView view = (CheckedTextView) convertView;
                if (view == null) {
                    view = (CheckedTextView) View.inflate(getContext(),
                            R.layout.settings_font_size_dialog_item, null);
                }
                view.setText(getEntries()[position]);
                Context privateContext = getContext().getApplicationContext();
                float currentSize = DensityUtil.pxToDp(((TextView) View.inflate(getContext(),
                        R.layout.settings_font_size_dialog_item, null)).getTextSize(), privateContext);
                float offset = privateContext.getResources().getIntArray(
                        R.array.text_size_offset)[position];
                view.setTextSize(currentSize + offset);
                return view;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle(R.string.settings_text_size)
                .setPositiveButton(null, null)
                .setNegativeButton(R.string.cancel, null);

        clickedDialogEntryIndex = findIndexOfValue(getValue());
        builder.setSingleChoiceItems(adapter, clickedDialogEntryIndex,
                (dialog, which) -> {
                    clickedDialogEntryIndex = which;

                    dialog.dismiss();

                    if (clickedDialogEntryIndex >= 0 && getEntryValues() != null) {
                        String val = getEntryValues()[clickedDialogEntryIndex].toString();
                        if (callChangeListener(val)) {
                            setValue(val);
                        }
                    }
                });

        builder.show();
    }
}
