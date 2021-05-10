package com.ilibellus.utils.notifications;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import com.ilibellus.Ilibellus;
import com.ilibellus.R;

@TargetApi(Build.VERSION_CODES.O)
public class NotificationChannels {

    private static List<NotificationChannel> channels = new ArrayList<NotificationChannel>() {{
        add(new NotificationChannel(NotificationManager.IMPORTANCE_DEFAULT, Ilibellus.getAppContext().getString(R.string
                .channel_backups_name), Ilibellus.getAppContext().getString(R.string
                .channel_backups_description)));
        add(new NotificationChannel(NotificationManager.IMPORTANCE_DEFAULT, Ilibellus.getAppContext().getString(R.string
                .channel_reminders_name), Ilibellus.getAppContext().getString(R.string
                .channel_reminders_description)));
    }};

    public static List<NotificationChannel> getChannels() {
        return channels;
    }

    public enum NotificationChannelNames {
        Backups, Reminders
    }
}
