package com.ilibellus.utils;

import android.annotation.SuppressLint;
import android.view.View;


public class AlphaManager {
    
    private AlphaManager(){}

    @SuppressLint("NewApi")
    public static void setAlpha(View v, float alpha) {
        if (v != null) {
			v.setAlpha(alpha);
		}
    }
}
