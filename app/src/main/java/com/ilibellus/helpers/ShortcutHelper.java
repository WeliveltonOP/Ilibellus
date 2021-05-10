package com.ilibellus.helpers;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;

import com.ilibellus.MainActivity;
import com.ilibellus.Ilibellus;
import com.ilibellus.R;
import com.ilibellus.helpers.date.DateHelper;
import com.ilibellus.models.Note;
import com.ilibellus.utils.Constants;


public class ShortcutHelper {


    /**
     * Adding shortcut on Home screen
     */
    public static void addShortcut(Context context, Note note) {

        String shortcutTitle = note.getTitle().length() > 0 ? note.getTitle() : DateHelper.getFormattedDate(note
                .getCreation(), Ilibellus.getSharedPreferences().getBoolean(Constants
                .PREF_PRETTIFIED_DATES, true));

        if(Build.VERSION.SDK_INT < 26) {
            Intent shortcutIntent = new Intent(context, MainActivity.class);
            shortcutIntent.putExtra(Constants.INTENT_KEY, note.get_id());
            shortcutIntent.setAction(Constants.ACTION_SHORTCUT);

            Intent addIntent = new Intent();
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutTitle);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                    Intent.ShortcutIconResource.fromContext(context, R.drawable.ic_shortcut));
            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

            context.sendBroadcast(addIntent);
        }
        else {
            ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
            if (shortcutManager.isRequestPinShortcutSupported()) {
                Intent intent = new Intent(context.getApplicationContext(), context.getClass());
                intent.setAction(Intent.ACTION_MAIN);
                ShortcutInfo pinShortcutInfo = new ShortcutInfo
                        .Builder(context,"pinned-shortcut")
                        .setIcon(Icon.createWithResource(context, R.drawable.ic_shortcut))
                        .setIntent(intent)
                        .setShortLabel(shortcutTitle)
                        .build();
                Intent pinnedShortcutCallbackIntent = shortcutManager.createShortcutResultIntent(pinShortcutInfo);
                //Get notified when a shortcut is pinned successfully//
                PendingIntent successCallback = PendingIntent.getBroadcast(context, 0, pinnedShortcutCallbackIntent, 0);
                shortcutManager.requestPinShortcut(pinShortcutInfo, successCallback.getIntentSender()
                );
            }
        }
    }

    /**
     * Removes note shortcut from home launcher
     */
    public static void removeshortCut(Context context, Note note) {
        Intent shortcutIntent = new Intent(context, MainActivity.class);
        shortcutIntent.putExtra(Constants.INTENT_KEY, note.get_id());
        shortcutIntent.setAction(Constants.ACTION_SHORTCUT);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		String shortcutTitle = note.getTitle().length() > 0 ? note.getTitle() : DateHelper.getFormattedDate(note
				.getCreation(), Ilibellus.getSharedPreferences().getBoolean(Constants
				.PREF_PRETTIFIED_DATES, true));

        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutTitle);

        addIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
        context.sendBroadcast(addIntent);
    }
}
