package com.ilibellus.models.holders;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.neopixl.pixlui.components.textview.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.ilibellus.R;
import com.ilibellus.models.views.SquareImageView;


public class NoteViewHolder {

	public NoteViewHolder(View view) {
		ButterKnife.bind(this, view);
	}

	@BindView(R.id.root) public View root;
	@BindView(R.id.card_layout) public View cardLayout;
	@BindView(R.id.category_marker) public View categoryMarker;

	@BindView(R.id.note_title) public TextView title;
	@BindView(R.id.note_content) public TextView content;
	@BindView(R.id.note_date) public TextView date;

	@BindView(R.id.archivedIcon) public ImageView archiveIcon;
	@BindView(R.id.locationIcon) public ImageView locationIcon;
	@BindView(R.id.alarmIcon) public ImageView alarmIcon;
	@BindView(R.id.lockedIcon) public ImageView lockedIcon;
	@Nullable
	@BindView(R.id.attachmentIcon) public ImageView attachmentIcon;
	@Nullable @BindView(R.id.attachmentThumbnail) public SquareImageView attachmentThumbnail;
}
