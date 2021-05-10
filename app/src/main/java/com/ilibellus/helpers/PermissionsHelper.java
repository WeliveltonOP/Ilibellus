package com.ilibellus.helpers;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.ilibellus.R;
import com.ilibellus.models.listeners.OnPermissionRequestedListener;


public class PermissionsHelper {


	public static void requestPermission(Activity activity, String permission, int rationaleDescription, View
			messageView, OnPermissionRequestedListener onPermissionRequestedListener) {

		if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {

			if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {

				Snackbar.make(messageView, rationaleDescription, Snackbar.LENGTH_INDEFINITE)
						.setAction(R.string.ok, new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								requestPermissionExecute(activity, permission, onPermissionRequestedListener, messageView);
							}
						})
						.show();
			} else {
				requestPermissionExecute(activity, permission, onPermissionRequestedListener, messageView);
			}
		} else {
			if (onPermissionRequestedListener != null) {
				onPermissionRequestedListener.onPermissionGranted();
			}
		}
	}

	public static void requestPermissionExecute(Activity activity, String permission, OnPermissionRequestedListener
			onPermissionRequestedListener, View messageView) {
		RxPermissions.getInstance(activity)
				.request(permission)
				.subscribe(granted -> {
					if (granted) {
						if (onPermissionRequestedListener != null) {
							onPermissionRequestedListener.onPermissionGranted();
						}
					} else {
						String msg = activity.getString(R.string.permission_not_granted) + ": " + permission;
						Snackbar.make(activity, activity.findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show();
					}
				});
	}
}
