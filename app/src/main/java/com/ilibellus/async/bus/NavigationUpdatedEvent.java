package com.ilibellus.async.bus;

import com.ilibellus.helpers.LogDelegate;

/**
 * Created by fede on 18/04/15.
 */
public class NavigationUpdatedEvent {

	public final Object navigationItem;


	public NavigationUpdatedEvent(Object navigationItem) {
		LogDelegate.d(this.getClass().getName());
		this.navigationItem = navigationItem;
	}
}
