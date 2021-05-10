package com.ilibellus.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.SparseArray;
import android.widget.RemoteViews;

import com.ilibellus.MainActivity;
import com.ilibellus.R;
import com.ilibellus.utils.Constants;


public class ListWidgetProvider extends WidgetProvider {

    @Override
    protected RemoteViews getRemoteViews(Context mContext, int widgetId,
                                         boolean isSmall, boolean isSingleLine, 
                                         SparseArray<PendingIntent> pendingIntentsMap) {
        RemoteViews views;
        if (isSmall) {
            views = new RemoteViews(mContext.getPackageName(),
                    R.layout.widget_layout_small);
            views.setOnClickPendingIntent(R.id.list,
                    pendingIntentsMap.get(R.id.list));
        } else if (isSingleLine) {
            views = new RemoteViews(mContext.getPackageName(),
                    R.layout.widget_layout);
            views.setOnClickPendingIntent(R.id.add,
                    pendingIntentsMap.get(R.id.add));
            views.setOnClickPendingIntent(R.id.list,
                    pendingIntentsMap.get(R.id.list));
            views.setOnClickPendingIntent(R.id.camera,
                    pendingIntentsMap.get(R.id.camera));
        } else {
            views = new RemoteViews(mContext.getPackageName(),
                    R.layout.widget_layout_list);
            views.setOnClickPendingIntent(R.id.add,
                    pendingIntentsMap.get(R.id.add));
            views.setOnClickPendingIntent(R.id.list,
                    pendingIntentsMap.get(R.id.list));
            views.setOnClickPendingIntent(R.id.camera,
                    pendingIntentsMap.get(R.id.camera));

            // Set up the intent that starts the ListViewService, which will
            // provide the views for this collection.
            Intent intent = new Intent(mContext, ListWidgetService.class);
            // Add the app widget ID to the intent extras.
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            views.setRemoteAdapter(R.id.widget_list, intent);

            Intent clickIntent = new Intent(mContext, MainActivity.class);
            clickIntent.setAction(Constants.ACTION_WIDGET);
            PendingIntent clickPI = PendingIntent.getActivity(mContext, 0,
                    clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            views.setPendingIntentTemplate(R.id.widget_list, clickPI);
        }
        return views;
    }

}
