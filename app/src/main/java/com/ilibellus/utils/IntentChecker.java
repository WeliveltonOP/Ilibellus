package com.ilibellus.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;


public class IntentChecker {

	/**
	 * Retrieves
	 * @param ctx
	 * @param intent
	 * @return
	 */
	public static String resolveActivityPackage(Context ctx, Intent intent) {
		ComponentName activity= intent.resolveActivity(ctx.getPackageManager());
		return activity != null ? activity.getPackageName() : "";
	}

	/**
	 * Checks intent and features availability
	 *
	 * @param features
	 * @param ctx
	 * @param intent
	 * @return
	 */
	public static boolean isAvailable(Context ctx, Intent intent, String[] features) {
		boolean res = getCompatiblePackages(ctx, intent).size() > 0;

		if (features != null) {
			for (String feature : features) {
				res = res && ctx.getPackageManager().hasSystemFeature(feature);
			}
		}
		return res;
	}

	/**
	 * Checks Intent's action
	 *
	 * @param i      Intent to ckeck
	 * @param action Action to compare with
	 * @return
	 */
	public static boolean checkAction(Intent i, String action) {
		return action.equals(i.getAction());
	}


	/**
	 * Checks Intent's actions
	 *
	 * @param i      Intent to ckeck
	 * @param actions Multiple actions to compare with
	 * @return
	 */
	public static boolean checkAction(Intent i, String... actions) {
		for (String action : actions) {
			if (checkAction(i, action)) return true;
		}
		return false;
	}

	private static List<ResolveInfo> getCompatiblePackages(Context ctx, Intent intent) {
		final PackageManager mgr = ctx.getPackageManager();
		return mgr.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
	}
}
