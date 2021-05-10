package com.ilibellus.models;

public class Stats extends StatsSingleNote {

    private int notesActive;
    private int notesArchived;
    private int notesTrashed;
    private int reminders;
    private int remindersFutures;
    private int notesChecklist;
    private int notesMasked;
    private int categories;

	private int location;

    private int wordsMax;
    private int wordsAvg;
    private int charsMax;
    private int charsAvg;
    private long usageTime;


    public int getNotesTotalNumber() {
        return notesActive + notesArchived + notesTrashed;
    }


    public int getNotesActive() {
        return notesActive;
    }


    public void setNotesActive(int notesActive) {
        this.notesActive = notesActive;
    }


    public int getNotesArchived() {
        return notesArchived;
    }


    public void setNotesArchived(int notesArchived) {
        this.notesArchived = notesArchived;
    }


    public int getNotesTrashed() {
        return notesTrashed;
    }


    public void setNotesTrashed(int notesTrashed) {
        this.notesTrashed = notesTrashed;
    }


    public int getReminders() {
        return reminders;
    }


    public void setReminders(int reminders) {
        this.reminders = reminders;
    }


    public int getRemindersFutures() {
        return remindersFutures;
    }


    public void setRemindersFutures(int remindersFutures) {
        this.remindersFutures = remindersFutures;
    }


    public int getNotesChecklist() {
        return notesChecklist;
    }


    public void setNotesChecklist(int notesChecklist) {
        this.notesChecklist = notesChecklist;
    }


    public int getNotesMasked() {
        return notesMasked;
    }


    public void setNotesMasked(int notesMasked) {
        this.notesMasked = notesMasked;
    }


    public int getCategories() {
        return categories;
    }


    public void setCategories(int categories) {
        this.categories = categories;
    }


    public int getTags() {
        return tags;
    }


    public void setTags(int tags) {
        this.tags = tags;
    }


    public int getWords() {
        return words;
    }


    public void setWords(int words) {
        this.words = words;
    }


    public int getChars() {
        return chars;
    }


    public void setChars(int chars) {
        this.chars = chars;
    }


    public int getWordsMax() {
        return wordsMax;
    }


    public void setWordsMax(int wordsMax) {
        this.wordsMax = wordsMax;
    }


    public int getWordsAvg() {
        return wordsAvg;
    }


    public void setWordsAvg(int wordsAvg) {
        this.wordsAvg = wordsAvg;
    }


    public int getCharsMax() {
        return charsMax;
    }


    public void setCharsMax(int charsMax) {
        this.charsMax = charsMax;
    }


    public int getCharsAvg() {
        return charsAvg;
    }


    public void setCharsAvg(int charsAvg) {
        this.charsAvg = charsAvg;
    }


	public int getLocation() {
		return location;
	}


	public void setLocation(int location) {
        this.location = location;
    }


    public long getUsageTime() {
        return usageTime;
    }


    public void setUsageTime(long usageTime) {
        this.usageTime = usageTime;
    }
}
