package com.ilibellus.async.bus;

import com.ilibellus.helpers.LogDelegate;

public class PasswordRemovedEvent {

	public PasswordRemovedEvent() {
		LogDelegate.d(this.getClass().getName());
	}
}
