package com.ilibellus.helpers.count;

import com.ilibellus.models.Note;
import rx.Observable;

public class IdeogramsWordCounter implements WordCounter {

    @Override
    public int countWords(Note note) {
        return countChars(note);
    }

    @Override
    public int countChars(Note note) {
        String titleAndContent = note.getTitle() + "\n" + note.getContent();
        return Observable
                .from(sanitizeTextForWordsAndCharsCount(note, titleAndContent).split(""))
                .filter(s -> !s.matches("\\s"))
                .count().toBlocking().single();
    }
}
