package com.ilibellus.async.bus;

import java.util.ArrayList;

import com.ilibellus.helpers.LogDelegate;
import com.ilibellus.models.Note;


public class NotesLoadedEvent {

	public ArrayList<Note> notes;


	public NotesLoadedEvent(ArrayList<Note> notes) {
		LogDelegate.d(this.getClass().getName());
		this.notes = notes;
	}
}
