package com.ilibellus.models.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.neopixl.pixlui.components.textview.TextView;
import com.ilibellus.MainActivity;
import com.ilibellus.R;
import com.ilibellus.models.NavigationItem;
import com.ilibellus.utils.Constants;
import com.ilibellus.utils.Fonts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class NavDrawerAdapter extends BaseAdapter {

    private Activity mActivity;
    private List<NavigationItem> items = new ArrayList<>();
    private LayoutInflater inflater;


    public NavDrawerAdapter(Activity mActivity, List<NavigationItem> items) {
        this.mActivity = mActivity;
        this.items = items;
        inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return items.size();
    }


    @Override
    public Object getItem(int position) {
        return items.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        NoteDrawerAdapterViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.drawer_list_item, parent, false);

            // Overrides font sizes with the one selected from user
            Fonts.overrideTextSize(mActivity, mActivity.getSharedPreferences(Constants.PREFS_NAME,
                    Context.MODE_MULTI_PROCESS), convertView);

            holder = new NoteDrawerAdapterViewHolder();

            holder.imgIcon = (ImageView) convertView.findViewById(R.id.icon);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        } else {
            holder = (NoteDrawerAdapterViewHolder) convertView.getTag();
        }

        // Set the results into TextViews	
        holder.txtTitle.setText(items.get(position).getText());

        if (isSelected(position)) {
            holder.imgIcon.setImageResource(items.get(position).getIconSelected());
            holder.txtTitle.setTypeface(null, Typeface.BOLD);
            int color = mActivity.getResources().getColor(R.color.colorPrimaryDark);
            holder.txtTitle.setTextColor(color);
            holder.imgIcon.getDrawable().mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        } else {
            holder.imgIcon.setImageResource(items.get(position).getIcon());
            holder.txtTitle.setTypeface(null, Typeface.NORMAL);
            holder.txtTitle.setTextColor(mActivity.getResources().getColor(R.color.drawer_text));
        }

        return convertView;
    }


    private boolean isSelected(int position) {

        // Getting actual navigation selection
        String[] navigationListCodes = mActivity.getResources().getStringArray(R.array.navigation_list_codes);

        // Managing temporary navigation indicator when coming from a widget
        String navigationTmp = MainActivity.class.isAssignableFrom(mActivity.getClass()) ? ((MainActivity) mActivity)
                .getNavigationTmp() : null;

        String navigation = navigationTmp != null ? navigationTmp
                : mActivity.getSharedPreferences(Constants.PREFS_NAME, Activity.MODE_MULTI_PROCESS)
                .getString(Constants.PREF_NAVIGATION, navigationListCodes[0]);

        // Finding selected item from standard navigation items or tags
        int index = Arrays.asList(navigationListCodes).indexOf(navigation);

        if (index == -1)
            return false;

        String navigationLocalized = mActivity.getResources().getStringArray(R.array.navigation_list)[index];
        return navigationLocalized.equals(items.get(position).getText());
    }

}


/**
 * Holder object
 *
 * @author fede
 */
class NoteDrawerAdapterViewHolder {

    ImageView imgIcon;
    TextView txtTitle;
}
