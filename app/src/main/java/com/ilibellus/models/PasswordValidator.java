package com.ilibellus.models;

public interface PasswordValidator {

    enum Result {
        SUCCEED, FAIL, RESTORE
    }

    void onPasswordValidated(Result result);
}
