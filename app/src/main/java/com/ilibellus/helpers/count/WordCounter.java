package com.ilibellus.helpers.count;

import java.util.regex.Pattern;

import com.ilibellus.models.Note;

public interface WordCounter {

    int countWords(Note note);

    int countChars(Note note);

    default String sanitizeTextForWordsAndCharsCount(Note note, String field) {
        if (note.isChecklist()) {
            String regex = "(" + Pattern.quote(it.feio.android.checklistview.interfaces.Constants.CHECKED_SYM) + "|"
                    + Pattern.quote(it.feio.android.checklistview.interfaces.Constants.UNCHECKED_SYM) + ")";
            field = field.replaceAll(regex, "");
        }
        return field;
    }
}
