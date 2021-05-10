package com.ilibellus.exceptions;

public class NotesLoadingException extends RuntimeException {

	private static final long serialVersionUID = 1288015037660807104L;

	public NotesLoadingException(String message, Exception e) {
		super(message, e);
	}

}
