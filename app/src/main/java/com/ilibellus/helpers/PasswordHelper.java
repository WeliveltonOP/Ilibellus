package com.ilibellus.helpers;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import de.greenrobot.event.EventBus;
import com.ilibellus.Ilibellus;
import com.ilibellus.R;
import com.ilibellus.async.bus.PasswordRemovedEvent;
import com.ilibellus.db.DbHelper;
import com.ilibellus.models.PasswordValidator;
import com.ilibellus.utils.Constants;
import com.ilibellus.utils.KeyboardUtils;
import com.ilibellus.utils.Security;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class PasswordHelper {


    public static void requestPassword(final Activity mActivity, final PasswordValidator mPasswordValidator) {
        LayoutInflater inflater = mActivity.getLayoutInflater();
        final View v = inflater.inflate(R.layout.password_request_dialog_layout, null);
        final EditText passwordEditText = (EditText) v.findViewById(R.id.password_request);

        MaterialDialog dialog = new MaterialDialog.Builder(mActivity)
                .autoDismiss(false)
                .title(R.string.insert_security_password)
                .customView(v, false)
                .positiveText(R.string.ok)
                .positiveColorRes(R.color.colorPrimary)
                .onPositive((dialog12, which) -> {
                    // When positive button is pressed password correctness is checked
                    String oldPassword = mActivity.getSharedPreferences(Constants.PREFS_NAME, Context
                            .MODE_MULTI_PROCESS)
                            .getString(Constants.PREF_PASSWORD, "");
                    String password = passwordEditText.getText().toString();
                    // The check is done on password's hash stored in preferences
                    boolean result = Security.md5(password).equals(oldPassword);

                    // In case password is ok dialog is dismissed and result sent to callback
                    if (result) {
                        KeyboardUtils.hideKeyboard(passwordEditText);
                        dialog12.dismiss();
                        mPasswordValidator.onPasswordValidated(PasswordValidator.Result.SUCCEED);
                        // If password is wrong the auth flow is not interrupted and simply a message is shown
                    } else {
                        passwordEditText.setError(mActivity.getString(R.string.wrong_password));
                    }
                })
                .neutralText(mActivity.getResources().getString(R.string.password_forgot))
                .onNeutral((dialog13, which) -> {
                    PasswordHelper.resetPassword(mActivity);
                    mPasswordValidator.onPasswordValidated(PasswordValidator.Result.RESTORE);
                    dialog13.dismiss();
                })
                .build();

        dialog.setOnCancelListener(dialog1 -> {
            KeyboardUtils.hideKeyboard(passwordEditText);
            dialog1.dismiss();
            mPasswordValidator.onPasswordValidated(PasswordValidator.Result.FAIL);
        });

        passwordEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                dialog.getActionButton(DialogAction.POSITIVE).callOnClick();
                return true;
            }
            return false;
        });

        dialog.show();

        new Handler().postDelayed(() -> KeyboardUtils.showKeyboard(passwordEditText), 100);
    }


    public static void resetPassword(final Activity mActivity) {
        View layout = mActivity.getLayoutInflater().inflate(R.layout.password_reset_dialog_layout, null);
        final EditText answerEditText = (EditText) layout.findViewById(R.id.reset_password_answer);

        MaterialDialog dialog = new MaterialDialog.Builder(mActivity)
                .title(Ilibellus.getSharedPreferences().getString(Constants.PREF_PASSWORD_QUESTION, ""))
                .customView(layout, false)
                .autoDismiss(false)
                .contentColorRes(R.color.text_color)
                .positiveText(R.string.ok)
                .onPositive((dialogElement, which) -> {
                    // When positive button is pressed answer correctness is checked
                    String oldAnswer = Ilibellus.getSharedPreferences().getString(Constants.PREF_PASSWORD_ANSWER, "");
                    String answer1 = answerEditText.getText().toString();
                    // The check is done on password's hash stored in preferences
                    boolean result = Security.md5(answer1).equals(oldAnswer);
                    if (result) {
                        dialogElement.dismiss();
                        removePassword();
                    } else {
                        answerEditText.setError(mActivity.getString(R.string.wrong_answer));
                    }
                }).build();
        dialog.show();

        answerEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                dialog.getActionButton(DialogAction.POSITIVE).callOnClick();
                return true;
            }
            return false;
        });

        new Handler().postDelayed(() -> KeyboardUtils.showKeyboard(answerEditText), 100);
    }


    public static void removePassword() {
        Observable
                .from(DbHelper.getInstance().getNotesWithLock(true))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(note -> {
                    note.setLocked(false);
                    DbHelper.getInstance().updateNote(note, false);
                })
                .doOnCompleted(() -> {
                    Ilibellus.getSharedPreferences().edit()
                            .remove(Constants.PREF_PASSWORD)
                            .remove(Constants.PREF_PASSWORD_QUESTION)
                            .remove(Constants.PREF_PASSWORD_ANSWER)
                            .remove("settings_password_access")
                            .apply();
                    EventBus.getDefault().post(new PasswordRemovedEvent());
                })
                .subscribe();
    }
}
