package com.ilibellus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.feio.android.analitica.AnalyticsHelper;

import com.google.android.material.snackbar.Snackbar;
import com.ilibellus.async.DataBackupIntentService;
import com.ilibellus.models.ONStyle;


public class SettingsActivity extends AppCompatActivity implements FolderChooserDialog.FolderCallback {

	@BindView(R.id.toolbar)
	Toolbar toolbar;

	private List<Fragment> backStack = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		ButterKnife.bind(this);
		initUI();
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
	}


	void initUI() {
		setSupportActionBar(toolbar);
		toolbar.setNavigationOnClickListener(v -> onBackPressed());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
	}


	void switchToScreen(String key) {
		SettingsFragment sf = new SettingsFragment();
		Bundle b = new Bundle();
		b.putString(SettingsFragment.XML_NAME, key);
		sf.setArguments(b);
		backStack.add(getSupportFragmentManager().findFragmentById(R.id.content_frame));
		replaceFragment(sf);
	}


	private void replaceFragment(Fragment sf) {
		getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out,
				R.animator.fade_in, R.animator.fade_out).replace(R.id.content_frame, sf).commit();
	}


	@Override
	public void onBackPressed() {
		if (!backStack.isEmpty()) {
			replaceFragment(backStack.remove(backStack.size() - 1));
		} else {
			super.onBackPressed();
		}
	}


	public void showMessage(int messageId, int resTextColor) {
		showMessage(getString(messageId), resTextColor);
	}


	public void showMessage(String message, int resTextColor) {
		// ViewGroup used to show Crouton keeping compatibility with the new Toolbar
		Snackbar.make(this, findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
				.setTextColor(getResources().getColor(resTextColor))
				.show();
	}


	@Override
	public void onFolderSelection(@NonNull FolderChooserDialog dialog, @NonNull File folder) {
		new MaterialDialog.Builder(this)
				.title(R.string.data_import_message_warning)
				.content(folder.getName())
				.positiveText(R.string.confirm)
				.onPositive((dialog1, which) -> {
					((Ilibellus)getApplication()).getAnalyticsHelper().trackEvent(AnalyticsHelper.CATEGORIES.SETTING,
							"settings_import_data");
					Intent service = new Intent(getApplicationContext(), DataBackupIntentService.class);
					service.setAction(DataBackupIntentService.ACTION_DATA_IMPORT_LEGACY);
					service.putExtra(DataBackupIntentService.INTENT_BACKUP_NAME, folder.getAbsolutePath());
					startService(service);
				}).build().show();
	}
}
