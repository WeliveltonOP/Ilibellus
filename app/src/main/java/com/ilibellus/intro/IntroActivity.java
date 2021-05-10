package com.ilibellus.intro;

import android.content.Context;
import android.os.Bundle;
import com.github.paolorotolo.appintro.AppIntro2;
import com.ilibellus.Ilibellus;
import com.ilibellus.utils.Constants;


public class IntroActivity extends AppIntro2 {

	@Override
	public void init(Bundle savedInstanceState) {
		addSlide(new IntroSlide1(), getApplicationContext());
		addSlide(new IntroSlide2(), getApplicationContext());
		addSlide(new IntroSlide3(), getApplicationContext());
		addSlide(new IntroSlide4(), getApplicationContext());
		addSlide(new IntroSlide5(), getApplicationContext());
		addSlide(new IntroSlide6(), getApplicationContext());
	}


	@Override
	public void onDonePressed() {
		Ilibellus.getAppContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS).edit()
				.putBoolean(Constants.PREF_TOUR_COMPLETE, true).apply();
		finish();
	}


	public static boolean mustRun() {
		return !Ilibellus.isDebugBuild() && !Ilibellus.getAppContext().getSharedPreferences(Constants.PREFS_NAME,
				Context.MODE_MULTI_PROCESS).getBoolean(Constants.PREF_TOUR_COMPLETE, false);
	}


	@Override
	public void onBackPressed() {
		// Does nothing, you HAVE TO SEE THE INTRO!
	}
}
