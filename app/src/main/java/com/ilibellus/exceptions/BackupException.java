package com.ilibellus.exceptions;

public class BackupException extends RuntimeException {

    private static final long serialVersionUID = 7892197590810157669L;

    public BackupException(String message, Exception e) {
        super(message, e);
    }

}
