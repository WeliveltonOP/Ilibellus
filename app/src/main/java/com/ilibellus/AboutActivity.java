package com.ilibellus;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.widget.Toolbar;


public class AboutActivity extends BaseActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        WebView webview = findViewById(R.id.webview);
        webview.loadUrl("file:///android_asset/html/about.html");

        initUI();
    }


	@Override
	public void onStart() {
		((Ilibellus)getApplication()).getAnalyticsHelper().trackScreenView(getClass().getName());
		super.onStart();
	}


    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return true;
    }


    private void initUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onNavigateUp());
    }

}
