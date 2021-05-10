package com.ilibellus.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;
import com.ilibellus.Ilibellus;
import com.ilibellus.R;
import com.ilibellus.helpers.date.DateHelper;
import com.ilibellus.models.Note;
import com.ilibellus.receiver.AlarmReceiver;
import com.ilibellus.utils.Constants;
import com.ilibellus.utils.ParcelableUtil;
import com.ilibellus.utils.date.DateUtils;

import java.util.Calendar;


public class ReminderHelper {

	public static void addReminder(Context context, Note note) {
		if (note.getAlarm() != null) {
			addReminder(context, note, Long.parseLong(note.getAlarm()));
		}
	}


	public static void addReminder(Context context, Note note, long reminder) {
		if (DateUtils.isFuture(reminder)) {
			Intent intent = new Intent(context, AlarmReceiver.class);
			intent.putExtra(Constants.INTENT_NOTE, ParcelableUtil.marshall(note));
			PendingIntent sender = PendingIntent.getBroadcast(context, getRequestCode(note), intent,
					PendingIntent.FLAG_CANCEL_CURRENT);
			AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				am.setExact(AlarmManager.RTC_WAKEUP, reminder, sender);
			} else {
				am.set(AlarmManager.RTC_WAKEUP, reminder, sender);
			}
		}
	}


	/**
	 * Checks if exists any reminder for given note
	 */
	public static boolean checkReminder(Context context, Note note) {
		return PendingIntent.getBroadcast(context, getRequestCode(note), new Intent(context, AlarmReceiver
				.class), PendingIntent.FLAG_NO_CREATE) != null;
	}


	static int getRequestCode(Note note) {
		Long longCode = note.getCreation() != null ? note.getCreation() : Calendar.getInstance().getTimeInMillis() / 1000L;
		return longCode.intValue();
	}


	public static void removeReminder(Context context, Note note) {
		if (!TextUtils.isEmpty(note.getAlarm())) {
			AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, AlarmReceiver.class);
			PendingIntent p = PendingIntent.getBroadcast(context, getRequestCode(note), intent, 0);
			am.cancel(p);
			p.cancel();
		}
	}


	public static void showReminderMessage(String reminderString) {
		if (reminderString != null) {
			long reminder = Long.parseLong(reminderString);
			if (reminder > Calendar.getInstance().getTimeInMillis()) {
				new Handler(Ilibellus.getAppContext().getMainLooper()).post(() -> Toast.makeText(Ilibellus
								.getAppContext(),
						Ilibellus.getAppContext().getString(R.string.alarm_set_on) + " " + DateHelper.getDateTimeShort
								(Ilibellus.getAppContext(), reminder), Toast.LENGTH_LONG).show());
			}
		}
	}
}
