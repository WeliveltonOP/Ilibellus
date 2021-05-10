package com.ilibellus.utils;

import android.content.Context;
import com.ilibellus.Ilibellus;
import com.ilibellus.R;
import com.ilibellus.models.Category;

import java.util.ArrayList;
import java.util.Arrays;


public class Navigation {

    public static final int NOTES = 0;
    public static final int ARCHIVE = 1;
    public static final int REMINDERS = 2;
    public static final int TRASH = 3;
    public static final int UNCATEGORIZED = 4;
    public static final int CATEGORY = 5;


    /**
     * Returns actual navigation status
     */
    public static int getNavigation() {
        String[] navigationListCodes = Ilibellus.getAppContext().getResources().getStringArray(R.array.navigation_list_codes);
        String navigation = getNavigationText();

        if (navigationListCodes[NOTES].equals(navigation)) {
            return NOTES;
        } else if (navigationListCodes[ARCHIVE].equals(navigation)) {
            return ARCHIVE;
        } else if (navigationListCodes[REMINDERS].equals(navigation)) {
            return REMINDERS;
        } else if (navigationListCodes[TRASH].equals(navigation)) {
            return TRASH;
        } else if (navigationListCodes[UNCATEGORIZED].equals(navigation)) {
            return UNCATEGORIZED;
        } else {
            return CATEGORY;
        }
    }


    public static String getNavigationText() {
        Context mContext = Ilibellus.getAppContext();
        String[] navigationListCodes = mContext.getResources().getStringArray(R.array.navigation_list_codes);
        @SuppressWarnings("static-access")
        String navigation = mContext.getSharedPreferences(Constants.PREFS_NAME,
                mContext.MODE_MULTI_PROCESS).getString(Constants.PREF_NAVIGATION, navigationListCodes[0]);
        return navigation;
    }


    /**
     * Retrieves category currently shown
     *
     * @return id of category or null if current navigation is not a category
     */
    public static Long getCategory() {
        if (getNavigation() == CATEGORY) {
			return Long.valueOf(Ilibellus.getAppContext().getSharedPreferences(Constants.PREFS_NAME, Context
					.MODE_MULTI_PROCESS).getString(Constants.PREF_NAVIGATION, ""));
		} else {
            return null;
        }
    }


    /**
     * Checks if passed parameters is the actual navigation status
     */
    public static boolean checkNavigation(int navigationToCheck) {
        return checkNavigation(new Integer[]{navigationToCheck});
    }


    public static boolean checkNavigation(Integer[] navigationsToCheck) {
        boolean res = false;
        int navigation = getNavigation();
        for (int navigationToCheck : new ArrayList<>(Arrays.asList(navigationsToCheck))) {
            if (navigation == navigationToCheck) {
                res = true;
                break;
            }
        }
        return res;
    }


    /**
     * Checks if passed parameters is the category user is actually navigating in
     */
    public static boolean checkNavigationCategory(Category categoryToCheck) {
        Context mContext = Ilibellus.getAppContext();
        String[] navigationListCodes = mContext.getResources().getStringArray(R.array.navigation_list_codes);
        String navigation = mContext.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS).getString(Constants.PREF_NAVIGATION, navigationListCodes[0]);
        return (categoryToCheck != null && navigation.equals(String.valueOf(categoryToCheck.getId())));
    }

}
