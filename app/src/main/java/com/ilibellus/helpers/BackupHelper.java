package com.ilibellus.helpers;


import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.ilibellus.Ilibellus;
import com.ilibellus.R;
import com.ilibellus.async.DataBackupIntentService;
import com.ilibellus.db.DbHelper;
import com.ilibellus.models.Attachment;
import com.ilibellus.models.Note;
import com.ilibellus.utils.Constants;
import com.ilibellus.utils.notifications.NotificationsHelper;
import rx.Observable;

public class BackupHelper {

	private static String TAG = BackupHelper.class.getSimpleName();


	public static void exportNotes(File backupDir) {
		for (Note note : DbHelper.getInstance(true).getAllNotes(false)) {
			exportNote(backupDir, note);
		}
	}


	public static void exportNote(File backupDir, Note note) {
		File noteFile = getBackupNoteFile(backupDir, note);
		try {
			FileUtils.write(noteFile, note.toJSON());
		} catch (IOException e) {
			LogDelegate.e("Error backupping note: " + note.get_id());
		}
	}

	@NonNull
	public static File getBackupNoteFile(File backupDir, Note note) {
		return new File(backupDir, String.valueOf(note.get_id()) + ".json");
	}


	/**
	 * Export attachments to backup folder
	 *
	 * @return True if success, false otherwise
	 */
	public static boolean exportAttachments(File backupDir) {
		return exportAttachments(backupDir, null);
	}


	/**
	 * Export attachments to backup folder notifying for each attachment copied
	 *
	 * @return True if success, false otherwise
	 */
	public static boolean exportAttachments(File backupDir, NotificationsHelper notificationsHelper) {
		File destinationattachmentsDir = new File(backupDir, StorageHelper.getAttachmentDir().getName());
		ArrayList<Attachment> list = DbHelper.getInstance().getAllAttachments();
		exportAttachments(notificationsHelper, destinationattachmentsDir, list, null);
		return true;
	}


	public static boolean exportAttachments(NotificationsHelper notificationsHelper, File destinationattachmentsDir,
											List<Attachment> list, List<Attachment> listOld) {

		boolean result = true;

		listOld = listOld == null ? Collections.EMPTY_LIST : listOld;
		int exported = 0;
		int failed = 0;
		for (Attachment attachment : list) {
			try {
				StorageHelper.copyToBackupDir(destinationattachmentsDir, FilenameUtils.getName(attachment.getUriPath()), Ilibellus.getAppContext().getContentResolver().openInputStream(attachment.getUri()));
				++exported;
			} catch (FileNotFoundException e) {
				LogDelegate.w("Attachment not found during backup: " + attachment.getUriPath());
				++failed;
				result = false;
			}

			String failedString = failed > 0 ? " (" + failed + " " + Ilibellus.getAppContext().getString(R.string.failed) + ")" : "";
			if (notificationsHelper != null) {
				notificationsHelper.updateMessage(
						TextHelper.capitalize(Ilibellus.getAppContext().getString(R.string.attachment))
								+ " " + exported + "/" + list.size() + failedString
				);
			}
		}
		
		Observable.from(listOld)
				.filter(attachment -> !list.contains(attachment))
				.forEach(attachment -> StorageHelper.delete(Ilibellus.getAppContext(), new File
						(destinationattachmentsDir.getAbsolutePath(),
								attachment.getUri().getLastPathSegment()).getAbsolutePath()));

		return result;
	}


	/**
	 * Imports backuped notes
	 *
	 * @param backupDir
	 */
	public static List<Note> importNotes(File backupDir) {
		List<Note> notes = new ArrayList<>();
		for (File file : FileUtils.listFiles(backupDir, new RegexFileFilter("\\d{13}.json"), TrueFileFilter.INSTANCE)) {
			notes.add(importNote(file));
		}
		return notes;
	}


	/**
	 * Imports single note from its file
	 */
	public static Note importNote(File file) {
		Note note = getImportNote(file);
		if (note.getCategory() != null) {
			DbHelper.getInstance().updateCategory(note.getCategory());
		}
		note.setAttachmentsListOld(DbHelper.getInstance().getNoteAttachments(note));
		DbHelper.getInstance().updateNote(note, false);
		return note;
	}


	/**
	 * Retrieves single note from its file
	 */
	public static Note getImportNote(File file) {
		try {
			Note note = new Note();
			String jsonString = FileUtils.readFileToString(file);
			if (!TextUtils.isEmpty(jsonString)) {
				note.buildFromJson(jsonString);
				note.setAttachmentsListOld(DbHelper.getInstance().getNoteAttachments(note));
			}
			return note;
		} catch (IOException e) {
			LogDelegate.e("Error parsing note json");
			return new Note();
		}
	}


	/**
	 * Import attachments from backup folder
	 */
	public static boolean importAttachments(File backupDir) {
		return importAttachments(backupDir, null);
	}


