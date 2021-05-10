package com.ilibellus.async;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.io.File;

import com.ilibellus.MainActivity;
import com.ilibellus.Ilibellus;
import com.ilibellus.R;
import com.ilibellus.db.DbHelper;
import com.ilibellus.helpers.BackupHelper;
import com.ilibellus.helpers.LogDelegate;
import com.ilibellus.helpers.SpringImportHelper;
import com.ilibellus.models.Attachment;
import com.ilibellus.models.Note;
import com.ilibellus.models.listeners.OnAttachingFileListener;
import com.ilibellus.utils.Constants;
import com.ilibellus.helpers.ReminderHelper;
import com.ilibellus.helpers.StorageHelper;
import com.ilibellus.utils.notifications.NotificationChannels;
import com.ilibellus.utils.notifications.NotificationsHelper;

public class DataBackupIntentService extends IntentService implements OnAttachingFileListener {

    public final static String INTENT_BACKUP_NAME = "backup_name";
    public final static String INTENT_BACKUP_INCLUDE_SETTINGS = "backup_include_settings";
    public final static String ACTION_DATA_EXPORT = "action_data_export";
    public final static String ACTION_DATA_IMPORT = "action_data_import";
    public final static String ACTION_DATA_IMPORT_LEGACY = "action_data_import_legacy";
    public final static String ACTION_DATA_DELETE = "action_data_delete";

    private SharedPreferences prefs;
    private NotificationsHelper mNotificationsHelper;

//    {
//        File autoBackupDir = StorageHelper.getBackupDir(Constants.AUTO_BACKUP_DIR);
//        BackupHelper.exportNotes(autoBackupDir);
//        BackupHelper.exportAttachments(autoBackupDir);
//    }


