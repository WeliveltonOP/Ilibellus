package com.ilibellus.async.notes;

import java.util.List;

import com.ilibellus.db.DbHelper;
import com.ilibellus.models.Category;
import com.ilibellus.models.Note;


public class NoteProcessorCategorize extends NoteProcessor {

    Category category;


    public NoteProcessorCategorize(List<Note> notes, Category category) {
        super(notes);
        this.category = category;
    }


    @Override
    protected void processNote(Note note) {
        note.setCategory(category);
        DbHelper.getInstance().updateNote(note, false);
    }
}
