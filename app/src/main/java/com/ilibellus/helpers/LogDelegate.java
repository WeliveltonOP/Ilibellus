package com.ilibellus.helpers;

import android.content.Context;
import android.util.Log;

import com.bosphere.filelogger.FL;
import com.bosphere.filelogger.FLConfig;
import com.bosphere.filelogger.FLConst;

import java.io.File;

import com.ilibellus.Ilibellus;
import com.ilibellus.exceptions.GenericException;

import static it.feio.android.checklistview.interfaces.Constants.TAG;
import static com.ilibellus.utils.ConstantsBase.PREF_ENABLE_FILE_LOGGING;

public class LogDelegate {

    private static Boolean fileLoggingEnabled;

    private LogDelegate() {
        // Public constructor hiding
    }

    public static void v(String message) {
        if (isFileLoggingEnabled()) {
            FL.v(message);
        } else {
            Log.v(TAG, message);
        }
    }

    public static void d(String message) {
        if (isFileLoggingEnabled()) {
            FL.d(message);
        } else {
            Log.d(TAG, message);
        }
    }

    public static void i(String message) {
        if (isFileLoggingEnabled()) {
            FL.i(message);
        } else {
            Log.i(TAG, message);
        }
    }

    public static void w(String message, Throwable e) {
        if (isFileLoggingEnabled()) {
            FL.w(message, e);
        } else {
            Log.w(TAG, message, e);
        }
    }

    public static void w(String message) {
        if (isFileLoggingEnabled()) {
            FL.w(message);
        } else {
            Log.w(TAG, message);
        }
    }

    public static void e(String message, Throwable e) {
        if (isFileLoggingEnabled()) {
            FL.e(message, e);
        } else {
            Log.e(TAG, message, e);
        }
    }

    public static void e(String message) {
        e(message, new GenericException(message));
    }

    private static boolean isFileLoggingEnabled() {
        if (fileLoggingEnabled == null) {
            fileLoggingEnabled = Ilibellus.getSharedPreferences().getBoolean(PREF_ENABLE_FILE_LOGGING, false);
            if (fileLoggingEnabled) {
                FL.init(new FLConfig.Builder(Ilibellus.getAppContext())
                        .minLevel(FLConst.Level.V)
                        .logToFile(true)
                        .dir(new File(StorageHelper.getExternalStoragePublicDir(), "logs"))
                        .retentionPolicy(FLConst.RetentionPolicy.FILE_COUNT)
                        .build());
                FL.setEnabled(true);
            }
        }
        return fileLoggingEnabled;
    }
}
