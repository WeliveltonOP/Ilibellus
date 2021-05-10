package com.ilibellus.async.notes;

import android.os.AsyncTask;
import de.greenrobot.event.EventBus;
import com.ilibellus.async.bus.NotesUpdatedEvent;
import com.ilibellus.models.Note;

import java.util.ArrayList;
import java.util.List;


public abstract class NoteProcessor {

	List<Note> notes;


	protected NoteProcessor(List<Note> notes) {
		this.notes = new ArrayList<>(notes);
	}


	public void process() {
		NotesProcessorTask task = new NotesProcessorTask();
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, notes);
	}


	protected abstract void processNote(Note note);


	class NotesProcessorTask extends AsyncTask<List<Note>, Void, List<Note>> {

		@Override
		protected List<Note> doInBackground(List<Note>... params) {
			List<Note> notes = params[0];
			for (Note note : notes) {
				processNote(note);
			}
			return notes;
		}


		@Override
		protected void onPostExecute(List<Note> notes) {
			afterProcess(notes);
		}
	}


	protected void afterProcess(List<Note> notes) {
		EventBus.getDefault().post(new NotesUpdatedEvent(notes));
	}
}
