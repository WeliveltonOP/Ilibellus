package com.ilibellus.async.notes;

import android.os.AsyncTask;
import android.util.Log;
import de.greenrobot.event.EventBus;
import com.ilibellus.async.bus.NotesLoadedEvent;
import com.ilibellus.db.DbHelper;
import com.ilibellus.exceptions.NotesLoadingException;
import com.ilibellus.models.Note;
import com.ilibellus.utils.Constants;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;


public class NoteLoaderTask extends AsyncTask<Object, Void, ArrayList<Note>> {

	private static NoteLoaderTask instance;

	private NoteLoaderTask() {}


	public static NoteLoaderTask getInstance() {

		if (instance != null) {
			if (instance.getStatus() == Status.RUNNING && !instance.isCancelled()) {
				instance.cancel(true);
			} else if (instance.getStatus() == Status.PENDING) {
				return instance;
			}
		}

		instance = new NoteLoaderTask();
		return instance;
	}


	@Override
	protected ArrayList<Note> doInBackground(Object... params) {

		ArrayList<Note> notes = new ArrayList<>();
		String methodName = params[0].toString();
		DbHelper db = DbHelper.getInstance();

		if (params.length < 2 || params[1] == null) {
			try {
				Method method = db.getClass().getDeclaredMethod(methodName);
				notes = (ArrayList<Note>)method.invoke(db);
			} catch (NoSuchMethodException e) {
				return notes;
			} catch (IllegalAccessException e) {
				throw new NotesLoadingException("Error retrieving notes", e);
			} catch (InvocationTargetException e) {
				throw new NotesLoadingException("Error retrieving notes", e);
			}
		} else {
			Object methodArgs = params[1];
			Class[] paramClass = new Class[]{methodArgs.getClass()};
			try {
				Method method = db.getClass().getDeclaredMethod(methodName, paramClass);
				notes = (ArrayList<Note>) method.invoke(db, paramClass[0].cast(methodArgs));
			} catch (Exception e) {
				throw new NotesLoadingException("Error retrieving notes", e);
			}
		}

		return notes;
	}


	@Override
	protected void onPostExecute(ArrayList<Note> notes) {

		super.onPostExecute(notes);
		EventBus.getDefault().post(new NotesLoadedEvent(notes));
	}
}
