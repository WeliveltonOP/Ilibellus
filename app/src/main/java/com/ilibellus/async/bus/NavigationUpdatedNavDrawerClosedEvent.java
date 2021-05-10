package com.ilibellus.async.bus;

import com.ilibellus.helpers.LogDelegate;

public class NavigationUpdatedNavDrawerClosedEvent {

	public final Object navigationItem;


	public NavigationUpdatedNavDrawerClosedEvent(Object navigationItem) {
		LogDelegate.d(this.getClass().getName());
		this.navigationItem = navigationItem;
	}
}
