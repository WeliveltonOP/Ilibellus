package com.ilibellus.utils;

import android.app.ProgressDialog;
import android.content.Context;


public class LoadingDialog extends ProgressDialog {

    Context context;


    public LoadingDialog(Context context) {
        super(context);
        this.context = context;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.cancel();
    }

}
