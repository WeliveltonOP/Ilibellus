package com.ilibellus.models.views;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import java.lang.ref.WeakReference;


public class SquareImageView extends AppCompatImageView {

    private WeakReference<AsyncTask<?, ?, ?>> mAsyncTaskReference;


    public SquareImageView(Context context) {
        super(context);
        setScaleType(ScaleType.CENTER_CROP);
    }


    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }


    public void setAsyncTask(AsyncTask<?, ?, ?> mAsyncTask) {
        this.mAsyncTaskReference = new WeakReference<AsyncTask<?, ?, ?>>(mAsyncTask);
    }


    public AsyncTask<?, ?, ?> getAsyncTask() {
        if (mAsyncTaskReference != null) {
            return mAsyncTaskReference.get();
        } else {
            return null;
        }
    }
}
