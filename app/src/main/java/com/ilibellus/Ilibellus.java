package com.ilibellus;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.StrictMode;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.squareup.leakcanary.LeakCanary;

import org.acra.ACRA;
import org.acra.annotation.AcraCore;
import org.acra.annotation.AcraHttpSender;
import org.acra.annotation.AcraToast;
import org.acra.sender.HttpSender;

import it.feio.android.analitica.AnalyticsHelper;
import it.feio.android.analitica.AnalyticsHelperFactory;
import it.feio.android.analitica.MockAnalyticsHelper;
import it.feio.android.analitica.exceptions.AnalyticsInstantiationException;
import it.feio.android.analitica.exceptions.InvalidIdentifierException;
import com.ilibellus.helpers.LanguageHelper;
import com.ilibellus.utils.Constants;
import com.ilibellus.utils.notifications.NotificationsHelper;


@AcraCore(buildConfigClass = BuildConfig.class)
@AcraHttpSender(uri = BuildConfig.CRASH_REPORTING_URL, httpMethod = HttpSender.Method.POST)
@AcraToast(resText = R.string.crash_toast)
public class Ilibellus extends MultiDexApplication {

	static SharedPreferences prefs;
	private static Context mContext;
	private AnalyticsHelper analyticsHelper;

	public static boolean isDebugBuild() {
		return BuildConfig.BUILD_TYPE.equals("debug");
	}

	public static Context getAppContext() {
		return Ilibellus.mContext;
	}

	/**
	 * Statically returns app's default SharedPreferences instance
	 *
	 * @return SharedPreferences object instance
	 */
	public static SharedPreferences getSharedPreferences() {
		return getAppContext().getSharedPreferences(Constants.PREFS_NAME, MODE_MULTI_PROCESS);
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
//		MultiDex.install(this);
		ACRA.init(this);
		ACRA.getErrorReporter().putCustomData("TRACEPOT_DEVELOP_MODE", isDebugBuild() ? "1" : "0");
	}

	@Override
	public void onCreate() {
		super.onCreate();

		if (initLeakCanary()) {
			return;
		}

		mContext = getApplicationContext();
		prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_MULTI_PROCESS);

		//enableStrictMode();

		new NotificationsHelper(this).initNotificationChannels();
	}

	private void enableStrictMode() {
		if (isDebugBuild()) {
			StrictMode.enableDefaults();
		}
	}

	/**
	 * Returns true if the process dedicated to LeakCanary for heap analysis is running
	 * and app's init must be skipped
	 */
	private boolean initLeakCanary() {
		if (!LeakCanary.isInAnalyzerProcess(this)) {
			LeakCanary.install(this);
			return false;
		}
		return true;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		String language = prefs.getString(Constants.PREF_LANG, "");
		LanguageHelper.updateLanguage(this, language);
	}

	public AnalyticsHelper getAnalyticsHelper() {
		if (analyticsHelper == null) {
			boolean enableAnalytics = prefs.getBoolean(Constants.PREF_SEND_ANALYTICS, true);
			try {
				String[] analyticsParams = BuildConfig.ANALYTICS_PARAMS.split(Constants.PROPERTIES_PARAMS_SEPARATOR);
				analyticsHelper = new AnalyticsHelperFactory().getAnalyticsHelper(this, enableAnalytics,
						analyticsParams);
			} catch (AnalyticsInstantiationException | InvalidIdentifierException e) {
				analyticsHelper = new MockAnalyticsHelper();
			}
		}
		return analyticsHelper;
	}
}
