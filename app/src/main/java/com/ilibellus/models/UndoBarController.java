package com.ilibellus.models;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewPropertyAnimator;
import it.feio.android.checklistview.utils.AlphaManager;
import com.ilibellus.R;

import java.util.Locale;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;


public class UndoBarController {

    private View mBarView;


    private UndoListener mUndoListener;

    // State objects
    private Parcelable mUndoToken;

    private CharSequence mUndoMessage;

    private Snackbar snack;

    private boolean isVisible;

    public interface UndoListener {

        void onUndo(Parcelable token);
    }

    public UndoBarController(View undoBarView, UndoListener undoListener) {
        mBarView = undoBarView;
        mUndoListener = undoListener;
    }


    public void showUndoBar(CharSequence message, Parcelable undoToken) {
        mUndoToken = undoToken;
        mUndoMessage = message;

        snack = Snackbar.make(mBarView, mUndoMessage, BaseTransientBottomBar.LENGTH_INDEFINITE);

        snack.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUndoListener.onUndo(mUndoToken);
            }
        });

        snack.show();

        isVisible = true;
    }


    public void hideUndoBar(boolean immediate) {
        mUndoMessage = null;
        mUndoToken = null;

        if(snack != null)
        {
            snack.dismiss();

            snack = null;
        }

        isVisible = false;
    }


    public void onSaveInstanceState(Bundle outState) {
        outState.putCharSequence("undo_message", mUndoMessage);
        outState.putParcelable("undo_token", mUndoToken);
    }


    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mUndoMessage = savedInstanceState.getCharSequence("undo_message");
            mUndoToken = savedInstanceState.getParcelable("undo_token");

            if (mUndoToken != null || !TextUtils.isEmpty(mUndoMessage)) {
                showUndoBar(mUndoMessage, mUndoToken);
            }
        }
    }

    public boolean isVisible() {
        return isVisible;
    }
}
