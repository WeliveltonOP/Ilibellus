package com.ilibellus.async.notes;

import java.util.List;

import com.ilibellus.Ilibellus;
import com.ilibellus.db.DbHelper;
import com.ilibellus.models.Note;
import com.ilibellus.helpers.ReminderHelper;
import com.ilibellus.helpers.ShortcutHelper;


public class NoteProcessorTrash extends NoteProcessor {

    boolean trash;


    public NoteProcessorTrash(List<Note> notes, boolean trash) {
        super(notes);
        this.trash = trash;
    }


    @Override
    protected void processNote(Note note) {
        if (trash) {
            ShortcutHelper.removeshortCut(Ilibellus.getAppContext(), note);
            ReminderHelper.removeReminder(Ilibellus.getAppContext(), note);
        } else {
            ReminderHelper.addReminder(Ilibellus.getAppContext(), note);
        }
        DbHelper.getInstance().trashNote(note, trash);
    }
}
