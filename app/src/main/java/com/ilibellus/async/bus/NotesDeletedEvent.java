package com.ilibellus.async.bus;

import java.util.List;

import com.ilibellus.helpers.LogDelegate;
import com.ilibellus.models.Note;


public class NotesDeletedEvent {

	public List<Note> notes;


	public NotesDeletedEvent(List<Note> notes) {
		LogDelegate.d(this.getClass().getName());
		this.notes = notes;
	}
}
