package com.ilibellus.async.notes;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import com.ilibellus.Ilibellus;
import com.ilibellus.db.DbHelper;
import com.ilibellus.helpers.LogDelegate;
import com.ilibellus.models.Attachment;
import com.ilibellus.models.Note;
import com.ilibellus.models.listeners.OnNoteSaved;
import com.ilibellus.helpers.ReminderHelper;
import com.ilibellus.helpers.StorageHelper;
import com.ilibellus.utils.date.DateUtils;


public class SaveNoteTask extends AsyncTask<Note, Void, Note> {

	private Context context;
	private boolean updateLastModification = true;
	private OnNoteSaved mOnNoteSaved;


	public SaveNoteTask(boolean updateLastModification) {
		this(null, updateLastModification);
	}


	public SaveNoteTask(OnNoteSaved mOnNoteSaved, boolean updateLastModification) {
		super();
		this.context = Ilibellus.getAppContext();
		this.mOnNoteSaved = mOnNoteSaved;
		this.updateLastModification = updateLastModification;
	}


	@Override
	protected Note doInBackground(Note... params) {
		Note note = params[0];
		purgeRemovedAttachments(note);
		boolean reminderMustBeSet = DateUtils.isFuture(note.getAlarm());
		if (reminderMustBeSet) {
			note.setReminderFired(false);
		}
		note = DbHelper.getInstance().updateNote(note, updateLastModification);
		if (reminderMustBeSet) {
			ReminderHelper.addReminder(context, note);
		}
		return note;
	}


	private void purgeRemovedAttachments(Note note) {
		List<Attachment> deletedAttachments = note.getAttachmentsListOld();
		for (Attachment attachment : note.getAttachmentsList()) {
			if (attachment.getId() != null) {
				// Workaround to prevent deleting attachments if instance is changed (app restart)
				if (deletedAttachments.indexOf(attachment) == -1) {
					attachment = getFixedAttachmentInstance(deletedAttachments, attachment);
				}
				deletedAttachments.remove(attachment);
			}
		}
		// Remove from database deleted attachments
		for (Attachment deletedAttachment : deletedAttachments) {
			StorageHelper.delete(context, deletedAttachment.getUri().getPath());
			LogDelegate.d("Removed attachment " + deletedAttachment.getUri());
		}
	}


    private Attachment getFixedAttachmentInstance(List<Attachment> deletedAttachments, Attachment attachment) {
        for (Attachment deletedAttachment : deletedAttachments) {
            if (deletedAttachment.getId().equals(attachment.getId())) {
				return deletedAttachment;
			}
        }
        return attachment;
    }


	@Override
	protected void onPostExecute(Note note) {
		super.onPostExecute(note);
		if (this.mOnNoteSaved != null) {
            mOnNoteSaved.onNoteSaved(note);
        }
	}
}
