package com.ilibellus.async.notes;

import de.greenrobot.event.EventBus;
import com.ilibellus.Ilibellus;
import com.ilibellus.async.bus.NotesDeletedEvent;
import com.ilibellus.db.DbHelper;
import com.ilibellus.models.Attachment;
import com.ilibellus.models.Note;
import com.ilibellus.helpers.StorageHelper;

import java.util.List;


public class NoteProcessorDelete extends NoteProcessor {


	private final boolean keepAttachments;


	public NoteProcessorDelete(List<Note> notes) {
		this(notes, false);
	}


	public NoteProcessorDelete(List<Note> notes, boolean keepAttachments) {
		super(notes);
		this.keepAttachments = keepAttachments;
	}


	@Override
	protected void processNote(Note note) {
		DbHelper db = DbHelper.getInstance();
		if (db.deleteNote(note) && !keepAttachments) {
			for (Attachment mAttachment : note.getAttachmentsList()) {
				StorageHelper.deleteExternalStoragePrivateFile(Ilibellus.getAppContext(), mAttachment.getUri()
						.getLastPathSegment());
			}
		}
	}


	@Override
	protected void afterProcess(List<Note> notes) {
		EventBus.getDefault().post(new NotesDeletedEvent(notes));
	}

}
