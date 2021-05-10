package com.ilibellus.async.bus;

import com.ilibellus.helpers.LogDelegate;


public class PushbulletReplyEvent {

	public String message;

	public PushbulletReplyEvent(String message) {
		LogDelegate.d(this.getClass().getName());
		this.message = message;
	}
}
