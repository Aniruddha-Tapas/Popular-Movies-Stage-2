package com.example.android.popularmovies.app;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;


public class MoviesCursorAdapter extends CursorAdapter {

    public MoviesCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        ImageView imageView  = new ImageView(context);

        imageView.setLayoutParams(new GridView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setBackgroundColor(Color.BLACK);
        imageView.setPadding(0,0,0,0);

        return imageView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        final ImageView imageView = (ImageView) view;

        final byte[] imageBytes = cursor.getBlob(
                cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_BITMAP) );

        if(imageBytes != null )
            imageView.setImageBitmap(
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length) );

    }
}
