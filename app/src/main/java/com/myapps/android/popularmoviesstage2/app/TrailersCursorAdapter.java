package com.example.android.popularmovies.app;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class TrailersCursorAdapter extends CursorAdapter {


    public TrailersCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item_movie_trailer, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView trailerUrl = (TextView) view.findViewById(R.id.list_item_movie_trailer_url_textview);
        trailerUrl.setText(cursor.getString(cursor.getColumnIndex("source")));

        TextView trailerName = (TextView) view.findViewById(R.id.list_item_movie_trailer_textview);
        trailerName.setText(cursor.getString(cursor.getColumnIndex("name")) );

        String uri = "@drawable/play_icon";
        int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
        ImageView playButton = (ImageView) view.findViewById(R.id.list_item_movie_trailer_imageview);
        Drawable res = context.getResources().getDrawable(imageResource);
        playButton.setImageDrawable(res);

    }
}
