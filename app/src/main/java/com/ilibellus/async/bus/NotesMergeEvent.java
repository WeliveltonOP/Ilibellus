package com.ilibellus.async.bus;

import com.ilibellus.helpers.LogDelegate;

public class NotesMergeEvent {

	public final boolean keepMergedNotes;


	public NotesMergeEvent(boolean keepMergedNotes) {
		LogDelegate.d(this.getClass().getName());
		this.keepMergedNotes = keepMergedNotes;
	}
}