	/**
	 * Import attachments from backup folder notifying for each imported item
	 */
	public static boolean importAttachments(File backupDir, NotificationsHelper notificationsHelper) {
		File attachmentsDir = StorageHelper.getAttachmentDir();
		File backupAttachmentsDir = new File(backupDir, attachmentsDir.getName());
		if (!backupAttachmentsDir.exists()) return true;
		boolean result = true;
		Collection list = FileUtils.listFiles(backupAttachmentsDir, FileFilterUtils.trueFileFilter(),
				TrueFileFilter.INSTANCE);
		Iterator i = list.iterator();
		int imported = 0;
		File file = null;
		while (i.hasNext()) {
			try {
				file = (File) i.next();
				FileUtils.copyFileToDirectory(file, attachmentsDir, true);
				if (notificationsHelper != null) {
					notificationsHelper.updateMessage(
							TextHelper.capitalize(Ilibellus.getAppContext().getString(R.string.attachment))
									+ " " + imported++ + "/" + list.size()
					);
				}
			} catch (IOException e) {
				result = false;
				LogDelegate.e("Error importing the attachment " + file.getName());
			}
		}
		return result;
	}


	/**
	 * Import attachments of a specific note from backup folder
	 */
	public static void importAttachments(Note note, File backupDir) throws IOException {

		File backupAttachmentsDir = new File(backupDir, StorageHelper.getAttachmentDir().getName());

		for (Attachment attachment : note.getAttachmentsList()) {
			String attachmentFileName = FilenameUtils.getName(attachment.getUriPath());
			File attachmentFile = new File(backupAttachmentsDir, attachmentFileName);
			if (attachmentFile.exists()) {
				FileUtils.copyFileToDirectory(attachmentFile, StorageHelper.getAttachmentDir(), true);
			} else {
				LogDelegate.e("Attachment file not found: " + attachmentFileName);
			}
		}
	}


	/**
	 * Starts backup service
	 * @param backupFolderName subfolder of the app's external sd folder where notes will be stored
	 */
	public static void startBackupService(String backupFolderName) {
		Intent service = new Intent(Ilibellus.getAppContext(), DataBackupIntentService.class);
		service.setAction(DataBackupIntentService.ACTION_DATA_EXPORT);
		service.putExtra(DataBackupIntentService.INTENT_BACKUP_NAME, backupFolderName);
		Ilibellus.getAppContext().startService(service);
	}


	/**
	 * Exports settings if required
	 */
	public static boolean exportSettings(File backupDir) {
		File preferences = StorageHelper.getSharedPreferencesFile(Ilibellus.getAppContext());
		return (StorageHelper.copyFile(preferences, new File(backupDir, preferences.getName())));
	}


	/**
	 * Imports settings
	 */
	public static boolean importSettings(File backupDir) {
		File preferences = StorageHelper.getSharedPreferencesFile(Ilibellus.getAppContext());
		File preferenceBackup = new File(backupDir, preferences.getName());
		return (StorageHelper.copyFile(preferenceBackup, preferences));
	}


	public static boolean deleteNoteBackup(File backupDir, Note note) {
		File noteFile = getBackupNoteFile(backupDir, note);
		boolean result = noteFile.delete();
		File attachmentBackup = new File(backupDir, StorageHelper.getAttachmentDir().getName());
		for (Attachment attachment : note.getAttachmentsList()) {
			result = result && new File(attachmentBackup, FilenameUtils.getName(attachment.getUri().getPath()))
					.delete();
		}
		return result;
	}


	public static void deleteNote(File file) {
		try {
			Note note = new Note();
			note.buildFromJson(FileUtils.readFileToString(file));
			DbHelper.getInstance().deleteNote(note);
		} catch (IOException e) {
			LogDelegate.e("Error parsing note json");
		}
	}


	/**
	 * Import database from backup folder. Used ONLY to restore legacy backup
	 *
	 * @deprecated {@link BackupHelper#importNotes(File)}
	 */
	@Deprecated
	public static boolean importDB(Context context, File backupDir) {
		File database = context.getDatabasePath(Constants.DATABASE_NAME);
		if (database.exists()) {
			database.delete();
		}
		return (StorageHelper.copyFile(new File(backupDir, Constants.DATABASE_NAME), database));
	}


	public static List<LinkedList<DiffMatchPatch.Diff>> integrityCheck(File backupDir) {
		List<LinkedList<DiffMatchPatch.Diff>> errors = new ArrayList<>();
		for (Note note : DbHelper.getInstance(true).getAllNotes(false)) {
			File noteFile = getBackupNoteFile(backupDir, note);
			try {
				String noteString = note.toJSON();
				String noteFileString = FileUtils.readFileToString(noteFile);
				if (noteString.equals(noteFileString)) {
					File backupAttachmentsDir = new File(backupDir, StorageHelper.getAttachmentDir().getName());
					for (Attachment attachment : note.getAttachmentsList()) {
						if (!new File(backupAttachmentsDir, FilenameUtils.getName(attachment.getUriPath())).exists()) {
							addIntegrityCheckError(errors, new FileNotFoundException("Attachment " + attachment
									.getUriPath() + " missing"));
						}
					}
				} else {
					errors.add(new DiffMatchPatch().diffMain(noteString, noteFileString));
				}
			} catch (IOException e) {
				LogDelegate.e(e.getMessage(), e);
				addIntegrityCheckError(errors, e);
			}
		}
		return errors;
	}


	private static void addIntegrityCheckError(List<LinkedList<DiffMatchPatch.Diff>> errors, IOException e) {
		LinkedList l = new LinkedList();
		l.add(new DiffMatchPatch.Diff(DiffMatchPatch.Operation.DELETE, e.getMessage()));
		errors.add(l);
	}


}
