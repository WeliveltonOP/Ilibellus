package com.ilibellus.async;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;


import java.util.List;

import com.ilibellus.BaseActivity;
import com.ilibellus.Ilibellus;
import com.ilibellus.db.DbHelper;
import com.ilibellus.helpers.LogDelegate;
import com.ilibellus.models.Note;
import com.ilibellus.helpers.ReminderHelper;


public class AlarmRestoreOnRebootService extends JobIntentService {

	public static final int JOB_ID = 0x01;

	public static void enqueueWork(Context context, Intent work) {
		enqueueWork(context, AlarmRestoreOnRebootService.class, JOB_ID, work);
	}

	@Override
	protected void onHandleWork(@NonNull Intent intent) {
		LogDelegate.i("System rebooted: service refreshing reminders");
		Context mContext = getApplicationContext();

		BaseActivity.notifyAppWidgets(mContext);

		List<Note> notes = DbHelper.getInstance().getNotesWithReminderNotFired();
		LogDelegate.d("Found " + notes.size() + " reminders");
		for (Note note : notes) {
			ReminderHelper.addReminder(Ilibellus.getAppContext(), note);
		}
	}

}
