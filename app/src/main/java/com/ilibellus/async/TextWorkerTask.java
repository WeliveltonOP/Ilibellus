package com.ilibellus.async;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;
import com.ilibellus.models.Note;
import com.ilibellus.helpers.TextHelper;

import java.lang.ref.WeakReference;


public class TextWorkerTask extends AsyncTask<Note, Void, Spanned[]> {

    private final WeakReference<Activity> mActivityWeakReference;
    private Activity mActivity;
    private TextView titleTextView;
    private TextView contentTextView;
    private boolean expandedView;


    public TextWorkerTask(Activity activity, TextView titleTextView,
                          TextView contentTextView, boolean expandedView) {
        mActivityWeakReference = new WeakReference<>(activity);
        mActivity = activity;
        this.titleTextView = titleTextView;
        this.contentTextView = contentTextView;
        this.expandedView = expandedView;
    }


    @Override
    protected Spanned[] doInBackground(Note... params) {
        return TextHelper.parseTitleAndContent(mActivity, params[0]);
    }


    @Override
    protected void onPostExecute(Spanned[] titleAndContent) {
        if (isAlive()) {
            titleTextView.setText(titleAndContent[0]);
            if (titleAndContent[1].length() > 0) {
                contentTextView.setText(titleAndContent[1]);
                contentTextView.setVisibility(View.VISIBLE);
            } else {
                if (expandedView) {
                    contentTextView.setVisibility(View.INVISIBLE);
                } else {
                    contentTextView.setVisibility(View.GONE);
                }
            }
        }
    }


    /**
     * Cheks if activity is still alive and not finishing
     *
     * @param weakDetailFragmentReference
     * @return True or false
     */
    private boolean isAlive() {
        return mActivityWeakReference != null
                && mActivityWeakReference.get() != null;

    }

}
