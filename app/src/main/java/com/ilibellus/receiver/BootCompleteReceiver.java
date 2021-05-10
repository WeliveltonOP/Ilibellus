package com.ilibellus.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ilibellus.async.AlarmRestoreOnRebootService;
import com.ilibellus.helpers.LogDelegate;


public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent intent) {
        LogDelegate.i("System rebooted: refreshing reminders");
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            AlarmRestoreOnRebootService.enqueueWork(ctx, intent);
        }
    }


}
