package com.ilibellus.helpers.count;

import com.ilibellus.models.Note;
import rx.Observable;

public class DefaultWordCounter implements WordCounter {

    @Override
    public int countWords(Note note) {
        int count = 0;
        String[] fields = {note.getTitle(), note.getContent()};
        for (String field : fields) {
            field = sanitizeTextForWordsAndCharsCount(note, field);
            boolean word = false;
            int endOfLine = field.length() - 1;
            for (int i = 0; i < field.length(); i++) {
                // if the char is a letter, word = true.
                if (Character.isLetter(field.charAt(i)) && i != endOfLine) {
                    word = true;
                    // if char isn't a letter and there have been letters before, counter goes up.
                } else if (!Character.isLetter(field.charAt(i)) && word) {
                    count++;
                    word = false;
                    // last word of String; if it doesn't end with a non letter, it  wouldn't count without this.
                } else if (Character.isLetter(field.charAt(i)) && i == endOfLine) {
                    count++;
                }
            }
        }
        return count;
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
