package com.ilibellus.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.Spanned;

import java.util.List;

import com.ilibellus.R;
import com.ilibellus.SnoozeActivity;
import com.ilibellus.db.DbHelper;
import com.ilibellus.helpers.LogDelegate;
import com.ilibellus.models.Attachment;
import com.ilibellus.models.Note;
import com.ilibellus.services.NotificationListener;
import com.ilibellus.helpers.BitmapHelper;
import com.ilibellus.utils.Constants;
import com.ilibellus.utils.ParcelableUtil;
import com.ilibellus.helpers.TextHelper;
import com.ilibellus.utils.notifications.NotificationChannels;
import com.ilibellus.utils.notifications.NotificationsHelper;


public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context mContext, Intent intent) {
		try {
			Note note = ParcelableUtil.unmarshall(intent.getExtras().getByteArray(Constants.INTENT_NOTE), Note
					.CREATOR);
			createNotification(mContext, note);
			SnoozeActivity.setNextRecurrentReminder(note);
			updateNote(note);
		} catch (Exception e) {
			LogDelegate.e("Error on receiving reminder", e);
		}
	}

	private void updateNote(Note note) {
		note.setArchived(false);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && !NotificationListener.isRunning()) {
			note.setReminderFired(true);
		}
		DbHelper.getInstance().updateNote(note, false);
	}

	private void createNotification(Context mContext, Note note) {

		// Retrieving preferences
		SharedPreferences prefs = mContext.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS);

		// Prepare text contents
		Spanned[] titleAndContent = TextHelper.parseTitleAndContent(mContext, note);
		String title = TextHelper.getAlternativeTitle(mContext, note, titleAndContent[0]);
		String text = titleAndContent[1].toString();

		Intent snoozeIntent = new Intent(mContext, SnoozeActivity.class);
		snoozeIntent.setAction(Constants.ACTION_SNOOZE);
		snoozeIntent.putExtra(Constants.INTENT_NOTE, (android.os.Parcelable) note);
		PendingIntent piSnooze = PendingIntent.getActivity(mContext, getUniqueRequestCode(note), snoozeIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		Intent postponeIntent = new Intent(mContext, SnoozeActivity.class);
		postponeIntent.setAction(Constants.ACTION_POSTPONE);
		postponeIntent.putExtra(Constants.INTENT_NOTE, (android.os.Parcelable) note);
		PendingIntent piPostpone = PendingIntent.getActivity(mContext, getUniqueRequestCode(note), postponeIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		String snoozeDelay = mContext.getSharedPreferences(Constants.PREFS_NAME,
				Context.MODE_MULTI_PROCESS).getString("settings_notification_snooze_delay", "10");

		// Next create the bundle and initialize it
		Intent intent = new Intent(mContext, SnoozeActivity.class);
		Bundle bundle = new Bundle();
		bundle.putParcelable(Constants.INTENT_NOTE, note);
		intent.putExtras(bundle);

		// Sets the Activity to start in a new, empty task
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// Workaround to fix problems with multiple notifications
		intent.setAction(Constants.ACTION_NOTIFICATION_CLICK + Long.toString(System.currentTimeMillis()));

		// Creates the PendingIntent
		PendingIntent notifyIntent = PendingIntent.getActivity(mContext, getUniqueRequestCode(note), intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationsHelper notificationsHelper = new NotificationsHelper(mContext);
        notificationsHelper.createNotification(NotificationChannels.NotificationChannelNames.Reminders, R.drawable.ic_stat_notification, title, notifyIntent).setLedActive()
				.setMessage(text);

        List<Attachment> attachments = note.getAttachmentsList();
		if (!attachments.isEmpty() && !attachments.get(0).getMime_type().equals(Constants.MIME_TYPE_FILES)) {
			Bitmap notificationIcon = BitmapHelper.getBitmapFromAttachment(mContext, note.getAttachmentsList().get(0), 128, 128);
			notificationsHelper.setLargeIcon(notificationIcon);
		}

		notificationsHelper.getBuilder()
				.addAction(R.drawable.ic_material_reminder_time_light, TextHelper.capitalize(mContext.getString(R.string.snooze)) + ": " + snoozeDelay, piSnooze)
				.addAction(R.drawable.ic_remind_later_light, TextHelper.capitalize(mContext.getString(R.string
								.add_reminder)), piPostpone);

		setRingtone(prefs, notificationsHelper);
		setVibrate(prefs, notificationsHelper);

		notificationsHelper.show(note.get_id());
	}


	private void setRingtone(SharedPreferences prefs, NotificationsHelper notificationsHelper) {
		String ringtone = prefs.getString("settings_notification_ringtone", null);
		if (ringtone != null) notificationsHelper.setRingtone(ringtone);
	}


	private void setVibrate(SharedPreferences prefs, NotificationsHelper notificationsHelper) {
		if (prefs.getBoolean("settings_notification_vibration", true)) notificationsHelper.setVibration();
	}


	private int getUniqueRequestCode(Note note) {
		return note.get_id().intValue();
	}
}
