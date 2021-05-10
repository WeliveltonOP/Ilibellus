package com.ilibellus.models.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;


public class NonScrollableListView extends ListView {

    public NonScrollableListView(Context context) {
        super(context);
    }


    public NonScrollableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public NonScrollableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void justifyListViewHeightBasedOnChildren() {

        ListAdapter adapter = getAdapter();

        if (adapter == null) {
            return;
        }
        ViewGroup vg = this;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = getLayoutParams();
        par.height = totalHeight + (getDividerHeight() * (adapter.getCount() - 1));
        setLayoutParams(par);
        requestLayout();
    }
}
