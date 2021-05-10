package com.ilibellus.services;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.os.Build;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import de.greenrobot.event.EventBus;
import com.ilibellus.Ilibellus;
import com.ilibellus.async.bus.NotificationRemovedEvent;
import com.ilibellus.db.DbHelper;
import com.ilibellus.helpers.LogDelegate;
import com.ilibellus.models.Note;
import com.ilibellus.utils.date.DateUtils;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationListener extends NotificationListenerService {


	@Override
	public void onCreate() {
		super.onCreate();
		EventBus.getDefault().register(this);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}


	@Override
	public void onNotificationPosted(StatusBarNotification sbn) {
		LogDelegate.d("Notification posted for note: " + sbn.getId());
	}


	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) {
		if (sbn.getPackageName().equals(getPackageName())) {
			EventBus.getDefault().post(new NotificationRemovedEvent(sbn));
			LogDelegate.d("Notification removed for note: " + sbn.getId());
		}
	}


	public void onEventAsync(NotificationRemovedEvent event) {
		Long nodeId = Long.valueOf(event.statusBarNotification.getTag());
		Note note = DbHelper.getInstance().getNote(nodeId);
		if (!DateUtils.isFuture(note.getAlarm())) {
			DbHelper.getInstance().setReminderFired(nodeId, true);
		}
	}


	public static boolean isRunning() {
		ContentResolver contentResolver = Ilibellus.getAppContext().getContentResolver();
		String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
		return enabledNotificationListeners != null && enabledNotificationListeners.contains(NotificationListener
				.class.getSimpleName());
	}

}
