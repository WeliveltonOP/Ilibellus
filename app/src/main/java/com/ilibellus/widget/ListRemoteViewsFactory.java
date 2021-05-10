package com.ilibellus.widget;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spanned;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.bumptech.glide.request.target.AppWidgetTarget;

import java.util.List;

import com.ilibellus.Ilibellus;
import com.ilibellus.R;
import com.ilibellus.db.DbHelper;
import com.ilibellus.helpers.LogDelegate;
import com.ilibellus.models.Attachment;
import com.ilibellus.models.Note;
import com.ilibellus.helpers.BitmapHelper;
import com.ilibellus.utils.Constants;
import com.ilibellus.utils.Navigation;
import com.ilibellus.helpers.TextHelper;


public class ListRemoteViewsFactory implements RemoteViewsFactory {

    private static boolean showThumbnails = true;
    private static boolean showTimestamps = true;
    private final int WIDTH = 80;
    private final int HEIGHT = 80;
    private Ilibellus app;
    private int appWidgetId;
    private List<Note> notes;
    private int navigation;

    private AppWidgetTarget appWidgetTarget;



    public ListRemoteViewsFactory(Application app, Intent intent) {
        this.app = (Ilibellus) app;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public static void updateConfiguration(Context mContext, int mAppWidgetId, String sqlCondition,
                                           boolean thumbnails, boolean timestamps) {
        LogDelegate.d("Widget configuration updated");
        mContext.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS).edit()
                .putString(Constants.PREF_WIDGET_PREFIX + String.valueOf(mAppWidgetId), sqlCondition).commit();
        showThumbnails = thumbnails;
        showTimestamps = timestamps;
    }

    @Override
    public void onCreate() {
        LogDelegate.d("Created widget " + appWidgetId);
        String condition = app.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS)
                .getString(
                        Constants.PREF_WIDGET_PREFIX
                                + String.valueOf(appWidgetId), "");
        notes = DbHelper.getInstance().getNotes(condition, true);
    }

    @Override
    public void onDataSetChanged() {
        LogDelegate.d("onDataSetChanged widget " + appWidgetId);
        navigation = Navigation.getNavigation();

        String condition = app.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS)
                .getString(
                        Constants.PREF_WIDGET_PREFIX
                                + String.valueOf(appWidgetId), "");
        notes = DbHelper.getInstance().getNotes(condition, true);
    }

    @Override
    public void onDestroy() {
        app.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS)
                .edit()
                .remove(Constants.PREF_WIDGET_PREFIX
                        + String.valueOf(appWidgetId)).commit();
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row = new RemoteViews(app.getPackageName(), R.layout.note_layout_widget);

        Note note = notes.get(position);

        Spanned[] titleAndContent = TextHelper.parseTitleAndContent(app, note);

        row.setTextViewText(R.id.note_title, titleAndContent[0]);
        row.setTextViewText(R.id.note_content, titleAndContent[1]);

        color(note, row);

        if (!note.isLocked() && showThumbnails && note.getAttachmentsList().size() > 0) {
      			Attachment mAttachment = note.getAttachmentsList().get(0);
      			Bitmap bmp = BitmapHelper.getBitmapFromAttachment(app, mAttachment, WIDTH, HEIGHT);
      			row.setBitmap(R.id.attachmentThumbnail, "setImageBitmap", bmp);
            row.setInt(R.id.attachmentThumbnail, "setVisibility", View.VISIBLE);
        } else {
            row.setInt(R.id.attachmentThumbnail, "setVisibility", View.GONE);
        }
        if(showTimestamps) {
          row.setTextViewText(R.id.note_date, TextHelper.getDateText(app, note, navigation));
        } else {
          row.setTextViewText(R.id.note_date, "");
        }

        // Next, set a fill-intent, which will be used to fill in the pending intent template
        // that is set on the collection view in StackWidgetProvider.
        Bundle extras = new Bundle();
        extras.putParcelable(Constants.INTENT_NOTE, note);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        // Make it possible to distinguish the individual on-click
        // action of a given item
        row.setOnClickFillInIntent(R.id.root, fillInIntent);

        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private void color(Note note, RemoteViews row) {

        String colorsPref = app.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS)
                .getString("settings_colors_widget",
                        Constants.PREF_COLORS_APP_DEFAULT);

        // Checking preference
        if (!colorsPref.equals("disabled")) {

            // Resetting transparent color to the view
            row.setInt(R.id.tag_marker, "setBackgroundColor", Color.parseColor("#00000000"));

            // If tag is set the color will be applied on the appropriate target
            if (note.getCategory() != null && note.getCategory().getColor() != null) {
                if (colorsPref.equals("list")) {
                    row.setInt(R.id.card_layout, "setBackgroundColor", Integer.parseInt(note.getCategory().getColor()));

                } else {
                    row.setImageViewResource(R.id.tag_marker, R.drawable.bg_category_marker);
                    row.setInt(R.id.tag_marker, "setColorFilter", Integer.parseInt(note.getCategory().getColor()));
                }
            } else {
                row.setInt(R.id.tag_marker, "setBackgroundColor", 0);
            }
        }
    }

}
