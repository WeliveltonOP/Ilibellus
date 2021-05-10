package com.ilibellus.async.bus;

import android.service.notification.StatusBarNotification;

import com.ilibellus.helpers.LogDelegate;


public class NotificationRemovedEvent {

	public StatusBarNotification statusBarNotification;


	public NotificationRemovedEvent(StatusBarNotification statusBarNotification) {
		LogDelegate.d(this.getClass().getName());
		this.statusBarNotification = statusBarNotification;
	}
}
