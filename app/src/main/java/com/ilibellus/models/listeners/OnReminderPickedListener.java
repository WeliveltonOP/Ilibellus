package com.ilibellus.models.listeners;


public interface OnReminderPickedListener {

    public void onReminderPicked(long reminder);

    public void onRecurrenceReminderPicked(String recurrenceRule);
}
