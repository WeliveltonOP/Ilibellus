package com.ilibellus.intro;

import android.graphics.Color;
import android.os.Bundle;
import com.ilibellus.R;


public class IntroSlide2 extends IntroFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		background.setBackgroundColor(Color.parseColor("#f44336"));
		title.setText(R.string.tour_listactivity_home_title);
		image.setImageResource(R.drawable.slide2);
		description.setText(R.string.tour_listactivity_home_detail);
	}
}