    public DataBackupIntentService() {
        super("DataBackupIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_MULTI_PROCESS);

        // Creates an indeterminate processing notification until the work is complete
//        mNotificationsHelper = new NotificationsHelper(this)
//                .createNotification(NotificationChannels.NotificationChannelNames.Backups,
//                        R.drawable.ic_content_save_white_24dp, getString(R.string.working),
//                        null)
//                .setIndeterminate().setOngoing();

        mNotificationsHelper = new NotificationsHelper(this).start(NotificationChannels.NotificationChannelNames.Backups,
                R.drawable.ic_content_save_white_24dp, getString(R.string.working));


        // If an alarm has been fired a notification must be generated
        if (ACTION_DATA_EXPORT.equals(intent.getAction())) {
            exportData(intent);
        } else if (ACTION_DATA_IMPORT.equals(intent.getAction()) || ACTION_DATA_IMPORT_LEGACY.equals(intent.getAction())) {
            importData(intent);
        } else if (SpringImportHelper.ACTION_DATA_IMPORT_SPRINGPAD.equals(intent.getAction())) {
            importDataFromSpringpad(intent, mNotificationsHelper);
        } else if (ACTION_DATA_DELETE.equals(intent.getAction())) {
            deleteData(intent);
        }
    }

	private void importDataFromSpringpad(Intent intent, NotificationsHelper mNotificationsHelper) {
		new SpringImportHelper(Ilibellus.getAppContext()).importDataFromSpringpad(intent, mNotificationsHelper);
		String title = getString(R.string.data_import_completed);
		String text = getString(R.string.click_to_refresh_application);
		createNotification(intent, this, title, text, null);
	}

	synchronized private void exportData(Intent intent) {

        boolean result = true;

        // Gets backup folder
        String backupName = intent.getStringExtra(INTENT_BACKUP_NAME);
        File backupDir = StorageHelper.getBackupDir(backupName);

        // Directory clean in case of previously used backup name
        StorageHelper.delete(this, backupDir.getAbsolutePath());

        // Directory is re-created in case of previously used backup name (removed above)
        backupDir = StorageHelper.getBackupDir(backupName);

		BackupHelper.exportNotes(backupDir);

        result = BackupHelper.exportAttachments(backupDir, mNotificationsHelper);

        if (intent.getBooleanExtra(INTENT_BACKUP_INCLUDE_SETTINGS, true)) {
			BackupHelper.exportSettings(backupDir);
        }

        String notificationMessage = result ? getString(R.string.data_export_completed) : getString(R.string.data_export_failed);
        mNotificationsHelper.finish(intent, notificationMessage);
    }


    synchronized private void importData(Intent intent) {

		boolean importLegacy = ACTION_DATA_IMPORT_LEGACY.equals(intent.getAction());

        // Gets backup folder
        String backupName = intent.getStringExtra(INTENT_BACKUP_NAME);
		File backupDir = importLegacy ? new File(backupName) : StorageHelper.getBackupDir(backupName);

        BackupHelper.importSettings(backupDir);

		if (importLegacy) {
			BackupHelper.importDB(this, backupDir);
		} else {
			BackupHelper.importNotes(backupDir);
		}

		BackupHelper.importAttachments(backupDir, mNotificationsHelper);

		resetReminders();

		mNotificationsHelper.cancel();

        createNotification(intent, this, getString(R.string.data_import_completed), getString(R.string.click_to_refresh_application), backupDir);

        // Performs auto-backup filling after backup restore
//        if (prefs.getBoolean(Constants.PREF_ENABLE_AUTOBACKUP, false)) {
//            File autoBackupDir = StorageHelper.getBackupDir(Constants.AUTO_BACKUP_DIR);
//            BackupHelper.exportNotes(autoBackupDir);
//            BackupHelper.exportAttachments(autoBackupDir);
//        }
	}

    synchronized private void deleteData(Intent intent) {

        // Gets backup folder
        String backupName = intent.getStringExtra(INTENT_BACKUP_NAME);
        File backupDir = StorageHelper.getBackupDir(backupName);

        // Backups directory removal
        StorageHelper.delete(this, backupDir.getAbsolutePath());

        String title = getString(R.string.data_deletion_completed);
        String text = backupName + " " + getString(R.string.deleted);
        createNotification(intent, this, title, text, backupDir);
    }


    /**
     * Creation of notification on operations completed
     */
    private void createNotification(Intent intent, Context mContext, String title, String message, File backupDir) {

        // The behavior differs depending on intent action
        Intent intentLaunch;
        if (DataBackupIntentService.ACTION_DATA_IMPORT.equals(intent.getAction())
                || SpringImportHelper.ACTION_DATA_IMPORT_SPRINGPAD.equals(intent.getAction())) {
			intentLaunch = new Intent(mContext, MainActivity.class);
			intentLaunch.setAction(Constants.ACTION_RESTART_APP);
        } else {
            intentLaunch = new Intent();
        }
        // Add this bundle to the intent
        intentLaunch.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentLaunch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Creates the PendingIntent
        PendingIntent notifyIntent = PendingIntent.getActivity(mContext, 0, intentLaunch,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationsHelper mNotificationsHelper = new NotificationsHelper(mContext);
        mNotificationsHelper.createNotification(NotificationChannels.NotificationChannelNames.Backups, R.drawable.ic_content_save_white_24dp, title, notifyIntent)
                .setMessage(message).setRingtone(prefs.getString("settings_notification_ringtone", null))
                .setLedActive();
        if (prefs.getBoolean("settings_notification_vibration", true)) mNotificationsHelper.setVibration();
        mNotificationsHelper.show();
    }


	/**
	 * Schedules reminders
	 */
	private void resetReminders() {
		LogDelegate.d("Resettings reminders");
		for (Note note : DbHelper.getInstance().getNotesWithReminderNotFired()) {
			ReminderHelper.addReminder(Ilibellus.getAppContext(), note);
		}
	}


    @Override
    public void onAttachingFileErrorOccurred(Attachment mAttachment) {
        // TODO Auto-generated method stub
    }


    @Override
    public void onAttachingFileFinished(Attachment mAttachment) {
        // TODO Auto-generated method stub
    }

}
