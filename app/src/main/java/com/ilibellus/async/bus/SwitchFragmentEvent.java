package com.ilibellus.async.bus;

import com.ilibellus.helpers.LogDelegate;

public class SwitchFragmentEvent {

	public enum Direction {
		CHILDREN, PARENT
	}


	public Direction direction;


	public SwitchFragmentEvent(Direction direction) {
		LogDelegate.d(this.getClass().getName());
		this.direction = direction;
	}
}
