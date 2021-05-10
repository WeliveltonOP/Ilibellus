package com.ilibellus.async;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

import it.feio.android.analitica.AnalyticsHelper;
import kotlin.Unit;

import com.ilibellus.BuildConfig;
import com.ilibellus.Ilibellus;
import com.ilibellus.R;
import com.ilibellus.helpers.AppVersionHelper;
import com.ilibellus.helpers.LogDelegate;
import com.ilibellus.models.misc.PlayStoreMetadataFetcherResult;
import com.ilibellus.utils.ConnectionManager;
import com.ilibellus.utils.Constants;
import com.ilibellus.utils.MiscUtils;
import com.ilibellus.helpers.SystemHelper;



public class UpdaterTask extends AsyncTask<String, Void, Void> {

	private static final String BETA = " Beta ";
	private final WeakReference<Activity> mActivityReference;
	private final Activity mActivity;
	private final SharedPreferences prefs;
	private boolean promptUpdate = false;
	private long now;


	public UpdaterTask(Activity mActivity) {
		this.mActivityReference = new WeakReference<>(mActivity);
		this.mActivity = mActivity;
		this.prefs = mActivity.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS);
	}


	@Override
	protected void onPreExecute() {
		now = System.currentTimeMillis();
		if (Ilibellus.isDebugBuild() || !ConnectionManager.internetAvailable(Ilibellus.getAppContext()) || now < prefs.getLong(Constants
				.PREF_LAST_UPDATE_CHECK, 0) + Constants.UPDATE_MIN_FREQUENCY) {
			cancel(true);
		}
		super.onPreExecute();
	}


	@Override
	protected Void doInBackground(String... params) {
		if (!isCancelled()) {
			try {
				// Temporary disabled untill MetadataFetcher will work again
				// promptUpdate = isVersionUpdated(getAppData());
				promptUpdate = false;
				if (promptUpdate) {
					prefs.edit().putLong(Constants.PREF_LAST_UPDATE_CHECK, now).apply();
				}
			} catch (Exception e) {
				LogDelegate.w("Error fetching app metadata", e);
			}
		}
		return null;
	}


	private void promptUpdate() {


//		MaterialDialog dialog = new MaterialDialog(mActivityReference.get(), MaterialDialog.getDEFAULT_BEHAVIOR())
//				.title(R.string.app_name, null)
//				.message(R.string.new_update_available, null, null )
//				.positiveButton(R.string.update, null, materialDialog -> {
//					if (MiscUtils.isGooglePlayAvailable(mActivity)) {
//						((Ilibellus)mActivity.getApplication()).getAnalyticsHelper().trackEvent(AnalyticsHelper.CATEGORIES.UPDATE, "Play Store");
//						mActivityReference.get().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
//								("market://details?id=" + mActivity.getPackageName())));
//					} else {
//						((Ilibellus)mActivity.getApplication()).getAnalyticsHelper().trackEvent(AnalyticsHelper.CATEGORIES.UPDATE, "Drive Repository");
//						mActivityReference.get().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants
//								.DRIVE_FOLDER_LAST_BUILD)));
//					}
//
//					return Unit.INSTANCE;
//				})
//				.negativeButton(R.string.not_now, null, materialDialog -> {
//					materialDialog.cancel();
//					return Unit.INSTANCE;
//				});
//
//		dialog.show();

		new MaterialDialog.Builder(mActivityReference.get())
				.title(R.string.app_name)
				.content(R.string.new_update_available)
				.positiveText(R.string.update)
				.negativeText(R.string.not_now)
				.negativeColorRes(R.color.colorPrimary)
				.onPositive(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
						if (MiscUtils.isGooglePlayAvailable(mActivity)) {
							((Ilibellus)mActivity.getApplication()).getAnalyticsHelper().trackEvent(AnalyticsHelper.CATEGORIES.UPDATE, "Play Store");
							mActivityReference.get().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
									("market://details?id=" + mActivity.getPackageName())));
						} else {
							((Ilibellus)mActivity.getApplication()).getAnalyticsHelper().trackEvent(AnalyticsHelper.CATEGORIES.UPDATE, "Drive Repository");
							mActivityReference.get().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants
									.DRIVE_FOLDER_LAST_BUILD)));
						}
					}
				}).build().show();
	}


	@Override
	protected void onPostExecute(Void result) {
		if (isAlive(mActivityReference)) {
			if (promptUpdate) {
				promptUpdate();
			} else {
				try {
					boolean appVersionUpdated = AppVersionHelper.isAppUpdated(mActivity);
					if (appVersionUpdated) {
						showChangelog();
						restoreReminders();
						AppVersionHelper.updateAppVersionInPreferences(mActivity);
					}
				} catch (NameNotFoundException e) {
					LogDelegate.e("Error retrieving app version", e);
				}
			}
		}
	}

	private void restoreReminders() {
		Intent service = new Intent(mActivity, AlarmRestoreOnRebootService.class);
		mActivity.startService(service);
	}

	private void showChangelog() {

//		MaterialDialog dialog = new MaterialDialog(mActivity, MaterialDialog.getDEFAULT_BEHAVIOR());
//
//		dialog.setContentView(R.layout.activity_changelog);
//
//		dialog.positiveButton(R.string.ok, null, null);
//
//		dialog.show();

		new MaterialDialog.Builder(mActivity)
				.customView(R.layout.activity_changelog, false)
				.positiveText(R.string.ok)
				.build().show();
	}


	private boolean isAlive(WeakReference<Activity> weakActivityReference) {
		return !(weakActivityReference.get() == null || weakActivityReference.get().isFinishing());
	}


	/**
	 * Fetches application data from internet
	 */
	private PlayStoreMetadataFetcherResult getAppData() throws IOException, JSONException {
		InputStream is = null;
		InputStreamReader inputStreamReader = null;
		try {
			StringBuilder sb = new StringBuilder();
			URLConnection conn = new URL(BuildConfig.VERSION_CHECK_URL).openConnection();
			is = conn.getInputStream();
			inputStreamReader = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(inputStreamReader);

			String inputLine;
			while ((inputLine = br.readLine()) != null) {
				sb.append(inputLine);
			}

			return new Gson().fromJson(sb.toString(), PlayStoreMetadataFetcherResult.class);
		} finally {
			SystemHelper.closeCloseable(inputStreamReader, is);
		}
	}


	/**
	 * Checks parsing "android:versionName" if app has been updated
	 */
	private boolean isVersionUpdated(PlayStoreMetadataFetcherResult playStoreMetadataFetcherResult)
			throws NameNotFoundException {

		String playStoreVersion = playStoreMetadataFetcherResult.getSoftwareVersion();

		// Retrieval of installed app version
		PackageInfo pInfo = mActivity.getPackageManager().getPackageInfo(
				mActivity.getPackageName(), 0);
		String installedVersion = pInfo.versionName;

		// Parsing version string to obtain major.minor.point (excluding eventually beta)
		String[] playStoreVersionArray = playStoreVersion.split(BETA)[0].split("\\.");
		String[] installedVersionArray = installedVersion.split(BETA)[0].split("\\.");

		// Versions strings are converted into integer
		String playStoreVersionString = playStoreVersionArray[0];
		String installedVersionString = installedVersionArray[0];
		for (int i = 1; i < playStoreVersionArray.length; i++) {
			playStoreVersionString += String.format("%02d", Integer.parseInt(playStoreVersionArray[i]));
			installedVersionString += String.format("%02d", Integer.parseInt(installedVersionArray[i]));
		}

		boolean playStoreHasMoreRecentVersion = Integer.parseInt(playStoreVersionString) > Integer.parseInt(installedVersionString);
		boolean outOfBeta = Integer.parseInt(playStoreVersionString) == Integer.parseInt(installedVersionString)
				&& playStoreVersion.split("b").length == 1 && installedVersion.split("b").length == 2;

		return playStoreHasMoreRecentVersion || outOfBeta;
	}
}
