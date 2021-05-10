package com.ilibellus;

import android.os.SystemClock;

import androidx.fragment.app.Fragment;

import com.squareup.leakcanary.LeakCanary;


public class BaseFragment extends Fragment {


    private static final long OPTIONS_ITEM_CLICK_DELAY_TIME = 1000;
    private long mLastClickTime;

    @Override
    public void onStart() {
        super.onStart();
        ((Ilibellus) getActivity().getApplication()).getAnalyticsHelper().trackScreenView(getClass().getName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LeakCanary.installedRefWatcher().watch(this);
    }

    protected boolean isOptionsItemFastClick() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < OPTIONS_ITEM_CLICK_DELAY_TIME){
            return true;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        return false;
    }
}
