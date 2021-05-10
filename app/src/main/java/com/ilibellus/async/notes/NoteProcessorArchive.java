package com.ilibellus.async.notes;

import java.util.List;

import com.ilibellus.db.DbHelper;
import com.ilibellus.models.Note;


public class NoteProcessorArchive extends NoteProcessor {

    boolean archive;


    public NoteProcessorArchive(List<Note> notes, boolean archive) {
        super(notes);
        this.archive = archive;
    }


    @Override
    protected void processNote(Note note) {
        DbHelper.getInstance().archiveNote(note, archive);
    }
}
