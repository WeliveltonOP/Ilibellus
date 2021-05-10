package com.ilibellus;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import de.greenrobot.event.EventBus;

import com.google.android.material.snackbar.Snackbar;
import com.ilibellus.async.bus.PasswordRemovedEvent;
import com.ilibellus.db.DbHelper;
import com.ilibellus.models.ONStyle;
import com.ilibellus.models.PasswordValidator;
import com.ilibellus.utils.Constants;
import com.ilibellus.helpers.PasswordHelper;
import com.ilibellus.utils.Security;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class PasswordActivity extends BaseActivity {

    private View snackbar_handle;
    private EditText passwordCheck;
    private EditText password;
    private EditText question;
    private EditText answer;
    private EditText answerCheck;
    private PasswordActivity mActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);
        int screenHeight = (int) (metrics.heightPixels * 0.80);
        setContentView(R.layout.activity_password);
        getWindow().setLayout(screenWidth, screenHeight);
        mActivity = this;
        setActionBarTitle(getString(R.string.title_activity_password));
        initViews();
    }


	@Override
	protected void onStart() {
		super.onStart();
		EventBus.getDefault().register(this, 1);
	}


	@Override
	public void onStop() {
		super.onStop();
		EventBus.getDefault().unregister(this);
	}


    private void initViews() {
		snackbar_handle = findViewById(android.R.id.content);
        password = findViewById(R.id.password);
        passwordCheck = findViewById(R.id.password_check);
        question = findViewById(R.id.question);
        answer = findViewById(R.id.answer);
        answerCheck = findViewById(R.id.answer_check);

        findViewById(R.id.password_remove).setOnClickListener(v -> {
			if (prefs.getString(Constants.PREF_PASSWORD, null) != null) {
				PasswordHelper.requestPassword(mActivity, passwordConfirmed -> {
					if (passwordConfirmed.equals(PasswordValidator.Result.SUCCEED)) {
						updatePassword(null, null, null);
					}
				});
			} else {
				Snackbar.make(mActivity, snackbar_handle, getString(R.string.password_not_set), Snackbar.LENGTH_SHORT)
						.setTextColor(getResources().getColor(ONStyle.WARN_COLOR))
						.show();
			}
		});

        findViewById(R.id.password_confirm).setOnClickListener(v -> {
			if (checkData()) {
				final String passwordText = password.getText().toString();
				final String questionText = question.getText().toString();
				final String answerText = answer.getText().toString();
				if (prefs.getString(Constants.PREF_PASSWORD, null) != null) {
					PasswordHelper.requestPassword(mActivity, passwordConfirmed -> {
						if (passwordConfirmed.equals(PasswordValidator.Result.SUCCEED)) {
							updatePassword(passwordText, questionText, answerText);
						}
					});
				} else {
					updatePassword(passwordText, questionText, answerText);
				}
			}
		});

        findViewById(R.id.password_forgotten).setOnClickListener(v -> {
			if (prefs.getString(Constants.PREF_PASSWORD, "").length() == 0) {

				Snackbar.make(mActivity, snackbar_handle, getString(R.string.password_not_set), Snackbar.LENGTH_SHORT)
						.setTextColor(getResources().getColor(ONStyle.WARN_COLOR)).show();

				return;
			}
			PasswordHelper.resetPassword(this);
		});
    }



	public void onEvent(PasswordRemovedEvent passwordRemovedEvent) {
			passwordCheck.setText("");
			password.setText("");
			question.setText("");
			answer.setText("");
			answerCheck.setText("");

			Snackbar snack = Snackbar.make(mActivity, snackbar_handle, getString(R.string.password_successfully_removed), Snackbar.LENGTH_SHORT)
					.setTextColor(getResources().getColor(ONStyle.WARN_COLOR));

			snack.addCallback(new Snackbar.Callback() {

				@Override
				public void onDismissed(Snackbar snackbar, int event) {
					onBackPressed();
				}

			});

			snack.show();
	}


	@SuppressLint("CommitPrefEdits")
	private void updatePassword(String passwordText, String questionText, String answerText) {
		if (passwordText == null) {
			if (prefs.getString(Constants.PREF_PASSWORD, "").length() == 0) {
				Snackbar.make(mActivity, snackbar_handle, getString(R.string.password_not_set), Snackbar.LENGTH_SHORT)
						.setTextColor(getResources().getColor(ONStyle.WARN_COLOR)).show();
				return;
			}
			new MaterialDialog.Builder(mActivity)
					.content(R.string.agree_unlocking_all_notes)
					.positiveText(R.string.ok)
					.onPositive(new MaterialDialog.SingleButtonCallback() {
						@Override
						public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
							PasswordHelper.removePassword();
						}
					}).build().show();
		} else if (passwordText.length() == 0) {
			Snackbar.make(mActivity, snackbar_handle, getString(R.string.empty_password), Snackbar.LENGTH_SHORT)
					.setTextColor(getResources().getColor(ONStyle.WARN_COLOR)).show();
		} else {
			Observable
					.from(DbHelper.getInstance().getNotesWithLock(true))
					.subscribeOn(Schedulers.newThread())
					.observeOn(AndroidSchedulers.mainThread())
					.doOnSubscribe(() -> prefs.edit()
							.putString(Constants.PREF_PASSWORD, Security.md5(passwordText))
							.putString(Constants.PREF_PASSWORD_QUESTION, questionText)
							.putString(Constants.PREF_PASSWORD_ANSWER, Security.md5(answerText))
							.commit())
					.doOnNext(note -> DbHelper.getInstance().updateNote(note, false))
					.doOnCompleted(() -> {

						Snackbar snack = Snackbar.make(mActivity, snackbar_handle, getString(R.string.password_successfully_changed), Snackbar.LENGTH_SHORT)
								.setTextColor(getResources().getColor(ONStyle.SUCCESS_COLOR));

						snack.addCallback(new Snackbar.Callback() {

							@Override
							public void onDismissed(Snackbar snackbar, int event) {
								onBackPressed();
							}

						});

						snack.show();
					})
					.subscribe();
		}
	}


    /**
     * Checks correctness of form data
     *
     * @return
     */
    private boolean checkData() {
        boolean res = true;

        if (password.getText().length() == passwordCheck.getText().length()
                && passwordCheck.getText().length() == 0) {
            return true;
        }

        boolean passwordOk = password.getText().toString().length() > 0;
        boolean passwordCheckOk = passwordCheck.getText().toString().length() > 0 && password.getText().toString()
                .equals(passwordCheck.getText().toString());
        boolean questionOk = question.getText().toString().length() > 0;
        boolean answerOk = answer.getText().toString().length() > 0;
        boolean answerCheckOk = answerCheck.getText().toString().length() > 0 && answer.getText().toString().equals
                (answerCheck.getText().toString());

        if (!passwordOk || !passwordCheckOk || !questionOk || !answerOk || !answerCheckOk) {
            res = false;
            if (!passwordOk) {
                password.setError(getString(R.string.settings_password_not_matching));
            }
            if (!passwordCheckOk) {
                passwordCheck.setError(getString(R.string.settings_password_not_matching));
            }
            if (!questionOk) {
                question.setError(getString(R.string.settings_password_question));
            }
            if (!answerOk) {
                answer.setError(getString(R.string.settings_answer_not_matching));
            }
            if (!answerCheckOk) {
                answerCheck.setError(getString(R.string.settings_answer_not_matching));
            }
        }
        return res;
    }


    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

}
